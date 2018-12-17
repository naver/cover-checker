package com.naver.nid.cover.github;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubDiffManagerTest {

	@Mock
	private PullRequestService mockPrService;

	@Test
	public void getFileDiff() throws IOException {
		RepositoryId repoId = new RepositoryId("test", "test");
		GithubDiffManager githubDiffManager = new GithubDiffManager(mockPrService, repoId, 1);
		List<CommitFile> result = new Gson()
				.fromJson(new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("github_diff.diff")))
						, new TypeToken<List<CommitFile>>() {}.getType());

		when(mockPrService.getFiles(repoId, 1)).thenReturn(result);

		assertEquals(result, githubDiffManager.getFiles());

	}
}