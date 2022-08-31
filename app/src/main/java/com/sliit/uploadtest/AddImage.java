package com.sliit.uploadtest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddImage {

    @SerializedName("file_name")
    @Expose
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}