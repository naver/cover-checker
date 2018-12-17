package com.naver.nid.cover.util;

import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterParserTest {

	@Test
	public void getParam() {
		String param = "-c /path -d /path -t 50 -g github_token -r repo -p 3 -type cobertura --diff-type github";
		Parameter parameter = new ParameterParser().getParam(param.split(" "));
		assertEquals("/path", parameter.getCoveragePath());
		assertEquals("/path", parameter.getDiffPath());
		assertEquals(50, parameter.getThreshold());
		assertEquals("github_token", parameter.getGithubToken());
		assertEquals(IGitHubConstants.HOST_API, parameter.getGithubUrl());
		assertEquals("repo", parameter.getRepo());
		assertEquals(3, parameter.getPrNumber());
		assertEquals("cobertura", parameter.getCoverageType());
		assertEquals("github", parameter.getDiffType());
	}

	@Test
	public void getFailParam() {
		assertNull(new ParameterParser().getParam(""));
	}

}