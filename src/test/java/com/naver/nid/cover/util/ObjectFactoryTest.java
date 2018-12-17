package com.naver.nid.cover.util;

import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.github.GithubPullRequestManager;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.parser.coverage.cobertura.CoberturaReportHandler;
import com.naver.nid.cover.parser.coverage.jacoco.JacocoReportParser;
import com.naver.nid.cover.parser.diff.DiffParser;
import com.naver.nid.cover.reporter.Reporter;
import org.eclipse.egit.github.core.RepositoryId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ObjectFactoryTest {

    @Test
    void getDiffParser() {
        assertEquals(DiffParser.class, new ObjectFactory(null).getDiffParser().getClass());
    }

    @Test
    void getCoberturaCoverageReportParser() throws NoSuchFieldException, IllegalAccessException {
        CoverageReportParser cobertura = new ObjectFactory(Parameter.builder().coverageType("cobertura").build()).getCoverageReportParser();
        Field handler = XmlCoverageReportParser.class.getDeclaredField("handler");
        handler.setAccessible(true);
        assertEquals(CoberturaReportHandler.class, handler.get(cobertura).getClass());
    }

    @Test
    void getJacocoCoverageReportParser() {
        CoverageReportParser jacoco = new ObjectFactory(Parameter.builder().build()).getCoverageReportParser();
        assertEquals(JacocoReportParser.class, jacoco.getClass());
    }

    @Test
    void getNewCoverageParser() {
        NewCoverageChecker newCoverageParser = new ObjectFactory(Parameter.builder().build()).getNewCoverageParser();
        assertEquals(NewCoverageChecker.class, newCoverageParser.getClass());
    }

}