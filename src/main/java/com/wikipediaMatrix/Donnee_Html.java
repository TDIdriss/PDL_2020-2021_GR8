package com.wikipediaMatrix;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.wikipediaMatrix.exception.ArticleInexistantException;
import com.wikipediaMatrix.exception.ConversionInvalideException;
import com.wikipediaMatrix.exception.ExtractionInvalideException;
import com.wikipediaMatrix.exception.UrlInvalideException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Classe permettant de recuperer et convertir les tableaux d'une page wikipedia en fichiers CSV
 * @author Groupe 4
 *
 */

public class Donnee_Html extends Donnee {
	/**
	 * Le HTML de la page wikipedia
	 */
	private String donneeHTML;
	private int nbTableauxExtraits, ligneActuelle, colonneActuelle, lignesEcrites, colonnesEcrites, maxColonnesLigne;
	private String[][] tableau;
	private Url url;
	private List<int[]> rowspanFound;
	private int nbLignesGlob;
	private int nbColonnesGlob;

	public Donnee_Html() {
		this.donneeHTML = "";
		this.rowspanFound =  new ArrayList<int[]>();
	}

	public String[][] getTableau(){
		return this.tableau;
	}

	public void setTableau(String[][] tableau) {
		this.tableau = tableau;
	}

	public void setColonneActuelle(int numColonne) {
		this.colonneActuelle = numColonne;
	}

	public void setLigneActuelle(int numLigne) {
		this.ligneActuelle = numLigne;
	}

	public String getHtml() {
		return this.donneeHTML;
	}

	public void setHtml(String html) {
		this.donneeHTML = html;
	}

	public void setUrl(Url url) {
		this.url = url;
	}
	
	/**
	 * Lance l'execution d'un thread pour l'extraction des tableaux de la page wikipedia
	 */
	@Override
	public void run() {
		try {
			extraire(this.url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Recupere le contenu de la page
	 * @param url
	 * @throws UrlInvalideException
	 * @throws ExtractionInvalideException
	 * @throws ConversionInvalideException
	 * @throws ArticleInexistantException
	 * @throws IOException 
	 */
	@Override
	public synchronized void extraire(Url url) throws UrlInvalideException, ExtractionInvalideException, IOException {
		startTimer();
		boolean hasPage = true;
		url.estTitreValide();
		url.estPageWikipedia();
		String langue = url.getLangue();
		String titre = url.getTitre();
		/* On recupere le nombre calcule de lignes et de colonnes de tous
		les tableaux de l'url*/
		try {
			URL urlExtraction = new URL("https://"+langue+".wikipedia.org/wiki/"+titre+"?action=render");
			this.setHtml(this.recupContenu(urlExtraction));
		} catch (ExtractionInvalideException erreurExtraction) {
			System.out.println("ERREUR : " + erreurExtraction.toString());
			hasPage = false;
		}
		supprimerPointsVirgule(this.donneeHTML);
		if(pageComporteTableau() && hasPage){
			String titreSain = titre.replaceAll("[\\/\\?\\:\\<\\>]", "");
			htmlVersCSV(titreSain);
		}
	}

	/**
	 * Methode qui parcoure les tables du HTML et les convertit en CSV
	 * On cree un fichier csv par tableau trouvé
	 * @throws ConversionInvalideException
	 * @throws IOException 
	 * @throws ExtractionInvalideException 
	 * @throws UrlInvalideException 
	 */
	public void htmlVersCSV(String titre) throws IOException, UrlInvalideException, ExtractionInvalideException {

		Document page = Jsoup.parseBodyFragment(this.donneeHTML);
		Elements wikitables = page.getElementsByClass("wikitable");
		Elements tablesNonWiki = page.select("table:not([^])");
		for (Element element : tablesNonWiki) {
			wikitables.add(element);
		}
		int nbTableaux = wikitables.size();
		this.nbTableauxExtraits += nbTableaux;
		nbLignesColonnes(wikitables);
		
		for (int i = 0 ; i < wikitables.size() ; i++) {
			String outputPath = "output/HTML/" + titre + "-" + i+1 + ".csv";
			FileOutputStream outputStream = new FileOutputStream(outputPath);
			OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
			// On recupere le nmobre de lignes et de colonnes du tableau en cours
			nbLignesGlob = getNbLignesTableaux().get(i);
			nbColonnesGlob = getNbColonnesTableaux().get(i);
			// On initialise la matrice de donnees a la bonne taille
			this.tableau = new String[nbLignesGlob][nbColonnesGlob];
//			this.tableau = new String[700][200];
			this.ligneActuelle = 0;
			System.out.println(1);
			// On remplit toutes les lignes et colonnes de la matrice
			for (String[] ligne: tableau) {
				java.util.Arrays.fill(ligne,"VIDE");
			}
			// On stocke les donnees en provenance de la wikitable dans une matrice
			stockerLignes(wikitables.get(i));
			// On cree un fichier CSV en parcourant la matrice
			ecrireTableau(writer);
			writer.close();
		}	
	}

	/**
	 * Methode qui parcoure les lignes puis les cellules d'une wikitable
	 * et qui appelle les methodes permettant de gerer les colspans et rowspans
	 * @param table
	 */
	private void stockerLignes(Element table) {
		this.maxColonnesLigne = 0;
		int count;
		Elements lignes = table.getElementsByTag("tr");
		// On parcoure les lignes de la wikitable1800
		for (Element ligne : lignes) {
			this.colonneActuelle = 0;
			Elements cellules = ligne.select("td, th");
			/* Parcours des cellules de la ligne, et appel de methodes
			gerant les colspans et rowspans si besoin */
			count = 1;
			for (Element cellule : cellules) {
				// Si on un colspan et un rowspan sur la meme cellule
				if ((cellule.hasAttr("colspan")) && (cellule.hasAttr("rowspan"))){
					String colspanValue = cellule.attr("colspan").replaceAll("[^0-9.]", "");
					String rowspanValue = cellule.attr("rowspan").replaceAll("[^0-9.]", "");
					int nbColspans = Integer.parseInt(colspanValue);
					int nbRowspans = Integer.parseInt(rowspanValue);
					gererColspansEtRowspans(nbRowspans, nbColspans, cellule);
				}
				// Si on a un rowspan uniquement
				else if (cellule.hasAttr("rowspan")) {
					String rowspanValue = cellule.attr("rowspan").replaceAll("[^0-9.]", "");
					int nbRowspans = Integer.parseInt(rowspanValue);

					int[] tab = {this.ligneActuelle, this.colonneActuelle, nbRowspans};
					this.rowspanFound.add(tab);
					correctRow();
					gererRowspans(nbRowspans, cellule, this.ligneActuelle);
				}
				// Si on a un colspan uniquement
				else if (cellule.hasAttr("colspan")) {
					String colspanValue = cellule.attr("colspan").replaceAll("[^0-9.]", "");
					int nbColspans = Integer.parseInt(colspanValue);
					gererColspans(nbColspans, cellule, this.colonneActuelle);
				}
				// La cellule est 'normale'
				else {
					stockerCellule(cellule);
				}
				this.colonneActuelle++;
				count++;
				if(this.colonneActuelle > this.maxColonnesLigne) this.maxColonnesLigne = this.colonneActuelle;
			}
			this.ligneActuelle++;
			this.lignesEcrites++;
		}
		this.colonnesEcrites += this.maxColonnesLigne;
		addSeparator();
	}

	private void correctRow() {

		for (int[] item : this.rowspanFound) {
			if (item[0] == this.ligneActuelle && item[1] == this.colonneActuelle) {
				return;
			}

			for (int i = 1; i < item[2]; i++){
				if (this.ligneActuelle == item[0] + i && this.colonneActuelle == item[1] && item[1] + 1 <= this.nbColonnesGlob - 1) {
					this.colonneActuelle++;
					return;
				}
			}
		}

	}

	private void addSeparator() {
		for (int i = 0; i < this.nbLignesGlob; i++) {
			for (int j = 0; j< this.nbColonnesGlob; j++){
				if ( j != nbColonnesGlob-1 ) {
					this.tableau[i][j] = this.tableau[i][j].concat("; ");
				}
			}

		}
	}

	/**
	 * Stocke la cellule dans une matrice a deux dimensions representant la wikitable
	 * @param cellule la cellule a ajouter
	 */
	public void stockerCellule(Element cellule) {
		/* Si les coordonnees donnees en parametre sont deja reservees, on avance
		 	d'autant de colonnes qu'il faudra jusqu'a pouvoir stocker notre cellule*/
		while(!this.tableau[this.ligneActuelle][this.colonneActuelle].equals("VIDE")) {
			this.colonneActuelle++;
		}
		// On ajoute le texte de la cellule extraite a la matrice
		if (this.tableau[this.ligneActuelle][this.colonneActuelle].equals("VIDE")) {
			this.tableau[this.ligneActuelle][this.colonneActuelle] = cellule.text();
		}
	}

	@Override
	String recupContenu(URL url) throws ExtractionInvalideException {
		try {
			StringBuilder result = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null)
				result.append(inputLine);

			in.close();
			return result.toString();
		} catch (Exception e) {
			throw new ExtractionInvalideException("Recuperation du contenu impossible");
		}
	}

	/**
	 * Stocke la cellule dans une matrice a deux dimensions representant la wikitable
	 * La ligne n'est pas l'attribut ligneActuelle, mais une ligne donnee en parametre
	 * Methode utilise uniquement lorsqu'une cellule a un attribut rowspan.
	 * @param cellule la cellule a ajouter
	 */
	//TODO regler
	private void stockerCelluleColspan(Element cellule, int colonneActuelle) {
		if (!this.tableau[this.ligneActuelle][colonneActuelle].equals("VIDE")) {
		}
		else {
			this.tableau[this.ligneActuelle][colonneActuelle] = cellule.text();
		}
	}

	//TODO regler
	private void stockerCelluleRowspan(Element cellule, int ligneActuelle) {
		if (!this.tableau[ligneActuelle][this.colonneActuelle].equals("VIDE")) {
		}
		else {
			this.tableau[ligneActuelle][this.colonneActuelle] = cellule.text();
		}
	}

	private void stockerRowspanSuivantColspan(Element cellule, int ligneActuelle, int colonneActuelle) {
		this.tableau[ligneActuelle][colonneActuelle] = cellule.text();
	}

	/**
	 * Cree un fichier csv a partir de la matrice a double dimension tableau.
	 * @param writer
	 * @throws IOException
	 */
	private void ecrireTableau(OutputStreamWriter writer) throws IOException {
		for (int i = 0; i < this.tableau.length; i++) {
			boolean contientInfos = ligneContientInfos(i);
			for (int j = 0; j < this.tableau[i].length; j++) {
				if (!this.tableau[i][j].equals("VIDE")) {
					writer.write(this.tableau[i][j]);
				}
			}
			// Si la ligne n'est pas vide, alors on fait un saut de ligne
			if (contientInfos) {
				writer.write("\n");				
			}
		}
	}

	/**
	 * Verification de la presence de tableaux dans les donnees
	 * @return boolean
	 * @throws ExtractionInvalideException 
	 */
	@Override
	public boolean pageComporteTableau() throws ExtractionInvalideException {
		Document page = Jsoup.parseBodyFragment(this.donneeHTML);
		if((!page.getElementsByClass("wikitable").isEmpty()) || (!page.select("table:not([^])").isEmpty())) {
			return true;
		}
		else {
			System.out.println(new ExtractionInvalideException("Aucun tableau present dans la page").getMessage());
			return false;	
		}
	}

	/**
	 * Suppression des points virgules, interprete dans le csv comme un changement de colonne
	 * @param html
	 */
	public void supprimerPointsVirgule(String html){
		String newHtml = html.replaceAll(";", " ");
		this.donneeHTML = newHtml;
	}

	/**
	 * Methode creeant le nombre de cellules necessaires, en fonction
	 * de la valeur de l'attribut colspan de la cellule
	 * @param nbColspans la valeur de l'attribut colspan
	 * @param cellule la cellule a ajouter
	 */
	private void gererColspans(int nbColspans, Element cellule, int colonneActuelle) {
		for (int i = 0 ; i < nbColspans; i++) {
			stockerCelluleColspan(cellule, colonneActuelle);
			//if(!(i+1 == nbColspans)) {
			//	colonneActuelle++;
			//}
			colonneActuelle++;
		}
		//this.colonneActuelle++;//todo
		colonneActuelle--;
		this.colonneActuelle = colonneActuelle;
	}

	/**
	 * Methode creeant le nombre de cellules necessaires, en fonction
	 * de la valeur de l'attribut colspan de la cellule
	 * @param nbRowspans la valeur de l'attribut rowspan
	 * @param cellule la cellule a ajouter
	 * @param ligneActuelle la ligne actuelle
	 */
	private void gererRowspans(int nbRowspans, Element cellule, int ligneActuelle) {

		for (int i = 0 ; i < nbRowspans; i++) {
			stockerCelluleRowspan(cellule, ligneActuelle);
			ligneActuelle++;
		}
	}

	/**
	 * Methode gerant le stockage d'une cellule contenant un attribut colspan et rowspan.
	 * Traite d'abord le colspan, puis le rowspan en prenant en compte le traitement du colspan.
	 * @param nbRowspans la valeur du rowspan
	 * @param nbColspans la valeur du colspan
	 * @param cellule la cellule courante
	 */
	private void gererColspansEtRowspans(int nbRowspans, int nbColspans, Element cellule) {
		int colonneColspan = this.colonneActuelle;
		for (int i = nbColspans; i > 0; i--) {
			int ligneRowspan = this.ligneActuelle+1;
			stockerCelluleColspan(cellule, colonneColspan);
			for (int j = nbRowspans-1; j > 0; j--) {
				stockerRowspanSuivantColspan(cellule, ligneRowspan, colonneColspan);
				ligneRowspan++;
			}
			colonneColspan++;
		}
		this.colonneActuelle = colonneColspan-1;
	}

	/**
	 * Renvoie true si la ligne du tableau a des valeurs provenant
	 * de l'extraction d'une wikitable, false sinon.
	 * @param nbLigne le numero de la ligne a tester
	 * @return true ou false
	 */
	private boolean ligneContientInfos(int nbLigne) {
		for (int nbColonne = 0; nbColonne < tableau[nbLigne].length; nbColonne++) {
			if (!tableau[nbLigne][nbColonne].equals("VIDE")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Renvoie le nombre de tableaux du fichier.
	 */
	@Override
	public int getNbTableaux() {
		return this.nbTableauxExtraits;
	}

	/**
	 * Renvoie le nombre de colonnes ecrites dans le csv
	 * a partir du parsing d'une page.
	 * @return
	 */
	public int getColonnesEcrites() {
		return this.colonnesEcrites;
	}

	/**
	 * Renvoie le nombre de lignes ecrites dans le csv 
	 * a partir du parsing d'une page.
	 * @return
	 */
	public int getLignesEcrites() {
		return this.lignesEcrites;
	}
}
