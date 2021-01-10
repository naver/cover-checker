/*
	Copyright 2021 NAVER Corp.

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

import java.io.File;
import java.util.regex.Matcher;

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
        return path.replaceAll("[\\\\/]", Matcher.quoteReplacement(File.separator));
    }
}
