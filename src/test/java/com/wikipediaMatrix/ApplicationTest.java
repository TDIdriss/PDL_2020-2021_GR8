package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ExtractionInvalideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationTest {


    URL url;
    Url ownUrl;
    Logger logger;

    public void setup() throws MalformedURLException {
        url = new URL("https://en.wikipedia.org/wiki/Comparison_of_operating_system_kernels");
        ownUrl = new Url(url);
        logger = LogManager.getLogger(ApplicationTest.class);
    }


    @Test
    @DisplayName("Test pageComporteTableau de l'extracteur Html")
    public void htmlPageComporteTableauTest() throws ExtractionInvalideException, MalformedURLException {
        setup();
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setHtml(donneeHtml.recupContenu(url));
        assertTrue(donneeHtml.pageComporteTableau());
    }

    @Test
    @DisplayName("Test pageComporteTableau de l'extracteur Wikitext")
    public void wikiTextPageComporteTableauTest() throws ExtractionInvalideException, MalformedURLException {
        setup();
        Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
        donneeWikitable.setWikitable(donneeWikitable.recupContenu(url));
        assertTrue(donneeWikitable.pageComporteTableau());
    }

    @Test
    @DisplayName("Test le nombre de tableaux extraits par l'extracteur Html")
    public void testHtmlExtraction() throws InterruptedException, MalformedURLException {
        setup();
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setUrl(ownUrl);
        donneeHtml.start();
        donneeHtml.join();
        assertEquals(3, donneeHtml.getNbTableaux());
    }


    @Test
    @DisplayName("Test le nombre de tableaux extraits par l'extracteur Wikitext")
    public void testWikiTableExtraction() throws InterruptedException, MalformedURLException {
        setup();
        Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
        donneeWikitable.setUrl(ownUrl);
        donneeWikitable.start();
        donneeWikitable.join();
        assertEquals(3, donneeWikitable.getNbTableaux());
    }

    @Test
    @DisplayName("Test si l'extracteur Html et Wikitext extraient le même nombre de tableau pour une Url")
    public void testGetNbTableaux() throws IOException, InterruptedException {
        String BASE_WIKIPEDIA_URL = "output/large_url_test.txt";
        BufferedReader br = new BufferedReader(new FileReader(BASE_WIKIPEDIA_URL));
        String url;
        while ((url = br.readLine()) != null) {
            Url wikiUrl = new Url(new URL(url));
            Donnee_Html donneeHtml = new Donnee_Html();
            Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
            donneeHtml.setUrl(wikiUrl);
            donneeHtml.start();
            donneeHtml.join();
            donneeWikitable.setUrl(wikiUrl);
            donneeWikitable.start();
            donneeWikitable.join();
            assert donneeHtml.getNbTableaux() == donneeWikitable.getNbTableaux();
        }
    }

    @Test
    @DisplayName("Test de la validité du format du csv généré par l'extracteur Wikitext")
    public void wikitextExtractorTest() throws MalformedURLException, InterruptedException {
        setup();
        Url urlTest = new Url(new URL("https://en.wikipedia.org/wiki/Comparison_between_U.S._states_and_countries_by_GDP_(PPP)"));
        assertTrue(urlTest.estTitreValide());
        Donnee_Wikitable donneeWikitable = new Donnee_Wikitable();
        donneeWikitable.setUrl(new Url(url));
        donneeWikitable.start();
        donneeWikitable.join();

        CSVValidator csvValidator = CSVValidator.getInstance();
        csvValidator.setPath("wikitext/");

        assertTrue(csvValidator.checkCSVWithSeparator(urlTest.getTitre()+"-1.csv", ';'));
    }

    @Test
    @DisplayName("Test de la validité du format du csv généré par l'extracteur Html")
    public void htmlExtractorTest() throws MalformedURLException, InterruptedException {
        Url urlTest = new Url(new URL("https://en.wikipedia.org/wiki/Comparison_between_U.S._states_and_countries_by_GDP_(PPP)"));
        assertTrue(urlTest.estTitreValide());
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setUrl(urlTest);
        donneeHtml.start();
        donneeHtml.join();

        CSVValidator csvValidator = CSVValidator.getInstance();

        assertTrue(csvValidator.checkCSVWithSeparator("HTML/" + urlTest.getTitre()+"-1.csv", ';'));
    }

    @Test
    public void checkAttrRowSpanContent() throws MalformedURLException, InterruptedException, ExtractionInvalideException {
        // Une Url qui contient un rowspan mal dont la valeur contien un << " >>  un caractère qui n'est pas un entier
        url = new URL("https://en.wikipedia.org/wiki/List_of_Intel_graphics_processing_units");
        ownUrl = new Url(url);
        logger = LogManager.getLogger(ApplicationTest.class);

        Donnee donnee = new Donnee_Html();
        Donnee_Html donneeHtml = (Donnee_Html) donnee;
        donneeHtml.setUrl(ownUrl);
        URL urlExtraction = new URL("https://"+ownUrl.getLangue()+".wikipedia.org/wiki/"+ownUrl.getTitre()+"?action=render");
        donneeHtml.setHtml(donneeHtml.recupContenu(urlExtraction));

        Document page = Jsoup.parseBodyFragment(donneeHtml.getHtml());
        Elements wikitables = page.getElementsByClass("wikitable");

        int nbTableaux = wikitables.size();
        // On parcoure l'ensemble des tableaux de la page
        for (int i = 0 ; i < nbTableaux ; i++) {
            // On recupere tout les rowspan
            Elements rowspans = wikitables.get(i).getElementsByAttribute("rowspan");

            // On vérifie que les éditeurs du tableau les ont bien écrit,
            // C'est à dire en qu'il doit est en entier et ne doit pas contenir d'autre caractères
            for (Element rowspan : rowspans) {
                Pattern pattern = Pattern.compile("[^0-9]");
                Matcher matcher = pattern.matcher(rowspan.attr("rowspan").replaceAll("[^0-9.]", "")) ;
                assertFalse(matcher.find());
            }
        }
    }

    @Test
    @DisplayName("Test avec des tableau contenant des row span ou de col span")
    public void rowSpanColSpanTableTest() throws MalformedURLException, InterruptedException {

        Url urlTest = new Url(new URL("https://en.wikipedia.org/wiki/List_of_AMD_graphics_processing_units"));
        Donnee_Html donneeHtml = new Donnee_Html();
        donneeHtml.setUrl(urlTest);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> donneeHtml.start());
        donneeHtml.join();
    }
}
