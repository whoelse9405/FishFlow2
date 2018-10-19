package com.example.emsys.fishflow;

public class ReportData {
    int cropId;
    String reportClass;
    String title;
    String contents;

    public ReportData(int cropId, String reportClass, String title, String contents) {
        this.cropId = cropId;
        this.reportClass = reportClass;
        this.title = title;
        this.contents = contents;
    }

    public ReportData(String reportClass, String title, String contents) {
        this.cropId = 0;    //크롭 정보가 필요하지 않을 때
        this.reportClass = reportClass;
        this.title = title;
        this.contents = contents;
    }

    public int getCropId() {
        return cropId;
    }

    public String getReportClass() {
        return reportClass;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }
}
