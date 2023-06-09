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
		assertEquals(Arrays.asList(CoverageType.COBERTURA, CoverageType.COBERTURA), parameter.getCoverageType());
		assertEquals("com.naver.nid.cover.github", parameter.getDiffType());
	}

	@Test
	public void testDifferentCoverageTypes() {
		String param = "-type cobertura -c /path -type jacoco -c /path2 -d /diff_path -t 50 -g github_token -r repo -p 3 --diff-type com.naver.nid.cover.github";
		Parameter parameter = new ParameterParser().getParam(param.split(" "));
		assertEquals(Arrays.asList("/path", "/path2"), parameter.getCoveragePath());
		assertEquals("/diff_path", parameter.getDiffPath());
		assertEquals(50, parameter.getThreshold());
		assertEquals("github_token", parameter.getGithubToken());
		assertEquals(IGitHubConstants.HOST_API, parameter.getGithubUrl());
		assertEquals("repo", parameter.getRepo());
		assertEquals(3, parameter.getPrNumber());
		assertEquals(Arrays.asList(CoverageType.COBERTURA, CoverageType.JACOCO), parameter.getCoverageType());
		assertEquals("com.naver.nid.cover.github", parameter.getDiffType());
	}

	@Test
	public void testCoverageTypeBroadcast() {
		String param = "-type cobertura -c /path -c /path2 -c /path3 -d /diff_path -t 50 -g github_token -r repo -p 3 --diff-type com.naver.nid.cover.github";
		Parameter parameter = new ParameterParser().getParam(param.split(" "));
		assertEquals(Arrays.asList("/path", "/path2", "/path3"), parameter.getCoveragePath());
		assertEquals("/diff_path", parameter.getDiffPath());
		assertEquals(50, parameter.getThreshold());
		assertEquals("github_token", parameter.getGithubToken());
		assertEquals(IGitHubConstants.HOST_API, parameter.getGithubUrl());
		assertEquals("repo", parameter.getRepo());
		assertEquals(3, parameter.getPrNumber());
		assertEquals(Arrays.asList(CoverageType.COBERTURA, CoverageType.COBERTURA, CoverageType.COBERTURA), parameter.getCoverageType());
		assertEquals("com.naver.nid.cover.github", parameter.getDiffType());
	}

	@Test
	public void testDefaultCoverageType() {
		String param = "-c /path -d /diff_path -t 50 -g github_token -r repo -p 3 --diff-type com.naver.nid.cover.github";
		Parameter parameter = new ParameterParser().getParam(param.split(" "));
		assertEquals(Arrays.asList("/path"), parameter.getCoveragePath());
		assertEquals("/diff_path", parameter.getDiffPath());
		assertEquals(50, parameter.getThreshold());
		assertEquals("github_token", parameter.getGithubToken());
		assertEquals(IGitHubConstants.HOST_API, parameter.getGithubUrl());
		assertEquals("repo", parameter.getRepo());
		assertEquals(3, parameter.getPrNumber());
		assertEquals(Arrays.asList(CoverageType.JACOCO), parameter.getCoverageType());
		assertEquals("com.naver.nid.cover.github", parameter.getDiffType());
	}

	@Test
	public void getFailParam() {
		assertNull(new ParameterParser().getParam(""));
	}

}