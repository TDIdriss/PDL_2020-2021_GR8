package com.wikipediaMatrix;

import java.io.FileReader;
import java.util.List;

import com.opencsv.*;

public class ManageCSV {

    private char separator = ';';

    public List<String[]> readCSV(String pathFile) {
        List<String[]> list = null;

        try{
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(this.separator)
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(new FileReader (pathFile))
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

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public static void main (String[] args) {
        ManageCSV ex = new ManageCSV();

        ex.setSeparator(';');
        List<String[]> list1 = ex.readCSV("/Users/laeba/Desktop/csv/testComp1.csv");

        ex.setSeparator(',');
        List<String[]> list2 = ex.readCSV("/Users/laeba/Desktop/csv/testComp2.csv");

        System.out.println("fichier1 == fichier2 : " + ex.compareCSV(list1, list2));

        //System.out.println("CSV Read complete");
    }
}
