package com.wikipediaMatrix;

/**
 * 
 * @author Groupe 4
 *
 */
public class ResultatEstNullException extends Exception{

	String message;
	
	public ResultatEstNullException(String message) {
		super();
		this.message = message;
	}

}
