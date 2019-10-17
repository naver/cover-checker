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
package com.naver.nid.cover;

import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.github.GithubPullRequestManager;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.diff.*;
import com.naver.nid.cover.parser.diff.model.Diff;
import com.naver.nid.cover.reporter.Reporter;
import com.naver.nid.cover.util.ObjectFactory;
import com.naver.nid.cover.util.Parameter;
import com.naver.nid.cover.util.ParameterParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public final class CoverChecker {

	private final GithubPullRequestManager prManager;
	private final CoverageReportParser coverageParser;
	private final DiffParser diffParser;
	private final NewCoverageChecker checker;
	private final Reporter reporter;

	public CoverChecker(ObjectFactory objectManager) {
		this(objectManager.getPrManager(),
				objectManager.getCoverageReportParser(),
				objectManager.getDiffParser(),
				objectManager.getNewCoverageParser(),
				objectManager.getReporter());
	}

	public boolean check(Parameter param) {
		try {
			RawDiffReader diffReader = createDiffReader(param);

			log.info("Check new line of code coverage by {}", coverageParser.getClass().getSimpleName());
			CompletableFuture<List<FileCoverageReport>> coverage = param.getCoveragePath().stream()
				.map(s -> executeByBackground((Function<String, List<FileCoverageReport>>) coverageParser::parse).apply(s))
				.reduce((f1, f2) -> f1.thenCombine(f2, (r1, r2) -> Stream.concat(r1.stream(), r2.stream()).collect(Collectors.toList())))
				.orElseThrow(() -> new IllegalStateException(""));

			log.info("read diff by {}", diffReader.getClass().getSimpleName());
			CompletableFuture<List<Diff>> diff = executeByBackground(diffParser::parse)
					.apply(diffReader)
					.thenApplyAsync(s -> s.collect(Collectors.toList()));

			NewCoverageCheckReport check = checker.check(coverage.join(), diff.join(), param.getThreshold(), param.getFileThreshold());

			reporter.report(check);
			log.info("check result {}", check.result());
			return true;
		} catch (Exception e) {
			NewCoverageCheckReport failResult = NewCoverageCheckReport.builder()
					.error(e)
					.build();

			reporter.report(failResult);
			return false;
		}
	}

	private <P, R> Function<P, CompletableFuture<R>> executeByBackground(Function<P, R> execute) {
		return (P param) -> CompletableFuture.supplyAsync(() -> execute.apply(param));
	}

	RawDiffReader createDiffReader(Parameter param) {
		if ("file".equals(param.getDiffType())) {
			return new FileRawDiffReader(param.getDiffPath());
		} else {
			return new GithubRawDiffReader(prManager);
		}
	}

	public static void main(String[] args) {
		Parameter param = new ParameterParser().getParam(args);
		if (param == null) return;

		new CoverChecker(new ObjectFactory(param)).check(param);
	}

}
