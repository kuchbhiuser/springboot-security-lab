OPERATIONS SECURITY LAB

A separate Java Spring Boot project for the requested manual Semgrep exercise.
It models customers, orders, inventory, invoices, shipments and support tickets.
Each business area has a REST controller, database service, integration client,
and seeded H2 records. Shared infrastructure includes a legacy query gateway.

Scope: 30 seeded SQL injection issues and 30 seeded hardcoded credential issues.
Safe parameterized queries, allowlisted sorting, environment-supplied credentials
and UI text are mixed into the application. This is an intentionally vulnerable
training application, bound to localhost. All partner credentials are fictional.
Integration previews construct request/configuration objects without sending
requests. No external accounts or databases are required.

TASK ORDER
1. Application and deliberately imperfect rules: complete.
2. Python scan-to-CSV script and CLI run: complete.
3. Excel verdicts and reasons: YOUR MANUAL STEP, deliberately not completed.
4. Total findings are available; final TP/FP analysis waits for your CSV review.

BUILD AND RUN
Requires Java 17+ and Maven 3.6.3+. Tested using Java 24 and Maven 3.9.10.
  mvn test
  mvn spring-boot:run
The application listens on http://127.0.0.1:8087.
Example: /api/customer/lookup?value=sample-customer
Example: /api/invoice/report?minimum=150
Example: /api/support/integration-preview
The other domains are order, inventory and shipment. Each domain has lookup,
search, sort, report, safe-lookup, safe-sort, integration-preview and POST notes.
Database content is reset when the application exits.

SCAN AND EXPORT
Create a Python virtual environment, then install requirements.txt.
From the project directory:
  python tools/scan_semgrep.py rules/security.yaml . --output-dir reports
Or pass absolute paths to the rule and repository from any directory.
Use --semgrep /path/to/semgrep if Semgrep is not on PATH.

The script runs Semgrep Community Edition with metrics disabled, preserves raw
JSON in reports/semgrep.json, and exports reports/findings.csv with exactly:
  RuleID, File Path, Start Line, End Line, Message
It also writes stderr.log and summary.json, and fails on scan errors instead of
presenting incomplete findings as a successful result. Running into an existing
output directory replaces the generated reports; save reviewed CSV separately.
The supplied rules scan src/main/java only, excluding tests and assessment files.

MANUAL REVIEW (STEP 3 — YOURS)
Open reports/findings.csv in Excel. Add columns named Verdict and Reason.
For each row, inspect the referenced Java code and enter TP or FP with a reason.
Save the result as reports/reviewed.csv (CSV UTF-8). The provided findings.csv has
no verdicts or reasons filled in. A missed vulnerability does not produce a CSV
row, so missed issues are a separate coverage question, not a false positive.

AFTER YOUR REVIEW
  python tools/summarize_review.py reports/reviewed.csv
This only counts YOUR decisions by RuleID. It rejects blank verdicts/reasons.
Use its per-rule total, TP count and FP count for the requested final analysis.
reports/analysis.txt is the current pending-review analysis table.

CURRENT EXECUTED SCAN
Semgrep 1.180.0: 21 production Java files parsed, no scan errors, 62 findings.
  lab-java-sql-injection: 30 findings
  lab-java-hardcoded-credentials: 32 findings
These are alert counts, not manually validated true-positive totals.
Both rules deliberately retain coverage limitations; neither is production-ready.
The SQL rule models local request-to-JdbcTemplate flow, and the credentials rule
models fixed strings reaching credential-like settings. Regex groups related
method/key names instead of listing individual vendor APIs.

ASSESSMENT MATERIAL (OPTIONAL SPOILERS)
assessment/seeded-issues.json inventories the 60 injected issues and intended
coverage gaps. assessment/coverage.txt records the twelve verified misses.
Keep those aside while doing the manual finding review if you want a blind pass.
There are no Semgrep ruleid/ok answer annotations in the application source.

VALIDATION
Five Spring Boot tests passed: all six domains reach H2 and integration setup;
injected input changes the vulnerable lookup while the bound lookup stays safe;
and a missed cross-method SQL issue is reachable and changes query behavior.
Python tool tests cover CSV quoting, CLI failure, scan errors, missing executable,
stale output cleanup, and incomplete/completed manual review handling.

Reference documentation:
https://docs.spring.io/spring-boot/3.5/system-requirements.html
https://semgrep.dev/docs/category/local-and-cli-scans

UPDATE: Six seeded misses per category, increased from three. The rules are
unchanged; six existing vulnerable flows now use additional realistic APIs.
Original scan artifacts are retained in reports/baseline. Current reports use
the modified application. Manual Excel verdicts remain yours to complete.
