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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Arrays;

public class ParameterParser {
	private static final Logger logger = LoggerFactory.getLogger(ParameterParser.class);

	private static final String THRESHOLD_OPTION = "threshold";
	private static final String GITHUB_TOKEN_OPTION = "com.naver.nid.cover.github-token";
	private static final String DIFF_OPTION = "diff";
	private static final String COVERAGE_PATH_OPTION = "com/naver/nid/cover";
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
				.coveragePath(Arrays.asList(cmd.getOptionValues("c")))
				.githubUrl(cmd.getOptionValue("u", "api.github.com"))
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

		commandOptions.addOption(Option.builder("d")
				.longOpt(DIFF_OPTION)
				.hasArg()
				.desc("diff file path(absolute recommend)")
				.build());

		commandOptions.addOption(Option.builder("dt")
				.longOpt("diff-type")
				.hasArg()
				.desc("diff type (com.naver.nid.cover.github | file)")
				.build());

		commandOptions.addOption(Option.builder("g")
				.longOpt(GITHUB_TOKEN_OPTION)
				.required()
				.hasArg()
				.desc("com.naver.nid.cover.github oauth token")
				.build());

		commandOptions.addOption(Option.builder("u")
				.longOpt("com.naver.nid.cover.github-url")
				.hasArg()
				.desc("The url when you working on com.naver.nid.cover.github enterprise url. default is api.com.naver.nid.cover.github.com")
				.build());

		commandOptions.addOption(Option.builder("p")
				.longOpt("pr")
				.hasArg()
				.desc("com.naver.nid.cover.github pr number")
				.build());

		commandOptions.addOption(Option.builder("r").required()
				.longOpt("repo")
				.hasArg()
				.desc("com.naver.nid.cover.github repo")
				.build());

		commandOptions.addOption(Option.builder("c")
				.longOpt(COVERAGE_PATH_OPTION)
				.hasArg()
				.required()
				.desc("coverage report path(absolute recommend)")
				.build());

		commandOptions.addOption(Option.builder(COVERAGE_TYPE_OPTION)
				.hasArg()
				.desc("coverage report type (jacoco | cobertura) default is jacoco")
				.build());

		commandOptions.addOption(Option.builder("t").required()
				.longOpt(THRESHOLD_OPTION).type(Integer.class)
				.hasArg()
				.desc("coverage pass threshold")
				.build());

		commandOptions.addOption(Option.builder("ft")
				.longOpt("file-threshold")
				.hasArg()
				.desc("coverage report type (jacoco | cobertura) default is jacoco")
				.build());

		return commandOptions;
	}
}
