#### [(${title})]
[# th:if="${total > 0}"]
[(${icon})] **[(${result})]** : [(${covered})] / [(${total})] ([(${#numbers.formatPercent(1.0 * covered / total, 2, 2)})])
[/]
[# th:if="${total <= 0}"]
[(${icon})] **[(${result})]** : [(${covered})] / [(${total})] (0%)
[/]

[# th:if="${detail != null && detail.size > 0}"]
#### file detail

|   |path|covered line|new line|coverage|
|----|----|----|----|----|
[# th:each="file : ${detail}"]|[(${file.icon})]|[(${file.name})]|[(${file.addedCoverLine})]|[(${file.addedLine})]|[(${#numbers.formatPercent(1.0 * file.addedCoverLine / file.addedLine, 2, 2)})]|
[/]

[/]