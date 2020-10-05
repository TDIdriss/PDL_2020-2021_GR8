package com.wikipediaMatrix;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Group 8
 *
 * Classe de tests that checks if the CSV is valid and
 *
 */
public class CSVValidatorTest {

    /**
     * instance de CSVValidator
     */
    private CSVValidator validator = CSVValidator.getInstance();

    /**
     * Vérification de CSVs
     */
    @Test
    public void checkCSVTest1() {
        byte count = 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        System.out.println("Validation of good.csv");
        count += validator.checkCSVWithSeparator("csv/good.csv", ';') ? 1 : 0;
        System.out.println("Validation of perfect.csv");
        count += validator.checkCSVWithSeparator("csv/perfect.csv", ',') ? 1 : 0;
        System.out.println("Validation of perfect_colon.csv");
        count += validator.checkCSVWithSeparator("csv/perfect_colon.csv", ':') ? 1 : 0;
        System.out.println("Validation of perfect_pipe.csv");
        count += validator.checkCSVWithSeparator("csv/perfect_pipe.csv", '|') ? 1 : 0;
        System.out.println("Validation of perfect_semicolon.csv");
        count += validator.checkCSVWithSeparator("csv/perfect_semicolon.csv", ';') ? 1 : 0;
        System.out.println("Validation of perfect_tab.csv");
        count += validator.checkCSVWithSeparator("csv/perfect_tab.csv", '\t') ? 1 : 0;

        System.out.println("Validation of double_quote.csv");
        count += validator.checkCSVWithSeparator("csv/double_quote.csv", ',') ? 1 : 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        assertEquals(7, count);

    }

    /**
     * Vérification de CSVs
     */
    @Test
    public void checkCSVTest2() {
        CSVValidator validator = CSVValidator.getInstance();
        byte count = 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        System.out.println("Validation of bad.csv");
        count += validator.checkCSVWithSeparator("csv/bad.csv", ';') ? 1 : 0;
        System.out.println("Validation of mult_long_columns.csv");
        count += validator.checkCSVWithSeparator("csv/mult_long_columns.csv", ',') ? 1 : 0;
        System.out.println("Validation of mult_long_columns_tabs.csv");
        count += validator.checkCSVWithSeparator("csv/mult_long_columns_tabs.csv", '\t') ? 1 : 0;
        System.out.println("Validation of one_long_column.csv");
        count += validator.checkCSVWithSeparator("csv/one_long_column.csv", ',') ? 1 : 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        assertEquals(0, count);
    }

    /**
     * Permet de vérifier si le CSV est valide en précisant le séparateur
     */
    @Test
    public void checkCSVWithSeparatorTest() {
        CSVValidator validator = CSVValidator.getInstance();

        System.out.println("Validation of extract_csv.csv");
        Assert.assertTrue(validator.checkCSVWithSeparator("csv/extract_csv.csv", ';'));
    }

    /**
     * Teste la lecture de CSVs
     */
    @Test
    public void readCsvTest() {
        String[] s1 = new String[]{"name","age","gender"};
        String[] s2 = new String[]{"james","21","m"};
        String[] s3 = new String[]{"lauren","19","f"};
        String[] s4 = new String[]{"simon","57","m"};
        String[] s5 = new String[]{"","",""};

        List<String[]> l1= new ArrayList<String[]>();
        l1.add(s1);
        l1.add(s2);
        l1.add(s3);
        l1.add(s4);
        l1.add(s5);

        List<String[]> l2 = validator.readCSV("output/csv/testComp1.csv", ';');

        assertTrue (validator.compareList(l1, l2));
    }

    /**
     * Compare les listes de CSVs
     */
    @Test
    public void compareListTest() {
        List<String[]> list1 = validator.readCSV("output/csv/testComp1.csv", ';');
        List<String[]> list2 = validator.readCSV("output/csv/testComp2.csv", ',');
        List<String[]> list3 = validator.readCSV("output/csv/testComp3.csv", ',');

        assertTrue(validator.compareList(list1, list2));
        assertFalse(validator.compareList(list1, list3));

    }

}
