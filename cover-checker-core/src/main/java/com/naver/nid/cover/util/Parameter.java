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
package com.naver.nid.cover.util;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
public class Parameter {
	private int threshold;
	private int fileThreshold;
	private String githubToken;
	private String diffPath;
	private List<String> coveragePath;
	private String githubUrl;
	private String repo;
	private String diffType;
	private int prNumber;
	private String coverageType;
}
