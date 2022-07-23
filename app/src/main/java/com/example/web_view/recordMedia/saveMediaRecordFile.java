package com.example.web_view.recordMedia;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.web_view.fileManager.DeleteFile;

import java.io.File;
import java.io.FileOutputStream;

public class saveMediaRecordFile {
    private Context mContext;
    private DownloadSuccessListener mDownloadSuccessListener;

    public static final File downloadFile = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS+"/record");
    private String TAG =this.getClass().getSimpleName();

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

        if(!checkTheStorageCapacity(500)){
            DeleteFile deleteFile = new DeleteFile();
            deleteFile.delete(saveMediaRecordFile.downloadFile, 1);
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

    /**
     * 检查存储容量有没有达到阈值
     * @param storageThresholdValue  存储阈值
     * @return
     */
    private Boolean checkTheStorageCapacity(int storageThresholdValue) {
        Log.e(TAG, "低存储警告");
        File root = Environment.getDataDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSizeLong();//获取单个数据块的大小
        long blockCount = sf.getBlockCountLong();//获取所有的数据块数
        long availCount = sf.getAvailableBlocksLong();//空闲的数据块的数量
        long allCapacity = (blockSize * blockCount) / 1024 / 1024;//总数据容量
        long freeCapacity = (blockSize * availCount) / 1024 / 1024;//可用数据容量
        if (freeCapacity > storageThresholdValue ) {
            return true;
        } else {
            return false;
        }
    }
}