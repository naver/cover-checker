package com.naver.nid.cover.github;

import com.naver.nid.cover.github.model.CommitStatusCreate;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class GithubStatusManagerTest {

	private GitHubClient mockGithubClient;

	@BeforeEach
	public void init() {
		mockGithubClient = mock(GitHubClient.class);
	}

	@Test
	public void setStatus() throws IOException {
		CommitStatusCreate commitStatus = CommitStatusCreate.builder()
				.state(CommitStatusCreate.State.success)
				.description("0 / 0 (100%) - pass")
				.context("coverchecker").build();
		when(mockGithubClient.post("/repos/test/test/statuses/sha", commitStatus, CommitStatus.class)).thenReturn(new CommitStatus().setDescription("0 / 0 (100%) - pass"));

		GithubStatusManager statusManager = new GithubStatusManager(mockGithubClient, new RepositoryId("test", "test"), "sha");

		statusManager.setStatus(commitStatus);

		verify(mockGithubClient).post("/repos/test/test/statuses/sha", commitStatus, CommitStatus.class);
	}

}