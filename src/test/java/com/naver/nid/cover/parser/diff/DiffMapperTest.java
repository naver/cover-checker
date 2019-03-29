package com.naver.nid.cover.parser.diff;

import com.naver.nid.cover.parser.diff.model.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiffMapperTest {

	private DiffMapper mapper = new DiffMapper();

	@Test
	public void testSingleLineFix() {
		String testContent = "@@ -1 +1 @@\n-test\n\\\\ No newline at end of file\n+test1\n\\\\ No newline at end of file";

		Diff test = mapper.apply(RawDiff.builder().fileName("test")
				.rawDiff(Arrays.asList(testContent.split("\n"))).build());

		assertEquals(1, test.getDiffSectionList().size());

		DiffSection diffSection = test.getDiffSectionList().get(0);
		List<Line> lineList = diffSection.getLineList();
		assertEquals(2, lineList.size());
		assertEquals("test", lineList.get(0).getBody());
		assertEquals(ModifyType.DEL, lineList.get(0).getType());
		assertEquals("test1", lineList.get(1).getBody());
		assertEquals(ModifyType.ADD, lineList.get(1).getType());
	}


	@Test
	public void testNewSingleLine() {
		String testContent = "@@ -0 +1 @@\n+test1\n\\\\ No newline at end of file";

		Diff test = mapper.apply(RawDiff.builder().fileName("test")
				.rawDiff(Arrays.asList(testContent.split("\n"))).build());

		assertEquals(1, test.getDiffSectionList().size());

		DiffSection diffSection = test.getDiffSectionList().get(0);
		List<Line> lineList = diffSection.getLineList();
		assertEquals(1, lineList.size());
		assertEquals("test1", lineList.get(0).getBody());
		assertEquals(ModifyType.ADD, lineList.get(0).getType());
	}
}