package com.naver.nid.cover.jacoco;

import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;

@Slf4j
class JacocoHtmlReportParserTest {

	@Test
	public void htmlFileParsing() {
		htmlFileParsingTest(new JacocoHtmlReportParser(file -> file.getName().endsWith(".java.html")));
	}

	void htmlFileParsingTest(CoverageReportParser parser) {
		URL diffUrl = getClass().getClassLoader().getResource("reports/html/com.naver.nid.cover.github/GithubCommentManager.java.html");

		log.debug("diffUrl {}", diffUrl.toString());
		List<FileCoverageReport> parse = parser.parse(diffUrl);

		log.debug("{}", parse);

		FileCoverageReport fileCoverageReport = parse.get(0);
		Assertions.assertEquals("com/naver/nid/cover/github/GithubCommentManager.java", fileCoverageReport.getFileName());
		Assertions.assertEquals("java", fileCoverageReport.getType());
		Assertions.assertEquals(CoverageStatus.COVERED, fileCoverageReport.getLineCoverageReportList().stream().filter(l -> l.getLineNum() == 21).findFirst().orElseThrow(AssertionError::new).getStatus());
	}

	@Test
	public void directoryParsing() {
		htmlDirectoryParsing(new JacocoHtmlReportParser(file -> file.getName().endsWith(".java.html")));
	}

	void htmlDirectoryParsing(CoverageReportParser parser) {
		URL reportUrl = getClass().getClassLoader().getResource("reports");
		log.debug("diffUrl {}", reportUrl);
		List<FileCoverageReport> reports = parser.parse(reportUrl);


		log.debug("{}", reports);

		FileCoverageReport fileCoverageReport = reports.get(0);
		Assertions.assertEquals("com/naver/nid/cover/github/GithubCommentManager.java", fileCoverageReport.getFileName());
		Assertions.assertEquals("java", fileCoverageReport.getType());
		Assertions.assertEquals(CoverageStatus.COVERED, fileCoverageReport.getLineCoverageReportList().stream().filter(l -> l.getLineNum() == 21).findFirst().orElseThrow(AssertionError::new).getStatus());
	}
}