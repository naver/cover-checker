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

import com.naver.nid.cover.parser.coverage.exception.ParseException;
import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public final class XmlCoverageReportParser implements CoverageReportParser {

	private final ReportXmlHandler handler;

	@Override
	public List<FileCoverageReport> parse(File reportFile) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			saxParserFactory.setFeature("http://xml.org/sax/features/validation", false);
			saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			saxParserFactory.setValidating(false); // disable xml DTD check
			log.debug("parse {}", reportFile.getName());
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(reportFile, handler);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			log.debug("FACTORY: {}", factory);

			return handler.getReports();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ParseException(e);
		}
	}
}
