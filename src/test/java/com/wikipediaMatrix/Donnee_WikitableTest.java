package com.wikipediaMatrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

/**
 * 
 * @author Groupe 5
 *
 */
public class Donnee_WikitableTest {

	/**
	 * La methode pageComporteTableau doit renvoyer true si la page contient au moins un tableau
	 * @throws ExtractionInvalideException
	 */
	@Test
	public void pageComporteTableau() throws ExtractionInvalideException {
		// On instancie un objet de la classe Donnee_Wikitable
		Donnee_Wikitable donnee_WikitextTest  = new Donnee_Wikitable();
		donnee_WikitextTest.setWikitable("{| class=\"wikitable\" |}");
		assertTrue(donnee_WikitextTest.pageComporteTableau());
	}
	
	/**
	 * La methode pageComporteTableau doit renvoyer false si la page ne contient pas de tableau
	 * @throws ExtractionInvalideException
	 */
	@Test
	public void pageNeComportePasTableau() throws ExtractionInvalideException {
		Donnee_Wikitable donnee_WikitextTest = new Donnee_Wikitable(); 
		donnee_WikitextTest.setWikitable("");
		assertFalse(donnee_WikitextTest.pageComporteTableau());
	}
	
	/**
	 * La methode supprDonneesInutiles doit supprimer toutes les donnees qui ne sont pas utiles pour la suite
	 * @throws ExtractionInvalideException
	 */
	@Test
	public void supprDonneesInutiles() throws ExtractionInvalideException {
		Donnee_Wikitable donnee_WikitextTest = new Donnee_Wikitable();
		String test = donnee_WikitextTest.wikitableReplace("scope=col;style=\"text-align:center\"align=\"center\"|&nbsp;<br /></center>|-/");
		assertEquals(test, ", | -/");
	}
	
	/**
	 * La methode supprEspacesInutiles doit supprimer tous les espaces qui ne sont pas utiles pour la suite
	 * @throws ExtractionInvalideException
	 */
	@Test
	public void supprEspacesInutiles() throws ExtractionInvalideException {
		Donnee_Wikitable donnee_WikitextTest = new Donnee_Wikitable();
		String test = donnee_WikitextTest.supprimerEspaceDebut("   wikitexte");
		assertEquals(test, "wikitexte");
	}
}
