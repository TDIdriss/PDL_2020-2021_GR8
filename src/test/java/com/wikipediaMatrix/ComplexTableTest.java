package com.wikipediaMatrix;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class ComplexTableTest {

    @Test
    @DisplayName("Test de la validité du format du csv généré par l'extracteur Html")
    public void htmlExtractorTest() throws MalformedURLException, InterruptedException {
        //Url urlTest = new Url(new URL("https://en.wikipedia.org/wiki/Comparison_between_Ido_and_Novial"));
        Url urlTest = new Url(new URL("https://en.wikipedia.org/wiki/List_of_AMD_graphics_processing_units"));
        assertTrue(urlTest.estTitreValide());
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setUrl(urlTest);
        donneeHtml.start();
        donneeHtml.join();

        CSVValidator csvValidator = CSVValidator.getInstance();

        assertTrue(csvValidator.checkCSVWithSeparator("HTML/" + urlTest.getTitre()+"-11.csv", ';'));
    }
}
