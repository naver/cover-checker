package com.naver.nid.cover.parser.coverage.cobertura;

import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoberturaXmlReportParserTest {

	@Test
	public void parseCobertura() {
		CoverageReportParser parser = new XmlCoverageReportParser(new CoberturaReportHandler());

		List<FileCoverageReport> parsed = parser.parse(getClass().getClassLoader().getResource("reports/coverage.xml"));
		assertEquals(1, parsed.size());
		assertSame(CoverageStatus.COVERED, parsed.stream()
				.filter(r -> r.getFileName().contains("CoberturaReportHandler"))
				.findFirst().flatMap(r -> r.getLineCoverageReportList().stream()
						.filter(lr -> lr.getLineNum() == 68).findFirst()).orElseThrow(AssertionError::new).getStatus());

	}

}