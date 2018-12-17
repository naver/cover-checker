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
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * 파일 타입에 따라 내부 파서를 분기해서 사용
 */
@Slf4j
public class JacocoReportParser implements CoverageReportParser {

	@Override
	public List<FileCoverageReport> parse(File reportFile) {
		String fileName = reportFile.getName();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		if (!reportFile.isDirectory() && "xml".equalsIgnoreCase(ext)) {
			log.debug("parse by xml report {}", fileName);
			return new XmlCoverageReportParser(new JacocoXmlReportHandler()).parse(reportFile);
		} else {
			log.debug("parse by html report {}", fileName);
			return new JacocoHtmlReportParser(f -> f.getName().endsWith(".java.html")).parse(reportFile);
		}
	}
}
