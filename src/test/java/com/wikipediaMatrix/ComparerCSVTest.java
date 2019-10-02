package com.wikipediaMatrix;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wikipediaMatrix.exception.ResultatEstNullException;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

/**
 * Classe de tests unitaires JUnit, utilisant Mockito pour tester les methodes de la classe ComparerCSV 
 * @author Groupe 5
 *
 */
public class ComparerCSVTest {

	// On "mock" les classes qui vont être utilisées dans le test
	Donnee donneeTest = Mockito.mock(Donnee.class);
	Donnee_Html donneeHtmlTest = Mockito.mock(Donnee_Html.class);
	Donnee_Wikitable donneeWikitableTest = Mockito.mock(Donnee_Wikitable.class);
	
	/**
	 * On teste tous les getters de ComparerCSV.
	 * @throws ResultatEstNullException si un getter ne retourne pas la valeur attendue
	 */
	@Test
	public void informationExtraction() throws ResultatEstNullException {
		/* on part du principe qu'on a fait tourner les objets
		donneeHtmlTest et donneeWikitableTest et que l'on a donc recupere
		plusieurs statistiques, auxquelles on est cense pouvoir acceder depuis un objet ComparerCSV.
		 */
		Mockito.when(donneeHtmlTest.getHtml()).thenReturn("");
		Mockito.when(donneeWikitableTest.getContenu()).thenReturn("");
		Mockito.when(donneeHtmlTest.getTime()).thenReturn((long) 100);
		Mockito.when(donneeWikitableTest.getTime()).thenReturn((long) 150);
		Mockito.when(donneeHtmlTest.getLignesEcrites()).thenReturn(25);
		Mockito.when(donneeWikitableTest.getLignesEcrites()).thenReturn(20);
		Mockito.when(donneeHtmlTest.getColonnesEcrites()).thenReturn(10);
		Mockito.when(donneeWikitableTest.getColonnesEcrites()).thenReturn(8);
		Mockito.when(donneeHtmlTest.getNbTableaux()).thenReturn(8);
		Mockito.when(donneeWikitableTest.getNbTableaux()).thenReturn(7);
		ComparerCSV comparerCSV = new ComparerCSV(donneeHtmlTest, donneeWikitableTest);
		// on recupere les statistiques liees aux extractions realisees
		comparerCSV.informationsExtraction();
		assertEquals(100, comparerCSV.getTempsExeHtml());
		assertEquals(150, comparerCSV.getTempsExeWikitable());
		assertEquals(25, comparerCSV.getLignesHtml());
		assertEquals(20, comparerCSV.getLignesWikitable());
		assertEquals(10, comparerCSV.getColonnesHtml());
		assertEquals(8, comparerCSV.getColonnesWikitable());
		assertEquals(8, comparerCSV.getTablesHtml());
		assertEquals(7, comparerCSV.getTablesWikitable());
	}

	/**
	 * On doit renvoyer une exception si on tente de recuperer la valeur
	 * de l'attribut html alors qu'elle est null.
	 */
	@Test
	public void getHtmlNull() {
		final ComparerCSV comparerCSVTest = new ComparerCSV(null, null);
		assertThrows(ResultatEstNullException.class,
				new Executable() {
					public void execute() throws Throwable {
						comparerCSVTest.getHtml();
					}
				});
	}
	
	/**
	 * On doit renvoyer une exception si on tente de recuperer la valeur
	 * de l'attribut wikitable alors qu'elle est null.
	 */
	@Test
	public void getWikitableNull() {
		final ComparerCSV comparerCSVTest = new ComparerCSV(null, null);
		assertThrows(ResultatEstNullException.class,
				new Executable() {
					public void execute() throws Throwable {
						comparerCSVTest.getWikitable();
					}
				});
	}
}

