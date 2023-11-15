package org.parser.models.sources;

import java.net.URL;

public class LiveWebSource extends WebSource {
    public LiveWebSource(URL mainPage) {
        this.mainPage = mainPage;
    }
}
