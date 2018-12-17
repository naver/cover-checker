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
package com.naver.nid.cover.parser.coverage.model;

/**
 * 라인 테스트 커버 상태
 *
 */
public enum CoverageStatus {
	NOTHING(0), COVERED(1), UNCOVERED(2), CONDITION(3);

	/**
	 * 우선순위 같은 라인에 두 상태가 있을 경우 어떤 상태로 처리 될 것인지를 나타내는 플래그
	 */
	public final int order;

	CoverageStatus(int order) {
		this.order = order;
	}
}
