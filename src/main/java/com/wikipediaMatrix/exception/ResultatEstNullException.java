package com.wikipediaMatrix.exception;

/**
 * 
 * @author Groupe 8
 *
 */
public class ResultatEstNullException extends Exception{

	String message;
	
	public ResultatEstNullException(String message) {
		super();
		this.message = message;
	}

}
