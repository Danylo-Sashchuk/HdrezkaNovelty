package org.parser.conc;

public class ScrapperThread implements Runnable {
    @Override
    public void run() {

    }
    //    private static final Logger LOG = Logger.getLogger(ScrapperThread.class.getName());
    //    private final ConcurrentLinkedDeque<Film> resultFilms;
    //    private final Film rawFilm;
    //    private final WebSource webSource;
    //
    //    public ScrapperThread(Film rawFilm, ConcurrentLinkedDeque<Film> resultFilms, WebSource webSource) {
    //        this.resultFilms = resultFilms;
    //        this.rawFilm = rawFilm;
    //        this.webSource = webSource;
    //    }
    //
    //    @Override
    //    public void run() {
    //        StringBuilder sb = new StringBuilder();
    //        for (Film film : films) {
    //            sb.append(film.getTitle()).append(", ");
    //        }
    //        LOG.info("Creating a new thread for films: " + sb);
    //        for (Film film : films) {
    //            try {
    //                LOG.info("Parsing film " + film.getTitle());
    //                createFilm(client, film);
    //            } catch (IOException | RuntimeException e) {
    //                LOG.severe("Error while parsing film " + film.getTitle());
    //                throw new RuntimeException(e);
    //            }
    //        }
    //        LOG.info("Thread finished for films: " + sb);
    //    }
    //
    //    private void createFilm(WebClient client, Film film) throws IOException {
    //        LOG.info("Jumping on page - " + film.getTitle());
    //        HtmlPage moviePage = client.getPage(film.getLink());
    //        Rating rating = getRating(moviePage);
    //        String originalTitle = getOriginalTitle(moviePage);
    //        film.setRating(rating);
    //        film.setOriginalTitle(originalTitle);
    //        resultFilms.add(film);
    //    }
    //
    //    private Rating getRating(HtmlPage page) {
    //        List<Object> byXPath = page.getByXPath("//span[@class=\"b-post__info_rates imdb\"]");
    //        if (byXPath.isEmpty()) {
    //            return null;
    //        }
    //        HtmlSpan ratingRow = (HtmlSpan) byXPath.get(0);
    //        float rating = Float.parseFloat(((DomText) ratingRow.getByXPath("./span/text()").get(0)).getWholeText());
    //        String count = ((DomText) ratingRow.getByXPath("./i/text()").get(0)).getWholeText();
    //        int votes = Integer.parseInt(count.substring(1, count.length() - 1).replaceAll("\\s+", ""));
    //        return new Rating(rating, votes);
    //    }
    //
    //    private String getOriginalTitle(HtmlPage moviePage) {
    //        return ((DomText) moviePage.getByXPath("//div[@class=\"b-post__origtitle" + "\"]/text()")
    //                .get(0)).getWholeText();
    //    }
}