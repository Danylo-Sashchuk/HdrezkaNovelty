package org.parser.models.jumpers;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.parser.models.WebSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 */

public class ConcurrentJumper extends Jumper {
    public ConcurrentJumper(WebClient client, WebSource webSource) {
        super(client, webSource);
    }

    @Override
    public void parsePage(int pageNumber) {
//        LOG.info("Parsing website's page " + currentPage);
//        String website = String.format(webSource.getMainPage().toString(), currentPage);
//        try {
//            HtmlPage page = client.getPage(website);
//            List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
//            List<DomElement> tempStorage = new ArrayList<>();
//            for (DomElement div : allFilms) {
//                tempStorage.add(div);
//                if (tempStorage.size() == 10) {
//                    parseFilms(tempStorage, client);
//                    tempStorage.clear();
//                }
//            }
//            if (!tempStorage.isEmpty()) {
//                parseFilms(tempStorage, client);
//                tempStorage.clear();
//            }
//        } catch (IOException e) {
//            LOG.severe("Error with " + currentPage + " page. Skipping the page.");
//        }
    }
}
