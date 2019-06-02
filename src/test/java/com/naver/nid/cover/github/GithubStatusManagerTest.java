package com.naver.nid.cover.github;

import com.naver.nid.cover.github.model.CommitState;
import com.naver.nid.cover.github.model.CommitStatusCreate;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CommitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GithubStatusManagerTest {

	private CommitService commitService;

	@BeforeEach
	public void init() {
		commitService = mock(CommitService.class);
	}

	@Test
	public void setStatus() throws IOException {
		CommitStatusCreate commitStatus = CommitStatusCreate.builder()
				.state(CommitState.SUCCESS)
				.description("0 / 0 (100%) - pass")
				.context("coverchecker").build();
		RepositoryId repoId = new RepositoryId("test", "test");
		when(commitService.createStatus(eq(repoId), eq("sha"), any(CommitStatus.class))).thenReturn(new CommitStatus().setDescription("0 / 0 (100%) - pass"));

		GithubStatusManager statusManager = new GithubStatusManager(commitService, repoId, "sha");

		statusManager.setStatus(commitStatus);

		verify(commitService).createStatus(eq(repoId), eq("sha"), any(CommitStatus.class));
	}

}