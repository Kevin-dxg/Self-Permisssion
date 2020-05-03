//package com.github.kevin.permission;
//
//import com.github.kevin.library.listener.PermissionRequest;
//import com.github.kevin.library.listener.RequestPermission;
//import com.github.kevin.library.utils.PermissionUtils;
//import android.support.v4.app.ActivityCompat;
//import android.support.annotation.NonNull;
//import java.lang.ref.WeakReference;
//
//public class MainActivity$Permissions implements RequestPermission<MainActivity> {
//    private static final int REQUEST_SHOWCAMERA = 666;
//    private static String[] PERMISSION_SHOWCAMERA;
//
//    @Override
//    public void requestPermission(MainActivity target, String[] permissions) {
//        PERMISSION_SHOWCAMERA = permissions;
//        if (PermissionUtils.hasSelfPermission(target, PERMISSION_SHOWCAMERA)) {
//            target.showCamera();
//        } else if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {
//            target.showRationableForCamera(new MainActivity$Permissions.PermissionRequestImpl(target));
//        } else {
//            ActivityCompat.requestPermissions(target, PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionResult(MainActivity target, int requestCode, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_SHOWCAMERA:
//                if (PermissionUtils.verifyPermission(grantResults)) {
//                    target.showCamera();
//                } else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {
//                    target.showNeverAskAgain();
//                } else {
//                    target.showDeniedForCamera();
//                }
//            default:
//                break;
//        }
//    }
//
//    private static final class PermissionRequestImpl implements PermissionRequest {
//        private final WeakReference<MainActivity> weakTarget;
//
//        private PermissionRequestImpl(MainActivity target) {
//            this.weakTarget = new WeakReference<>(target);
//        }
//
//        @Override
//        public void proceed() {
//            MainActivity target = this.weakTarget.get();
//            if (target != null) {
//                ActivityCompat.requestPermissions(target, MainActivity$Permissions.PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);
//            }
//        }
//    }
//
//
//}
