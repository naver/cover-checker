# Coverage Checker

CoverChecker check test coverage of new line of code. If you create new pull request then CoverChecker feedback to  how much test covered new lines.

Code coverage is describe one of code quality. Good test code find bug before release also prevent service disable.

> [Spring REST doc](https://spring.io/projects/spring-restdocs) is one of spring component. it makes api document from test code.

Therefore many developer wants write test code and improve code coverage. But old legacy code and project release date is not allow write them. CoverChecker will reduce pressure and get more coverage. It makes your code more durable.

# CoverChecker do...

![example](doc/example.png)

- Find new code line from pull request
- Get test coverage each file from coverage report
- Combine information and check test code cover new code line
- Write report on pull request
- CoverChecker will fail when coverage not satisfy your goal

# Run with jenkins

1. install jdk 8 on your ci
2. build CoverChecker
3. Fix your project generate test coverage report(use jacoco or cobertura)
4. execute coverchecker in you build job

# How to build

Use maven wrapper

```sh
./mvnw clean compile package
```

then maven make jar `target/coverchecker-${version}-jar-with-dependencies.jar`

# Execute with parameter

```sh
java -jar target/coverchecker-${version}-jar-with-dependencies.jar \
    -c ${coverageReportPath} \
    -g ${githubAccessToken} \
    -r ${githubRepositoryPath} \
    -t ${coverageThreshold} \
    -u ${githubHost} \
    -p ${pullrequestNo} \
    -type (jacoco | cobertura)
```

### Parameter

```sh
usage: coverchecker.jar -c <arg> [-d <arg>] [-dt <arg>] [-ft <arg>] -g <arg> [-p <arg>] -r <arg> -t <arg> [-type <arg>] -u <arg>

-c,--cover <arg>          coverage report path(recommend absolute path)
-d,--diff <arg>           diff file path(recommend absolute path)
-dt,--diff-type <arg>     diff type (github | file)
-ft,--file-threshold <arg>file pass threshold default : 0
-g,--github-token <arg>   github oauth token
-p,--pr <arg>             github pr number default : ${ghprbPullId} from github pull request builder
-r,--repo <arg>           github repo
-t,--threshold <arg>      coverage pass threshold
-type <arg>               coverage report type (jacoco | cobertura) default : jacoco
-u,--github-url <arg>     github url
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
