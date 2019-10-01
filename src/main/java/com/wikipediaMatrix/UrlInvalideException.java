package com.wikipediaMatrix;

/**
 * 
 * Classe creant une Exception dans differents cas d'erreur d'URL :
 * - URL d'une page inexistante
 * - Connexion via l'URL echouee
 * - Langue non geree
 * 
 * @author Groupe 4
 *
 */
public class UrlInvalideException extends Exception {

	String message;
	
	public UrlInvalideException(String message) {
		super();
		this.message = message;
	}
}

