package com.naver.nid.cover.util;

import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.github.parser.GithubDiffReader;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.cobertura.CoberturaCoverageReportHandler;
import com.naver.nid.cover.jacoco.JacocoReportParser;
import com.naver.nid.cover.parser.diff.DiffParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectFactoryTest {

    @Test
    void getDiffParser() {
        assertEquals(GithubDiffReader.class, new ObjectFactory(Parameter.builder().repo("test/test").build()).getDiffParser().getClass());
    }

    @Test
    void getCoberturaCoverageReportParser() throws NoSuchFieldException, IllegalAccessException {
        CoverageReportParser cobertura = new ObjectFactory(Parameter.builder().coverageType("cobertura").build()).getCoverageReportParser();
        Field handler = XmlCoverageReportParser.class.getDeclaredField("handler");
        handler.setAccessible(true);
        assertEquals(CoberturaCoverageReportHandler.class, handler.get(cobertura).getClass());
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