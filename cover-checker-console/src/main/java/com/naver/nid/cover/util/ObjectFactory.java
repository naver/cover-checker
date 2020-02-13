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
package com.naver.nid.cover.util;

import com.naver.nid.cover.github.parser.GithubDiffReader;
import com.naver.nid.cover.parser.diff.DiffParser;
import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.github.manager.GithubPullRequestManager;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.cobertura.CoberturaCoverageReportHandler;
import com.naver.nid.cover.jacoco.JacocoReportParser;
import com.naver.nid.cover.parser.diff.FileDiffReader;
import com.naver.nid.cover.reporter.Reporter;
import com.naver.nid.cover.reporter.ConsoleReporter;
import com.naver.nid.cover.github.reporter.GithubPullRequestReporter;

/**
 * {@link Parameter}에 따라 내부 객체를 생성하는 Factory 객체
 */
public class ObjectFactory {

    private final Parameter param;

    public ObjectFactory(Parameter param) {
        this.param = param;
    }

    public DiffParser getDiffReader() {
        if ("file".equals(param.getDiffType())) {
            return new FileDiffReader(param.getDiffPath());
        } else {
            return new GithubDiffReader(getPrManager());
        }
    }

    public CoverageReportParser getCoverageReportParser() {
        if ("cobertura".equals(param.getCoverageType())) {
            return new XmlCoverageReportParser(new CoberturaCoverageReportHandler());
        } else {
            return new JacocoReportParser();
        }
    }

    public NewCoverageChecker getNewCoverageParser() {
        return new NewCoverageChecker();
    }

    public Reporter getReporter() {
        ConsoleReporter consoleReporter = new ConsoleReporter();
        if (param.getPrNumber() == -1) {
            return consoleReporter;
        }
        return consoleReporter.andThen(new GithubPullRequestReporter(getPrManager()))::accept;
    }

    public GithubPullRequestManager getPrManager() {
        return new GithubPullRequestManager(param.getGithubUrl(), param.getGithubToken(), param.getRepo(), param.getPrNumber());
    }
}
