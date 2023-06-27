package com.naver.nid.cover.util;

public enum CoverageType {
    JACOCO("jacoco"),
    COBERTURA("cobertura");

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
            default:
                throw new IllegalArgumentException("Unknown coverage type: " + coverageType +
                        ", available types: " + JACOCO + ", " + COBERTURA);
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
