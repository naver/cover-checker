package com.naver.nid.cover.jacoco;

import org.junit.jupiter.api.Test;

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