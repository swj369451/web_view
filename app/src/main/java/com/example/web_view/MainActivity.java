
package com.example.web_view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;


import com.example.web_view.fileManager.DeleteFile;
import com.example.web_view.recordMedia.saveMediaRecordFile;
import com.example.web_view.webrtc.MediaCommunication;
import com.example.web_view.webrtc.WebRTC;

import org.webrtc.SurfaceViewRenderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebRTC webRTC;
    private Permissions permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查权限
        permission = new Permissions(this, this, this::init);
        permission.requestPermission();

    }

    private void init() {
        mWebView = findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //获取log的级别
                switch (consoleMessage.messageLevel()) {
                    case ERROR://将error信息上报到服务端
                        Log.e(TAG, "【webView==】" + consoleMessage.message());
                        break;
                }
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }


        });
        mWebView.setWebContentsDebuggingEnabled(true);

        WebSettings webSettings = mWebView.getSettings();
        //允许在webView中执行js
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //不使用缓存，只从网络获取数据.
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //设置下载调用函数
        saveMediaRecordFile mSaveMediaRecordFile = new saveMediaRecordFile();
        mSaveMediaRecordFile.setDownloadGifSuccessListener(absolutePath ->
                Toast.makeText(MainActivity.this,
                        String.format("下载成功，【】", absolutePath),
                        Toast.LENGTH_LONG).show());
        mWebView.addJavascriptInterface(mSaveMediaRecordFile, "Android");

        MediaCommunication mediaCommunication = new MediaCommunication(this);
        mWebView.addJavascriptInterface(mediaCommunication, "AndroidWebRTC");

        mWebView.loadUrl("https://webrtccommunication.ppamatrix.com:1447/rtc/doorplate.html");


        //获取屏幕分享
        webRTC = WebRTC.get();
        webRTC.init(this);
        webRTC.getScreenSourceData();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 处理屏幕分享
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WebRTC.REQ_REMOTE_HELP && resultCode == RESULT_OK) {
            webRTC.setScreenCapturer(data);
        }
    }
}