package com.wikipediaMatrix;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wikipediaMatrix.exception.ExtractionInvalideException;
import com.wikipediaMatrix.exception.UrlInvalideException;
import org.json.JSONObject;

/**
 * Classe permettant de recuperer et convertir des Wikitable en CSV
 * @author Groupe 4
 *
 */

public class Donnee_Wikitable extends Donnee{

    private String wikitable;
    private int lignesEcrites = 0;
    private int colonnesEcrites = 0;
    private String[][] tab;
    private int maxLigne = 0;
    private int maxColone = 0;
    private int nbTableauxExtraits = 0;
    private Url url;

    public Donnee_Wikitable(){
        this.wikitable = "";
        this.tab = new String[500][200];
        initTab();

    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public void setWikitable(String wikitable) {
        this.wikitable = wikitable;
    }

    public String getWikitable() {
        return this.wikitable;
    }

    @Override
    public void run() {
        try {
            extraire(this.url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getContenu() {
        return this.wikitable;
    }

    @Override
    String recupContenu(URL url) throws ExtractionInvalideException {

        Url url1 = new Url(url);

        try {
            url1.estPageWikipedia();
            url1.estTitreValide();
            String langue = url1.getLangue();
            String titre = url1.getTitre();
            url = new URL("https://"+langue+".wikipedia.org/w/api.php?action=parse&page="+titre+"&prop=wikitext&format=json");
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
     * Recupere les donnees en JSON pour les mettre dans un CSV
     * @param url
     * @throws UrlInvalideException
     * @throws ExtractionInvalideException
     * @throws MalformedURLException
     */
    @Override
    public synchronized void extraire(Url url) throws  ExtractionInvalideException, IOException {
        startTimer();
        url.estTitreValide();
        String titre = url.getTitre();


        String json = recupContenu(url.getURL());
        if(!hasErrorOnPage(json)) {
            wikitable = jsonVersWikitable(json);
            wikitableVersCSV(titre,wikitable);
        }
        else {
            System.out.println("La page " + titre + "ne permet pas d'extraction en json");
        }
    }

    /**
     * Renvoie true si le json ne permet pas d'extraire le wikitext.
     * @param json
     * @return
     */
    private boolean hasErrorOnPage(String json) {
        JSONObject objetJson = new JSONObject(json);
        return objetJson.has("error");
    }

    /**
     * Recupere le wikitext dans le JSON
     * @param json
     * @return String
     * @throws ExtractionInvalideException
     */
    public String jsonVersWikitable(String json) throws ExtractionInvalideException {
        String wikitext ="";
        if(!hasErrorOnPage(json)) {
            try {
                JSONObject objetJson1 = new JSONObject(json);
                JSONObject objetJson2 = (JSONObject) objetJson1.get("parse");
                JSONObject objetJson3 = (JSONObject) objetJson2.get("wikitext");

                wikitext = objetJson3.getString("*");
            } catch (Exception e) {
                throw new ExtractionInvalideException("Extraction JSON vers Wikitext echouee");
            }
        }
        return wikitext;
    }

    /**
     * Amelioration possible : reduire la complexite de cette methode (en la coupant en plusieurs parties)
     * @param wikitable
     * @throws IOException
     * @throws ExtractionInvalideException
     */
    public void wikitableVersCSV(String titre, String wikitable) throws IOException {
        int i = 0,j = 0, nbtab = 0;
        boolean first = false, tableau = false, dansCell = false, drap = false;

        wikitable = wikitableReplace(wikitable);
        wikitableVersCSVAux(wikitable, titre);
    }

    public void wikitableVersCSVAux(String wikiText, String titre) {
        String[] lignes = wikiText.split("\n");
        ArrayList<String> finalLines = new ArrayList<>();
        int nbtab = 0;
        boolean tableau = false;
        for (String ligne : lignes) {
            if (ligne.contains("rowspan") || ligne.contains("colspan") || ligne.startsWith(":")) {
                tableau = false;
                finalLines.clear();
            }
            if(ligne.startsWith(" ") || ligne.startsWith("	")) {
                ligne = supprimerEspaceDebut(ligne);
            }
            if (tableau && ligne.contains("|}")) {
                tableau = false;
                try {
                    nbtab++;
                    this.nbTableauxExtraits++;
                    saveFile(formatTable(finalLines),titre,nbtab);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (tableau) {
                if((!ligne.contains("|-") && !ligne.contains("|+")) && (ligne.startsWith("!") || ligne.startsWith("|"))) {
                    finalLines.add(ligne);
                }
            }
            if(ligne.startsWith("{|")) {
                tableau = true;
                finalLines.clear();
            }
        }
    }

    private String formatTable(ArrayList<String> content) {
        String rawTable = "";
        for (String ligne : content) {
            ligne = ligne.replaceAll("^(\\||! ?)\\|?","");
            ligne = ligne.replaceAll("([{a-zA-Z]*icon\\|[a-zA-Z} ]*\\[\\[)","");
            ligne = ligne.replaceAll("(]])","");
            ligne = ligne.replaceAll("align=[a-z-\"]*\\|","");

            rawTable = rawTable.concat(ligne+"\n");
        }
        rawTable = rawTable.replaceAll("(\\|\\||!!)",";");

        return rawTable;
    }

    private int rowColSpan(String line) {
        Pattern pattern = Pattern.compile(" *((col|row)span=\"?([0-9]+)\"? ?)((.)*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String val = matcher.group(3);
            if (val != null &&!val.isEmpty())
                return Integer.parseInt(val);
        }
        return 0;
    }

    private String getCell(String line) {
        Pattern patternCell = Pattern.compile(" *((col|row)span=\"?([0-9]+)\"? ?)?((.)*)");
        Matcher matcher = patternCell.matcher(line);
        if (matcher.matches()) {
            String val = matcher.group(4);
            return val;
        }
        return "";
    }


    private void saveFile(String content, String title, int nbtab) throws IOException {
        String outputPath = "output/wikitext/" + title + nbtab + ".csv";
        FileOutputStream outputStream = new FileOutputStream(outputPath);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        writer.write(content);
        writer.close();
    }
    /**
     * Methode gerant les rowspans et colspan d'une ligne
     * @param ligne
     * @param i
     * @param j
     * @return
     */
    public int gererColspanEtRowspan(String ligne, int i, int j) {
        if(ligne.endsWith("|")) {
            ligne = ligne+" ";
        }
        String[] mots = ligne.split("\\|");
        int nbrow = 0;
        int nbcol = 0;
        boolean drap = false, col = false, row = false;
        for(String mot : mots) {
            while(tab[i][j] != "VIDE") {
                j++;
            }

            if(mot.contains("colspan")) {
                mot = mot.replaceAll("colspan", ",");
                for (int a=0; a < mot.length(); a++){
                    if (mot.charAt(a) == ',' && mot.charAt(a+1) == '=') {
                        if (mot.charAt(a+2) == '"') {
                            nbcol = Integer.parseInt(Character.toString(mot.charAt(a+3)));
                            mot = mot.substring(a+5, mot.length());
                        }else {
                            nbcol = Integer.parseInt(Character.toString(mot.charAt(a+2)));
                            mot = mot.substring(a+3, mot.length());
                        }
                    }
                }
                col = true;
                drap = true;
            }
            if(mot.contains("rowspan")) {
                mot = mot.replaceAll("rowspan", ",");
                for (int a=0; a < mot.length(); a++){
                    if (mot.charAt(a) == ',' && mot.charAt(a+1) == '=') {
                        if ((mot.charAt(a+2) == '"') || (mot.charAt(a+2) == ' ')) {
                            nbrow = Integer.parseInt(Character.toString(mot.charAt(a+3)));
                        }else {
                            char test = mot.charAt(a+2);
                            nbrow = Integer.parseInt(Character.toString(test));
                        }
                    }
                }
                row = true;
                drap = true;
            }
            if(!drap) {

                if(row && col) {
                    for(int x = 0; x < nbrow; x++) {
                        for(int y = 0; y < nbcol; y++) {
                            tab[i+x][j+y]=mot;
                        }
                    }
                    j+=nbcol;
                }else {
                    if(col){
                        for(int x = 0; x < nbcol; x++) {
                            tab[i][j]=mot;
                            j++;
                        }
                    }

                    if(row){
                        for(int x = 0; x < nbrow; x++) {
                            tab[i+x][j]=mot;
                        }
                        nbrow=0;
                        j++;
                    }

                    if(!col && !row){
                        tab[i][j] = mot;
                        j++;
                    }
                }
                col = false;
                row = false;
            }
            drap=false;
        }
        return j;
    }

    /**
     * Initialise le tableau
     */
    public void initTab() {
        for (String[] ligne: tab) {
            java.util.Arrays.fill(ligne,"VIDE");
        }
    }

    /**
     * ecrit les fichier csv
     * @param titre
     * @param nbtab
     * @throws IOException
     */
    public void ecrireCsv(String titre, int nbtab) throws IOException{
        boolean drap = false;
        String outputPath = "output/wikitext/" + titre + nbtab + ".csv";
        FileOutputStream outputStream = new FileOutputStream(outputPath);


        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        this.lignesEcrites = maxLigne;
        this.colonnesEcrites += maxColone;
        for(int k = 0; k < maxLigne; k++) {
            for(int l = 0; l < maxColone; l++) {
                if(tab[k][l].startsWith("!")) {
                    tab[k][l]=tab[k][l].substring(1, tab[k][l].length());
                }
                if(tab[k][l] != "VIDE") {
                    writer.write(tab[k][l].concat(";"));
                }else {
                    drap = true;
                }
            }
            if(!drap) {
                writer.write("\n");
            }else {
                maxLigne--;
            }
        }
        writer.close();
    }

    /**
     * Supprime les espaces en debut de ligne
     * @param ligne
     * @return
     */
    public String supprimerEspaceDebut(String ligne) {
        while(ligne.startsWith(" ") || ligne.startsWith("	")) {
            ligne = ligne.substring(1,ligne.length());
        }
        return ligne;
    }

    /**
     * Methode supprimant plusieurs expressions inutiles pour le parsing
     * @param wikitable
     * @return
     */
    public String wikitableReplace(String wikitable) {
        wikitable = wikitable.replaceAll("scope=col", "");
        wikitable = wikitable.replaceAll("style=\"text-align:center\"", "");
        wikitable = wikitable.replaceAll("&nbsp;", " ");
        wikitable = wikitable.replaceAll(";", ",");
        wikitable = wikitable.replaceAll("<br />", "");
        wikitable = wikitable.replaceAll("</center>", "");
        wikitable = wikitable.replaceAll("<center>", "");
        wikitable = wikitable.replaceAll("\\|-/", "\\| -/");
        return wikitable;
    }


    /**
     * Verification de la presence de tableaux dans les donnees
     * @return boolean
     * @throws ExtractionInvalideException
     */
    @Override
    public boolean pageComporteTableau() throws ExtractionInvalideException {
        if(!wikitable.contains("{|")){
            System.out.println(new ExtractionInvalideException("Aucun tableau present dans la page").getMessage());
            return false;
        }
        return true;
    }

    /**
     * Renvoie le nombre de colonnes ecrites pour la page courante
     * @return
     */
    public int getColonnesEcrites() {
        return colonnesEcrites;
    }

    /**
     * Renvoie le nombre de lignes ecrites pour la page courante
     * @return
     */
    public int getLignesEcrites() {
        return lignesEcrites;
    }

    /**
     * Renvoie le nombre de tableaux detectes sur la page
     */
    @Override
    public int getNbTableaux() {
        return this.nbTableauxExtraits;
    }
}
