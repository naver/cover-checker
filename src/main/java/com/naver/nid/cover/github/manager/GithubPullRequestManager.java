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
package com.naver.nid.cover.github.manager;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;

public class GithubPullRequestManager {

	private final GitHubClient ghClient;
	private final RepositoryId repoId;
	private final PullRequest pr;
	private PullRequestService prService;
	private int prNumber;

	public GithubPullRequestManager(String host, String oauthToken, String repoName, int prNumber) {
		this(new GitHubClient(host).setOAuth2Token(oauthToken), repoName, prNumber);
	}

	public GithubPullRequestManager(GitHubClient github, String repoName, int prNumber) {
		this.ghClient = github;
		this.prNumber = prNumber;

		String[] repoNames = repoName.split("/");
		repoId = new RepositoryId(repoNames[0], repoNames[1]);

		prService = new PullRequestService(ghClient);
		try {
			pr = prService.getPullRequest(repoId, this.prNumber);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public User getUser() throws IOException {
		return new UserService(ghClient).getUser();
	}

	public GithubCommentManager commentManager() {
		return new GithubCommentManager(new IssueService(ghClient), repoId, pr.getNumber());
	}

	public GithubStatusManager statusManager() {
		return new GithubStatusManager(new CommitService(ghClient), repoId, pr.getHead().getSha());
	}

	public GithubDiffManager diffManager() {
		return new GithubDiffManager(prService, repoId, prNumber);
	}
}
