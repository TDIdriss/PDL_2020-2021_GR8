package com.wikipediaMatrix.exception;

/**
 * 
 * Classe creant une Exception dans differents cas d'erreur lors de l'extraction des donnees de la page Wikipedia :
 * - Pas de tableau dans la page
 * 
 * @author Groupe 8
 *
 */
public class ExtractionInvalideException extends Exception{

	String message;
	
	public ExtractionInvalideException(String message) {
		super();
		this.message = message;
	}
}

