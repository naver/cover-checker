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
package com.naver.nid.cover.github.reporter;

import com.naver.nid.cover.checker.model.CommitState;
import com.naver.nid.cover.checker.model.NewCoverageCheckReport;
import com.naver.nid.cover.github.manager.GithubCommentManager;
import com.naver.nid.cover.github.manager.GithubPullRequestManager;
import com.naver.nid.cover.github.manager.GithubStatusManager;
import com.naver.nid.cover.github.manager.model.CommitStatusCreate;
import com.naver.nid.cover.reporter.Reporter;
import com.naver.nid.cover.reporter.exception.ReportException;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class GithubPullRequestReporter implements Reporter {
    private static final Logger logger = LoggerFactory.getLogger(GithubPullRequestReporter.class);

    private static final String REPORT_HEADER = "[PR Coverage check]";

    private TemplateEngine templateEngine;

    private GithubPullRequestManager manager;
    private GithubCommentManager commentManager;
    private GithubStatusManager statusManager;

    public GithubPullRequestReporter(GithubPullRequestManager manager) {
        githubInit(manager);
        templateInit();
    }

    private void githubInit(GithubPullRequestManager manager) {
        this.manager = manager;
        this.commentManager = manager.commentManager();
        this.statusManager = manager.statusManager();
    }

    private void templateInit() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".md");
        templateResolver.setCacheable(false);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public void report(NewCoverageCheckReport result) {
        logger.debug("report {}", result);


        String comment = getComment(result);
        CommitStatusCreate commitStatus = getCommitStatus(result);

        logger.debug("result comment {}", comment);
        logger.debug("result commit status {}", commitStatus);
        try {
            User watcher = manager.getUser();
            commentManager.deleteComment(oldReport(watcher));
            commentManager.addComment(comment);
            logger.debug("add comment {}", comment);

            statusManager.setStatus(commitStatus);
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }

    private CommitStatusCreate getCommitStatus(NewCoverageCheckReport result) {
        if (result.getError() != null) {
            return CommitStatusCreate.builder()
                    .state(CommitState.ERROR)
                    .description("error - " + result.getError().getMessage())
                    .context("coverchecker").build();
        } else {
            return CommitStatusCreate.builder()
                    .state(result.result().githubState)
                    .description(String.format("%d / %d (%d%%) - %s", result.getCoveredNewLine(), result.getTotalNewLine(), result.getCoverage(), result.result()))
                    .context("coverchecker").build();
        }
    }

    private String getComment(NewCoverageCheckReport result) {
        if (result.getError() != null) {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("title", REPORT_HEADER);
            templateParams.put("error", result.getError().getMessage());
            templateParams.put("icon", result.result().icon);
            return bind("error", templateParams);
        }

        String resultString = result.result().toString();
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("title", REPORT_HEADER);
        templateParams.put("result", resultString);
        templateParams.put("icon", result.result().icon);
        templateParams.put("covered", result.getCoveredNewLine());
        templateParams.put("total", result.getTotalNewLine());
        templateParams.put("percent", result.getCoverage());
        templateParams.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        templateParams.put("detail", result.getCoveredFilesInfo());

        return bind("result", templateParams);
    }

    private String bind(String templateName, Map<String, Object> templateParams) {
        return templateEngine.process(templateName, new ExpressionContext(templateEngine.getConfiguration(), Locale.US, templateParams));
    }


    Predicate<Comment> oldReport(User watcher) {
        return c -> c.getUser().getId() == watcher.getId() && c.getBody().contains(REPORT_HEADER);
    }

}
