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
package com.naver.nid.cover.checker.model;

import lombok.Builder;
import lombok.Data;

import static com.naver.nid.cover.checker.model.ResultIcon.CHECK_FILE_FAIL;
import static com.naver.nid.cover.checker.model.ResultIcon.CHECK_FILE_PASS;

@Data
@Builder
public class NewCoveredFile {

	private String name;
	private int addedLine;
	private int addedCoverLine;
	private int threshold;

	public int getCoverage() {
		if(addedLine == 0) return 0;
		return Math.floorDiv(100 * addedCoverLine, addedLine);
	}

	public boolean isPass() {
		return getCoverage() >= threshold;
	}

	public String getIcon() {
		return isPass()? CHECK_FILE_PASS : CHECK_FILE_FAIL;
	}
}
