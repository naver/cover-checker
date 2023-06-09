package com.naver.nid.cover;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {

    @Test
    public void testRun() {
        int exitCode = Launcher.run(new String[]{""});
        assertEquals(1, exitCode);
        // execution fail
        // because there is no parameters for execute cover-checker
        assertThrows(NullPointerException.class, () -> Launcher.run("-c path -t 80 -dt file".split(" ")));
    }

}