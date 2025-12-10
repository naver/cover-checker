package com.naver.nid.cover.lcov;

import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.coverage.model.LineCoverageReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LcovCoverageReportParser implements CoverageReportParser {

    private static final String TEST_START = "TN";
    private static final String FILENAME = "SF";
    private static final String ACCESS = "DA";

    @Override
    public List<FileCoverageReport> parse(File reportFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(reportFile))) {
            String line;

            String reportFilePath = reportFile.toPath().getFileName().toString();
            String fileType = reportFilePath.substring(reportFilePath.lastIndexOf('.') + 1);

            List<FileCoverageReport> reports = new ArrayList<>();
            FileCoverageReport report = new FileCoverageReport();

            while ((line = br.readLine()) != null) {
                int idx = line.indexOf(':');
                if (idx == -1 && line.equals("end_of_record")) {
                    reports.add(report);
                    continue;
                }
                String[] lineParsed = line.split(":", 2);
                String lineHeader = lineParsed[0];
                String lineValue = lineParsed.length < 2 ? "" : lineParsed[1];

                if (TEST_START.equals(lineHeader)) {
                    report = new FileCoverageReport();
                    report.setType(fileType);
                    report.setLineCoverageReportList(new ArrayList<>());
                } else if (FILENAME.equals(lineHeader)) {
                    report.setFileName(Paths.get(lineValue));
                } else if (ACCESS.equals(lineHeader)) {
                    int[] infos = Stream.of(lineValue.split(",", 2)).mapToInt(Integer::parseInt).toArray();
                    LineCoverageReport lReport = new LineCoverageReport();
                    lReport.setStatus(infos[1] > 0 ? CoverageStatus.COVERED : CoverageStatus.UNCOVERED);
                    lReport.setLineNum(infos[0]);
                    lReport.setLineContent("");
                    report.getLineCoverageReportList().add(lReport);
                }
            }

            return reports;
        } catch (IOException e) {
            throw new LcovFileNotFound(reportFile.getPath(), e);
        }
    }
}