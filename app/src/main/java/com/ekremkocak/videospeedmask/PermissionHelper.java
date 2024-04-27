package com.ekremkocak.videospeedmask;


import android.content.Context;
import android.content.pm.PackageManager;
import java.util.Arrays;
/**
 * Helper for permissions checks.
 */
public final class PermissionHelper {
    /**
     * Checks if caller has the {@code android.Manifest.permission.DUMP} permission.
     *
     * @throws SecurityException if it doesn't.
     */
    public static void checkHasDumpPermissionGranted(Context context,
                                                     String message) {
        checkHasAtLeastOnePermissionGranted(context, message, android.Manifest.permission.DUMP);
    }
    /**
     * Checks if caller has at least one of the give permissions.
     *
     * @throws SecurityException if it doesn't.
     */
    public static void checkHasAtLeastOnePermissionGranted(Context context,
                                                           String message, String...permissions) {
        if (!hasAtLeastOnePermissionGranted(context, permissions)) {
            if (permissions.length == 1) {
                throw new SecurityException("You need " + permissions[0] + " to: " + message);
            }
            throw new SecurityException("You need one of " + Arrays.toString(permissions)
                    + " to: " + message);
        }
    }
    /**
     * Returns whether the given {@code uids} has at least one of the give permissions.
     */
    public static boolean hasAtLeastOnePermissionGranted(Context context,
                                                         String... permissions) {
        for (String permission : permissions) {
            if (context.checkCallingOrSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }
    private PermissionHelper() {
        throw new UnsupportedOperationException("provides only static methods");
    }
}
