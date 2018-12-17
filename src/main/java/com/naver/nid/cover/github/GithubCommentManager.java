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

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class GithubCommentManager {
	private static final Logger logger = LoggerFactory.getLogger(GithubCommentManager.class);

	private IssueService issueService;

	private RepositoryId repoId;
	private int issueNumber;

	GithubCommentManager(IssueService issueService, RepositoryId repo, int issueNumber) {
		this.issueService = issueService;
		this.repoId = repo;
		this.issueNumber = issueNumber;
	}

	public int deleteComment(Predicate<Comment> commentPredicate) throws IOException {
		List<Comment> comments = issueService.getComments(repoId, issueNumber);
		int delCount = 0;
		for (Comment c : comments) {

			if (logger.isDebugEnabled()) {
				logger.debug("pre filtered comment {} : {}/{}", c.getUser().getLogin(), c.getId(), c.getBody());
			}

			if (!commentPredicate.test(c)) {
				continue;
			}

			logger.debug("delete comment {} {}", repoId, c.getId());
			issueService.deleteComment(repoId, c.getId());
			delCount++;
		}

		logger.debug("delete {} comments", delCount);
		return delCount;
	}

	public void addComment(String comment) throws IOException {
		issueService.createComment(repoId, issueNumber, comment);
	}

}
