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

/**
 * 결과 icon 을 정의한 상수 클래스
 */
public class ResultIcon {
	public static final String CHECK_FILE_PASS = ":large_blue_circle:";
	public static final String CHECK_FILE_FAIL = ":red_circle:";


	public static final String CHECK_ALL_PASS = ":heart_eyes:";
	public static final String CHECK_ALL_FAIL = ":disappointed:";
	public static final String CHECK_ALL_CONFUSE = ":confused:";
	public static final String CHECK_ALL_ERROR = ":fearful:";


	private ResultIcon() {}
}
