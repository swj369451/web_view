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


    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public List<String> getDeletedFilename() {
        return deletedFilename;
    }

    public void setDeletedFilename(List<String> deletedFilename) {
        this.deletedFilename = deletedFilename;
    }
}
