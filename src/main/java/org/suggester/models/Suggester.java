package org.suggester.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                    URL image = new URL(((Attr) div.getByXPath("./div/a/img/@src").get(0)).getValue());
                    URL link = new URL(((Attr) div.getByXPath("./div/a/@href").get(0)).getValue());
                    String title = ((DomText)div.getByXPath("./div[@class=\"b-content__inline_item-link\"]/a/text()").get(0)).getWholeText();
                    Film film = new Film();
                    film.setTitle(title);
                    film.setYear(Integer.parseInt(description[0]));
                    film.setCountry(description[1].trim());
                    film.setGenre(description[2].trim());
                    film.setImage(image);
                    film.setLink(link);
                    watchableFilms.add(film);
                }
            }
            return watchableFilms;
        }
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
