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

import org.apache.commons.cli.*;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

public class ParameterParser {
	private static final Logger logger = LoggerFactory.getLogger(ParameterParser.class);

	private static final String THRESHOLD_OPTION = "threshold";
	private static final String GITHUB_TOKEN_OPTION = "github-token";
	private static final String DIFF_OPTION = "diff";
	private static final String COVERAGE_PATH_OPTION = "cover";
	private static final String COVERAGE_TYPE_OPTION = "type";

	public Parameter getParam(String... commandArgs) {
		Options commandOptions = executeOption();

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		CommandLine cmd;
		try {
			cmd = parser.parse(commandOptions, commandArgs);
		} catch (ParseException e) {
			PrintWriter printWriter = new PrintWriter(System.out);
			printWriter.append(e.getMessage()).append('\n').append('\n');
			formatter.printHelp(printWriter, 80, "coverchecker.jar", "", commandOptions, 0, 0, "", true);
			printWriter.flush();
			return null;
		}

		Parameter param = Parameter.builder()
				.threshold(Integer.parseInt(cmd.getOptionValue("t")))
				.fileThreshold(getFileThreshold(cmd))
				.githubToken(cmd.getOptionValue("g"))
				.diffPath(cmd.getOptionValue("d"))
				.coveragePath(cmd.getOptionValue("c"))
				.githubUrl(cmd.getOptionValue("u", IGitHubConstants.HOST_API))
				.repo(cmd.getOptionValue("r"))
				.diffType(cmd.getOptionValue("diff-type"))
				.prNumber(Integer.parseInt(getPrNumber(cmd)))
				.coverageType(cmd.getOptionValue(COVERAGE_TYPE_OPTION))
				.build();

		logger.debug("execute by {}", param);
		return param;
	}

	private int getFileThreshold(CommandLine cmd) {
		String ft = cmd.getOptionValue("ft");
		if (ft != null && ft.length() > 0) {
			return Integer.parseInt(ft);
		}

		return 0;
	}

	private String getPrNumber(CommandLine cmd) {
		String p = cmd.getOptionValue("p");
		if (p != null && p.length() > 0) {
			return p;
		}

		return System.getenv("ghprbPullId");
	}

	private Options executeOption() {
		Options commandOptions = new Options();

		Option diffPath = new Option("d", DIFF_OPTION, true, "diff file path(absolute recommend)");
		commandOptions.addOption(diffPath);

		Option githubToken = new Option("g", GITHUB_TOKEN_OPTION, true, "github oauth token");
		githubToken.setRequired(true);
		commandOptions.addOption(githubToken);

		Option githubUrl = Option.builder("u")
				.desc("The url when you working on github enterprise url. default is api.github.com").hasArg()
				.longOpt("github-url")
				.build();
		commandOptions.addOption(githubUrl);

		Option githubPrNum = Option.builder("p")
				.desc("github pr number").hasArg()
				.longOpt("pr")
				.build();
		commandOptions.addOption(githubPrNum);

		Option githubRepo = Option.builder("r").required()
				.desc("github repo").hasArg()
				.longOpt("repo")
				.build();
		commandOptions.addOption(githubRepo);

		Option thresholdOption = Option.builder("t").required()
				.desc("coverage pass threshold").hasArg()
				.longOpt(THRESHOLD_OPTION).type(Integer.class)
				.build();
		commandOptions.addOption(thresholdOption);

		Option coverReportPath = Option.builder("c")
				.longOpt(COVERAGE_PATH_OPTION).hasArg()
				.required()
				.desc("coverage report path(absolute recommend)").build();
		commandOptions.addOption(coverReportPath);

		Option coverReportType = Option.builder(COVERAGE_TYPE_OPTION)
				.hasArg()
				.desc("coverage report type (jacoco | cobertura) default is jacoco")
				.build();
		commandOptions.addOption(coverReportType);

		Option fileThresholdOption = Option.builder("ft")
				.longOpt("file-threshold")
				.hasArg()
				.desc("coverage report type (jacoco | cobertura) default is jacoco")
				.build();
		commandOptions.addOption(fileThresholdOption);

		Option diffType = Option.builder("dt")
				.longOpt("diff-type")
				.hasArg()
				.desc("diff type (github | file)")
				.build();
		commandOptions.addOption(diffType);

		return commandOptions;
	}
}
