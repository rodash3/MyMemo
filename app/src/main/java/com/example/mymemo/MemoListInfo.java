package com.example.mymemo;

public class MemoListInfo {
    private String title;
    private String contents;
    private String previewImg;
    private String[] imgs;
    private String fileName;

    public MemoListInfo(String title, String contents, String fileName) {
        this.title = title;
        this.contents = contents;
        this.fileName = fileName;
    }

    public MemoListInfo(String title, String contents, String previewImg, String[] imgs, String fileName) {
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

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
