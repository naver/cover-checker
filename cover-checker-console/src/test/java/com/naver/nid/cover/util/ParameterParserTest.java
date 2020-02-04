package com.naver.nid.cover.util;

import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParameterParserTest {

	@Test
	public void getParam() {
		String param = "-c /path -c /path2 -d /path -t 50 -g github_token -r repo -p 3 -type cobertura --diff-type com.naver.nid.cover.github";
		Parameter parameter = new ParameterParser().getParam(param.split(" "));
		assertEquals(Arrays.asList("/path", "/path2"), parameter.getCoveragePath());
		assertEquals("/path", parameter.getDiffPath());
		assertEquals(50, parameter.getThreshold());
		assertEquals("github_token", parameter.getGithubToken());
		assertEquals(IGitHubConstants.HOST_API, parameter.getGithubUrl());
		assertEquals("repo", parameter.getRepo());
		assertEquals(3, parameter.getPrNumber());
		assertEquals("cobertura", parameter.getCoverageType());
		assertEquals("com.naver.nid.cover.github", parameter.getDiffType());
	}

	@Test
	public void getFailParam() {
		assertNull(new ParameterParser().getParam(""));
	}

}