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
import java.util.List;
import java.util.stream.Collectors;

public class ParameterParser {
	private static final Logger logger = LoggerFactory.getLogger(ParameterParser.class);

	private static final String THRESHOLD_OPTION = "threshold";
	private static final String GITHUB_TOKEN_OPTION = "github-token";
	private static final String DIFF_OPTION = "diff";
	private static final String COVERAGE_PATH_OPTION = "cover";
	private static final String COVERAGE_TYPE_OPTION = "type";

	private Parameter fail(Options commandOptions, String message) {
		final PrintWriter printWriter = new PrintWriter(System.out);
		printWriter.append(message).append('\n').append('\n');
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(printWriter, 80, "coverchecker.jar", "", commandOptions, 0, 0, "", true);
		printWriter.flush();
		return null;
	}

	public Parameter getParam(String... commandArgs) {
		final Options commandOptions = executeOption();
		final CommandLineParser parser = new DefaultParser();

		CommandLine cmd;
		try {
			cmd = parser.parse(commandOptions, commandArgs);
		} catch (ParseException e) {
			return fail(commandOptions, e.getMessage());
		}

		final List<String> coveragePaths = Arrays.asList(cmd.getOptionValues("c"));
		List<CoverageType> coverageTypes;
		try {
			coverageTypes = parseCoverageTypes(cmd.getOptionValues(COVERAGE_TYPE_OPTION));
		} catch (IllegalArgumentException e) {
			return fail(commandOptions, e.getMessage());
		}

		if ((coverageTypes.size() == 1) && coveragePaths.size() > 1) {
			// If there's a single "-type" arg, broadcast it for all coverage paths.
			final CoverageType coverageType = coverageTypes.get(0);
			coverageTypes = coveragePaths.stream().map(path -> coverageType).collect(Collectors.toList());
		}

		if (coveragePaths.size() != coverageTypes.size()) {
			return fail(commandOptions, "Unmatched number of --cover and -type parameters");
		}

		Parameter param = Parameter.builder()
				.threshold(Integer.parseInt(cmd.getOptionValue("t")))
				.fileThreshold(getFileThreshold(cmd))
				.githubToken(cmd.getOptionValue("g"))
				.diffPath(cmd.getOptionValue("d"))
				.coveragePath(coveragePaths)
				.githubUrl(cmd.getOptionValue("u", "api.github.com"))
				.repo(cmd.getOptionValue("r"))
				.diffType(cmd.getOptionValue("diff-type"))
				.prNumber(Integer.parseInt(getPrNumber(cmd)))
				.coverageType(coverageTypes)
				.build();

		logger.debug("execute by {}", param);
		return param;
	}

	private static List<CoverageType> parseCoverageTypes(String[] optionValues) {
		if (optionValues == null) {
			return Arrays.asList(CoverageType.JACOCO);
		}

		return Arrays.stream(optionValues)
				.map(CoverageType::parse)
				.collect(Collectors.toList());
	}

	private int getFileThreshold(CommandLine cmd) {
		String ft = cmd.getOptionValue("ft");
		if (ft != null && ft.length() > 0) {
			return Integer.parseInt(ft);
		}

		return 0;
	}

	private String getPrNumber(CommandLine cmd) {
		String p = cmd.getOptionValue("p", "-1");
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
				.desc("diff type (github | file)")
				.build());

		commandOptions.addOption(Option.builder("g")
				.longOpt(GITHUB_TOKEN_OPTION)
				.hasArg()
				.desc("github oauth token")
				.build());

		commandOptions.addOption(Option.builder("u")
				.longOpt("github-url")
				.hasArg()
				.desc("The url when you working on github enterprise url. default is api.github.com")
				.build());

		commandOptions.addOption(Option.builder("p")
				.longOpt("pr")
				.hasArg()
				.desc("github pr number")
				.build());

		commandOptions.addOption(Option.builder("r")
				.longOpt("repo")
				.hasArg()
				.desc("github repo")
				.build());

		commandOptions.addOption(Option.builder("c")
				.longOpt(COVERAGE_PATH_OPTION)
				.hasArg()
				.required()
				.desc("coverage report paths(absolute recommend), coverage report path can take multiple paths for multi-module project")
				.build());

		commandOptions.addOption(Option.builder(COVERAGE_TYPE_OPTION)
				.hasArg()
				.desc("coverage report type (jacoco | cobertura) default is jacoco")
				.build());

		commandOptions.addOption(Option.builder("t")
				.required()
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
