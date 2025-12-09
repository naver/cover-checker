package com.naver.nid.cover.lcov;

import com.naver.nid.cover.parser.coverage.model.FileCoverageReport;
import com.naver.nid.cover.util.PathUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LcovCoverageReportParserTest {

    @Test
    @DisplayName("Parse test - normal info")
    void testRead() {
        ClassLoader classLoader = getClass().getClassLoader();

        LcovCoverageReportParser parser = new LcovCoverageReportParser();
        List<FileCoverageReport> parse = parser.parse(Objects.requireNonNull(classLoader.getResource("sample.info")));

        assertEquals(5, parse.size());
        assertEquals(PathUtils.generalizeSeparator("src/index.js"), parse.get(4).getFileName().toString());
    }

    @Test
    @DisplayName("Read Test - wrong file")
    void testFileNotExist() {
        assertThrows(LcovFileNotFound.class, () -> new LcovCoverageReportParser().parse("wrong"));
    }
}