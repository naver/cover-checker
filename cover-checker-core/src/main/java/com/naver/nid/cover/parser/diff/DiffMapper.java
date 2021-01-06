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
package com.naver.nid.cover.parser.diff;

import com.naver.nid.cover.parser.diff.exception.ParseException;
import com.naver.nid.cover.parser.diff.model.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 1차로 파싱된 {@link RawDiff}를 {@link Diff}로 파싱
 * 단계를 나누어 공통으로 처리할 수 있는 부분을 분리
 *
 */
@Slf4j
public class DiffMapper implements Function<RawDiff, Diff> {
	private static final DiffMapper DEFAULT = new DiffMapper();
	public static DiffMapper getDefault() {
		return DEFAULT;
	}

	private static final Pattern NEW_FILE_COUNT_PATTERN = Pattern.compile("\\+([0-9]+),([0-9]+)");
	private static final Pattern SINGLE_LINE_FILE_COUNT_PATTERN = Pattern.compile("\\+([0-9]+)");
	private static final Pattern SECTION_START_PATTERN = Pattern.compile("@@ -([0-9]+),?([0-9]+)? \\+([0-9]+),?([0-9]+)? @@");

	@Override
	public Diff apply(RawDiff rawDiff) {
		Diff diff = new Diff();

		// set file name
		Optional<Path> fileName = Optional.ofNullable(rawDiff.getFileName()).map(this::removeNewFilePrefix);
		log.info("parse diff / {}", fileName);

		if (!fileName.isPresent()) {
			log.error("diff parse fail");
			rawDiff.getRawDiff().forEach(l -> log.error("{}", l));

			// 실패할 경우 다음 파일을 불러옴
			return null;
		}

		diff.setFileName(fileName.get());

		// set diff detail
		if (rawDiff.getType() == FileType.BINARY) {
			diff.setDiffSectionList(Collections.emptyList());
			return diff;
		}

		Optional<List<DiffSection>> diffSectionList = getDiffSectionListFromRawDiff(rawDiff);
		if (!diffSectionList.isPresent()) {
			log.error("unexpected diff type {}", rawDiff);
			return null;
		}

		diff.setDiffSectionList(diffSectionList.get());
		return diff;
	}

	private int getBodyLine(RawDiff rawDiff) {
		for (int i = 0; i < rawDiff.size(); i++) {
			String line = rawDiff.getRawDiffLine(i);
			if (isSectionStart(line)) {
				return i;
			}
		}

		return -1;
	}

	private boolean isSectionStart(String currLine) {
		Matcher matcher = SECTION_START_PATTERN.matcher(currLine.trim());
		return matcher.find();
	}

	private boolean isEof(String line) {
		return line.contains("\\ No newline at end of file");
	}

	private Path removeNewFilePrefix(String path) {
		if (path.startsWith("b/")) {
			path = path.replaceFirst("b/", "");
		}
		return Paths.get(path);
	}

	private Optional<List<DiffSection>> getDiffSectionListFromRawDiff(RawDiff rawDiff) {
		List<DiffSection> diffSectionList = new ArrayList<>();
		DiffSection section = null;

		// find modified lines
		int baseLineNum = -1;
		int lineCnt = -1;
		int bodyLine = getBodyLine(rawDiff);
		if (bodyLine == -1) {
			log.warn("unknown diff pattern {}", rawDiff.getRawDiff());
			return Optional.empty();
		}

		for (int i = bodyLine; i < rawDiff.size(); i++) {
			String currLine = rawDiff.getRawDiffLine(i);
			log.debug("parse line {}", currLine);
			if (isEof(currLine)) continue;
			if (isSectionStart(currLine)) {

				// get matcher which matched section pattern
				Optional<Matcher> matchedPattern = Stream.of(NEW_FILE_COUNT_PATTERN, SINGLE_LINE_FILE_COUNT_PATTERN)
						.map(p -> p.matcher(currLine))
						.filter(Matcher::find)
						.findFirst();

				// current line is not matched any start pattern
				if (!matchedPattern.isPresent()) {
					log.error("not expect pattern {}", currLine);
					return Optional.empty();
				}
				Matcher matcher = matchedPattern.get();
				baseLineNum = Integer.parseInt(matcher.group(1));
				lineCnt = 0;

				if (section != null) {
					diffSectionList.add(section);
				}
				section = new DiffSection();
			} else if (currLine.length() > 0) { // 완전히 빈 라인은 확인하지 않는다.
				Line line = Line.builder()
						.type(ModifyType.of(currLine.charAt(0)))
						.body(currLine.substring(1)) // + / - 제거
						.lineNumber(baseLineNum + lineCnt).build();
				if (section == null) {
					log.error("section parse first {}", currLine);
					throw new ParseException(new IllegalStateException("section not found"));
				}
				section.addLine(line);
				if (line.getType() != ModifyType.DEL) lineCnt++;
			}
		}

		if (section != null) {
			diffSectionList.add(section); // 마지막 섹션 추가
		}

		return Optional.of(diffSectionList);
	}

}
