package org.suggester.models;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class WebSource {
    protected URL mainPage;

    public URL getMainPage() {
        return mainPage;
    }

    URL getPage(URL url) throws MalformedURLException {
        return url;
    }
}
