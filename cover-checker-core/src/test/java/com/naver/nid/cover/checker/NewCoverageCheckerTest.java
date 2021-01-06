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

import java.nio.file.Paths;
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
        List<Diff> diffList = Collections.singletonList(Diff.builder().fileName(Paths.get("test.java")).diffSectionList(diffSectionList).build());


        LineCoverageReport lineCoverageReport = new LineCoverageReport();
        lineCoverageReport.setStatus(CoverageStatus.COVERED);
        lineCoverageReport.setLineNum(1);

        LineCoverageReport lineCoverageReport2 = new LineCoverageReport();
        lineCoverageReport2.setStatus(CoverageStatus.UNCOVERED);
        lineCoverageReport2.setLineNum(2);

        FileCoverageReport fileCoverageReport = new FileCoverageReport();
        fileCoverageReport.setType("java");
        fileCoverageReport.setFileName(Paths.get("test.java"));
        fileCoverageReport.setLineCoverageReportList(Arrays.asList(lineCoverageReport, lineCoverageReport2));
        List<FileCoverageReport> coverage = Collections.singletonList(fileCoverageReport);

        NewCoverageCheckReport newCoverageCheckReport = NewCoverageCheckReport.builder()
                .threshold(60)
                .totalNewLine(2)
                .coveredNewLine(1)
                .coveredFilesInfo(
                        Collections.singletonList(NewCoveredFile.builder()
                                .name(Paths.get("test.java"))
                                .addedLine(2)
                                .addedCoverLine(1)
                                .build()))
                .build();

        NewCoverageCheckReport check = checker.check(coverage, diffList, 60, 0);
        assertEquals(newCoverageCheckReport, check);

    }

    @Test
    public void coverCheckTestForNonJava() {
        NewCoverageChecker checker = new NewCoverageChecker();

        List<Line> lines = Arrays.asList(
                Line.builder().lineNumber(1).type(ModifyType.ADD).build()
                , Line.builder().lineNumber(2).type(ModifyType.ADD).build());

        List<DiffSection> diffSectionList = Collections.singletonList(DiffSection.builder().lineList(lines).build());
        List<Diff> diffList = Collections.singletonList(Diff.builder().fileName(Paths.get("test.kt")).diffSectionList(diffSectionList).build());


        LineCoverageReport lineCoverageReport = new LineCoverageReport();
        lineCoverageReport.setStatus(CoverageStatus.COVERED);
        lineCoverageReport.setLineNum(1);

        LineCoverageReport lineCoverageReport2 = new LineCoverageReport();
        lineCoverageReport2.setStatus(CoverageStatus.UNCOVERED);
        lineCoverageReport2.setLineNum(2);

        FileCoverageReport fileCoverageReport = new FileCoverageReport();
        fileCoverageReport.setType("java");
        fileCoverageReport.setFileName(Paths.get("test.kt"));
        fileCoverageReport.setLineCoverageReportList(Arrays.asList(lineCoverageReport, lineCoverageReport2));
        List<FileCoverageReport> coverage = Collections.singletonList(fileCoverageReport);

        NewCoverageCheckReport newCoverageCheckReport = NewCoverageCheckReport.builder()
                .threshold(60)
                .totalNewLine(2)
                .coveredNewLine(1)
                .coveredFilesInfo(
                        Collections.singletonList(NewCoveredFile.builder()
                                .name(Paths.get("test.kt"))
                                .addedLine(2)
                                .addedCoverLine(1)
                                .build()))
                .build();

        NewCoverageCheckReport check = checker.check(coverage, diffList, 60, 0);
        assertEquals(newCoverageCheckReport, check);
    }

    @Test
    public void coverCheckTestForMultiModule() {
        NewCoverageChecker checker = new NewCoverageChecker();

        List<Line> lines = Arrays.asList(
                Line.builder().lineNumber(1).type(ModifyType.ADD).build()
                , Line.builder().lineNumber(2).type(ModifyType.ADD).build());

        List<DiffSection> diffSectionList = Collections.singletonList(DiffSection.builder().lineList(lines).build());
        List<Diff> diffList = Arrays.asList(
                Diff.builder().fileName(Paths.get("Module1/src/main/kotlin/test.kt")).diffSectionList(diffSectionList).build(),
                Diff.builder().fileName(Paths.get("Module2/src/main/kotlin/test.kt")).diffSectionList(diffSectionList).build());


        LineCoverageReport lineCoverageReport = new LineCoverageReport();
        lineCoverageReport.setStatus(CoverageStatus.COVERED);
        lineCoverageReport.setLineNum(1);

        LineCoverageReport lineCoverageReport2 = new LineCoverageReport();
        lineCoverageReport2.setStatus(CoverageStatus.UNCOVERED);
        lineCoverageReport2.setLineNum(2);

        FileCoverageReport fileCoverageReport = new FileCoverageReport();
        fileCoverageReport.setType("kt");
        fileCoverageReport.setFileName(Paths.get("Module1/src/main/kotlin/test.kt"));
        fileCoverageReport.setLineCoverageReportList(Arrays.asList(lineCoverageReport, lineCoverageReport2));

        FileCoverageReport fileCoverageReport2 = new FileCoverageReport();
        fileCoverageReport2.setType("kt");
        fileCoverageReport2.setFileName(Paths.get("Module2/src/main/kotlin/test.kt"));
        fileCoverageReport2.setLineCoverageReportList(Arrays.asList(lineCoverageReport, lineCoverageReport2));

        List<FileCoverageReport> coverage = Arrays.asList(fileCoverageReport, fileCoverageReport2);

        NewCoverageCheckReport newCoverageCheckReport = NewCoverageCheckReport.builder()
                .threshold(60)
                .totalNewLine(4)
                .coveredNewLine(2)
                .coveredFilesInfo(
                        Arrays.asList(NewCoveredFile.builder()
                                        .name(Paths.get("Module2/src/main/kotlin/test.kt"))
                                        .addedLine(2)
                                        .addedCoverLine(1)
                                        .build(),
                                NewCoveredFile.builder()
                                        .name(Paths.get("Module1/src/main/kotlin/test.kt"))
                                        .addedLine(2)
                                        .addedCoverLine(1)
                                        .build()))
                .build();

        NewCoverageCheckReport check = checker.check(coverage, diffList, 60, 0);
        assertEquals(newCoverageCheckReport, check);
    }
}