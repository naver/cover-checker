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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.util.List;

@Slf4j
public class GithubDiffManager {

	private PullRequestService prService;
	private RepositoryId repoId;
	private int prNumber;

	GithubDiffManager(PullRequestService prService, RepositoryId repoId, int prNumber) {
		this.prService = prService;
		this.repoId = repoId;
		this.prNumber = prNumber;
	}

	public List<CommitFile> getFiles() throws IOException {
		log.info("get diff {}/{}", repoId.generateId(), prNumber);
		return prService.getFiles(repoId, prNumber);
	}

}
