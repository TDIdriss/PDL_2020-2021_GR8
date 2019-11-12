package com.wikipediaMatrix;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.http.HttpResponse;

import java.io.FileReader;
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
        this.path = "output/";
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

    public List<String[]> readCSV(String pathFile, char separator) {
        List<String[]> list = null;

        try{
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(separator)
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(new FileReader(pathFile))
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build();

            list = csvReader.readAll();
        } catch(Exception e) { }

        return list;
    }


    public boolean compareCSV(List<String[]> list1, List<String[]> list2){

        //test du nombre de ligne du fichier
        if (list1.size()==list2.size()){


            for(int i=0; i < list1.size() ; i++) {
                String[] subtab1 = list1.get(i);
                String[] subtab2 = list2.get(i);

                //test du nombre de valeur par ligne
                if (subtab1.length == subtab2.length) {

                    for (int j = 0; j < subtab1.length; j++) {
                        String item1 = subtab1[j].trim();
                        String item2 = subtab2[j].trim();

                        if (!item1.equals(item2)){
                            return false;
                        }

                    }

                }
                else {
                    return false;
                }
            }

        }

        return true;
    }



    public static void main(String[] args) {
        CSVValidator csvValidator = CSVValidator.getInstance();

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        System.out.println("double_quote.csv is valid                     : " + csvValidator.checkCSV("csv/double_quote.csv"));

        System.out.println("\n| --------------------------------------- VALIDATION DE CSV --------------------------------------- |");
    }

}
