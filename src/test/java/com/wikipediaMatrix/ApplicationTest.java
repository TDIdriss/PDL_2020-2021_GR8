package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ExtractionInvalideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ApplicationTest {


    URL url;
    Url ownUrl;

    public void setup() throws MalformedURLException {
        url = new URL("https://en.wikipedia.org/wiki/Comparison_between_Ido_and_Novial");
        ownUrl = new Url(url);
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

    @Test(expected = ExtractionInvalideException.class)
    public void getNbTableauxTest() throws IOException, InterruptedException {
        setup();
        Donnee_Html donneeHtml = new Donnee_Html();
        Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
        donneeHtml.setUrl(ownUrl);
        donneeWikitable.setUrl(ownUrl);
        donneeHtml.start();
        donneeHtml.join();
        donneeWikitable.start();
        donneeWikitable.join();

        Logger logger = LogManager.getLogger(ApplicationTest.class);

        logger.info(donneeHtml.getNbTableaux());
        logger.info(donneeWikitable.getNbTableaux());

        assertEquals(donneeHtml.getNbTableaux(), donneeWikitable.getNbTableaux());

    }


}
