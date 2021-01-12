package com.naver.nid.cover.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PathUtilsTest {

    @ParameterizedTest
    @MethodSource("testCase")
    void generalizeSeparator(String path, String expect) {
        assertEquals(expect, PathUtils.generalizeSeparator(path));
    }

    static Stream<Arguments> testCase() {
        return Stream.of(
                arguments(null, null),
                arguments("", ""),
                arguments("test/test.java","test" + File.separator + "test.java"),
                arguments("test\\test.java","test" + File.separator + "test.java"));
    }
}