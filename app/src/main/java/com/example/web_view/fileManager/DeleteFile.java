package com.example.web_view.fileManager;

import android.os.Environment;

import java.io.File;
import java.util.Arrays;

/**
 * 删除指定文件
 */
public class DeleteFile {

    public DeleteFileResponse delete(File file, int number) {

        //获取文件进行排序
        File[] files = file.listFiles();
        Arrays.sort(files, (f1, f2) -> {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;// 如果 if 中修改为返回-1 同时此处修改为返回 1 排序就会是递减
        });

        //删除文件，构建反馈对象
        DeleteFileResponse deleteFileResponse = new DeleteFileResponse();
        deleteFileResponse.setFilepath(file.getPath());
        for (int i = 0; i < number; i++) {
            deleteFileResponse.getDeletedFilename().add(files[i].getName());
            files[i].delete();
        }
        return deleteFileResponse;
    }
}
