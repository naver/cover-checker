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
package com.naver.nid.cover.reporter;

import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.checker.model.NewCoverageCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleReporter implements Reporter {
	private static final Logger logger = LoggerFactory.getLogger(ConsoleReporter.class);

	@Override
	public void report(NewCoverageCheckReport result) {
		logger.info("coverage check result");
		if (result.result() != NewCoverageCheckResult.ERROR) {
			logger.info("{}/{} {}", result.getCoveredNewLine(), result.getTotalNewLine(), result.getCoverage());

			result.getCoveredFilesInfo().stream()
					.filter(f -> f.getAddedLine() > 0)
					.forEach(f -> logger.info("{} {}/{} {}", f.getName(), f.getAddedCoverLine(), f.getAddedLine(), f.getCoverage()));
		} else {
			logger.error("result error occurred", result.getError());
		}
	}
}
