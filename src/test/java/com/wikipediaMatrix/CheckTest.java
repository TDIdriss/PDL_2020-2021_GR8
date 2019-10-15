package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.ResultatEstNullException;
import com.wikipediaMatrix.exception.UrlInvalideException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckTest {

    @Test
    public void verifierUrlInvalide() throws IOException, UrlInvalideException, InterruptedException, ResultatEstNullException {

        try {
            int nbtab = 0;

            Url wikiUrl = new Url(new URL(""));
            if(wikiUrl.estUrlValide()) {
                Donnee_Wikitable testWikitable = new Donnee_Wikitable();

                testWikitable.setUrl(wikiUrl);
                testWikitable.start();

                testWikitable.join();

            }

            assert(true);
        }
        catch (MalformedURLException ex) {
            assert(false);
        }

    }
}
