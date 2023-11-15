package org.parser.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.parser.models.processors.ConcurrentProcessor;
import org.parser.models.processors.Processor;
import org.parser.models.processors.SequentialProcessor;
import org.parser.models.sources.WebSource;
import org.parser.ratingStrategies.WeightedAverageStrategy;
import org.parser.util.Config;
import org.parser.util.FilmComparator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static final Logger LOG = Logger.getLogger(Parser.class.getName());
    private final Set<String> countriesWhitelist;
    private final int startYear;
    private final int endYear;
    private final int startPage;
    private final int endPage;
    private final WebSource webSource;
    private Processor processor;
    private FilmComparator filmComparator;

    private Parser(ParserBuilder builder) {
        LOG.log(Level.INFO, "Creating Parser");
        this.countriesWhitelist = builder.countriesWhitelist;
        this.startYear = builder.startYear;
        this.endYear = builder.endYear;
        this.startPage = builder.startPage;
        this.endPage = builder.endPage;
        this.filmComparator = builder.filmComparator;
        this.webSource = builder.webSource;
        LOG.log(Level.INFO, "Parser is created");
    }

    public void changeFilmComparator(FilmComparator filmComparator) {
        this.filmComparator = filmComparator;
    }

    public List<Film> parse() {
        try (WebClient client = new WebClient()) {
            webSource.setClient(client);
            webSource.configureClient();
            //todo where to initialize processor

            processor = new ConcurrentProcessor(webSource);
            for (int i = startPage; i <= endPage; i++) {
                List<Film> filmsOnPage = parsePage(i);
                if (!filmsOnPage.isEmpty()) {
                    processor.process(new ArrayList<>(filmsOnPage));
                }
            }
            processor.endProcessing();

            LOG.info("Sorting films");
            List<Film> result = processor.getResult();
            result.sort(filmComparator);
            return result;
        } catch (IOException e) {
            LOG.severe("Severe error, parsing cannot be continued." + e);
            LOG.info("Returning parsed films");
            return processor.getResult();
        } catch (IllegalStateException e) {
            throw e;
        }
    }


    private List<Film> parsePage(int currentPage) throws IOException {
        LOG.info("Parsing website's page " + currentPage);
        List<Film> films = new ArrayList<>();
        String website = String.format(webSource.getMainPage().toString(), currentPage);
        try {
            HtmlPage page = webSource.getPage(website);
            List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
            for (DomElement div : allFilms) {
                Film film = parseFilm(div);
                if (film != null) {
                    films.add(film);
                }
            }
        } catch (IOException e) {
            LOG.severe("Error with " + currentPage + " page. Skipping the page.");
        }
        return films;
    }

    private Film parseFilm(DomElement div) {
        Film rawFilm = null;
        try {
            String[] description = webSource.getDescription(div);
            if (description.length < 3) {
                LOG.info("Movie does not have enough information to be taken into consideration.");
                return null;
            }
            String title = webSource.getTitle(div);
            LOG.info("Parsing: %s - %s, %s, %s ".formatted(title, description[0], description[1], description[2]));
            if (!isWatchable(description)) {
                return null;
            }
            URL image = webSource.getImageUrl(div);
            URL link = webSource.getLink(webSource.getMovieUrl(div));
            rawFilm = new Film(image, title, Integer.parseInt(description[0]), description[1].trim(),
                    description[2].trim(), link);
        } catch (RuntimeException | IOException e) {
            LOG.severe("Error with film. Skipping the film.");
        }
        return rawFilm;
    }

    private boolean isWatchable(String[] description) {
        int year = Integer.parseInt(description[0]);
        String country = description[1].trim();
        return countriesWhitelist.contains(country) && year >= startYear && year <= endYear;
    }

    public static class ParserBuilder {
        private final Config config = Config.get();
        private final float weightedAverageRatingWeight = Float.parseFloat(config.getProperty(
                "weightedAverageRatingWeight"));
        private final float weightedAverageVotesWeight = Float.parseFloat(config.getProperty(
                "weightedAverageVotesWeight"));
        private FilmComparator filmComparator =
                new FilmComparator(new WeightedAverageStrategy(weightedAverageRatingWeight,
                        weightedAverageVotesWeight));
        private Set<String> countriesWhitelist = new HashSet<>(List.of(Config.get()
                .getProperty("countriesWhitelist")
                .split(",")));
        private int startYear = Integer.parseInt(config.getProperty("startYear"));
        private int endYear = Integer.parseInt(config.getProperty("endYear"));
        private int startPage = Integer.parseInt(config.getProperty("startPage"));
        private int endPage = Integer.parseInt(config.getProperty("endPage"));
        private WebSource webSource;

        public ParserBuilder(WebSource webSource) {
            this.webSource = webSource;
        }

        public ParserBuilder countriesWhitelist(Set<String> countriesWhitelist) {
            this.countriesWhitelist = countriesWhitelist;
            return this;
        }

        public ParserBuilder startYear(int startYear) {
            this.startYear = startYear;
            return this;
        }

        public ParserBuilder endYear(int endYear) {
            this.endYear = endYear;
            return this;
        }

        public ParserBuilder startPage(int startPage) {
            this.startPage = startPage;
            return this;
        }

        public ParserBuilder endPage(int endPage) {
            this.endPage = endPage;
            return this;
        }

        public ParserBuilder filmComparator(FilmComparator filmComparator) {
            this.filmComparator = filmComparator;
            return this;
        }

        public Parser build() {
            if (endYear < startYear) {
                LOG.severe("Parser cannot be created. End year for searching is less than start year");
                throw new IllegalArgumentException("Parser cannot be created. End year for searching is less than " + "start" + " year");
            }
            if (endPage < startPage) {
                LOG.severe("Parser cannot be created. End page for parsing is less than start page");
                throw new IllegalArgumentException("Parser cannot be created. End page for parsing is less than " +
                                                   "start" + " " + "page");
            }
            return new Parser(this);
        }

        public ParserBuilder webSource(WebSource webSource) {
            this.webSource = webSource;
            return this;
        }
    }
}

