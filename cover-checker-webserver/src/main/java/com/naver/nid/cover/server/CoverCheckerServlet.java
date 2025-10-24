package com.naver.nid.cover.server;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.List;

import com.naver.nid.cover.CoverChecker;
import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.cobertura.CoberturaCoverageReportHandler;
import com.naver.nid.cover.github.manager.GithubPullRequestManager;
import com.naver.nid.cover.github.parser.GithubDiffReader;
import com.naver.nid.cover.github.reporter.GithubPullRequestReporter;
import com.naver.nid.cover.jacoco.JacocoReportParser;
import com.naver.nid.cover.jacoco.JacocoXmlCoverageReportHandler;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.parser.diff.DiffParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoverCheckerServlet extends HttpServlet {

    private final String githubHost;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authenticate = req.getHeader("Authentication");
        String repo = req.getParameter("repo");
        int prNumber = Integer.parseInt(req.getParameter("pr_no"));

        List<CoverageReportParser> coverageParser = Arrays.asList(
                new JacocoReportParser(),
                new XmlCoverageReportParser(new CoberturaCoverageReportHandler()));
        GithubPullRequestManager prManager = new GithubPullRequestManager(githubHost, authenticate, repo, prNumber);
        CoverChecker checker = new CoverChecker(
                coverageParser,
                new GithubDiffReader(prManager),
                new NewCoverageChecker(),
                new GithubPullRequestReporter(prManager));

        checker.check(null);
    }

}
