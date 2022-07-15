
package com.example.web_view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.net.URL;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {


    //权限
    private final static int PERMISSIONS_REQUEST_CODE = 1;
    private static final int REQ_REMOTE_HELP = 2;
    private final String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private WebView mWebView;

    private VideoCapturer videoCapturer;
    private PeerConnectionFactory peerConnectionFactory;
    private SurfaceTextureHelper surfaceTextureHelper;
    private EglBase.Context eglBaseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }




    }

    private void initWebrtc() {
        // create PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
         peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        eglBaseContext = EglBase.create().getEglBaseContext();
         surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);


    }


    private void screenShare() {
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this, "您的手机不支持这个功能", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (manager == null) {
            Toast.makeText(this, "截屏服务不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = manager.createScreenCaptureIntent();

        startActivityForResult(intent, REQ_REMOTE_HELP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_REMOTE_HELP && resultCode == RESULT_OK) {
            VideoCapturer videoCapturer = new ScreenCapturerAndroid(data, new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    super.onStop();
                }
            });

            VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
            videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(),videoSource.getCapturerObserver());
            videoCapturer.startCapture(480, 640, 30);

            SurfaceViewRenderer localView = findViewById(R.id.localView);
            localView.setMirror(true);
            localView.init(eglBaseContext, null);

            // create VideoTrack
            VideoTrack videoTrack = peerConnectionFactory.createVideoTrack("101", videoSource);
            // display in localView
            videoTrack.addSink(localView);
        }


    }

    private void init() {
        mWebView = findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                //获取log的级别
                switch (consoleMessage.messageLevel()) {
                    case ERROR://将error信息上报到服务端

                        //LogUtil.uploadH5Error(consoleMessage.message());
//                        Toast.makeText(MainActivity.this, "webView==" + consoleMessage.message() + " level=" + consoleMessage.messageLevel(), Toast.LENGTH_SHORT).show();

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
        mWebView.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//
//                if (request.getUrl().toString().contains("file:///storage/")) {
//                    try {
//                        File file = new File(Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_DOWNLOADS) + "/1.webm");
//                        return new WebResourceResponse("video/*", "UTF-8", new FileInputStream(file));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        mWebView.setWebContentsDebuggingEnabled(true);


        WebSettings webSettings = mWebView.getSettings();
        //允许在webView中执行js
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
//        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.setAllowContentAccess(true);
//        webSettings.setAllowUniversalAccessFromFileURLs(true);
        //不使用缓存，只从网络获取数据.
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        DownloadBlobFileJSInterface mDownloadBlobFileJSInterface = new DownloadBlobFileJSInterface(this);
        mDownloadBlobFileJSInterface.setDownloadGifSuccessListener(absolutePath -> Toast.makeText(MainActivity.this, "下载成功，在Download目录下" + Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_LONG).show());

//        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
//            mWebView.loadUrl(DownloadBlobFileJSInterface.getBase64StringFromBlobUrl(url));
//        });

        mWebView.loadUrl("https://webrtccommunication.ppamatrix.com:1447/rtc/index.html");


        //js中使用java对象
        mWebView.addJavascriptInterface(mDownloadBlobFileJSInterface, "Android");

//        //初始化屏幕
//        screenShare();
//        //初始化webrtc
//        initWebrtc();
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