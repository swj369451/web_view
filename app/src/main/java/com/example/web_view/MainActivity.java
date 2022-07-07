
package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.MutableContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    //权限
    private final static int PERMISSIONS_REQUEST_CODE = 1;
    private final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
    };
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
    }

    private void init() {
        mWebView = findViewById(R.id.webView);


        mWebView.addJavascriptInterface(null, "android");

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                //获取log的级别
                switch (consoleMessage.messageLevel()) {
                    case ERROR://将error信息上报到服务端
                        System.out.println("webview==" + consoleMessage.message() + " level=" + consoleMessage.messageLevel());
                        //LogUtil.uploadH5Error(consoleMessage.message());
                        break;
                }
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });


        WebSettings webSettings = mWebView.getSettings();
        //允许使用js
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        //不使用缓存，只从网络获取数据.
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl("https://webrtccommunication.ppamatrix.com:1447/rtc/index.html");

    }


    private void requestPermission() {
        if (!checkPermissionAllGranted()) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSIONS_REQUEST_CODE);
        } else {
            init();
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                //Log.e("err","权限"+permission+"没有授权");
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                // 所有的权限都授予了
                init();
            } else {
                requestPermission();
                Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                //容易判断错
                //MyDialog("提示", "某些权限未开启,请手动开启", 1) ;
            }
        }
    }
}