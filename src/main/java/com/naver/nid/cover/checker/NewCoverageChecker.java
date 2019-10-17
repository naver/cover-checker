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
package com.naver.nid.cover.checker;

import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.checker.model.NewCoveredFile;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.coverage.model.LineCoverageReport;
import com.naver.nid.cover.parser.diff.model.Diff;
import com.naver.nid.cover.parser.diff.model.Line;
import com.naver.nid.cover.parser.diff.model.ModifyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>코드의 변경점과 coverage 리포트를 이용해 신규 코드에 대한 커버리지를 측정</p>
 *
 * <p>NewCoverageChecker calculate test code coverage that updated.</p>
 */
public class NewCoverageChecker {
	private static final Logger logger = LoggerFactory.getLogger(NewCoverageChecker.class);

	/**
	 * <p>coverage report 와 diff 를 확인하여 신규 코드의 테스트 코드의 커버 정도를 확인한다.</p>
	 *
	 * <p>The method Check will calculate test coverage for new code for compare test coverage report and code diff.</p>
	 *
	 * @param coverage      파일 커버리지 리포트 결과
	 * @param diff          구 버전 파일과 신 버전 파일의 차이
	 * @param threshold     커버지리 통과 조건
	 * @param fileThreshold 파일별 커버리지 통과 조건
	 * @return 전체 커버리지, 파일 별 커버리지
	 */
	public NewCoverageCheckReport check(List<FileCoverageReport> coverage, List<Diff> diff, int threshold, int fileThreshold) {
		Map<String, List<Line>> diffMap = diff.stream()
				.filter(d -> !d.getFileName().startsWith("src/test/java/"))
				.filter(d -> d.getFileName().endsWith(".java"))
				.filter(d -> !d.getDiffSectionList().isEmpty())
				.peek(d -> logger.debug("diff file {}", d.getFileName()))
				.collect(Collectors.toMap(Diff::getFileName
						, d -> d.getDiffSectionList().stream()
								.flatMap(s -> s.getLineList().stream())
								.filter(l -> l.getType() == ModifyType.ADD)
								.collect(Collectors.toList())
						, (u1, u2) -> Stream.concat(u1.stream(), u2.stream()).collect(Collectors.toList())));

		// TODO redesign for multi module
		Map<String, List<LineCoverageReport>> coverageMap = coverage.stream()
				.peek(r -> logger.debug("file coverage {}", r.getFileName()))
				.collect(Collectors.toMap(FileCoverageReport::getFileName
						, FileCoverageReport::getLineCoverageReportList
						, (u1, u2) -> Stream.concat(u1.stream(), u2.stream()).collect(Collectors.toList())));


		NewCoverageCheckReport result = combine(coverageMap, diffMap);
		result.setFileThreshold(fileThreshold);
		result.setThreshold(threshold);
		logger.debug("coverage {} threshold {}", result, threshold);
		return result;
	}

	/**
	 * combine coverage report & new line report to Coverage check report
	 *
	 * @param coverageReport line coverage report for each files
	 * @param newCodeLines   add line of code for each files
	 * @return new line of code coverage result
	 */
	private NewCoverageCheckReport combine(Map<String, List<LineCoverageReport>> coverageReport, Map<String, List<Line>> newCodeLines) {
		int totalAddLineCount = 0;
		int coveredLineCount = 0;

		Set<String> files = new HashSet<>(coverageReport.keySet());

		List<NewCoveredFile> coveredFileList = new ArrayList<>();
		for (String file : files) {
			// TODO 다른 모듈의 동일 패키지 동일 파일 이름일 경우에 대한 처리 필요

			// 코드 커버리지의 끝 경로가 같은 경우에 대해 검색
			List<Line> diffList = newCodeLines.entrySet().stream()
				.filter(e -> e.getKey().endsWith(file))
				.findFirst()
				.map(Map.Entry::getValue)
				.orElse(Collections.emptyList());

			if (diffList.isEmpty()) {
				logger.debug("file({}) is not changed", file);
				continue;
			}

			List<LineCoverageReport> lineCoverageReports = coverageReport.get(file);

			logger.debug("check file {}", file);

			Map<Integer, CoverageStatus> lineCoverStatus = lineCoverageReports.stream()
					.collect(Collectors.toMap(LineCoverageReport::getLineNum, LineCoverageReport::getStatus
							, (s1, s2) -> s1.order < s2.order ? s2 : s1));

			// 추가된 라인 수
			List<Integer> addedLineNumber = diffList.stream().filter(l -> l.getType() == ModifyType.ADD)
					.mapToInt(Line::getLineNumber)
					.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

			long nonTestedList = addedLineNumber.stream()
					.mapToInt(i -> i)
					.filter(i -> lineCoverStatus.get(i) == CoverageStatus.NOTHING
							|| lineCoverStatus.get(i) == null)
					.peek(l -> {
						if (logger.isDebugEnabled()) {
							logger.debug("{}", l);
						}
					})
					.count();

			// 전체 라인 중 실제 실행되는 코드가 아닌 경우 제외
			long currTotalAddLineCount = addedLineNumber.size() - nonTestedList;
			// 테스트 된 라인 수
			long currCoveredLineCount = addedLineNumber.stream().mapToInt(i -> i)
					.filter(i -> lineCoverStatus.get(i) == CoverageStatus.COVERED
							|| lineCoverStatus.get(i) == CoverageStatus.CONDITION).count();

			if (currTotalAddLineCount == 0) { // 단순 필드 변경의 경우 결과 목록에 넣지 않는다.
				continue;
			}
			coveredFileList.add(NewCoveredFile.builder()
					.name(file)
					.addedLine((int) currTotalAddLineCount)
					.addedCoverLine((int) currCoveredLineCount).build());

			totalAddLineCount += currTotalAddLineCount;
			coveredLineCount += currCoveredLineCount;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("result total add line {} , covered line {}", totalAddLineCount, coveredLineCount);
		}

		// sort by file coverage
		coveredFileList.sort(Comparator.comparingInt(f -> Math.floorDiv(100 * f.getAddedCoverLine(), f.getAddedLine())));

		return NewCoverageCheckReport.builder()
				.coveredNewLine(coveredLineCount)
				.totalNewLine(totalAddLineCount)
				.coveredFilesInfo(coveredFileList)
				.build();
	}

}
