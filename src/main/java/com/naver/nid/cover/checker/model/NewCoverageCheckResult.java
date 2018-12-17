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

import static com.naver.nid.cover.checker.model.ResultIcon.*;

public enum NewCoverageCheckResult {
	PASS(CHECK_ALL_PASS), FAIL(CHECK_ALL_FAIL), CHECK(CHECK_ALL_CONFUSE), ERROR(CHECK_ALL_ERROR);

	public final String icon;

	NewCoverageCheckResult(String icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
