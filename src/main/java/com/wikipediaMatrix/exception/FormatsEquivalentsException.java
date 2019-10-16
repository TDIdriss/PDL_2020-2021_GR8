package com.wikipediaMatrix.exception;

/**
 * 
 * Classe creant une Exception lors de la comparaison des 2 extractions (format HTML et Wikitext) :
 * - Nombre de lignes equivalentes
 * - Nombre de colonnes equivalentes
 * @author Groupe 4
 *
 */
public class FormatsEquivalentsException extends Exception{

	String message;
	
	public FormatsEquivalentsException(String message) {
		super();
		this.message = message;
	}
}

