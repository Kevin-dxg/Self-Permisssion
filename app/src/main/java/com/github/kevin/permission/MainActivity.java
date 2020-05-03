package com.github.kevin.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.kevin.annotation.NeedsPermisssion;
import com.github.kevin.annotation.OnNeverAskAgain;
import com.github.kevin.annotation.OnPermisssionDenied;
import com.github.kevin.annotation.OnShowRationable;
import com.github.kevin.library.PermissionManager;
import com.github.kevin.library.listener.PermissionRequest;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void camera(View view) {
        PermissionManager.request(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS});
    }


    //在需要获取权限的地方注释
    @NeedsPermisssion()
    void showCamera() {
        Log.e(TAG, "showCamera()");
    }

    //提示用户为何要开启权限
    @OnShowRationable()
    void showRationableForCamera(final PermissionRequest request) {
        Log.e(TAG, "showRationableForCamera()");
        new AlertDialog.Builder(this).setMessage("提示用户为何要开启权限")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //再次执行权限请求
                        request.proceed();
                    }
                })
                .show();
    }

    //用户拒绝权限时的提示
    @OnPermisssionDenied()
    void showDeniedForCamera() {
        Log.e(TAG, "showDeniedForCamera()");
    }

    //用户选择不再询问后的提示
    @OnNeverAskAgain()
    void showNeverAskAgain() {
        Log.e(TAG, "showNeverAskForCamera()");
        new AlertDialog.Builder(this).setMessage("用户选择不再询问后的提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "OnNeverAskAgain()");
                    }
                })
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult()");
        PermissionManager.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
