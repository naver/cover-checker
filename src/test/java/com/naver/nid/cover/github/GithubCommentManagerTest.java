package com.naver.nid.cover.github;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GithubCommentManagerTest {

	private int prNum = 1;
	private IssueService mockIssue;
	private RepositoryId repositoryId;

	@BeforeEach
	public void init() {
		mockIssue = mock(IssueService.class);
		repositoryId = new RepositoryId("test", "test");
	}

	@Test
	public void deleteComment() throws IOException {
		when(mockIssue.getComments(repositoryId, prNum))
				.thenReturn(Collections.singletonList(new Comment().setId(1).setBody("test")
						.setUser(new User().setId(1))));
		doNothing().when(mockIssue)
				.deleteComment(repositoryId, 1);

		GithubCommentManager manager = new GithubCommentManager(mockIssue, repositoryId, prNum);


		assertEquals(1, manager.deleteComment(c -> c.getBody().equals("test")));
	}


	@Test
	public void insertComment() throws IOException {
		when(mockIssue.createComment(repositoryId, prNum, "test2")).thenReturn(new Comment().setBody("test2"));

		GithubCommentManager manager = new GithubCommentManager(mockIssue, repositoryId, prNum);

		manager.addComment("test2");

		verify(mockIssue).createComment(repositoryId, prNum, "test2");
	}
}