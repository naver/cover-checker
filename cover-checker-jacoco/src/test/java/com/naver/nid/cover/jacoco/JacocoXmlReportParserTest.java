package com.naver.nid.cover.jacoco;

import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class JacocoXmlReportParserTest {

	@Test
	public void testJacocoXmlParse() {
		xmlFileParseTest(new XmlCoverageReportParser(new JacocoXmlCoverageReportHandler()));
	}

	void xmlFileParseTest(CoverageReportParser parser) {
		List<FileCoverageReport> parsed = parser.parse(getClass().getClassLoader().getResource("reports/jacoco.xml"));
		assertEquals(38, parsed.size());
		assertSame(CoverageStatus.COVERED, parsed.stream()
				.filter(r -> r.getFileName().endsWith("Parameter.java"))
				.findFirst().flatMap(r -> r.getLineCoverageReportList().stream()
						.filter(lr -> lr.getLineNum() == 8).findFirst()).orElseThrow(AssertionError::new).getStatus());
	}
}
