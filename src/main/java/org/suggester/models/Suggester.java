package org.suggester.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Suggester {
    private final String hdrezka = "https://hdrezka.website/page/%d/?filter=last&genre=1";
    private final Set<String> countriesWhiteList = new HashSet<>(List.of("CША", "Великобритания", "Канада",
            "Германия", "Франция", "Италия", "Украина"));
    private int startYear = 2020;
    private int endYear = 0;
    private int page = 1;

    public List<Film> parse() throws IOException {
        try (WebClient client = new WebClient()) {
            String website = String.format(hdrezka, page);
            setupClient(client);
            HtmlPage page = client.getPage(website);
            List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
            List<Film> watchableFilms = new ArrayList<>();
            for (DomElement div : allFilms) {
                String[] description = getDescription(div);
                if (isWatchable(description)) {
                    try {
                        URL image = new URL(((Attr) div.getByXPath("./div/a/img/@src").get(0)).getValue());
                        URL link = new URL(((Attr) div.getByXPath("./div/a/@href").get(0)).getValue());
                        String title = ((DomText) div.getByXPath("./div[@class=\"b-content__inline_item-link\"]/a" +
                                                                 "/text()")
                                .get(0)).getWholeText();
                        HtmlPage moviePage = client.getPage(link);
                        Rating rating = getRating(link, moviePage);
                        String originalTitle = ((DomText) moviePage.getByXPath("//div[@class=\"b-post__origtitle\"]/text()")
                                .get(0)).getWholeText();
                        Film film = new Film(image, title, originalTitle, Integer.parseInt(description[0]),
                                description[1].trim(), description[2].trim(), link, rating);
                        watchableFilms.add(film);
                    } catch (RuntimeException e) {
                        System.out.println("Film parsing is interrupted, skip this film.");
                    }
                }
            }
            Collections.sort(watchableFilms);
            return watchableFilms;
        }
    }

    private Rating getRating(URL link, HtmlPage page) {
        List<Object> byXPath = page.getByXPath("//span[@class=\"b-post__info_rates imdb\"]");
        if (byXPath.isEmpty()) {
            return null;
        }
        HtmlSpan ratingRow = (HtmlSpan) byXPath.get(0);
        float rating = Float.parseFloat(((DomText) ratingRow.getByXPath("./span/text()").get(0)).getWholeText());
        String count = ((DomText) ratingRow.getByXPath("./i/text()").get(0)).getWholeText();
        int votes = Integer.parseInt(count.substring(1, count.length() - 1).replaceAll("\\s+", ""));
        return new Rating(rating, votes);
    }

    private String[] getDescription(DomElement div) {
        DomText domText = (DomText) div.getByXPath("./div[2]/div[1]/text()").get(0);
        return domText.getWholeText().split(",");
    }

    private void setupClient(WebClient client) {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    private boolean isWatchable(String[] description) {
        return countriesWhiteList.contains(description[1].trim()) && Integer.parseInt(description[0]) >= startYear;
    }

}
