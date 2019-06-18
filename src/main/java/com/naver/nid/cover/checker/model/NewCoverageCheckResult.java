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

import com.naver.nid.cover.github.model.CommitState;

import static com.naver.nid.cover.checker.model.ResultIcon.*;

public enum NewCoverageCheckResult {
	PASS(CHECK_ALL_PASS, CommitState.SUCCESS),
	FAIL(CHECK_ALL_FAIL, CommitState.FAILURE),
	CHECK(CHECK_ALL_CONFUSE, CommitState.PENDING),
	ERROR(CHECK_ALL_ERROR, CommitState.ERROR);

	public final String icon;
	public final CommitState githubState;

	NewCoverageCheckResult(String icon, CommitState githubState) {
		this.icon = icon;
		this.githubState = githubState;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
