package com.naver.nid.cover.util;

public enum CoverageType {
    JACOCO("jacoco"),
    COBERTURA("cobertura"),
    LCOV("lcov")
    ;

    final String name;

    CoverageType(String name) {
        this.name = name;
    }

    public static CoverageType parse(String coverageType) {
        switch (coverageType.toLowerCase()) {
            case "jacoco":
                return JACOCO;
            case "cobertura":
                return COBERTURA;
            case "lcov":
                return LCOV;
            default:
                throw new IllegalArgumentException("Unknown coverage type: " + coverageType +
                        ", available types: " + JACOCO + ", " + COBERTURA + ", " + LCOV);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
