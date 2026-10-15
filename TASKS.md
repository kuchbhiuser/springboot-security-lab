# Spring Boot security lab task list

- [x] 1. Create a standalone Spring Boot operations portal with 30 SQL injection and 30 hardcoded credential issues, plus safe controls.
- [x] 2. Add two Semgrep rules with realistic, documented coverage gaps; run the CLI and preserve JSON.
- [x] 3. Build and test the requested Python rule/repository-to-CSV tool.
- [x] 4. Build the Java project and verify representative routes against an in-memory database.
- [x] 5. Deliver findings CSV and total findings by rule.
- [ ] 6. USER ONLY: open CSV in Excel, add Verdict (TP / FP) and Reason, and review every finding. Do not prefill verdicts.
- [ ] 7. After the user's review, calculate final TP and FP totals from their reviewed CSV.

This project is separate from the earlier Semgrep examples and portfolio site. All credentials are fictional training values. No publishing or external integration calls are part of this task.

Verified: Maven test succeeded (5 tests, 0 failures/errors); Semgrep scanned 21 production Java files with 0 errors and 62 findings (SQL 30, credentials 32). Six seeded issues per category remain undetected. Python utility tests pass. The next step is the user's manual Excel review; final TP/FP totals must come from those verdicts.

Additional coverage gaps: controller-bound request parameters, parameter maps,
NamedParameterJdbcTemplate, HttpHeaders.setBearerAuth, fixed char arrays, and
Properties.put. Prior report artifacts are preserved under reports/baseline.
