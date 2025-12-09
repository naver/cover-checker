package com.naver.nid.cover.lcov;

public class LcovFileNotFound extends RuntimeException {
    public LcovFileNotFound(String message, Throwable cause) {
        super(String.format("%s not found", message), cause, true, false);
    }
}
