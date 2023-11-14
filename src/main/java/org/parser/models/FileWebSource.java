package org.parser.models;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FileWebSource extends WebSource {
    Properties map = new Properties();

    public FileWebSource(String mapLocation, URL mainPage) {
        try (InputStream input = new FileInputStream(mapLocation)) {
            map.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mainPage = mainPage;
    }

    @Override
    URL getLink(URL url) throws MalformedURLException {
        String path = map.getProperty(URLEncoder.encode(String.valueOf(url),
                StandardCharsets.UTF_8));
        return new URL("file:src/test/resources/" + path);
    }
}
