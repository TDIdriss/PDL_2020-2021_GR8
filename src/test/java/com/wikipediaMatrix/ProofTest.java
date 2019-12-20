package com.wikipediaMatrix;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * @author Group 5
 *
 * Classe de tests qui vérifie si l'url est valide et corresponds au CSV récupéré
 *
 */
public class ProofTest {

    /**
     * Test de la validité du format du csv généré par l'extracteur Html
     *
     * @throws InterruptedException Lève une exception lors d'une erreur de lecture
     */
    @Test
    @DisplayName("Test de la validité du format du csv généré par l'extracteur Html")
    public void proofHTMLExtractorTest() throws InterruptedException {

        String BASE_WIKIPEDIA_URL = "output/proof_html.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(BASE_WIKIPEDIA_URL));
            String url;
            boolean bool;

            while ((url = br.readLine()) != null) {
                bool = testHTMLUrl(url);
                System.out.println("Vérification de : " + url + " : " + bool);

                assertTrue(bool);
            }


        } catch (FileNotFoundException e) {
            System.out.println("Erreur lors de la lecture du fichier");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la ligne");
        }

    }

    /**
     * <p>
     * Permet de vérifier avec l'url fournit si tous les csv associés sont valides
     * et les compare à la vérité terrain
     * </p>
     * @param url url du fichier
     * @return un boolean true ou false selon la correspondance
     * @throws MalformedURLException si l'url n'est pas correcte
     * @throws InterruptedException s'il y a une erreur à l'extraction
     */
    private boolean testHTMLUrl(String url) throws MalformedURLException, InterruptedException {
        CSVValidator csvValidator = CSVValidator.getInstance();
        int numberOfCSV;
        String uri1, uri2;
        char separator = ';';

        Url wikiUrl = new Url(new URL(url));
        assertTrue(wikiUrl.estTitreValide());

        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setUrl(wikiUrl);
        donneeHtml.start();
        donneeHtml.join();
        numberOfCSV = donneeHtml.getNbTableaux();

        for (int i = 1; i <= numberOfCSV; i++){
            if (!csvValidator.checkCSVWithSeparator("HTML/" + wikiUrl.getTitre() + "-" + i + ".csv", ';'))
                return false;

            uri1 = "output/proof/HTML/" + wikiUrl.getTitre() + "-" + i + ".csv";
            uri2 = "output/HTML/" + wikiUrl.getTitre() + "-" + i + ".csv";

            if (!csvValidator.compareCSV(uri1, separator, uri2, separator))
                return false;
        }

        return true;
    }

}
