package com.wikipediaMatrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

import com.wikipediaMatrix.exception.*;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * 
 * @author Groupe 4
 *
 */
public class WikiExtractMainTest {	

	int nbTablesHtml, nbLignesHtml, nbColonnesHtml, tempsExeHtml, nbTablesWikitext, nbLignesWikitext, nbColonnesWikitext, tempsExeWikitext = 0;

	/**
	 * Methode demarrant le parsing en csv des wikitables de 336 pages wikipedia, a partir du html et du wikitext
	 *
	 * @throws UrlInvalideException si l'url est invalide
	 * @throws IOException si erreur survenue
	 * @throws ResultatEstNullException si le resultat est null
	 * @throws InterruptedException si erreur survenue
	 */
	@Test
	public void lancerExtraction() throws UrlInvalideException, IOException, ResultatEstNullException, InterruptedException {
		String BASE_WIKIPEDIA_URL = "output/url_test.txt";
		BufferedReader br = new BufferedReader(new FileReader(BASE_WIKIPEDIA_URL));
		String url;
		double urlActuelle = 1.0;
		while ((url = br.readLine()) != null) {
			Url wikiUrl = new Url(new URL(url));
			if(wikiUrl.estUrlValide()) {
				Donnee_Html testHtml = new Donnee_Html();
				Donnee_Wikitable testWikitable = new Donnee_Wikitable();
				ComparerCSV comparerCsv = new ComparerCSV(testHtml, testWikitable);
				System.out.println(urlActuelle/336*100 + "% - Extraction de la page " + wikiUrl.getTitre());
				testHtml.setUrl(wikiUrl);
				testHtml.start();
				testWikitable.setUrl(wikiUrl);
				testWikitable.start();
				testHtml.join();
				testWikitable.join();
				comparerCsv.informationsExtraction();
				nbTablesHtml += comparerCsv.getTablesHtml();
				nbColonnesHtml += comparerCsv.getColonnesHtml();
				nbLignesHtml += comparerCsv.getLignesHtml();
				tempsExeHtml += comparerCsv.getTempsExeHtml();
				nbTablesWikitext += comparerCsv.getTablesWikitable();
				nbColonnesWikitext += comparerCsv.getColonnesWikitable();
				nbLignesWikitext += comparerCsv.getLignesWikitable();
				tempsExeWikitext += comparerCsv.getTempsExeWikitable();
				urlActuelle++;
			}
		}
		long tempsExeTotal = (System.currentTimeMillis());
		br.close();
		System.out.println("Temps d'execution : " + tempsExeTotal/1000 + " secondes");
		System.out.println("-----------STATISTIQUES-----------");
		System.out.println("- HTML - Temps d'execution : " + (tempsExeTotal - tempsExeWikitext)/1000 + " secondes.");
		System.out.println("Nombre de tableaux parsés: " + nbTablesHtml + ", lignes parsées : " + nbLignesHtml + ", colonnes parsées : " + nbColonnesHtml);
		System.out.println("- WIKITEXT - Temps d'execution : " + tempsExeWikitext/1000 + " secondes.");
		System.out.println("Nombre de tableaux parsés: " + nbTablesWikitext + ", lignes parsées : " + nbLignesWikitext + ", colonnes parsées : " + nbColonnesWikitext);
	}


	@Test
	public void getUrlValidesTest() {
		try {
			HashSet<Url> lesUrlValides = new HashSet<Url>();
			String BASE_WIKIPEDIA_URL = "output/url_test.txt";
			BufferedReader br = new BufferedReader(new FileReader(BASE_WIKIPEDIA_URL));
			String url;
			while ((url = br.readLine()) != null) {
				Url wikiUrl = new Url(new URL(url));
				if(wikiUrl.estUrlValide()) {
					lesUrlValides.add(wikiUrl);
				}
			}
			br.close();

			assertTrue(true);
		}
		catch (Exception ex){
			fail();
		}
	}
}
