package org.suggester.models;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.suggester.util.FilmComparator;

@Suite
@SelectClasses({FilmTest.class, ArchiveSuggesterTest.class, AbstractSuggesterTest.class})
public class AllTests {

}
