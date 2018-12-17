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
package com.naver.nid.cover.parser.coverage;

import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.diff.exception.ParseException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public interface CoverageReportParser {

	List<FileCoverageReport> parse(File reportFile);

	default List<FileCoverageReport> parse(String reportPath) {
		return parse(new File(reportPath));
	}

	default List<FileCoverageReport> parse(URL reportPath) {
		try {
			return parse(new File(reportPath.toURI()));
		} catch (URISyntaxException e) {
			throw new ParseException(e);
		}
	}

}
