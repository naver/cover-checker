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
package com.naver.nid.cover.checker.util;


import com.naver.nid.cover.checker.model.NewCoverageCheckResult;
import com.naver.nid.cover.github.model.CommitStatusCreate;

public class ResultUtils {

	public static CommitStatusCreate.State resultToGithubState(NewCoverageCheckResult result) {
		switch (result) {
			case CHECK: return CommitStatusCreate.State.pending;
			case PASS: return CommitStatusCreate.State.success;
			case FAIL: return CommitStatusCreate.State.failure;
			default: return CommitStatusCreate.State.error;
		}
	}
}
