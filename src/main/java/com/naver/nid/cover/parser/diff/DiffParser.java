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

import com.naver.nid.cover.parser.diff.model.Diff;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public final class DiffParser {

	private final DiffMapper diffMapper;

	public DiffParser() {
		this(DiffMapper.getDefault());
	}

	public DiffParser(DiffMapper diffMapper) {
		this.diffMapper = diffMapper;
		log.debug("init with mapper {}", diffMapper.getClass().getSimpleName());
	}

	public Stream<Diff> parse(RawDiffReader rawDiffReader) {
		return StreamSupport.stream(rawDiffReader.toIterable().spliterator(), false).map(diffMapper);
	}
}
