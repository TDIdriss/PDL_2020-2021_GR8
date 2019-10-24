package com.wikipediaMatrix;

import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.List;

import static org.toilelibre.libe.curl.Curl.curl;

public class CSVValidator {

    private static CSVValidator csvValidator = new CSVValidator();

    private String apiUrl;
    private String headers;
    private String path;

    private CSVValidator() {
        this.apiUrl = "https://validation.openbridge.io/dryrun";
        this.headers = "Content-Type: multipart/form-data";
        this.path = "output/csv/";
    }

    public static CSVValidator getInstance() {
        return csvValidator;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public boolean checkCSV(String csvFile) {
        boolean result = false;
        //Premier appel curl pour vérifier le csv
        String csvPath = path + csvFile;
        HashMap<String, String> headersMap = curlForCheckCSV(csvPath);

        if (headersMap.get("HttpCode").equals("302")) {
            String locationUrl = headersMap.get("Location");

            result = curlForGetResult(locationUrl);
        }

        return result;
    }

    private HashMap<String, String> curlForCheckCSV(String csvPath) {
        HashMap<String, String> headersMap = new HashMap<String, String>();

        CUrl curl = new CUrl(this.apiUrl)
                .opt("-H", this.headers)
                .opt("-F", "file=@" + csvPath);

        curl.exec();

        headersMap.put("HttpCode", Integer.toString(curl.getHttpCode()));

        List<List<String[]>> responseHeaders = curl.getResponseHeaders();
        for (List<String[]> l : responseHeaders) {
            for (String[] st : l) {
                headersMap.put(st[0], st[1]);
            }
        }

        return headersMap;
    }

    private boolean curlForGetResult(String url) {

        HttpResponse response = curl("curl \"" + url + "\"");

        return response.getStatusLine().getStatusCode() == 200;
    }


    public static void main(String[] args) {
        CSVValidator csvValidator = CSVValidator.getInstance();

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        System.out.println("bad.csv is valid                     : " + csvValidator.checkCSV("bad.csv"));
        System.out.println("bad_quote.csv is valid               : " + csvValidator.checkCSV("bad_quote.csv"));
        System.out.println("good.csv is valid                    : " + csvValidator.checkCSV("good.csv"));
        System.out.println("mult_long_columns.csv is valid       : " + csvValidator.checkCSV("mult_long_columns.csv"));
        System.out.println("mult_long_columns_tabs.csv is valid  : " + csvValidator.checkCSV("mult_long_columns_tabs.csv"));
        System.out.println("one_long_column.csv is valid         : " + csvValidator.checkCSV("one_long_column.csv"));
        System.out.println("perfect.csv is valid                 : " + csvValidator.checkCSV("perfect.csv"));
        System.out.println("perfect_colon.csv is valid           : " + csvValidator.checkCSV("perfect_colon.csv"));
        System.out.println("perfect_pipe.csv is valid            : " + csvValidator.checkCSV("perfect_pipe.csv"));
        System.out.println("perfect_semicolon.csv is valid       : " + csvValidator.checkCSV("perfect_semicolon.csv"));
        System.out.println("perfect_tab.csv is valid             : " + csvValidator.checkCSV("perfect_tab.csv"));

        System.out.println("\n| --------------------------------------- VALIDATION DE CSV --------------------------------------- |");
    }

}
