package com.example.web_view.fileManager;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 文件删除反馈
 */
@Data
public class DeleteFileResponse {
    //文件路径
    private String filepath;
    //已删除文件名
    private List<String> deletedFilename = new ArrayList<>();
}
