package com.example.web_view;

import android.os.Environment;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/");
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getFileList() {

        //如果是文件夹的话

//        if (path.isDirectory()) {
//            //返回文件夹中有的数据
//            File[] files = path.listFiles();
//            //先判断下有没有权限，如果没有权限的话，就不执行了
//            if (null == files)
//                return;
//            for (int i = 0; i < files.length; i++) {
//                getFileList(files[i], fileList);
//            }
//        }
    }
}