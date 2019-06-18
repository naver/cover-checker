package com.naver.nid.cover.reporter.github;

import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.checker.model.NewCoveredFile;
import com.naver.nid.cover.github.GithubCommentManager;
import com.naver.nid.cover.github.GithubPullRequestManager;
import com.naver.nid.cover.github.GithubStatusManager;
import com.naver.nid.cover.github.model.CommitState;
import com.naver.nid.cover.github.model.CommitStatusCreate;
import org.eclipse.egit.github.core.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GithubPullRequestReporterTest {

	private static final String COMMENT_WITH_FILE = "#### [PR Coverage check]\n" +
			"\n" +
			":heart_eyes: **pass** : 2 / 3 (66.67%)\n" +
			"\n" +
			"\n" +
			"\n\n" +
			"#### file detail\n" +
			"\n" +
			"|   |path|covered line|new line|coverage|\n" +
			"|----|----|----|----|----|\n" +
			"|:large_blue_circle:|test.java|50|100|50.00%|\n" +
			"|:large_blue_circle:|test2.java|50|100|50.00%|\n\n\n";

	private static final String COMMENT_WITH_CONFUSE = "#### [PR Coverage check]\n" +
			"\n" +
			":confused: **check** : 2 / 3 (66.67%)\n" +
			"\n" +
			"\n" +
			"\n\n" +
			"#### file detail\n" +
			"\n" +
			"|   |path|covered line|new line|coverage|\n" +
			"|----|----|----|----|----|\n" +
			"|:large_blue_circle:|test.java|50|100|50.00%|\n" +
			"|:red_circle:|test2.java|30|100|30.00%|\n\n\n";

	private static final String COMMENT_WITHOUT_FILE = "#### [PR Coverage check]\n" +
			"\n\n" +
			":heart_eyes: **pass** : 0 / 0 (0%)\n\n\n";

	private static final String COMMENT_ERROR = "#### [PR Coverage check]\n" +
			"\n" +
			"coverage check fail. please retry. :fearful:\n" +
			"\n" +
			"[Please let me know](https://github.com/naver/cover-checker/issues/new) when error again.\n" +
			"\n" +
			"test error";

	private GithubPullRequestManager mockPrManager;
	private GithubCommentManager mockCommentManager;
	private GithubStatusManager mockStatusManager;
	private User mockUser;

	@BeforeEach
	public void init() throws IOException {
		mockPrManager = mock(GithubPullRequestManager.class);
		mockCommentManager = mock(GithubCommentManager.class);
		mockStatusManager = mock(GithubStatusManager.class);

		when(mockPrManager.commentManager()).thenReturn(mockCommentManager);
		when(mockPrManager.statusManager()).thenReturn(mockStatusManager);
		mockUser = new User().setId(1);

		when(mockPrManager.getUser()).thenReturn(mockUser);
	}

	@Test
	public void reportTest() throws IOException {
		GithubPullRequestReporter reporter = new GithubPullRequestReporter(mockPrManager);
		when(mockCommentManager.deleteComment(reporter.oldReport(mockUser))).thenReturn(1);
		doNothing().when(mockCommentManager).addComment(COMMENT_WITH_FILE);

		CommitStatusCreate commitStatus = CommitStatusCreate.builder()
				.state(CommitState.SUCCESS)
				.description("2 / 3 (66%) - pass")
				.context("coverchecker").build();
		doNothing().when(mockStatusManager).setStatus(commitStatus);

		NewCoverageCheckReport result = NewCoverageCheckReport.builder()
				.totalNewLine(3)
				.coveredNewLine(2)
				.threshold(50)
				.coveredFilesInfo(Arrays.asList(NewCoveredFile.builder().name("test.java").addedLine(100).addedCoverLine(50).build(),
						NewCoveredFile.builder().name("test2.java").addedLine(100).addedCoverLine(50).build()))
				.build();

		reporter.report(result);

		verify(mockCommentManager).addComment(COMMENT_WITH_FILE);
		verify(mockStatusManager).setStatus(commitStatus);
	}


	@Test
	public void reportConfuseTest() throws IOException {
		GithubPullRequestReporter reporter = new GithubPullRequestReporter(mockPrManager);
		when(mockCommentManager.deleteComment(reporter.oldReport(mockUser))).thenReturn(1);
		doNothing().when(mockCommentManager).addComment(COMMENT_WITH_CONFUSE);

		CommitStatusCreate commitStatus = CommitStatusCreate.builder()
				.state(CommitState.PENDING)
				.description("2 / 3 (66%) - check")
				.context("coverchecker").build();
		doNothing().when(mockStatusManager).setStatus(commitStatus);

		NewCoverageCheckReport result = NewCoverageCheckReport.builder()
				.totalNewLine(3)
				.coveredNewLine(2)
				.threshold(50)
				.coveredFilesInfo(Arrays.asList(NewCoveredFile.builder().name("test.java").addedLine(100).addedCoverLine(50).build(),
						NewCoveredFile.builder().name("test2.java").addedLine(100).addedCoverLine(30).build()))
				.build();
		result.setFileThreshold(50);

		reporter.report(result);

		verify(mockCommentManager).addComment(COMMENT_WITH_CONFUSE);
		verify(mockStatusManager).setStatus(commitStatus);
	}

	@Test
	public void reportNoneSourceTest() throws IOException {
		GithubPullRequestReporter reporter = new GithubPullRequestReporter(mockPrManager);
		when(mockCommentManager.deleteComment(reporter.oldReport(mockUser))).thenReturn(1);
		doNothing().when(mockCommentManager).addComment(COMMENT_WITHOUT_FILE);

		CommitStatusCreate commitStatus = CommitStatusCreate.builder()
				.state(CommitState.SUCCESS)
				.description("0 / 0 (100%) - pass")
				.context("coverchecker").build();
		doNothing().when(mockStatusManager).setStatus(commitStatus);

		NewCoverageCheckReport result = NewCoverageCheckReport.builder()
				.totalNewLine(0)
				.coveredNewLine(0)
				.threshold(50)
				.build();

		reporter.report(result);

		verify(mockCommentManager).addComment(COMMENT_WITHOUT_FILE);
		verify(mockStatusManager).setStatus(commitStatus);
	}

	@Test
	public void reportError() throws IOException {
		GithubPullRequestReporter reporter = new GithubPullRequestReporter(mockPrManager);
		when(mockCommentManager.deleteComment(reporter.oldReport(mockUser))).thenReturn(1);

		doNothing().when(mockCommentManager).addComment(COMMENT_ERROR);
		CommitStatusCreate commitStatus = CommitStatusCreate.builder()
				.state(CommitState.ERROR)
				.description("error - test error")
				.context("coverchecker").build();
		doNothing().when(mockStatusManager).setStatus(commitStatus);

		NewCoverageCheckReport result = NewCoverageCheckReport.builder()
				.error(new Exception("test error"))
				.build();
		reporter.report(result);


		verify(mockCommentManager).addComment(COMMENT_ERROR);
		verify(mockStatusManager).setStatus(commitStatus);
	}
}