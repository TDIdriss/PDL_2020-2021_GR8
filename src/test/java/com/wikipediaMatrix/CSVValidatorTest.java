package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ExtractionInvalideException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CSVValidatorTest {


    @Test
    public void checkCSVTest1() {
        CSVValidator validator = CSVValidator.getInstance();
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
        //System.out.println("Validation of double_quote.csv");
        //count += validator.checkCSV("csv/double_quote.csv") ? 1 : 0;
        System.out.println("Validation of mult_long_columns.csv");
        count += validator.checkCSV("csv/mult_long_columns.csv") ? 1 : 0;
        System.out.println("Validation of mult_long_columns_tabs.csv");
        count += validator.checkCSV("csv/mult_long_columns_tabs.csv") ? 1 : 0;
        System.out.println("Validation of one_long_column.csv");
        count += validator.checkCSV("csv/one_long_column.csv") ? 1 : 0;

        System.out.println("| --------------------------------------- VALIDATION DE CSV --------------------------------------- |\n");

        assertEquals(0, count);
    }

}
