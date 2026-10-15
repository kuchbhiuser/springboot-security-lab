#!/usr/bin/env python3
"""Summarize a completed manual TP/FP review CSV."""
import argparse
import csv
import sys
from collections import Counter, defaultdict
from pathlib import Path


def summarize(path: Path) -> int:
    with path.open(newline="", encoding="utf-8") as handle:
        reader = csv.DictReader(handle)
        reader.fieldnames = [name.lstrip("\ufeff").strip().lower() for name in (reader.fieldnames or [])]
        rows = [{(k or "").strip().lower(): v for k, v in row.items()} for row in reader]
    if not rows or "verdict" not in rows[0] or "reason" not in rows[0] or "ruleid" not in rows[0]:
        print("Review CSV must contain Verdict and Reason columns.", file=sys.stderr)
        return 2
    bad = [i + 2 for i, row in enumerate(rows) if (row.get("verdict") or "").strip().upper() not in {"TP", "FP"} or not (row.get("reason") or "").strip() or not (row.get("ruleid") or "").strip()]
    if bad:
        print(f"Incomplete review at CSV row(s): {', '.join(map(str, bad))}", file=sys.stderr)
        return 2
    grouped = defaultdict(Counter)
    for row in rows:
        grouped[row["ruleid"].strip()][row["verdict"].strip().upper()] += 1
    print(f"Total reviewed: {len(rows)}")
    for rule_id in sorted(grouped):
        print(f"{rule_id}: TP {grouped[rule_id]['TP']}, FP {grouped[rule_id]['FP']}")
        print(f"Total findings: {sum(grouped[rule_id].values())}")
    return 0


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("csv", type=Path)
    args = parser.parse_args(argv)
    return summarize(args.csv)


if __name__ == "__main__":
    raise SystemExit(main())
