package com.github.kevin.library;

import android.content.Context;

import com.github.kevin.library.listener.RequestPermission;

public class PermissionManager {

    public static void request(Context context, String[] permissions) {
        String className = context.getClass().getName() + "$Permissions";
        try {
            Class<?> clazz = Class.forName(className);
            RequestPermission requestPermission = (RequestPermission) clazz.newInstance();
            requestPermission.requestPermission(context, permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void onRequestPermissionsResult(Context context, int requestCode, int[] grantResults) {
        String className = context.getClass().getName() + "$Permissions";
        try {
            Class<?> clazz = Class.forName(className);
            RequestPermission requestPermission = (RequestPermission) clazz.newInstance();
            requestPermission.onRequestPermissionResult(context, requestCode, grantResults);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
