package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ArticleInexistantException;
import com.wikipediaMatrix.exception.UrlInvalideException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe permettant de recuperer les informations d'une page via son url
 * Validation de l'url requise
 * @author Groupe 8
 *
 */
public class Url {

	private URL url;
	private String titre;
	private String langue;
	private String oldid;

	public Url(URL url) {
		this.url = url;
		this.titre = "";
		this.langue = "";
		try {
			estPageWikipedia();
		} catch (UrlInvalideException e) {
			e.printStackTrace();
		}
		try {
			estTitreValide();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Verification de l'url (provient bien du site Wikipedia) et de la langue de la page
	 * Initialiation variable langue
	 * @return true si la langue est en francais ou en anglais, false sinon
	 * @throws UrlInvalideException si l'url est invalide
	 */
	public boolean estPageWikipedia() throws UrlInvalideException {
		try {

			String debutURL = url.toString().substring(0, url.toString().lastIndexOf('/')+1);;
			if (!debutURL.matches("https://(fr|en).wikipedia.org/(w|wiki)/")) {
				throw new UrlInvalideException("URL non prise en charge");
			}
			langue = url.toString().substring(8, url.toString().indexOf('.'));
		} catch (Exception e) {}
		return true;
	}

	/**
	 * Verification du titre de la page
	 * Initialiation variable titre
	 * @return true si le titre comporte au moins un caractere, false sinon
	 * @throws MalformedURLException si erreur survenue
	 */
	public boolean estTitreValide() throws MalformedURLException {
		titre = url.toString().substring(url.toString().lastIndexOf('/')+1);
		// "\p{Graph}" -> chiffre, lettre, ponctuation
		if (!titre.matches("^[\\p{Graph}å\\–]+$")) {
			System.out.println(new MalformedURLException("Titre de la page invalide"));
			return false;
		}
		Pattern patternCell = Pattern.compile("(.)*title=((.)+)&(.)*");
		Matcher matcher = patternCell.matcher(url.toString());
		if (matcher.matches()) {
			titre = matcher.group(2);
		}
		patternCell = Pattern.compile("(.)*oldid=([0-9]+)");
		matcher = patternCell.matcher(url.toString());
		if (matcher.matches()) {
			oldid = matcher.group(2);
		}
		return true;
	}

	public String getOldid() {
		return oldid;
	}

	/**
	 * Tester une connexion avec le serveur HTTP afin de savoir si l'url renvoie bien a une page existante
	 * ATTENTION methode lourde (en temps et en memoire)
	 * @return true si la connexion HTTP est reussie, false sinon
	 * @throws ArticleInexistantException si l'article n'existe pas
	 * @throws IOException si erreur survenue
	 */
	public boolean testerConnexionHTTP() throws ArticleInexistantException, IOException {
		HttpURLConnection connexion = (HttpURLConnection)url.openConnection();
		if ((connexion.getResponseCode() != HttpURLConnection.HTTP_OK)) {
			throw new ArticleInexistantException("Aucun article disponible pour cette url");
		}
		connexion.disconnect();
		return true;
	}

	/**
	 * Methode implementant verifiant l'url dans sa globalite:
	 * - url provenant de wikipedia 
	 * - page en anglais ou en francais
	 * - titre de page existant
	 * - test de la connexion a la page 
	 * @return true si url valide et connexion reussie, false sinon
	 * @throws UrlInvalideException si l'url est invalide
	 * @throws MalformedURLException si url n'est pas correcte
	 */
	public boolean estUrlValide() throws UrlInvalideException, MalformedURLException {
		return estTitreValide() && estPageWikipedia() /*&& testerConnexionHTTP()*/;
	}

	/**
	 * Recuperer l'URL 
	 * @return String url
	 */
	public URL getURL() {
		return this.url;
	}

	/**
	 * Recuperer le titre de la page url
	 * @return String titre
	 */
	public String getTitre(){
		return this.titre.toString();
	}

	/**
	 * Recuperer la langue de la page url
	 * @return String langue
	 */
	public String getLangue() {
		return this.langue;
	}
}
