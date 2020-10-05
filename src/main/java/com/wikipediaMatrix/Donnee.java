package com.wikipediaMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import com.wikipediaMatrix.exception.ArticleInexistantException;
import com.wikipediaMatrix.exception.ConversionInvalideException;
import com.wikipediaMatrix.exception.ExtractionInvalideException;
import com.wikipediaMatrix.exception.UrlInvalideException;
import org.json.JSONException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Classe abstraite pour recuperer les donnees
 * @author Groupe 8
 *
 */

public abstract class Donnee extends Thread{

	private long tempsOriginal;
	private Map<Integer, Integer> nbLignesTableaux = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> nbColonnesTableaux = new HashMap<Integer, Integer>();;
	private int nbTableaux = 0;
	public static CyclicBarrier newBarrier = new CyclicBarrier(2);

	@Override
	public void run() {
		// lancer une extraction
	}

	public Map<Integer, Integer> getNbLignesTableaux() {
		return nbLignesTableaux;
	}

	public Map<Integer, Integer> getNbColonnesTableaux() {
		return nbColonnesTableaux;
	}

	/**
	 * A partir d'une Url, determine de combien de lignes et de colonnes
	 * sera compose le csv en sortie, en prenant en compte les rowspans et colspans
	 * @param wikitables table
	 * @throws MalformedURLException si l'url est mal écrit
	 * @throws UrlInvalideException si l'url est invalide on leve une exception
	 * @throws ExtractionInvalideException si il y a un probleme d'extraction, on leve une exception
	 */
	public void nbLignesColonnes(Elements wikitables) throws MalformedURLException, UrlInvalideException, ExtractionInvalideException {
		nbTableaux = wikitables.size();
		// On parcoure l'ensemble des tableaux de la page
		for (int i = 0 ; i < nbTableaux ; i++) {
			int[] nbLignesColonnes = getNbLignesColonnes(wikitables.get(i));
			nbLignesTableaux.put(i, nbLignesColonnes[0]);
			nbColonnesTableaux.put(i, nbLignesColonnes[1]);
		}
	}

	/**
	 * Determine le nombre de lignes et de colonnes qu'une wikitable possedera une fois parsee en CSV
	 * @param wikitable table
	 * @return nb de lignes colonnes
	 */
	int[] getNbLignesColonnes(Element wikitable) {
		int[] nbLignesColonnes = new int[2];
		// Calcul du nombre de lignes
		Elements lignes = wikitable.getElementsByTag("tr");
		int nbLignes = lignes.size();
		// Calcul du nombre de colonnes
		int nbColonnesMax = 0;
		int nbColonnes = 0;
		for (int i = 0; i < lignes.size(); i++) {
			// On va chercher la ligne avec le plus grand nombre de colonnes
			int nbColonnesAjouteesColspans = getNbColonnesAjouteesColspans(lignes.get(i));
			nbColonnes = lignes.get(i).select("td, th").size() + nbColonnesAjouteesColspans;
			if(nbColonnes > nbColonnesMax) {
				nbColonnesMax = nbColonnes;
			}
		}

		Elements rowspans = wikitable.getElementsByAttribute("rowspan");

		// nombre de lignes total ajoutees par les rowspans	
		int totalRowspans = getNbLignesAjouteesRowspans(rowspans);

		//nbLignesColonnes[0] = nbLignes + totalRowspans;
		//nbLignesColonnes[1] = nbColonnesMax+1;
		//TODO Code review
		nbLignesColonnes[0] = nbLignes;
		nbLignesColonnes[1] = nbColonnesMax;

		return nbLignesColonnes;
	}

	/**
	 * Renvoie le nombre de lignes rajoutees par les rowspans pour un tableau
	 * @param rowspans données rowspans
	 * @return
	 */
    int getNbLignesAjouteesRowspans(Elements rowspans) {
		int totalRowspans = 0;
		for (Element rowspan : rowspans) {
			int valueRowspan = Integer.parseInt(rowspan.attr("rowspan").replaceAll("[^0-9.]", ""));
			totalRowspans += valueRowspan -1;
		}
		return totalRowspans;
	}

	/**
	 * Renvoie le nombre de colonnes rajoutees par les colspans dans un tableau
	 * @param ligne données ligne
	 * @return
	 */
	private int getNbColonnesAjouteesColspans(Element ligne) {
		int totalColspans = 0;
		for (Element colspan : ligne.getElementsByAttribute("colspan")) {
			String colspanValue = colspan.attr("colspan").replaceAll("[^0-9.]", "");
			int valueColspan = Integer.parseInt(colspanValue);
			totalColspans += valueColspan-1;
		}
		return totalColspans;
	}

	/**
	 * Extraction des donnees
	 * @param url url à extraire
	 * @throws UrlInvalideException si l'url est invalide on lève une exception
	 * @throws ExtractionInvalideException s'il y a une erreur lors de l'extraction on lève une exception
	 * @throws MalformedURLException s'il y a une erreur dans le mail on lève une exception
	 * @throws ConversionInvalideException s'il y a une erreur lors de la conversion on lève une exception
	 * @throws JSONException si erreur survenue avec format json
	 * @throws IOException si erreur survenue
	 * @throws ArticleInexistantException si article inexistant on lève une exception
	 */
	abstract void extraire(Url url) throws UrlInvalideException, ExtractionInvalideException, MalformedURLException, ConversionInvalideException, IOException, JSONException, ArticleInexistantException;

	/**
	 * A partir de l'url donnee, recupere le contenu de la page en json
	 * @param url
	 * @return String
	 * @throws ExtractionInvalideException
	 */
	abstract String recupContenu(URL url) throws ExtractionInvalideException;


	/**
	 * On verifie que la page demandee contient bien un article
	 * @return si l'url contient un article
	 * @param url url dans lequel on va verifier la presence d'article
	 * @throws ArticleInexistantException si absence d'article
	 * @throws ExtractionInvalideException si erreur à l'extraction on lève une exception
	 */
	public boolean contientUnArticle(URL url) throws ArticleInexistantException, ExtractionInvalideException{
		String contenu = recupContenu(url);
		if (contenu.contains("<table id=\"noarticletext\"")) {
			return true;
		}
		else {
			throw new ArticleInexistantException("Il n'y a pas d'articles pour cette page.");
		}
	}

	/**
	 * Verification de la presence de tableaux
	 * @return boolean
	 * @throws ExtractionInvalideException si erreur à l'extraction on lève une exception
	 */
	abstract boolean pageComporteTableau() throws ExtractionInvalideException;

	/**
	 * Récupération du nombre de tableau
	 * @return le nombre de tableauK
	 */
	public abstract int getNbTableaux();

	/**
	 * Demarre le chronometre en back
	 */
	public void startTimer(){
		this.tempsOriginal = System.currentTimeMillis();
	}

	/**
	 * Donne le temps du chronometre a l'instant T
	 * @return long
	 */
	public long getTime(){
		return System.currentTimeMillis() - tempsOriginal;
	}
}