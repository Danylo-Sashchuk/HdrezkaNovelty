package org.parser.models;

import java.net.URL;

public class LiveWebSource extends WebSource {
    public LiveWebSource(URL mainPage) {
        this.mainPage = mainPage;
    }

    @Override
    URL getLink(URL url) {
        return url;
    }
}
