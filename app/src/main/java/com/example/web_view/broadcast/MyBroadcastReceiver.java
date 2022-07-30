package com.example.web_view.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.example.web_view.MainActivity;
import com.example.web_view.fileManager.DeleteFile;
import com.example.web_view.recordMedia.saveMediaRecordFile;

import java.io.File;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        if (action.equals(Intent.ACTION_DEVICE_STORAGE_LOW)) {
            Log.e(TAG, "低存储警告");
            Toast.makeText(context, "低存储警告", Toast.LENGTH_SHORT).show();
            File root = Environment.getDataDirectory();
            StatFs sf = new StatFs(root.getPath());
            long blockSize = sf.getBlockSize();//获取单个数据块的大小
            long blockCount = sf.getBlockCount();//获取所有的数据块数
            long availCount = sf.getAvailableBlocks();//空闲的数据块的数量
            long allCapacity = (blockSize * blockCount) / 1024 / 1024;//总数据容量
            long freeCapacity = (blockSize * availCount) / 1024 / 1024;//可用数据容量
            DeleteFile deleteFile = new DeleteFile();
            deleteFile.delete(saveMediaRecordFile.downloadFile, 1);
        }

        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            Log.e(TAG, "自重启");
            Toast.makeText(context, "自重启", Toast.LENGTH_SHORT).show();
            intent = new Intent(context, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //自启动APP（Activity）
            context.startActivity(intent);
            //自启动服务（Service）
            context.startService(intent);
        }
    }
}
