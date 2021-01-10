package com.naver.nid.cover.util;

import java.io.File;

public class PathUtils {
    private PathUtils() {
    }

    /**
     * <p>generalize file path</p>
     *
     * <h3>in Windows</h3>
     * <ul>
     * <li>- test/test.java -> test\test.java</li>
     * <li>- test\test.java -> test\test.java</li>
     * </ul>
     *
     * <h3>in Posix</h3>
     *  <ul>
     *  <li>- test/test.java -> test/test.java</li>
     *  <li>- test\test.java -> test/test.java</li>
     *  </ul>
     * @param path path from reports(github, jacoco, cobertura...)
     * @return generalized path that replaced separator that OS used
     */
    public static String generalizeSeparator(String path) {
        if (path == null || path.isEmpty()) return path;
        return path.replaceAll("[\\\\/]", File.separator);
    }
}
