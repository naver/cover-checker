package com.naver.nid.cover;

import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.checker.model.NewCoveredFile;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.coverage.model.LineCoverageReport;
import com.naver.nid.cover.parser.diff.DiffParser;
import com.naver.nid.cover.parser.diff.model.Diff;
import com.naver.nid.cover.parser.diff.model.DiffSection;
import com.naver.nid.cover.parser.diff.model.Line;
import com.naver.nid.cover.parser.diff.model.ModifyType;
import com.naver.nid.cover.reporter.Reporter;
import com.naver.nid.cover.reporter.ConsoleReporter;
import com.naver.nid.cover.util.Parameter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CoverCheckerTest {

	private Reporter reporter = new ConsoleReporter();

	@Mock
	private DiffParser diffParser;

	@Mock
	private CoverageReportParser coverageReportParser;

	@Mock
	private NewCoverageChecker checker;

	@Test
	public void testCheck() {
		CoverChecker coverChecker = Mockito.spy(new CoverChecker(coverageReportParser, diffParser, checker, reporter));

		List<Line> lines = Arrays.asList(
				Line.builder().lineNumber(1).type(ModifyType.ADD).build()
				, Line.builder().lineNumber(2).type(ModifyType.ADD).build());

		List<DiffSection> diffSectionList = Collections.singletonList(DiffSection.builder().lineList(lines).build());
		Stream<Diff> diffStream = Stream.of(Diff.builder().fileName("test.java").diffSectionList(diffSectionList).build(),
			Diff.builder().fileName("test2.java").diffSectionList(diffSectionList).build());


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
		FileCoverageReport fileCoverageReport2 = new FileCoverageReport();
		fileCoverageReport.setType("java");
		fileCoverageReport.setFileName("test2.java");
		fileCoverageReport.setLineCoverageReportList(Arrays.asList(lineCoverageReport, lineCoverageReport2));
		List<FileCoverageReport> coverageModule1 = Collections.singletonList(fileCoverageReport);
		List<FileCoverageReport> coverageModule2 = Collections.singletonList(fileCoverageReport2);

		NewCoverageCheckReport newCoverageCheckReport = NewCoverageCheckReport.builder()
				.threshold(50)
				.totalNewLine(2)
				.coveredNewLine(1)
				.coveredFilesInfo(
						Collections.singletonList(NewCoveredFile.builder()
								.name("test.java")
								.addedLine(2)
								.addedCoverLine(1)
								.build()))
				.build();
		newCoverageCheckReport.setFileThreshold(30);
		List<FileCoverageReport> coverageList = Stream.concat(coverageModule1.stream(), coverageModule2.stream()).collect(Collectors.toList());

		List<Diff> diffList = diffStream.collect(Collectors.toList());
		doReturn(diffList.stream()).when(diffParser).parse();
		doReturn(coverageModule1).when(coverageReportParser).parse("test-module1");
		doReturn(coverageModule2).when(coverageReportParser).parse("test-module2");
		doReturn(newCoverageCheckReport).when(checker).check(coverageList, diffList, 50, 30);

		Parameter param = Parameter.builder()
				.coveragePath(null)
				.diffPath("/path")
				.coveragePath(Arrays.asList("test-module1", "test-module2"))
				.coverageType("jacoco")
				.githubToken("token")
				.githubUrl("enterprise.github.com")
				.prNumber(1)
				.threshold(50)
				.fileThreshold(30)
				.repo("test/test")
				.diffType("file")
				.build();

		assertTrue(coverChecker.check(param), "Coverchecker must finish successfully");

		verify(diffParser).parse();
		verify(coverageReportParser).parse("test-module1");
		verify(coverageReportParser).parse("test-module2");
		verify(checker).check(coverageList, diffList, 50, 30);
	}
}