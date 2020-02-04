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
package com.naver.nid.cover.github.manager.model;

import com.naver.nid.cover.checker.model.CommitState;
import lombok.Builder;
import lombok.Data;
import org.eclipse.egit.github.core.CommitStatus;

@Data
@Builder(builderClassName = "Builder")
public class CommitStatusCreate {
	private final CommitState state;
	private String targetUrl;
	private String description;
	private String context;

	public String getDescription() {
		if (description.length() > 135) {
			return description.substring(0, 132) + "...";
		} else {
			return description;
		}
	}

	public CommitStatus toCommitStatus() {
		return new CommitStatus()
			.setContext(getContext())
			.setDescription(getDescription())
			.setState(getState().toApiValue())
			.setTargetUrl(getTargetUrl());
	}
}
