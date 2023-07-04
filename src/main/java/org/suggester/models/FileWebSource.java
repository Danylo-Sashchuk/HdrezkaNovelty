package org.suggester.models;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FileWebSource extends WebSource {
    Properties map = new Properties();

    public FileWebSource(String mapLocation, URL page) {
        try (InputStream input = new FileInputStream(mapLocation)) {
            map.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mainPage = page;
    }

    @Override
    URL getPage(URL url) throws MalformedURLException {
        String link = map.getProperty(URLEncoder.encode(String.valueOf(url),
                StandardCharsets.UTF_8));
        return new URL("file:src/test/resources/" + link);
    }
}
