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


/**
 * Cette classe permet de vérifier si un CSV est valide et comparer des csv
 *
 * @author Groupe 5
 */
public class CSVValidator {

    /**
     * Unique instance du CSVValidator
     */
    private static CSVValidator csvValidator = new CSVValidator();

    /**
     * Url de l'API de validation
     */
    private String apiUrl;

    /**
     * Headers de la requête de l'API de la validation
     */
    private String headers;

    /**
     * Repertoire par défaut des fichiers
     */
    private String path;


    /**
     * Constructeur
     */
    private CSVValidator() {
        this.apiUrl = "https://validation.openbridge.io/dryrun";
        this.headers = "Content-Type: multipart/form-data";
        this.path = "output/";
    }


    /**
     * Permet d'avoir l'instance du CSVValidator
     *
     * @return l'instance du CSVValidator
     */
    public static CSVValidator getInstance() {
        return csvValidator;
    }


    /**
     * Redéfinir le repertoire par défaut
     *
     * @param path nouveau répertoire par défaut
     */
    public void setPath(String path) {
        this.path = path;
    }


    /**
     * Permet de vérifier si un csv est valide
     *
     * @param csvFile nom du fichier à vérifier
     * @return true si le csv est valide, sinon false
     */
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


    /**
     * Permet de vérifier si un csv est valide
     *
     * @param csvFile nom du fichier à vérifier
     * @param separator separateur du csv
     * @return true si le csv est valide, sinon false
     */
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


    /**
     * Envoie le fichier vers l'API de validation
     *
     * @param csvPath chemin de fichier à envoyer
     * @return HashMap qui contient les headers réponse de l'API
     */
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


    /**
     * Permet de vérifier si le code de la requête est 200
     *
     * @param url lien du l'url
     * @return true si le code HTTP est 200, sinon false
     */
    private boolean curlForGetResult(String url) {

        HttpResponse response = curl("curl \"" + url + "\"");

        return response.getStatusLine().getStatusCode() == 200;
    }


    /**
     * Permet de lire un fichier CSV
     *
     * @param pathFile chemin du fichier csv
     * @param separator séparateur utilisé pour le fichier
     * @return List<String[]> une liste de tableau contenant chaque valeur du fichier
     */
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


    /**
     * Permet de formater les valeurs des tableau en trimant
     *
     * @param list liste à formater
     * @return une liste formater
     */
    public List<String[]> correctLinesCSV(List<String[]> list) {
        List<String[]> listAux = new ArrayList<String[]>();

        for (String[] s : list) {
            if (!(s.length == 1 && s[0].trim().equals(""))) {
                listAux.add(s);
            }
        }

        return listAux;
    }


    /**
     * Compare deux fichiers CSV
     *
     * @param uri1 chemin du premier fichier
     * @param separator1 separateur du premier fichier
     * @param uri2 chemin du second fichier
     * @param separator2 separateur du second fichier
     * @return true si les valeurs des fichiers sont identique, sinon false
     */
    public boolean compareCSV(String uri1, char separator1, String uri2, char separator2){
        List<String[]> list1 = readCSV(uri1, separator1);
        List<String[]> list2 = readCSV(uri2, separator2);

        return compareList(list1, list2);
    }


    /**
     * Compare les valeurs de 2 listes
     *
     * @param list1 première liste
     * @param list2 seconde liste
     * @return true si les valeurs sont identique, sinon false
     */
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
