# Coverage Checker [![Build Status](https://travis-ci.org/naver/cover-checker.svg?branch=master)](https://travis-ci.org/naver/cover-checker)

CoverChecker provides a test coverage for new added lines of code.
If you create new pull request, CoverChecker would give feedback regarding how much of the new lines your test code has covered.

Good Test code makes people find bug more efficiently before service release and prevents service from being disabled.
To check the test code quality, most of developers refer the code coverage status as an important index.
This is why most of them spend their resources to update their test code to achieve more code coverage whenever they add new lines of code.
However, it has been hard to check and improve the code coverage as there are lots of old legacy codes which is not within the scope of the current test code.

> [Spring REST doc](https://spring.io/projects/spring-restdocs) is one of spring component. it makes api document from test code.

CoverChecker provides a feature to check the code coverage for new added code, not a whole code.
It will reduce the pressure to cover the whole and help you to achieve more coverage steadly and efficiently.
Makes your code more durable!

# CoverChecker do...

![example](doc/example.png)

- Find new code line from pull request
- Get test coverage each file from coverage report
- Combine information and check test code cover new code line
- Write report on pull request
- CoverChecker will fail when coverage doesn't satisfy your goal

# Run with jenkins

1. install jdk 8 on your ci
2. build CoverChecker
3.  Fix your project to generate test coverage report(CoverChecker only Jacoco or Cobertura)
4. execute CoverChecker in you build job

# How to build

Use maven wrapper

```sh
./mvnw clean compile package
```

then maven would make jar `cover-checker-console/target/coverchecker-${version}-jar-with-dependencies.jar`

# Execute with parameter

```sh
java -jar cover-checker-console/target/cover-checker-console-${version}-jar-with-dependencies.jar \
    --cover ${coverageReportPath} \
    [--cover ${otherCoverageReportPath}] \
    --github-token ${githubAccessToken} \
    --repo ${githubRepositoryPath} \
    --threshold ${coverageThreshold} \
    --github-url ${githubHost} \
    --pr ${pullrequestNo} \
    -type (jacoco | cobertura)
```

### Parameter

```sh
usage: coverchecker.jar -c <arg> [-d <arg>] [-dt <arg>] [-ft <arg>] [-g <arg>]
       [-p <arg>] [-r <arg>] -t <arg> [-type <arg>] [-u <arg>]
-c,--cover <arg>          coverage report paths(absolute recommend), coverage
                          report path can take multiple paths for multi-module
                          project
-d,--diff <arg>           diff file path(absolute recommend)
-dt,--diff-type <arg>     diff type (github | file)
-ft,--file-threshold <arg>coverage report type (jacoco | cobertura) default is
                          jacoco
-g,--github-token <arg>   github oauth token
-p,--pr <arg>             github pr number
-r,--repo <arg>           github repo
-t,--threshold <arg>      coverage pass threshold
-type <arg>               coverage report type (jacoco | cobertura) default is
                          jacoco
-u,--github-url <arg>     The url when you working on github enterprise url.
                          default is api.github.com
```

## License

```
Copyright 2018 NAVER Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
