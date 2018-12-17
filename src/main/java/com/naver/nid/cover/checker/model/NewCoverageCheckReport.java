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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCoverageCheckReport {
	private int totalNewLine;
	private int coveredNewLine;
	private int threshold;
	private String error;
	private List<NewCoveredFile> coveredFilesInfo;

	public void setFileThreshold(int threshold) {
		Optional.ofNullable(coveredFilesInfo).ifPresent(info -> info.forEach(f -> f.setThreshold(threshold)));
	}

	/**
	 * 전체 테스트 커버리지 비율 계산
	 *
	 * @return 테스트 커버리지 비율
	 * <ul>
	 * <li>전체 수정 라인 대비 테스트로 커버 된 라인 수의 백분위</li>
	 * <li>전체 수정라인 & 테스트된 라인이 없을 경우 설정파일이나 리소스파일 수정으로 보고 무조건 성공으로 취급</li>
	 * <li>그 밖 에 모든경우는 실패로 처리</li>
	 * </ul>
	 */
	public int getCoverage() {
		if (totalNewLine != 0) {
			return Math.floorDiv(coveredNewLine * 100, totalNewLine); // 백분위
		} else if (totalNewLine == coveredNewLine) { // 변경은 있지만 자바소스코드가 아닌 경우
			return 100;
		} else {
			return 0;
		}
	}

	public NewCoverageCheckResult result() {
		if (error != null) {
			return NewCoverageCheckResult.ERROR;
		}

		int totCover = getCoverage();
		if(totCover == 100) return NewCoverageCheckResult.PASS;
		boolean totalCoverage = totCover >= threshold;
		boolean fileCoverage = Optional.ofNullable(coveredFilesInfo).map(files -> files.stream().allMatch(NewCoveredFile::isPass)).orElse(false);

		if (!totalCoverage) {
			return NewCoverageCheckResult.FAIL;
		} else if (fileCoverage) {
			return NewCoverageCheckResult.PASS;
		} else {
			return NewCoverageCheckResult.CHECK;
		}
	}
}
