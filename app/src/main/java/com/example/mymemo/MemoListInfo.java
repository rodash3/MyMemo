package com.example.mymemo;

import java.util.ArrayList;

public class MemoListInfo {
    private String title;
    private String contents;
    private String previewImg;
    private ArrayList<String> imgs;
    private String fileName;

    public MemoListInfo(String title, String contents, String fileName) {
        this.title = title;
        this.contents = contents;
        this.fileName = fileName;
    }

    public MemoListInfo(String title, String contents, String previewImg, ArrayList<String> imgs, String fileName) {
        this.title = title;
        this.contents = contents;
        this.previewImg = previewImg;
        this.imgs = imgs;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
