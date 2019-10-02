package com.wikipediaMatrix.exception;

/**
 * 
 * Classe creant une Exception dans differents cas d'erreur lors de la convertion des donnees vers le fichier CSV:
 * @author Groupe 4
 *
 */
public class ConversionInvalideException extends Exception{

	String message;
	
	public ConversionInvalideException(String message) {
		super();
		this.message = message;
	}
}

