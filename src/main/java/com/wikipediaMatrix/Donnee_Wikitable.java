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
 * @author Groupe 8
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
    private static String separator = System.getProperty("file.separator") ;



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
            url = new URL("https://"+langue+".wikipedia.org/w/api.php?action=parse&oldid="+url1.getOldid()+"&prop=wikitext&format=json");
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
     * @param wikitable contenu de la page
     * @throws IOException
     * @throws ExtractionInvalideException
     */
    public void wikitableVersCSV(String titre, String wikitable) {

        wikitable = wikitableReplace(wikitable);
        ArrayList<String> tableaux  = reconstituerTable(wikitable);
        int i = 0;
        for (String table : tableaux) {
            try {
                wikitableVersCSVAux2(table, titre, i + 1);
                i++;
            } catch (Exception e) {
                System.out.println("Ectraction wikitext a échoué");
            }
        }
    }


    private void wikitableVersCSVAux2(String wikitable, String title, int nbTab) throws IOException {
        int i = 0; int j = 0; int nb; int nbColMax = countCol(wikitable);
        boolean first = false, tableau = false;
        ArrayList<String[]> tab = new ArrayList<>();

        String[] lines = wikitable.split("\n");
        for (String line : lines) {
            if(line.startsWith(" ") || line.startsWith("	"))
                line = supprimerEspaceDebut(line);
            if (line.contains("{|")) {
                tab = new ArrayList<>();
                tableau = true;
                first = true;
            }
            if (line.contains("|}")) {
                tableau = false;
                i = j = 0;
                saveFile(tab, title, nbTab);
            }
            if (tableau) {
                if (first) {
                    if (line.startsWith("!")) {
                        tab.add(new String[nbColMax]);
                        if (line.contains("!!")) {
                            line = line.replaceAll("^(\\||! ?)\\|?","");
                            String[] innerLines = line.split(" ?\\|\\| ?| ?!!");
                            for (String innerLine : innerLines) {
                                innerLine = formatLine(innerLine);
                                innerLine = innerLine.trim();
                                if (tab.size() <= i)
                                    tab.add(new String[nbColMax]);
                                tab.get(i)[j] = innerLine;
                                j++;
                            }
                        }
                        else {
                            line = formatLine(line);
                            if (line.contains("rowspan")) {
                                nb = rowColSpan(line);
                                for (int k = 0; k < nb; k++) {
                                    if (tab.size() <= i+k) {
                                        String[] tabLine = new String[nbColMax];
                                        tabLine[j] = getCell(line);
                                        tab.add(tabLine);
                                    } else {
                                        tab.get(i+k)[j] = getCell(line);
                                    }
                                }
                            }
                            else if (line.contains("colspan")) {
                                nb = rowColSpan(line);
                                for (int k = 0; k < nb; k++) {
                                    if (tab.size() <= i)
                                        tab.add(new String[nbColMax]);
                                    tab.get(i)[j] = getCell(line);
                                }
                            }
                            else {
                                if (tab.size() <= i)
                                    tab.add(new String[nbColMax]);
                                tab.get(i)[j] = getCell(line);
                            }
                            j++;
                        }
                        first = false;
                    }
                } else {
                    if (line.contains("|-")) {
                        i++;
                        if (tab.size() <= i)
                            tab.add(new String[nbColMax]);
                        j = 0;
                    }
                    else if ((line.startsWith("|") || line.startsWith("||") || line.startsWith("!") || line.startsWith("!!")) && !line.contains("|+")) {
                        if (!line.contains("||")) {
                            line = formatLine(line);
                            if (line.contains("rowspan")) {
                                nb = rowColSpan(line);
                                for (int k = 0; k < nb; k++) {
                                    if (tab.size() <= i+k) {
                                        String[] tabLine = new String[nbColMax];
                                        tabLine[j] = getCell(line);
                                        tab.add(tabLine);
                                    } else {
                                        while (tab.get(i+k)[j] != null)
                                            j++;
                                        tab.get(i+k)[j] = getCell(line);
                                    }
                                }
                                j++;
                            }
                            else if (line.contains("colspan")) {
                                nb = rowColSpan(line);
                                for (int k = 0; k < nb; k++) {
                                    if (tab.size() <= i)
                                        tab.add(new String[nbColMax]);
                                    tab.get(i)[j] = getCell(line);
                                    j++;

                                }
                            }
                            else {
                                if (tab.size() <= i)
                                    tab.add(new String[nbColMax]);
                                while (tab.get(i)[j] != null)
                                    j++;
                                tab.get(i)[j] = getCell(line);
                                j++;
                            }
                        }
                        else {
                            line = line.replaceAll("^(\\||! ?)\\|?","");
                            String[] innerLines = line.split("\\|\\| | !!");
                            for (String innerLine : innerLines) {
                                innerLine = formatLine(innerLine);
                                innerLine = innerLine.trim();
                                if (innerLine.contains("rowspan")) {
                                    nb = rowColSpan(innerLine);
                                    for (int k = 0; k < nb; k++) {
                                        if (tab.size() <= i+k) {
                                            String[] tabLine = new String[nbColMax];
                                            tabLine[j] = getCell(innerLine);
                                            tab.add(tabLine);
                                        } else {
                                            while (tab.get(i+k)[j] != null)
                                                j++;
                                            tab.get(i+k)[j] = getCell(innerLine);
                                        }
                                    }
                                    j++;
                                }
                                else if (innerLine.contains("colspan")) {
                                    nb = rowColSpan(innerLine);
                                    for (int k = 0; k < nb; k++) {
                                        if (tab.size() <= i)
                                            tab.add(new String[nbColMax]);
                                        tab.get(i)[j] = getCell(innerLine);
                                        j++;

                                    }
                                }
                                else {
                                    if (tab.size() <= i)
                                        tab.add(new String[nbColMax]);
                                    while (tab.get(i)[j] != null)
                                        j++;
                                    tab.get(i)[j] = getCell(innerLine);
                                    j++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int countCol(String json){
        String[] tab = json.split("\\|-");
        Long count = 0L;

        String ligne = tab[0].contains("!") ? tab[0] : tab[1];
        String[] cels = ligne.split("!!?");
        int i = 0;
        for(String cel : cels){
            cel = cel.trim();
            i++;
            if (i > 1) {
                Pattern pattern = Pattern.compile("colspan=\"([0-9]+)\"(.)*");
                Matcher matcher = pattern.matcher(cel);
                if (matcher.matches()) {
                    String val = matcher.group(1);
                    count += Long.parseLong(val);
                }
                else {
                    count++;
                }
            }
        }
        return count.intValue();
    }

    private ArrayList<String> reconstituerTable(String wikiText) {
        ArrayList<String> tableaux = new ArrayList<>();
        StringBuilder currentTab = new StringBuilder();
        String[] lines = wikiText.split("\n");
        boolean tab = false;
        int i = 0;
        for (String line: lines) {
            if (line.contains("{|")) {
                tab = true;
                i++;
            }
            if (Pattern.compile("(\\|})$").matcher(line).find()) {
                tab = false;
                currentTab.append(line);
                tableaux.add(currentTab.toString());
                currentTab = new StringBuilder();
            }
            if (tab)
                currentTab.append(line).append("\n");
        }

        return tableaux;
    }

    private String formatLine(String ligne) {
        ligne = ligne.replaceAll("^(\\||! ?)\\|?","");
        ligne = ligne.replaceAll("([{a-zA-Z]*icon\\|[a-zA-Z} ]*\\[\\[)","");
        ligne = ligne.replaceAll("(dunno)","");
        ligne = ligne.replaceAll("(\\{\\{|\\[\\[)(n/a)","");
        ligne = ligne.replaceAll("(]]?|}}|((\\[?\\[( )*|\\{\\{)([a-zA-Z( )*]+\\|)?))","");
        ligne = ligne.replaceAll("v?align=[a-z-\"]*( )*?\\|","");
        ligne = ligne.replaceAll("style=\"((.)+)\"","");
        ligne = ligne.replaceAll("\\|?","");
        ligne = ligne.replaceAll("<ref((.*))?>((.*))</ref>","");
        ligne = ligne.replaceAll("url=((.*))?","");
        ligne = ligne.replaceAll("<((.*))?/>","");
        ligne = ligne.replaceAll("<[a-zA-Z0-9=( )*]*>","");
        ligne = ligne.replaceAll("</[a-zA-Z0-9=( )*]*>","");
        ligne = ligne.replaceAll("(F|)ile:(.)* ?","");
        return ligne;
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


    private void saveFile(ArrayList<String[]> tab, String title, int nbtab) throws IOException {


        String outputPath = "output"+separator+"wikitext"+separator+ title +"-"+ nbtab + ".csv";
        FileOutputStream outputStream = new FileOutputStream(outputPath);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        int j, i = tab.size();
        for (String[] line : tab) {
            i--;
            j = line.length;
            for (String cellule : line) {
                j--;
                if (j == 0) {
                    writer.write(cellule == null ? "" : cellule);
                    if (i > 0)
                        writer.write("\n");
                }
                else
                    writer.write(cellule+";");
            }
        }
        writer.close();
        nbTableauxExtraits++;
    }

    /**
     * Methode gerant les rowspans et colspan d'une ligne
     * @param ligne
     * @param i
     * @param j
     * @return
     */

    /**
     * Initialise le tableau
     */
    public void initTab() {
        for (String[] ligne: tab) {
            java.util.Arrays.fill(ligne,"VIDE");
        }
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
        wikitable = wikitable.replaceAll("scope=\"?(col|row)\"?", "");
        wikitable = wikitable.replaceAll("style=\"((.)*)\"", "");
        wikitable = wikitable.replaceAll("<!((.)*)>", "");
        wikitable = wikitable.replaceAll("&nbsp;", " ");
        wikitable = wikitable.replaceAll(";", ",");
        wikitable = wikitable.replaceAll("<br />", "");
        wikitable = wikitable.replaceAll("</center>", "");
        wikitable = wikitable.replaceAll("<center>", "");
        wikitable = wikitable.replaceAll("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?", "");
        wikitable = wikitable.replaceAll("\\|-/", "\\| -/");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?author"," author");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?publisher"," publisher");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?date","date");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?archive-url"," archive-url");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?title"," title");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?accessdate"," accessdate");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?url-status"," url-status");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?archive-date"," archive-date");
        wikitable = wikitable.replaceAll("\n ?\\|( )*?url( )*=","");
        wikitable = wikitable.replaceAll("\n ?\n","\n");
        wikitable = wikitable.replaceAll("\\|-class=\"((.)*)\"\n","\\|-\n");
        wikitable = wikitable.replaceAll("\\|-(\n)?\\|-","\\|-\n");
        wikitable = wikitable.replaceAll("\\|-(\n)?\\|}","\\|}");
        wikitable = wikitable.replaceAll("(mi)?\\|abbr=(.)","");
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
