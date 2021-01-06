package com.naver.nid.cover.parser.diff;

import com.naver.nid.cover.parser.diff.model.Diff;
import com.naver.nid.cover.parser.diff.model.DiffSection;
import com.naver.nid.cover.parser.diff.model.Line;
import com.naver.nid.cover.parser.diff.model.ModifyType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDiffParseTest {
	private static final Logger logger = LoggerFactory.getLogger(FileDiffParseTest.class);

	@Test
	public void parserSimpleTest() {

		String s = "diff --git a/src/main/java/com/naver/nid/push/token/service/TokenService.java b/src/main/java/com/naver/nid/push/token/service/TokenService.java\n" +
				"index cef524e..fab825e 100644\n" +
				"--- a/src/main/java/com/naver/nid/push/token/service/TokenService.java\n" +
				"+++ b/src/main/java/com/naver/nid/push/token/service/TokenService.java\n" +
				"@@ -109,4 +109,8 @@ private PushInfraInfo convertToPushInfraInfo(DeviceInfo deviceInfo) {\n" +
				" \t\tlogger.debug(\"device info transform {} -> {}\", deviceInfo, info);\n" +
				" \t\treturn info;\n" +
				" \t}\n" +
				"+\n" +
				"+\tpublic Mono<Integer> forTest() {\n" +
				"+\t\treturn Mono.just(100);\n" +
				"+\t}\n" +
				" }";

		FileDiffReader reader = new FileDiffReader(new BufferedReader(new StringReader(s)));
		Stream<Diff> parse = reader.parse();

		List<Diff> collect = parse.collect(Collectors.toList());
		logger.debug("{}", collect);

		assertEquals(1, collect.size());
		Diff diff = collect.get(0);
		assertEquals(Paths.get("src/main/java/com/naver/nid/push/token/service/TokenService.java"), diff.getFileName());
		assertEquals(1, diff.getDiffSectionList().size());

		DiffSection diffSection = diff.getDiffSectionList().get(0);
		Line line = diffSection.getLineList().get(0);
		assertEquals(109, line.getLineNumber());
		assertEquals("\t\tlogger.debug(\"device info transform {} -> {}\", deviceInfo, info);", line.getBody());
	}

	@Test
	public void parserComplexTest() throws URISyntaxException, FileNotFoundException {
		URL diffUrl = getClass().getClassLoader().getResource("test_diff.diff");
		FileDiffReader reader = new FileDiffReader(new BufferedReader(new FileReader(new File(diffUrl.toURI()))));
		Stream<Diff> parse = reader.parse();
		List<Diff> parsedList = parse.collect(Collectors.toList());

		logger.debug("parsed {}", parsedList);

		int testLineNum = parsedList.stream()
//				.peek(diff -> logger.debug("diff file name {}", diff.getFileName()))
				.filter(d -> d.getFileName().endsWith("TestService.java"))
				.flatMap(d -> d.getDiffSectionList().stream())
//				.peek(diffSection -> logger.debug("find diff {}", diffSection))
				.flatMap(diffSection -> diffSection.getLineList().stream())
				.filter(l -> l.getBody().contains("return Mono.just(100);"))
				.filter(l -> l.getType() == ModifyType.ADD)
				.findFirst().orElseThrow(AssertionError::new).getLineNumber();

		assertEquals(114, testLineNum);
	}
}