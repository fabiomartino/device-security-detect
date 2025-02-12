package com.mukha.andrei.plugins.device.secutiry.detect;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Capacitor plugin for detecting device security risks, such as rooting, emulators, debugging, and PIN security.
 */
@CapacitorPlugin(name = "DeviceSecurityDetect")
public class DeviceSecurityDetectPlugin extends Plugin {
    private DeviceSecurityDetect implementation;

    /**
     * Called when the plugin is loaded. Initializes the implementation with the application context.
     */
    @Override
    public void load() {
        implementation = new DeviceSecurityDetect(getContext());
    }

    /**
     * Checks if the device is rooted or jailbroken.
     * @param call The Capacitor PluginCall object to handle the response.
     */
    @PluginMethod
    public void isJailBreakOrRooted(PluginCall call) {
        boolean isDeviceRooted = implementation.isDeviceRooted();
        
        JSObject ret = new JSObject();
        ret.put("value", isDeviceRooted);
        call.resolve(ret);
    }

    /**
     * Checks if the application is running on an emulator.
     * @param call The Capacitor PluginCall object to handle the response.
     */
    @PluginMethod
    public void isSimulator(PluginCall call) {
        boolean isSimulator = implementation.isSimulator();
        
        JSObject ret = new JSObject();
        ret.put("value", isSimulator);
        call.resolve(ret);
    }

    /**
     * Checks if the application is running in debug mode.
     * @param call The Capacitor PluginCall object to handle the response.
     */
    @PluginMethod
    public void isDebuggedMode(PluginCall call) {
        boolean isDebuggedMode = implementation.isDebuggedMode();
        
        JSObject ret = new JSObject();
        ret.put("value", isDebuggedMode);
        call.resolve(ret);
    }

    /**
     * Checks if the device has a secure lock screen (PIN, password, or biometric authentication enabled).
     * @param call The Capacitor PluginCall object to handle the response.
     */
    @PluginMethod
    public void pinCheck(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", implementation.pinCheck(getContext()));
        call.resolve(ret);
    }
}
