package org.parser.util;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebHelper {
    public static void setClientSettings(WebClient client) {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }
}
