package com.example.web_view.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private String TAG=this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();


        if(action.equals(Intent.ACTION_DEVICE_STORAGE_LOW)){
            Log.e(TAG, "低存储警告");
        }

        File root= Environment.getDataDirectory();
        StatFs sf=new StatFs(root.getPath());
        long blockSize=sf.getBlockSize();//获取单个数据块的大小
        long blockCount=sf.getBlockCount();//获取所有的数据块数
        long availCount=sf.getAvailableBlocks();//空闲的数据块的数量
        long allCapacity=(blockSize*blockCount)/1024/1024;//总数据容量
        long freeCapacity=(blockSize*availCount)/1024/1024;//可用数据容量
    }
}
