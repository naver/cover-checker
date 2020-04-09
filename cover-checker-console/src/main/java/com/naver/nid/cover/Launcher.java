package com.naver.nid.cover;

import com.naver.nid.cover.util.ObjectFactory;
import com.naver.nid.cover.util.Parameter;
import com.naver.nid.cover.util.ParameterParser;

public class Launcher {

    public static void main(String[] args) {
        Parameter param = new ParameterParser().getParam(args);
        if (param == null) return;

        ObjectFactory objectManager = new ObjectFactory(param);
        new CoverChecker(objectManager.getCoverageReportParser(),
                objectManager.getDiffReader(),
                objectManager.getNewCoverageParser(),
                objectManager.getReporter()).check(param);
    }

}
