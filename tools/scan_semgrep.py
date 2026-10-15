#!/usr/bin/env python3
"""Run Semgrep and export raw JSON, findings CSV, and a rule summary."""
import argparse
import csv
import json
import subprocess
import sys
from collections import Counter
from pathlib import Path

FIELDS = ["RuleID", "File Path", "Start Line", "End Line", "Message"]


def scan(rule: Path, repo: Path, semgrep: str, output_dir: Path) -> int:
    rule = rule.resolve()
    repo = repo.resolve()
    output_dir.mkdir(parents=True, exist_ok=True)
    for name in ("semgrep.json", "findings.csv", "summary.json"):
        (output_dir / name).unlink(missing_ok=True)
    command = [semgrep, "scan", "--oss-only", "--metrics=off", "--disable-version-check",
               "--disable-nosem", "--no-git-ignore", "--no-rewrite-rule-ids",
               "--config", str(rule), "--json", "."]
    try:
        completed = subprocess.run(command, text=True, capture_output=True, check=False, cwd=str(repo))
    except OSError as exc:
        (output_dir / "stderr.log").write_text(str(exc) + "\n", encoding="utf-8")
        print(str(exc), file=sys.stderr)
        return 127
    (output_dir / "stderr.log").write_text(completed.stderr, encoding="utf-8")
    (output_dir / "semgrep.json").write_text(completed.stdout, encoding="utf-8")
    if completed.returncode:
        if completed.stderr:
            sys.stderr.write(completed.stderr)
        return completed.returncode
    try:
        data = json.loads(completed.stdout)
    except json.JSONDecodeError as exc:
        print(f"Semgrep returned invalid JSON: {exc}", file=sys.stderr)
        if completed.stderr:
            sys.stderr.write(completed.stderr)
        return 2
    results = data.get("results", [])
    errors = data.get("errors", [])
    if errors:
        print(f"Semgrep reported {len(errors)} error(s); see semgrep.json", file=sys.stderr)
        return 1
    with (output_dir / "findings.csv").open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=FIELDS)
        writer.writeheader()
        for result in results:
            start, end = result.get("start", {}), result.get("end", {})
            writer.writerow({
                "RuleID": result.get("check_id", ""),
                "File Path": result.get("path", ""),
                "Start Line": start.get("line", ""),
                "End Line": end.get("line", ""),
                "Message": result.get("extra", {}).get("message", ""),
            })
    counts = Counter(r.get("check_id", "") for r in results)
    (output_dir / "summary.json").write_text(json.dumps({"total": len(results), "count_per_rule": dict(sorted(counts.items())), "review": "pending"}, indent=2) + "\n", encoding="utf-8")
    print(f"Total findings: {len(results)}")
    for rule_id, count in sorted(counts.items()):
        print(f"{rule_id}: {count}")
    if completed.stderr:
        sys.stderr.write(completed.stderr)
    return 0


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("rule", type=Path, help="Semgrep rule/config path")
    parser.add_argument("repo", type=Path, help="Repository path to scan")
    parser.add_argument("--semgrep", default="semgrep", help="Semgrep executable")
    parser.add_argument("--output-dir", type=Path, default=Path("semgrep-output"))
    args = parser.parse_args(argv)
    if not args.rule.is_file():
        parser.error(f"rule does not exist or is not a file: {args.rule}")
    if not args.repo.is_dir():
        parser.error(f"repository does not exist or is not a directory: {args.repo}")
    return scan(args.rule, args.repo, args.semgrep, args.output_dir)


if __name__ == "__main__":
    raise SystemExit(main())
