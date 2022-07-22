package com.example.web_view.recordMedia;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import java.io.File;
import java.io.FileOutputStream;

public class saveMediaRecordFile {
    private Context mContext;
    private DownloadSuccessListener mDownloadSuccessListener;

    public static final File downloadFile = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);

    public saveMediaRecordFile(Context context) {
        this.mContext = context;
    }

    /**
     * 设置下载回调函数
     *
     * @param listener
     */
    public void setDownloadGifSuccessListener(DownloadSuccessListener listener) {
        mDownloadSuccessListener = listener;
    }

    /**
     * 给js调用的接口，用于保持录制视频文件
     *
     * @param base64Data base64数据
     * @param fileName   保持的文件名
     */
    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data, String fileName) {
        if (!downloadFile.exists()) {
            downloadFile.mkdir();
        }

        File filePath = new File(downloadFile + "/" + fileName);
        saveFileToPath(base64Data, filePath);

        if (mDownloadSuccessListener != null) {
            mDownloadSuccessListener.downloadSuccess(filePath.getAbsolutePath());
        }
    }


    /**
     * 保存文件
     *
     * @param base64
     * @param filePath 文件路径
     */
    private void saveFileToPath(String base64, File filePath) {
        try {
            byte[] fileBytes = Base64.decode(base64.replaceFirst(
                    "data:image/gif;base64,", ""), 0);
            FileOutputStream os = new FileOutputStream(filePath, false);
            os.write(fileBytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载回调函数
     */
    public interface DownloadSuccessListener {
        void downloadSuccess(String absolutePath);
    }



}