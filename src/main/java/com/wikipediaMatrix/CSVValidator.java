package com.wikipediaMatrix;

public class CSVValidator {

    private static CSVValidator csvValidator = new CSVValidator();

    private String apiUrl;
    private String header;
    private String path;
    private String csvFile;

    private void CSVValidator() {
        this.apiUrl = "https://validation.openbridge.io/dryrun";
        this.header = "Content-Type: multipart/form-data";
        this.path = "output/csv/";
        this.csvFile = "";
    }

    public static CSVValidator getInstance() {
        return csvValidator;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }
}
