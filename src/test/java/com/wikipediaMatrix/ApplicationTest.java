package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ExtractionInvalideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ApplicationTest {


    URL url;
    Url ownUrl;
    Logger logger;

    public void setup() throws MalformedURLException {
        url = new URL("https://en.wikipedia.org/wiki/Comparison_between_Ido_and_Novial");
        ownUrl = new Url(url);
        logger = LogManager.getLogger(ApplicationTest.class);
    }


    @Test
    public void htmlPageComporteTableauTest() throws ExtractionInvalideException, MalformedURLException {
        setup();
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setHtml(donneeHtml.recupContenu(url));
        assertTrue(donneeHtml.pageComporteTableau());
    }

    @Test
    public void wikiTextPageComporteTableauTest() throws ExtractionInvalideException, MalformedURLException {
        setup();
        Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
        donneeWikitable.setWikitable(donneeWikitable.recupContenu(url));
        assertTrue(donneeWikitable.pageComporteTableau());
    }

    @Test
    public void testHtmleExtraction() throws InterruptedException, MalformedURLException {
        setup();
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setUrl(ownUrl);
        donneeHtml.start();
        donneeHtml.join();
        assertEquals(3, donneeHtml.getNbTableaux());
    }


    @Test
    public void testWikiTableExtraction() throws InterruptedException, MalformedURLException {
        setup();
        Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
        donneeWikitable.setUrl(ownUrl);
        donneeWikitable.start();
        donneeWikitable.join();
        assertEquals(3, donneeWikitable.getNbTableaux());
    }

}
