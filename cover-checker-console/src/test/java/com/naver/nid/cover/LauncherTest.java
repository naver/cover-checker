package com.naver.nid.cover;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {

    @Test
    public void testMain() {
        Launcher.main(new String[]{""});
        // execution fail
        // because there is no parameters for execute cover-checker
        assertThrows(NullPointerException.class, () -> Launcher.main("-c path -t 80 -dt file".split(" ")));
    }

}