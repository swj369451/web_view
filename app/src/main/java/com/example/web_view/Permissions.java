package com.example.web_view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    private final static int PERMISSIONS_REQUEST_CODE = 1;
    //申请的权限
    private final String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final Activity activity;
    private final SuccessListener successListener;
    private Context context;

    public Permissions(Activity activity,Context context,SuccessListener successListener) {
        this.activity=activity;
        this.context=context;
        this.successListener =successListener;
    }

    public void requestPermission() {
        if (!checkPermissionAllGranted()) {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_CODE);
        }else {
            if(successListener!=null){
                successListener.success();
            }
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                //Log.e("err","权限"+permission+"没有授权");
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
//                init();
                if(successListener!=null){
                    successListener.success();
                }
            } else {
                requestPermission();
            }
        }
    }

    /**
     * 下载回调函数
     */
    public interface SuccessListener {
        void success();
    }
}
