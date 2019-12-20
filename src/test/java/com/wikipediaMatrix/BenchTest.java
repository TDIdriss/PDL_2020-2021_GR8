package com.wikipediaMatrix;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BenchTest {

    /*
     *  the challenge is to extract as many relevant tables as possible
     *  and save them into CSV files
     *  from the 300+ Wikipedia URLs given
     *  see below for more details
     **/
    @Test
    public void testBenchExtractors() throws Exception {

        // directory where CSV files are exported (HTML extractor)
        String outputDirHtml = "output" + File.separator + "HTML" + File.separator;
        assertTrue(new File(outputDirHtml).isDirectory());
        // directory where CSV files are exported (Wikitext extractor)
        String outputDirWikitext = "output" + File.separator + "wikitext" + File.separator;
        assertTrue(new File(outputDirWikitext).isDirectory());

        File file = new File("output" + File.separator + "large_url_test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String url;
        int nurl = 0;
        int numberOfCSV;

        Donnee_Html donneeHtml;
        Donnee_Wikitable donneeWikitable;

        while ((url = br.readLine()) != null) {
            System.out.println("Wikipedia url: " + url);

            Url wikiUrl = new Url(new URL(url));
            assertTrue(wikiUrl.estTitreValide());

            System.out.println("\nCSV page name: " + wikiUrl.getTitre());

            donneeHtml = new Donnee_Html();
            donneeHtml.setUrl(wikiUrl);
            donneeHtml.start();
            donneeHtml.join();
            numberOfCSV = donneeHtml.getNbTableaux();
            for (int i = 1; i <= numberOfCSV; i++){
                System.out.println("CSV generate : " + mkCSVFileName(outputDirHtml + wikiUrl.getTitre(), i));
            }


            donneeWikitable = new Donnee_Wikitable();
            donneeWikitable.setUrl(wikiUrl);
            donneeWikitable.start();
            donneeWikitable.join();
            numberOfCSV = donneeHtml.getNbTableaux();
            for (int i = 1; i <= numberOfCSV; i++){
                System.out.println("CSV generate : " + mkCSVFileName(outputDirWikitext + wikiUrl.getTitre(), i));
            }

            nurl++;
        }

        br.close();
        assertEquals(nurl, 336);

    }

    private String mkCSVFileName(String url, int n) {
        return url.trim() + "-" + n + ".csv";
    }

}