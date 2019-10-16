package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ArticleInexistantException;
import com.wikipediaMatrix.exception.UrlInvalideException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Classe permettant de recuperer les informations d'une page via son url
 * Validation de l'url requise
 * @author Groupe 4
 *
 */
public class Url {

	private URL url;
	private String titre;
	private String langue;

	public Url(URL url) {
		this.url = url;
		this.titre = "";
		this.langue = "";
	}

	/**
	 * Verification de l'url (provient bien du site Wikipedia) et de la langue de la page
	 * Initialiation variable langue
	 * @return true si la langue est en francais ou en anglais, false sinon
	 * @throws UrlInvalideException
	 */
	public boolean estPageWikipedia() throws UrlInvalideException {
		String debutURL = url.toString().substring(0, url.toString().lastIndexOf('/')+1);;
		if (!debutURL.matches("https://(fr|en).wikipedia.org/wiki/")) {
			throw new UrlInvalideException("URL non prise en charge");
		}  
		langue = url.toString().substring(8, url.toString().indexOf('.'));
		return true;
	}

	/**
	 * Verification du titre de la page
	 * Initialiation variable titre
	 * @return true si le titre comporte au moins un caractere, false sinon
	 * @throws MalformedURLException 
	 */
	public boolean estTitreValide() throws MalformedURLException {
		titre = url.toString().substring(url.toString().lastIndexOf('/')+1);
		// "\p{Graph}" -> chiffre, lettre, ponctuation
		if (!titre.matches("^[\\p{Graph}å\\–]+$")) {
			System.out.println(new MalformedURLException("Titre de la page invalide"));
			return false;
		}
		return true;
	}

	/**
	 * Tester une connexion avec le serveur HTTP afin de savoir si l'url renvoie bien a une page existante
	 * ATTENTION methode lourde (en temps et en memoire)
	 * @return true si la connexion HTTP est reussie, false sinon
	 * @throws UrlInvalideException 
	 * @throws ArticleInexistantException
	 * @throws IOException 
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
	 * @throws UrlInvalideException
	 * @throws MalformedURLException 
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
