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
package com.naver.nid.cover.github;

import com.naver.nid.cover.github.model.CommitStatusCreate;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CommitService;

@Slf4j
public class GithubStatusManager {

	private final CommitService commitService;
	private RepositoryId repoId;
	private String sha;

	GithubStatusManager(CommitService service, RepositoryId repoId, String sha) {
		commitService = service;
		this.repoId = repoId;
		this.sha = sha;
	}

	public void setStatus(CommitStatusCreate status) {
		String url = String.format("/repos/%s/statuses/%s", repoId.generateId(), sha);
		try {
			commitService.createStatus(repoId, sha, status.toCommitStatus());
		} catch (Exception e) {
			log.warn("error while set status({}) {}", url, status, e);
		}
	}
}
