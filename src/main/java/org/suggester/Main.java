package org.suggester;

import org.suggester.models.Film;
import org.suggester.models.Suggester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(new File(System.getProperty("user.dir") + "/src/test/resources/webmap.properties"))) {
            prop.load(input);
        } catch (IOException e) {
            //LOG.severe("Properties has not been read\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Properties has not been read", e);
        }
        for (String key : prop.stringPropertyNames()) {
            System.out.println(key + " = " + prop.getProperty(key));
        }
//        Suggester suggester = new Suggester.SuggesterBuilder().endPage(2).build();
//        for (Film film : suggester.parse()) {
//            System.out.println(film);
//        }
    }
}
