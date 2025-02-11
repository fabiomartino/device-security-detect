package com.mukha.andrei.plugins.device.secutiry.detect;

import android.content.Context;
import android.app.KeyguardManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DeviceSecurityDetect {
    public boolean isDeviceRooted() {
        return checkBuildTags() || checkSuBinary() || isSuBinaryAvailable() || areOtaCertsMissing();
    }

    public boolean isSimulator() {
        Log.d("DeviceSecurityDetect", "Checking if device is running on a simulator");
        return false; // Not yet implemented
    }

    public boolean isDebuggedMode() {
        Log.d("DeviceSecurityDetect", "Checking if application is running in debug mode");
        return false; // Not yet implemented
    }

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
