/*
	Copyright 2018 NAVER Corp.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.naver.nid.cover.parser.coverage.jacoco;

import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.exception.ParseException;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.coverage.model.LineCoverageReport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * jacoco html report를 파싱
 */
public class JacocoHtmlReportParser implements CoverageReportParser {
	private static final Logger logger = LoggerFactory.getLogger(JacocoHtmlReportParser.class);

	private final Predicate<File> reportFileChecker;

	public JacocoHtmlReportParser(Predicate<File> reportExt) {
		reportFileChecker = reportExt;
	}

	@Override
	public List<FileCoverageReport> parse(File reportFile) {
		logger.debug("parse {}", reportFile.getAbsolutePath());
		if (reportFile.isFile()) {
			if (reportFileChecker.test(reportFile)) {
				return Collections.singletonList(parseFile(reportFile));
			} else {
				throw new ParseException(String.format("wrong file type %s", reportFile.getName()));
			}
		} else {
			return parseDirectory(reportFile);
		}
	}

	private List<FileCoverageReport> parseDirectory(File reportDirectory) {
		String dirPath = reportDirectory.getAbsolutePath();
		List<FileCoverageReport> reports = new ArrayList<>();
		try {
			Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					File currFile = file.toFile();
					if (reportFileChecker.test(currFile)) {
						reports.add(parseFile(currFile));
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new ParseException(e);
		}

		return reports;
	}

	private FileCoverageReport parseFile(File file) {
		try {
			Document doc = Jsoup.parse(file, "UTF-8");


			FileCoverageReport fileReport = new FileCoverageReport();

			String filePath = file.getCanonicalPath();
			logger.debug("parse {}", filePath);
			String[] split = filePath.split("/");
			String sourcePath = split[split.length - 2].replace(".", "/") + "/" + split[split.length - 1].replace(".html", "");
			fileReport.setFileName(sourcePath);
			fileReport.setType(file.getName().split("\\.")[1]);

			List<LineCoverageReport> lineCoverageReports = doc.select(".linenums span").stream()
					.flatMap(e -> e.getAllElements().stream())
					.peek(e -> logger.debug("curr elements {}", e.wholeText()))
					.map(e -> {
						LineCoverageReport lineCoverageReport = new LineCoverageReport();
						lineCoverageReport.setLineNum(Integer.parseInt(e.id().substring(1))); // L$(lineNum)
						lineCoverageReport.setLineContent(e.text());
						lineCoverageReport.setStatus(classNamesToStatus(e.classNames()));

						return lineCoverageReport;
					}).sorted(Comparator.comparingInt(LineCoverageReport::getLineNum))
					.collect(Collectors.toList());

			fileReport.setLineCoverageReportList(lineCoverageReports);

			return fileReport;
		} catch (IOException e) {
			throw new ParseException("error on file read", e);
		}
	}

	private CoverageStatus classNamesToStatus(Set<String> className) {
		if (className.contains("fc")) return CoverageStatus.COVERED;
		if (className.contains("nc")) return CoverageStatus.UNCOVERED;
		if (className.contains("pc")) return CoverageStatus.CONDITION;

		return CoverageStatus.NOTHING;
	}
}
