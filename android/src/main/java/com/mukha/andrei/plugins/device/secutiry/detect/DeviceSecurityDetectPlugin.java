package com.mukha.andrei.plugins.device.secutiry.detect;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "DeviceSecurityDetect")
public class DeviceSecurityDetectPlugin extends Plugin {
    private DeviceSecurityDetect implementation = new DeviceSecurityDetect();

    @PluginMethod
    public void isJailBreakOrRooted(PluginCall call) {
        boolean isDeviceRooted = implementation.isDeviceRooted();

        JSObject ret = new JSObject();
        ret.put("value", isDeviceRooted);
        call.resolve(ret);
    }

    @PluginMethod
    public void isSimulator(PluginCall call) {
        boolean isSimulator = implementation.isSimulator();

        JSObject ret = new JSObject();
        ret.put("value", isSimulator);
        call.resolve(ret);
    }

    @PluginMethod
    public void isDebuggedMode(PluginCall call) {
        boolean isDebuggedMode = implementation.isDebuggedMode();

        JSObject ret = new JSObject();
        ret.put("value", isDebuggedMode);
        call.resolve(ret);
    }

    @PluginMethod
    public void pinCheck(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", implementation.pinCheck(getContext()));
        call.resolve(ret);
    }
}
