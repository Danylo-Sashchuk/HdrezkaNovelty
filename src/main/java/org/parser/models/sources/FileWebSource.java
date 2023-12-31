package org.parser.models.sources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public URL getLink(URL url) throws MalformedURLException {
        String path = map.getProperty(URLEncoder.encode(String.valueOf(url),
                StandardCharsets.UTF_8));
        return new URL("file:src/test/resources/" + path);
    }
}
