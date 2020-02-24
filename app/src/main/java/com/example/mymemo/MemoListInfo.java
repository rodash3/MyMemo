package com.example.mymemo;

import java.util.ArrayList;

public class MemoListInfo {
    private String title; // 메모 제목
    private String contents; // 메모 내용
    private String previewImg; // 썸네일 이미지 경로
    private ArrayList<String> imgs; // 메모에 삽입된 전체 이미지 경로
    private String fileName; // 메모 텍스트 파일 이름

    MemoListInfo(String title, String contents, String fileName) {
        this.title = title;
        this.contents = contents;
        this.fileName = fileName;
    }

    MemoListInfo(String title, String contents, String previewImg, ArrayList<String> imgs, String fileName) {
        this.title = title;
        this.contents = contents;
        this.previewImg = previewImg;
        this.imgs = imgs;
        this.fileName = fileName;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getContents() {
        return contents;
    }

    void setContents(String contents) {
        this.contents = contents;
    }

    String getPreviewImg() {
        return previewImg;
    }

    void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }

    ArrayList<String> getImgs() {
        return imgs;
    }

    void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    String getFileName() {
        return fileName;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
