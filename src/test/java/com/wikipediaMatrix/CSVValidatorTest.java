package com.wikipediaMatrix;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVValidatorTest {

    private CSVValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = CSVValidator.getInstance();
    }

    @Test
    public void checkCSVTest1() {
        byte count = 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        System.out.println("Validation of good.csv");
        count += validator.checkCSV("csv/good.csv") ? 1 : 0;
        System.out.println("Validation of perfect.csv");
        count += validator.checkCSV("csv/perfect.csv") ? 1 : 0;
        System.out.println("Validation of perfect_colon.csv");
        count += validator.checkCSV("csv/perfect_colon.csv") ? 1 : 0;
        System.out.println("Validation of perfect_pipe.csv");
        count += validator.checkCSV("csv/perfect_pipe.csv") ? 1 : 0;
        System.out.println("Validation of perfect_semicolon.csv");
        count += validator.checkCSV("csv/perfect_semicolon.csv") ? 1 : 0;
        System.out.println("Validation of perfect_tab.csv");
        count += validator.checkCSV("csv/perfect_tab.csv") ? 1 : 0;

        System.out.println("Validation of double_quote.csv");
        count += validator.checkCSV("csv/double_quote.csv") ? 1 : 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        assertEquals(7, count);

    }


    @Test
    public void checkCSVTest2() {
        CSVValidator validator = CSVValidator.getInstance();
        byte count = 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        System.out.println("Validation of bad.csv");
        count += validator.checkCSV("csv/bad.csv") ? 1 : 0;
        System.out.println("Validation of mult_long_columns.csv");
        count += validator.checkCSV("csv/mult_long_columns.csv") ? 1 : 0;
        System.out.println("Validation of mult_long_columns_tabs.csv");
        count += validator.checkCSV("csv/mult_long_columns_tabs.csv") ? 1 : 0;
        System.out.println("Validation of one_long_column.csv");
        count += validator.checkCSV("csv/one_long_column.csv") ? 1 : 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        assertEquals(0, count);
    }

    @Test
    public void readCsvTest() {
        String[] s1= new String[]{"name","age","gender"};
        String[] s2= new String[]{"james","21","m"};
        String[] s3= new String[]{"lauren","19","f"};
        String[] s4= new String[]{"simon","57","m"};

        List<String[]> l1= new ArrayList<String[]>();
        l1.add(s1);
        l1.add(s2);
        l1.add(s3);
        l1.add(s4);

        List<String[]> l2 = validator.readCSV("output/csv/testComp1.csv", ';');

        assertTrue (validator.compareCSV(l1, l2));
    }

    @Test
    public void compareCSV1() {
        List<String[]> list1 = validator.readCSV("output/csv/testComp1.csv", ';');
        List<String[]> list2 = validator.readCSV("output/csv/testComp2.csv", ',');

        assertTrue(validator.compareCSV(list1, list2));
    }


    @Test
    public void compareCSV2() {
        List<String[]> list1 = validator.readCSV("output/csv/testComp1.csv", ';');
        List<String[]> list2 = validator.readCSV("output/csv/testComp3.csv", ',');

        assertFalse(validator.compareCSV(list1, list2));
    }


}
