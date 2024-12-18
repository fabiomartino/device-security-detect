package com.mukha.andrei.plugins.device.secutiry.detect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DeviceSecurityDetect {
    public boolean isDeviceRooted() {
        return checkBuildTags() || checkSuBinary() || isSuBinaryAvailable() || areOtaCertsMissing();
    }

    private boolean checkBuildTags() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkSuBinary() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su", "/system/bin/su",
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

    private boolean areOtaCertsMissing() {
        final String OTA_CERTS_PATH = "/etc/security/otacerts.zip";
        return !new File(OTA_CERTS_PATH).exists();
    }

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
}
