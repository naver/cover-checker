package com.naver.nid.cover.util;

import com.naver.nid.cover.checker.NewCoverageChecker;
import com.naver.nid.cover.cobertura.CoberturaCoverageReportHandler;
import com.naver.nid.cover.jacoco.JacocoReportParser;
import com.naver.nid.cover.parser.coverage.CoverageReportParser;
import com.naver.nid.cover.parser.coverage.XmlCoverageReportParser;
import com.naver.nid.cover.parser.diff.FileDiffReader;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectFactoryTest {

    @Test
    void getDiffParser() {
        String diffPath = getClass().getClassLoader().getResource("test_diff.diff").getPath();
        assertEquals(FileDiffReader.class, new ObjectFactory(Parameter.builder().diffPath(diffPath).diffType("file").build()).getDiffReader().getClass());
    }

    @Test
    void getCoberturaCoverageReportParser() throws NoSuchFieldException, IllegalAccessException {
        CoverageReportParser cobertura = new ObjectFactory(Parameter.builder()
                .coveragePath(Arrays.asList("dummy/path.xml"))
                .coverageType(Arrays.asList(CoverageType.COBERTURA))
                .build()).getCoverageReportParser().get(0);
        Field handler = XmlCoverageReportParser.class.getDeclaredField("handler");
        handler.setAccessible(true);
        assertEquals(CoberturaCoverageReportHandler.class, handler.get(cobertura).getClass());
    }

    @Test
    void getJacocoCoverageReportParser() {
        CoverageReportParser jacoco = new ObjectFactory(Parameter.builder()
                .coveragePath(Arrays.asList("dummy/path.xml"))
                .coverageType(Arrays.asList(CoverageType.JACOCO))
                .build()).getCoverageReportParser().get(0);
        assertEquals(JacocoReportParser.class, jacoco.getClass());
    }

    @Test
    void getNewCoverageParser() {
        NewCoverageChecker newCoverageParser = new ObjectFactory(Parameter.builder().build()).getNewCoverageParser();
        assertEquals(NewCoverageChecker.class, newCoverageParser.getClass());
    }

}