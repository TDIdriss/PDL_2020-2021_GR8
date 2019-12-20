package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Classe permettant a l'utilisateur de faire une extraction a la carte
 * @author Groupe 5
 *
 */
public class WikiExtractMain {

	private static int nbTablesHtml;
	private static int nbColonnesHtml;
	private static int nbLignesHtml;
	private static long tempsExeHtml;
	private static int nbTablesWikitext;
	private static int nbColonnesWikitext;
	private static int nbLignesWikitext;
	private static long tempsExeWikitext;
//	private static final Logger LOG = LogManager.getLogger(WikiExtractMain.class);

	public static void main(String[] args) throws MalformedURLException, IOException, UrlInvalideException, ExtractionInvalideException, ConversionInvalideException, ArticleInexistantException, ResultatEstNullException, InterruptedException {

		Scanner entree = new Scanner(System.in);
		String choix = "";
		System.out.println("Quel type d'extraction voulez-vous realiser ? Entrez H pour HTML, W pour WIKITEXT ou bien X pour les deux en meme temps.");

		if(entree.hasNextLine()) {
			choix = entree.nextLine();
		}

		if(choix.equals("H")) {
			lancerSimpleExtraction(true);
		}
		else if (choix.equals("W")) {
			lancerSimpleExtraction(false);
		}
		else if (choix.equals("X")) {
			lancerDoubleExtraction();
		}
		else {
			System.out.println("Les seules lettres acceptees sont H, W et X !");
		}
		entree.close();
	}


	/**
	 * Methode demarrant le parsing en csv des wikitables de 336 pages wikipedia, a partir du html et du wikitext
	 *
	 * @throws UrlInvalideException si l'url est invalide
	 * @throws IOException si erreur survenue
	 * @throws ResultatEstNullException si le resultat est null
	 * @throws InterruptedException si erreur survenue
	 */
	public static void lancerDoubleExtraction() throws UrlInvalideException, IOException, ResultatEstNullException, InterruptedException {
		double urlActuelle = 1.0;
		for (Url urlValide : getUrlValides()) {
			System.out.println(urlActuelle/336*100 + "% - Extraction de la page " + urlValide.getTitre());
			Donnee_Html donnee_Html = new Donnee_Html();
			donnee_Html.setUrl(urlValide);
			donnee_Html.start();
			Donnee_Wikitable donnee_Wikitable= new Donnee_Wikitable();
			donnee_Wikitable.setUrl(urlValide);
			donnee_Wikitable.start();
			donnee_Html.join();
			donnee_Wikitable.join();
			updateComparerCSV(donnee_Html, donnee_Wikitable);
			urlActuelle++;
		}
		getStatistiques();
	}

	public static void lancerSimpleExtraction(boolean isHtml) throws MalformedURLException, IOException, UrlInvalideException, InterruptedException, ResultatEstNullException {
		Donnee_Html fakeDonneeHtml = new Donnee_Html();
		Donnee_Wikitable fakeDonneeWikitable = new Donnee_Wikitable();
		double urlActuelle = 1.0;
		if (isHtml) {
			for (Url urlValide : getUrlValides()) {
				System.out.println(urlActuelle/336*100 + "% - Extraction de la page " + urlValide.getTitre());
				Donnee_Html donnee_Html = new Donnee_Html();
				donnee_Html.setUrl(urlValide);
				donnee_Html.start();
				donnee_Html.join();
				updateComparerCSV(donnee_Html, fakeDonneeWikitable);
				urlActuelle++;
			}
			getStatistiques();
		}
		else {
			for (Url urlValide : getUrlValides()) {
				System.out.println(urlActuelle/336*100 + "% - Extraction de la page " + urlValide.getTitre());
				Donnee_Wikitable donnee_Wikitable= new Donnee_Wikitable();
				donnee_Wikitable.setUrl(urlValide);
				donnee_Wikitable.start();
				donnee_Wikitable.join();
				updateComparerCSV(fakeDonneeHtml, donnee_Wikitable);
				urlActuelle++;
			}
			getStatistiques();
		}

	}

	public static Set<Url> getUrlValides() throws MalformedURLException, IOException, UrlInvalideException{
		HashSet<Url> lesUrlValides = new HashSet<Url>();
		String BASE_WIKIPEDIA_URL = "output/url_file.txt";
		BufferedReader br = new BufferedReader(new FileReader(BASE_WIKIPEDIA_URL));
		String url;
		while ((url = br.readLine()) != null) {
			Url wikiUrl = new Url(new URL(url));
			if(wikiUrl.estUrlValide()) {
				lesUrlValides.add(wikiUrl);
			}
		}
		br.close();
		return lesUrlValides;
	}
	
	public static void updateComparerCSV(Donnee_Html donnee_Html, Donnee_Wikitable donnee_Wikitable) throws ResultatEstNullException {
		ComparerCSV comparerCsv = new ComparerCSV(donnee_Html, donnee_Wikitable);
		comparerCsv.informationsExtraction();
		nbTablesHtml += comparerCsv.getTablesHtml();
		nbColonnesHtml += comparerCsv.getColonnesHtml();
		nbLignesHtml += comparerCsv.getLignesHtml();
		tempsExeHtml += comparerCsv.getTempsExeHtml();
		nbTablesWikitext += comparerCsv.getTablesWikitable();
		nbColonnesWikitext += comparerCsv.getColonnesWikitable();
		nbLignesWikitext += comparerCsv.getLignesWikitable();
		tempsExeWikitext += comparerCsv.getTempsExeWikitable();
	}
	
	public static void getStatistiques() {
		long tempsExeTotal = (System.currentTimeMillis());
		System.out.println("Temps d'execution : " + tempsExeTotal/1000 + " secondes");
		System.out.println("-----------STATISTIQUES-----------");
		System.out.println("- HTML - Temps d'execution : " + tempsExeHtml/1000 + " secondes.");
		System.out.println("Nombre de tableaux parsés: " + nbTablesHtml + ", lignes parsées : " + nbLignesHtml + ", colonnes parsées : " + nbColonnesHtml);
		System.out.println("- WIKITEXT - Temps d'execution : " + tempsExeWikitext/1000 + " secondes.");
		System.out.println("Nombre de tableaux parsés: " + nbTablesWikitext + ", lignes parsées : " + nbLignesWikitext + ", colonnes parsées : " + nbColonnesWikitext);
	}

//	Logger example
//	=====================================================
//	 	LOG.debug("This Will Be Printed On Debug");
//        LOG.info("This Will Be Printed On Info");
//        LOG.warn("This Will Be Printed On Warn");
//        LOG.error("This Will Be Printed On Error");
//        LOG.fatal("This Will Be Printed On Fatal");
//	=====================================================
}
