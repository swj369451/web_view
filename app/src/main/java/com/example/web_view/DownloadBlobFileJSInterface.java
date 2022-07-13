package com.example.web_view;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadBlobFileJSInterface {
        private Context mContext;
        private DownloadGifSuccessListener mDownloadGifSuccessListener;

        public DownloadBlobFileJSInterface(Context context) {
            this.mContext = context;
        }

        public void setDownloadGifSuccessListener(DownloadGifSuccessListener listener) {
            mDownloadGifSuccessListener = listener;
        }

        @JavascriptInterface
        public void getBase64FromBlobData(String base64Data,String fileName) {
            convertToGifAndProcess(base64Data,fileName);
        }

//        @JavascriptInterface
//        public File getRecordList(){
//            File file = new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS) + "/1.webm");
//            return file;
//        }

//        /**
//         * 插入js代码，转换成base64
//         * @param blobUrl 获取到的url
//         * @return
//         */
//        public static String getBase64StringFromBlobUrl(String blobUrl) {
//            if (blobUrl.startsWith("blob")) {
//                return "javascript: var xhr = new XMLHttpRequest();" +
//                        "xhr.open('GET', '" + blobUrl + "', true);" +
//                        "xhr.responseType = 'blob';" +
//                        "xhr.onload = function(e) {" +
//                        "    if (this.status == 200) {" +
//                        "        var blobFile = this.response;" +
//                        "        var reader = new FileReader();" +
//                        "        reader.readAsDataURL(blobFile);" +
//                        "        reader.onloadend = function() {" +
//                        "            base64data = reader.result;" +
//                        "            Android.getBase64FromBlobData(base64data);" +
//                        "        }" +
//                        "    }" +
//                        "};" +
//                        "xhr.send();";
//            }
//            return "javascript: console.log('It is not a Blob URL');";
//        }

        /**
         * 转换成file
         * @param base64
         */
        private void convertToGifAndProcess(String base64,String fileName) {
            File gifFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + "/" +fileName);
            saveFileToPath(base64, gifFile);
            if (mDownloadGifSuccessListener != null) {
                mDownloadGifSuccessListener.downloadGifSuccess(gifFile.getAbsolutePath());
            }
        }


        /**
         * 保存文件
         * @param base64
         * @param gifFilePath
         */
        private void saveFileToPath(String base64, File gifFilePath) {
            try {
                byte[] fileBytes = Base64.decode(base64.replaceFirst(
                        "data:image/gif;base64,", ""), 0);
                FileOutputStream os = new FileOutputStream(gifFilePath, false);
                os.write(fileBytes);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public interface DownloadGifSuccessListener {
            void downloadGifSuccess(String absolutePath);
        }
    } 