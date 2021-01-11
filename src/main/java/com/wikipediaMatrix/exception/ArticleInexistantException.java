package com.wikipediaMatrix.exception;
 
/**
 * 
 * @author Groupe 8
 *
 */
public class ArticleInexistantException extends Exception { 
 
	String message; 
	 
	public ArticleInexistantException(String message) { 
		super(); 
		this.message = message; 
	} 
} 
