package org.parser.models;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({FilmTest.class, ArchiveParserTest.class})
public class TestAll {

}
