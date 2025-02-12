package com.mukha.andrei.plugins.device.secutiry.detect;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.app.KeyguardManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class DeviceSecurityDetect {
    private Context context;

    // Constructor to initialize the class with a Context instance
    public DeviceSecurityDetect(Context context) {
        this.context = context;
    }

    // Checks if the device is rooted
    public boolean isDeviceRooted() {
        return checkBuildTags() || checkSuBinary() || isSuBinaryAvailable() || areOtaCertsMissing();
    }

    // Checks if the device is running in an emulator
    public boolean isSimulator() {
        Log.d("DeviceSecurityDetect", "Checking if device is running on a simulator");
        return checkBuildProperties() || checkEmulatorFiles() || checkQEmuDriverFile() || checkQEmuProps();
    }

    // Checks if the application is running in debug mode
    public boolean isDebuggedMode() {
        Log.d("DeviceSecurityDetect", "Checking if application is running in debug mode");
        try {
            return (getApplicationFlags() & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            Log.e("DeviceSecurityDetect", "Error checking debug mode: " + e.getMessage());
            return false;
        }
    }

    // Checks if PIN or biometric authentication is enabled
    public boolean pinCheck(Context context) {
        Log.d("DeviceSecurityDetect", "Checking if PIN or biometric authentication is enabled");
        try {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        } catch (Exception ex) {
            Log.e("DeviceSecurityDetect", "Error checking PIN security: " + ex.getMessage());
            return false;
        }
    }

    // Checks for suspicious build tags that indicate a rooted device
    private boolean checkBuildTags() {
        String buildTags = android.os.Build.TAGS;
        if (buildTags == null) {
            return false;
        }

        String[] suspiciousTags = {
            "test-keys",            // Common for many rooted devices
            "dev-keys",             // Development keys, often seen in custom ROMs
            "userdebug",            // User-debuggable build, common in rooted devices
            "engineering",          // Engineering build, may indicate a modified system
            "release-keys-debug",   // Debug version of release keys
            "custom",               // Explicitly marked as custom
            "rooted",               // Explicitly marked as rooted (rare, but possible)
            "supersu",              // Indicates SuperSU rooting tool
            "magisk",               // Indicates Magisk rooting framework
            "lineage",              // LineageOS custom ROM
            "unofficial"            // Unofficial build, common in custom ROMs
        };

        for (String tag : suspiciousTags) {
            if (buildTags.contains(tag)) {
                return true;
            }
        }

        return false;
    }

    // Checks for the presence of the su binary, a common indicator of rooting
    private boolean checkSuBinary() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    // Checks if OTA security certificates are missing, another potential root indicator
    private boolean areOtaCertsMissing() {
        final String OTA_CERTS_PATH = "/etc/security/otacerts.zip";
        return !new File(OTA_CERTS_PATH).exists();
    }

    // Checks if the su binary is accessible through system commands
    private boolean isSuBinaryAvailable() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    // Checks system properties for known emulator identifiers
    private boolean checkBuildProperties() {
        String[] knownEmulatorBuildProperties = {
            "ro.hardware",
            "goldfish",
            "ro.hardware",
            "ranchu",
            "ro.kernel.qemu",
            "1",
            "ro.product.model",
            "sdk",
            "ro.product.model",
            "google_sdk",
            "ro.product.model",
            "sdk_x86",
            "ro.product.model",
            "vbox86p"
        };

        for (int i = 0; i < knownEmulatorBuildProperties.length; i += 2) {
            if (getSystemProperty(knownEmulatorBuildProperties[i]).equals(knownEmulatorBuildProperties[i + 1])) {
                return true;
            }
        }
        return false;
    }

    // Checks for the existence of known emulator-related files
    private boolean checkEmulatorFiles() {
        String[] knownEmulatorFiles = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        };
        for (String path : knownEmulatorFiles) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    // Checks if the QEMU driver file exists, another emulator indicator
    private boolean checkQEmuDriverFile() {
        File driverFile = new File("/proc/tty/driver");
        if (driverFile.exists() && driverFile.canRead()) {
            try {
                String driverData = new BufferedReader(new InputStreamReader(new FileInputStream(driverFile))).readLine();
                return driverData.contains("goldfish") || driverData.contains("qemu");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Checks system properties that indicate an emulator
    private boolean checkQEmuProps() {
        int minQemuProps = 6;
        String[] knownQemuProps = {
            "ro.product.device",
            "qemu",
            "ro.product.brand",
            "generic",
            "ro.product.manufacturer",
            "unknown",
            "ro.product.model",
            "sdk",
            "ro.hardware",
            "goldfish",
            "ro.hardware",
            "ranchu"
        };

        int matchCount = 0;
        for (int i = 0; i < knownQemuProps.length; i += 2) {
            if (getSystemProperty(knownQemuProps[i]).equals(knownQemuProps[i + 1])) {
                matchCount++;
            }
        }
        return matchCount >= minQemuProps;
    }

    // Retrieves a system property value
    private String getSystemProperty(String propertyName) {
        try {
            Class<?> systemPropertyClazz = Class.forName("android.os.SystemProperties");
            return (String) systemPropertyClazz.getMethod("get", String.class).invoke(systemPropertyClazz, propertyName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Retrieves application flags from the PackageManager
    private int getApplicationFlags() throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.flags;
    }
}
