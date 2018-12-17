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
import com.naver.nid.cover.parser.diff.model.FileType;
import com.naver.nid.cover.parser.diff.model.RawDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * diff 목록 중 하나의 파일만을 읽는다.
 * <p>
 * Thread non-safe
 */
public class FileRawDiffReader implements RawDiffReader {
	private static final Logger logger = LoggerFactory.getLogger(FileRawDiffReader.class);

	private BufferedReader reader;
	private String beforeHeader;

	public FileRawDiffReader(String path) {
		try {
			this.reader = new BufferedReader(new FileReader(path));
			beforeHeader = reader.readLine();
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	public FileRawDiffReader(BufferedReader reader) {
		try {
			this.reader = reader;
			beforeHeader = reader.readLine();
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	@Override
	public boolean hasNext() {
		return beforeHeader != null;
	}

	@Override
	public RawDiff next() {
		try {
			List<String> list = new ArrayList<>();
			if (!hasNext()) return RawDiff.END_OF_DIFF; // 완료 됨

			String[] parsed = beforeHeader.split(" ");
			RawDiff.RawDiffBuilder builder = RawDiff.builder().fileName(parsed[parsed.length-1]);

			String temp = reader.readLine();
			logger.debug("readline : {}", temp);
			while (!temp.contains("diff --git ")) {
				list.add(temp);
				temp = reader.readLine();
				logger.debug("readline : {}", temp);
				if (temp == null) {
					break;
				}
			}
			beforeHeader = temp;
			builder.type(list.stream().anyMatch(l -> l.contains("Binary files"))? FileType.BINARY: FileType.SOURCE);
			return builder.rawDiff(list).build();
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}
}
