package com.naver.nid.cover.checker;

import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.checker.model.NewCoveredFile;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.coverage.model.LineCoverageReport;
import com.naver.nid.cover.parser.diff.model.Diff;
import com.naver.nid.cover.parser.diff.model.DiffSection;
import com.naver.nid.cover.parser.diff.model.Line;
import com.naver.nid.cover.parser.diff.model.ModifyType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewCoverageCheckerTest {

	@Test
	public void coverCheckTest() {
		NewCoverageChecker checker = new NewCoverageChecker();

		List<Line> lines = Arrays.asList(
				Line.builder().lineNumber(1).type(ModifyType.ADD).build()
				, Line.builder().lineNumber(2).type(ModifyType.ADD).build());

		List<DiffSection> diffSectionList = Collections.singletonList(DiffSection.builder().lineList(lines).build());
		List<Diff> diffList = Collections.singletonList(Diff.builder().fileName("test.java").diffSectionList(diffSectionList).build());


		LineCoverageReport lineCoverageReport = new LineCoverageReport();
		lineCoverageReport.setStatus(CoverageStatus.COVERED);
		lineCoverageReport.setLineNum(1);

		LineCoverageReport lineCoverageReport2 = new LineCoverageReport();
		lineCoverageReport2.setStatus(CoverageStatus.UNCOVERED);
		lineCoverageReport2.setLineNum(2);

		FileCoverageReport fileCoverageReport = new FileCoverageReport();
		fileCoverageReport.setType("java");
		fileCoverageReport.setFileName("test.java");
		fileCoverageReport.setLineCoverageReportList(Arrays.asList(lineCoverageReport, lineCoverageReport2));
		List<FileCoverageReport> coverage = Collections.singletonList(fileCoverageReport);

		NewCoverageCheckReport newCoverageCheckReport = NewCoverageCheckReport.builder()
				.threshold(60)
				.totalNewLine(2)
				.coveredNewLine(1)
				.coveredFilesInfo(
						Collections.singletonList(NewCoveredFile.builder()
								.name("test.java")
								.addedLine(2)
								.addedCoverLine(1)
								.build()))
				.build();

		NewCoverageCheckReport check = checker.check(coverage, diffList, 60, 0);
		assertEquals(newCoverageCheckReport, check);

	}

}