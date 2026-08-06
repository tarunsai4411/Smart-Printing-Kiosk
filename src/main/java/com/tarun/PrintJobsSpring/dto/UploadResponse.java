package com.tarun.PrintJobsSpring.dto;

public class UploadResponse {

    private String fileName;
    private String filePath;
    private int pages;

    public UploadResponse() {
    }

    public UploadResponse(String fileName, String filePath, int pages) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.pages = pages;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}
