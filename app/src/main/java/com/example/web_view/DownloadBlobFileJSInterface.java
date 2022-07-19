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
    public void getBase64FromBlobData(String base64Data, String fileName) {
        convertToGifAndProcess(base64Data, fileName);
    }

    /**
     * 转换成file
     *
     * @param base64
     */
    private void convertToGifAndProcess(String base64, String fileName) {
        creatfile(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS));
        File gifFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);

        saveFileToPath(base64, gifFile);
        if (mDownloadGifSuccessListener != null) {
            mDownloadGifSuccessListener.downloadGifSuccess(gifFile.getAbsolutePath());
        }
    }

    private void creatfile(File dir) {
        if (!dir.exists())
            dir.mkdir();
        return;
    }

    /**
     * 保存文件
     *
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