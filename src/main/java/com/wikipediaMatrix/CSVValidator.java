package com.wikipediaMatrix;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.http.HttpResponse;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    @Deprecated
    public boolean checkCSV(String csvFile){
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

    public boolean checkCSVWithSeparator(String csvFile, char separator) {
        List<String[]> data = readCSV(path + csvFile, separator);
        Iterator<String[]> it = data.iterator();
        int length;

        if (it.hasNext())
            length = it.next().length;
        else
            return true;

        while (it.hasNext()) {
            if (length != it.next().length)
                return false;
        }

        return true;
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

            CSVReader csvReader = new CSVReaderBuilder(new FileReader (pathFile))
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build();

            list = csvReader.readAll();
        } catch(Exception e) { }

        assert list != null;
        return correctLinesCSV(list);
    }


    public List<String[]> correctLinesCSV(List<String[]> list) {
        List<String[]> listAux = new ArrayList<String[]>();

        for (String[] s : list) {
            if (!(s.length == 1 && s[0].trim().equals(""))) {
                listAux.add(s);
            }
        }

        return listAux;
    }

    public boolean compareCSV(String uri1, char separator1, String uri2, char separator2){
        List<String[]> list1 = readCSV(uri1, separator1);
        List<String[]> list2 = readCSV(uri2, separator2);

        return compareList(list1, list2);
    }


    public boolean compareList(List<String[]> list1, List<String[]> list2){

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
        else return false;

        return true;
    }

}
