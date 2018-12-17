package com.naver.nid.cover.parser.coverage.jacoco;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacocoReportParserTest {

	@Test
	public void testJacocoHtmlParserTest() {
		new JacocoHtmlReportParserTest().htmlFileParsingTest(new JacocoReportParser());
		new JacocoHtmlReportParserTest().htmlDirectoryParsing(new JacocoReportParser());
	}

	@Test
	public void testJacocoXmlParserTest() {
		new JacocoXmlReportParserTest().xmlFileParseTest(new JacocoReportParser());
	}

}