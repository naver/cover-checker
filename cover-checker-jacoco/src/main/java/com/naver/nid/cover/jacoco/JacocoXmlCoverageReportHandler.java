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
package com.naver.nid.cover.jacoco;

import com.naver.nid.cover.parser.coverage.CoverageReportXmlHandler;
import com.naver.nid.cover.parser.coverage.model.CoverageStatus;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.parser.coverage.model.LineCoverageReport;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JacocoXmlCoverageReportHandler extends CoverageReportXmlHandler {

	private String pkgPath;
	private Map<String, FileCoverageReport> reportMap = new HashMap<>();
	private FileCoverageReport currentFile;
	private List<LineCoverageReport> lineReports;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (localName.equals("")) localName = qName;
		switch (localName) {
			case "package":
				pkgPath = attributes.getValue("name");
				log.debug("found new package {}", pkgPath);
				break;
			case "sourcefile":
				currentFile = new FileCoverageReport();
				String name = attributes.getValue("name");
				currentFile.setFileName(pkgPath + "/" + name);
				currentFile.setType(name.substring(name.lastIndexOf('.') + 1));
				log.debug("found new file {}", currentFile.getFileName());
				lineReports = new ArrayList<>();
				break;
			case "line":
				LineCoverageReport lineReport = new LineCoverageReport();
				lineReport.setLineNum(Integer.parseInt(attributes.getValue("nr")));
				lineReport.setStatus(getLineStatus(attributes));
				lineReport.setLineContent("");
				log.debug("found new line {}[{}]", lineReport.getLineNum(), lineReport.getStatus());
				lineReports.add(lineReport);
				break;
			default:
				// ignore other tags
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("")) localName = qName;
		if (localName.equals("sourcefile")) {
			currentFile.setLineCoverageReportList(lineReports);
			reportMap.put(currentFile.getFileName(), currentFile);
			log.debug("save file map {}", currentFile.getFileName());
		}
	}

	private CoverageStatus getLineStatus(Attributes attributes) {
		int missInstruction = Integer.parseInt(attributes.getValue("mi"));
		int missBranch = Integer.parseInt(attributes.getValue("mb"));
		int coverBranch = Integer.parseInt(attributes.getValue("cb"));

		if (missInstruction == 0 && missBranch == 0) {
			return CoverageStatus.COVERED;
		} else if (missBranch > 0 && coverBranch > 0) {
			return CoverageStatus.CONDITION;
		}

		return CoverageStatus.UNCOVERED;
	}

	@Override
	public List<FileCoverageReport> getReports() {
		return new ArrayList<>(reportMap.values());
	}
}
