import Foundation
import Capacitor

@objc(DeviceSecurityDetectPlugin)
public class DeviceSecurityDetectPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "DeviceSecurityDetectPlugin"
    public let jsName = "DeviceSecurityDetect"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "isJailBreakOrRooted", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isSimulator", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isDebuggedMode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "pinCheck", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = DeviceSecurityDetect()

    @objc func isJailBreakOrRooted(_ call: CAPPluginCall) {
        log("Checking if device is jailbroken from plugin")
        call.resolve([
            "value": implementation.isJailBreak()
        ])
    }

    @objc func isSimulator(_ call: CAPPluginCall) {
        log("Checking if device is running on a simulator from plugin")
        call.resolve([
            "value": implementation.isSimulator()
        ])
    }

    @objc func isDebuggedMode(_ call: CAPPluginCall) {
        log("Checking if application is running in debug mode from plugin")
        call.resolve([
            "value": implementation.isDebuggedMode()
        ])
    }

    @objc func pinCheck(_ call: CAPPluginCall) {
        log("Checking PIN status from plugin")
        call.resolve([
            "value": implementation.pinCheck()
        ])
    }
}
