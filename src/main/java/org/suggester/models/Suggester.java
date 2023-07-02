package org.suggester.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.*;

public class Suggester {
    static private final Set<String> countriesWhiteList = Set.copyOf(List.of("CША", "Великобритания", "Канада",
            "Германия", "Франция", "Италия", "Украина"));
    static int startYear = 2020;
    static int endYear = 0;
    static final String hdrezka = "https://hdrezka.website/page/%d/?filter=last&genre=1";
    static int page = 1;

    public static void main(String[] args) throws IOException {
        try (WebClient client = new WebClient()) {
            String website = String.format(hdrezka, page);
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            HtmlPage page = client.getPage(website);
            List<HtmlDivision> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item-link\"]");
            List<HtmlDivision> watchableFilms = new ArrayList<>();
            for (HtmlDivision div : allFilms) {
                DomText textNode = (DomText) div.getFirstChild()
                        .getNextSibling()
                        .getNextSibling()
                        .getNextSibling()
                        .getFirstChild();
                String[] text = textNode.getWholeText().split(",");
                if (countriesWhiteList.contains(text[1].trim()) && Integer.parseInt(text[0]) >= startYear) {
                    watchableFilms.add(div);
                }
            }
            for (HtmlDivision div : watchableFilms) {
                DomText textNode = (DomText) div.getFirstChild()
                        .getNextSibling()
                        .getNextSibling()
                        .getNextSibling()
                        .getFirstChild();
                System.out.println(textNode.getWholeText());
            }
        }
    }

}
