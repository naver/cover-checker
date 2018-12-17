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
package com.naver.nid.cover.parser.diff.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RawDiff {
	public static final RawDiff END_OF_DIFF = RawDiff.builder().build();

	private String fileName;
	private FileType type;
	private List<String> rawDiff;

	public static boolean isFinish(RawDiff rd) {
		if (rd == null) return true;
		return rd == END_OF_DIFF;
	}

	public int size() {
		if (isFinish(this)) return 0;
		return rawDiff.size();
	}

	public String getRawDiffLine(int i) {
		return rawDiff.get(i);
	}
}
