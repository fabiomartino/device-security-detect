import Foundation
import MachO
import UIKit
import LocalAuthentication

@objc public class DeviceSecurityDetect: NSObject {

    // MARK: - Constants for Suspicious Items

    private static let suspiciousLibraryPatterns: [String] = [
        "frida",        // Frida related libraries
        "libinjector"   // Other common library names
    ]

    private static let suspiciousURLSchemes: [String] = [
        "cydia://",
        "sileo://",
        "zbra://",
        "filza://",
        "undecimus://",
        "activator://"
    ]

    private static let suspiciousAppsPathToCheck: [String] = [
        "/Applications/Cydia.app",
        "/Applications/FakeCarrier.app",
        "/Applications/Icy.app",
        "/Applications/IntelliScreen.app",
        "/Applications/MxTube.app",
        "/Applications/RockApp.app",
        "/Applications/SBSettings.app",
        "/Applications/SBSetttings.app",
        "/Applications/Snoop-itConfig.app",
        "/Applications/WinterBoard.app",
        "/Applications/blackra1n.app",
        "/Applications/VnodeBypass.app",
        "/Applications/RootHide.app",
        "/Applications/Dopamine.app"
    ]
    
    private static let suspiciousSystemPathsToCheck: [String] = [
        "/.bootstrapped_electra",
        "/.cydia_no_stash",
        "/.installed_unc0ver",
        "/Library/MobileSubstrate/CydiaSubstrate.dylib",
        "/Library/MobileSubstrate/DynamicLibraries",
        "/Library/MobileSubstrate/DynamicLibraries/LiveClock.plist",
        "/Library/MobileSubstrate/DynamicLibraries/Veency.plist",
        "/Library/MobileSubstrate/MobileSubstrate.dylib",
        "/System/Library/LaunchDaemons/com.ikey.bbot.plist",
        "/System/Library/LaunchDaemons/com.saurik.Cydia.Startup.plist",
        "/bin.sh",
        "/bin/bash",
        "/etc/apt",
        "/etc/apt/sources.list.d/electra.list",
        "/etc/apt/sources.list.d/sileo.sources",
        "/etc/apt/undecimus/undecimus.list",
        "/jb/amfid_payload.dylib",
        "/jb/jailbreakd.plist",
        "/jb/lzma",
        "/jb/libjailbreak.dylib",
        "/jb/offsets.plist",
        "/private/var/Users/",
        "/private/var/cache/apt/",
        "/private/var/db/stash",
        "/private/var/jailbreak",
        "/private/var/lib/apt",
        "/private/var/lib/apt/",
        "/private/var/lib/cydia",
        "/private/var/log/syslog",
        "/private/var/mobile/Library/Cydia/",
        "/private/var/mobile/Library/SBSettings/Themes",
        "/private/var/stash",
        "/private/var/tmp/cydia.log",
        "/usr/bin/cycript",
        "/usr/bin/ssh",
        "/usr/lib/libcycript.dylib",
        "/usr/lib/libjailbreak.dylib",
        "/usr/libexec/cydia/",
        "/usr/libexec/cydia/firmware.sh",
        "/usr/libexec/sftp-server",
        "/usr/libexec/ssh-keysign",
        "/usr/local/bin/cycript",
        "/usr/sbin/frida-server",
        "/usr/sbin/sshd",
        "/usr/share/jailbreak/injectme.plist",
        "/var/checkra1n.dmg",
        "/var/jb",
        "/var/lib/cydia",
        "/var/lib/dpkg/info/mobilesubstrate.md5sums",
        "/var/log/apt"
    ]

    // MARK: - Jailbreak Detection Methods

    /**
     Checks if the device is jailbroken by looking for suspicious apps, system paths, and files.
     Also checks for common jailbreak tools and system alterations.
     - Returns: A boolean indicating whether the device is jailbroken.
     */
    @objc public func isJailBreak() -> Bool {
        log("Checking if device is jailbroken")

        if checkForJailbreakTools() || isContainsSuspiciousApps() || isSuspiciousSystemPathsExists() || canEditSystemFiles() {
            return true
        }

        if isSuspiciousLibraryLoaded() || isFridaEnvironmentVariablePresent() || isForkOrSystemCallDetected() || checkDYLD() {
            return true
        }

        return false
    }

    /**
     Checks if the device is running on a simulator using compile-time and runtime checks.
     - Returns: A boolean indicating whether the device is a simulator.
     */
    @objc public func isSimulator() -> Bool {
        log("Checking if device is running on a simulator")
        return checkCompile() || checkRuntime()
    }

    /**
     Checks if the application is running in debug mode by inspecting process flags.
     - Returns: A boolean indicating whether the application is in debug mode.
     */
    @objc public func isDebuggedMode() -> Bool {
        log("Checking if application is running in debug mode")
        var info = kinfo_proc()
        var size = MemoryLayout.stride(ofValue: info)
        var mib: [Int32] = [CTL_KERN, KERN_PROC, KERN_PROC_PID, getpid()]

        let sysctlResult = sysctl(&mib, UInt32(mib.count), &info, &size, nil, 0)

        if sysctlResult == 0 {
            return (info.kp_proc.p_flag & P_TRACED) != 0
        }

        return false
    }

    /**
     Checks if PIN or biometric authentication is enabled on the device using Local Authentication.
     - Returns: A boolean indicating whether PIN or biometric authentication is enabled.
     */
    @objc public func pinCheck() -> Bool {
        log("Checking if PIN or biometric authentication is enabled")
        let context = LAContext()
        var error: NSError?

        if context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) {
            return true
        } else {
            log("Error checking PIN/Biometric authentication: \(error?.localizedDescription ?? "Unknown error")")
            return false
        }
    }

    // MARK: - Helper Methods for Simulator Detection

    /**
     Runtime check for simulator based on environment variables.
     - Returns: A boolean indicating whether the device is running on a simulator in runtime.
     */
    private func checkRuntime() -> Bool {
        return ProcessInfo.processInfo.environment["SIMULATOR_DEVICE_NAME"] != nil
    }

    /**
     Compile-time check for simulator using targetEnvironment.
     - Returns: A boolean indicating whether the device is running on a simulator in compile time.
     */
    private func checkCompile() -> Bool {
        #if targetEnvironment(simulator)
            return true
        #else
            return false
        #endif
    }

    // MARK: - Helper Methods for Jailbreak Detection

    /**
     Checks for the presence of suspicious URL schemes used by jailbreak tools.
     - Returns: A boolean indicating whether suspicious URL schemes are found.
     */
    private func checkForJailbreakTools() -> Bool {
        return checkForURLSchemes(DeviceSecurityDetect.suspiciousURLSchemes)
    }


    /**
     General method to check for the presence of any suspicious URL schemes.
     - Returns: A boolean indicating whether any suspicious URL schemes are found.
     */
    private func checkForURLSchemes(_ schemes: [String]) -> Bool {
        return schemes.first(where: { UIApplication.shared.canOpenURL(URL(string: $0)!) }) != nil
    }

    /**
     Checks for suspicious apps by verifying their paths on the device.
     - Returns: A boolean indicating whether suspicious apps are installed.
     */
    private func isContainsSuspiciousApps() -> Bool {
        return DeviceSecurityDetect.suspiciousAppsPathToCheck.contains { FileManager.default.fileExists(atPath: $0) }
    }

    /**
     Checks for suspicious system paths commonly used by jailbreaks.
     - Returns: A boolean indicating whether suspicious system paths exist.
     */
    private func isSuspiciousSystemPathsExists() -> Bool {
        return DeviceSecurityDetect.suspiciousSystemPathsToCheck.contains { FileManager.default.fileExists(atPath: $0) }
    }

    /**
     Checks if the device allows modification of system files by attempting to write to a protected file path.
     - Returns: A boolean indicating whether system files can be modified.
     */
    private func canEditSystemFiles() -> Bool {
        let testFilePath = "/private/jailBreakTestFile.txt"
        let testContent = "Test content to check if device allows file modifications."
        
        do {
            try testContent.write(toFile: testFilePath, atomically: true, encoding: .utf8)
            try FileManager.default.removeItem(atPath: testFilePath)
            return true
        } catch {
            return false
        }
    }

    /**
     Checks if any suspicious libraries are loaded into the process.
     - Returns: A boolean indicating whether any suspicious libraries are loaded.
     */
    private func isSuspiciousLibraryLoaded() -> Bool {
        let libraryCount = _dyld_image_count()
        for index in 0..<libraryCount {
            guard let imageName = _dyld_get_image_name(index) else {
                continue
            }
            let libraryName = String(cString: imageName)

            if DeviceSecurityDetect.suspiciousLibraryPatterns.contains(where: { libraryName.lowercased().contains($0.lowercased()) }) {
                return true
            }
        }

        return false
    }

    /**
     Checks for the presence of Frida-related environment variables indicating a possible Frida-based debugging or tampering tool.
     - Returns: A boolean indicating whether Frida-related environment variables are found.
     */
    private func isFridaEnvironmentVariablePresent() -> Bool {
        let environmentVariables = ["FRIDA", "FRIDA_SERVER"]
        let environment = ProcessInfo.processInfo.environment
        return environmentVariables.contains { environment[$0] != nil }
    }

    /**
     Checks if the device's process has been forked or if there are system call anomalies indicative of tampering.
     - Returns: A boolean indicating whether a fork or system call anomaly is detected.
     */
    private func isForkOrSystemCallDetected() -> Bool {
        let pid = getpgrp()

        if pid < 0 {
            return true
        } else {
            return false
        }
    }

    /**
     Checks for suspicious DYLD libraries associated with known jailbreak tools.
     - Returns: A boolean indicating whether any suspicious DYLD libraries are loaded.
     */
    private func checkDYLD() -> Bool {
        let suspiciousLibraries: Set<String> = [
            "systemhook.dylib",
            "SubstrateLoader.dylib",
            "SSLKillSwitch2.dylib",
            "SSLKillSwitch.dylib",
            "MobileSubstrate.dylib",
            "TweakInject.dylib",
            "CydiaSubstrate",
            "cynject",
            "CustomWidgetIcons",
            "PreferenceLoader",
            "RocketBootstrap",
            "WeeLoader",
            "/.file",
            "libhooker",
            "SubstrateInserter",
            "SubstrateBootstrap",
            "ABypass",
            "FlyJB",
            "Substitute",
            "Cephei",
            "Electra",
            "AppSyncUnified-FrontBoard.dylib",
            "Shadow",
            "FridaGadget",
            "frida",
            "libcycript"
        ]
        
        for index in 0..<_dyld_image_count() {
            let imageName = String(cString: _dyld_get_image_name(index))
            for library in suspiciousLibraries where imageName.localizedCaseInsensitiveContains(library) {
                return true
            }
        }
        
        return false
    }
}
