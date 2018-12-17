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

import com.naver.nid.cover.github.GithubDiffManager;
import com.naver.nid.cover.github.GithubPullRequestManager;
import com.naver.nid.cover.parser.diff.exception.ParseException;
import com.naver.nid.cover.parser.diff.model.FileType;
import com.naver.nid.cover.parser.diff.model.RawDiff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.CommitFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class GithubRawDiffReader implements RawDiffReader {

	private final GithubDiffManager diffManager;

	private Iterator<CommitFile> files;

	public GithubRawDiffReader(GithubPullRequestManager prManager) {
		this.diffManager = prManager.diffManager();
	}

	@Override
	public boolean hasNext() {
		if (files == null) {
			try {
				files = diffManager.getFiles().iterator();
			} catch (IOException e) {
				throw new ParseException("error while get file patch list", e);
			}
		}
		return files.hasNext();
	}

	@Override
	public RawDiff next() {
		CommitFile file = files.next();
		log.info("get file {}", file.getFilename());
		List<String> lines;
		if (file.getPatch() != null && file.getPatch().length() > 0) {
			lines = Arrays.asList(file.getPatch().split("\r?\n"));
		} else {
			lines = Collections.emptyList();
		}
		return RawDiff.builder()
				.fileName(file.getFilename())
				.rawDiff(lines)
				.type(file.getPatch() != null ? FileType.SOURCE : FileType.BINARY)
				.build();
	}

}
