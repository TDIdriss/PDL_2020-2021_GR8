package com.wikipediaMatrix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * 
 * @author Groupe 5
 *
 */
public class Donnee_HtmlTest {

	// On "mock" les classes qui vont être utilisees dans le test
	@Mock
	Donnee donneeTest;
	Donnee_Html donnee_HtmlTest;
	Url urlTest = Mockito.mock(Url.class);

	// Dit a Mockito de creer les mocks definis sous l'annotation @Mock
	@Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

	/**
	 * La methode pageCompoteTableau doit renvoyer true si la page contient au moins un tableau
	 * @throws ExtractionInvalideException
	 */
	@Test
	public void pageComporteTableau() throws ExtractionInvalideException {
		// On instancie un objet de la classe Donnee_Html
		Donnee_Html donnee_HtmlTest  = new Donnee_Html();
		donnee_HtmlTest.setHtml("<html><head></head><body><table class=\"wikitable\"></table></body></html>");
		assertTrue(donnee_HtmlTest.pageComporteTableau());
	}

	/**
	 * La methode pageComporteTableau doit renvoyer false si la page ne contient pas de tableau
	 * @throws ExtractionInvalideException
	 */
	@Test
	public void pageNeComportePasTableau() throws ExtractionInvalideException {
		final Donnee_Html donnee_HtmlTest = new Donnee_Html(); 
		donnee_HtmlTest.setHtml("");
		assertFalse(donnee_HtmlTest.pageComporteTableau());
	}
	
	/**
	 * On ne doit supprimer que les points virgules
	 */
	@Test
	public void suppressionPointsVirgules() {
		Donnee_Html donnee_HtmlTest = new Donnee_Html();
		String htmlFactice = "aZ012-*/'|[] {}();";
		donnee_HtmlTest.supprimerPointsVirgule(htmlFactice);
		assertTrue(donnee_HtmlTest.getHtml().equals("aZ012-*/'|[] {}() "));
	}
	
	/**
	 * La methode getNbTableaux doit renvoyer le bon nombre de tableaux (wikitables et tableaux normaux)
	 */
	@Test
	public void getNbTableaux() {
		Donnee_Html donnee_Html = new Donnee_Html();
		donnee_Html.setHtml("<table class=\"wikitable\"></table><table class=\"wikitable\"></table><table></table>");
		Document page = Jsoup.parseBodyFragment(donnee_Html.getHtml());
		Elements wikitables = page.getElementsByClass("wikitable");
		Elements tablesNonWiki = page.select("table:not([^])");
		for (Element element : tablesNonWiki) {
			wikitables.add(element);
		}
		assertTrue(wikitables.size() == 3);
	}
}