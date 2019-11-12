package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ResultatEstNullException;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManageCsvTest {

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

       ManageCSV manageCSV = new ManageCSV();
       manageCSV.setSeparator(';');
       List<String[]> l2 = manageCSV.readCSV("output/csv/testComp1.csv");

       assertTrue (manageCSV.compareCSV(l1, l2));
    }

    @Test
    public void compareCsvTest() {
        ManageCSV manageCSV = new ManageCSV();
        manageCSV.setSeparator(';');
        List<String[]> l1 = manageCSV.readCSV("output/csv/testComp1.csv");
        manageCSV.setSeparator(',');
        List<String[]> l2 = manageCSV.readCSV("output/csv/testComp2.csv");
        manageCSV.setSeparator(',');
        List<String[]> l3 = manageCSV.readCSV("output/csv/perfect.csv");

        assertTrue(manageCSV.compareCSV(l1, l2) && !manageCSV.compareCSV(l1,l3));

    }

}
