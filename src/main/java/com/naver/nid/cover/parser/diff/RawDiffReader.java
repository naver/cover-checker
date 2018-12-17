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

import com.naver.nid.cover.parser.diff.model.RawDiff;

import java.util.Iterator;

/**
 * Diff 정보를 읽어오는 인터페이스
 * 다양한 diff 정보를 내부에서 처리할 RawDiff로 변환하여 반환한다.
 */
public interface RawDiffReader extends Iterator<RawDiff> {

	/**
	 * 현재 iterator를 {@link Iterable}로 감싼다.
	 * iterable의 여러 기능 ({@code foreach}, {@link java.util.Spliterator} 생성)을 사용할 수 있다.
	 *
	 * @return
	 */
	default Iterable<RawDiff> toIterable() {
		return () -> this;
	}
}
