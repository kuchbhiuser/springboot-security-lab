import csv
import io
import json
import tempfile
import unittest
from contextlib import redirect_stdout, redirect_stderr
from pathlib import Path
from unittest.mock import patch

import scan_semgrep
import summarize_review


class ToolTests(unittest.TestCase):
    def test_scan_exports_json_csv_and_summary_with_quoted_message(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); rule = root / "rule.yml"; repo = root / "repo"; out = root / "out"
            rule.write_text("rules: []"); repo.mkdir()
            payload = {"results": [{"check_id": "r1", "path": "a,b.java", "start": {"line": 3}, "end": {"line": 4}, "extra": {"message": 'say "hi", now'}}]}
            with patch("scan_semgrep.subprocess.run", return_value=type("P", (), {"returncode": 0, "stdout": json.dumps(payload), "stderr": ""})()):
                buf = io.StringIO()
                with redirect_stdout(buf): self.assertEqual(scan_semgrep.scan(rule, repo, "semgrep", out), 0)
            self.assertIn("r1: 1", buf.getvalue())
            with (out / "findings.csv").open(newline="") as handle:
                row = next(csv.DictReader(handle))
            self.assertEqual(row["Message"], 'say "hi", now')

    def test_scan_preserves_stderr_and_exit_code(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); rule = root / "r"; repo = root / "repo"; rule.write_text(""); repo.mkdir()
            err = io.StringIO()
            with patch("scan_semgrep.subprocess.run", return_value=type("P", (), {"returncode": 7, "stdout": "", "stderr": "boom\n"})()), redirect_stderr(err):
                self.assertEqual(scan_semgrep.scan(rule, repo, "semgrep", root / "out"), 7)
            self.assertEqual(err.getvalue(), "boom\n")

    def test_scan_errors_exit_zero_keeps_raw_json_and_cleans_outputs(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); rule = root / "r"; repo = root / "repo"; out = root / "out"
            rule.write_text(""); repo.mkdir(); out.mkdir(); (out / "findings.csv").write_text("old")
            raw = '{"errors":[{"message":"bad"}]}\n'
            proc = type("P", (), {"returncode": 0, "stdout": raw, "stderr": ""})()
            with patch("scan_semgrep.subprocess.run", return_value=proc):
                self.assertEqual(scan_semgrep.scan(rule, repo, "semgrep", out), 1)
            self.assertEqual((out / "semgrep.json").read_text(), raw)
            self.assertFalse((out / "findings.csv").exists())

    def test_scan_missing_executable(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); rule = root / "r"; repo = root / "repo"; rule.write_text(""); repo.mkdir()
            with patch("scan_semgrep.subprocess.run", side_effect=OSError("missing")):
                self.assertEqual(scan_semgrep.scan(rule, repo, "missing", root / "out"), 127)

    def test_review_rejects_incomplete_and_counts_valid(self):
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "review.csv"
            path.write_text("RuleID,Verdict,Reason\nr,TP,confirmed\nr,FP,noise\n")
            out = io.StringIO()
            with redirect_stdout(out): self.assertEqual(summarize_review.summarize(path), 0)
            self.assertIn("r: TP 1, FP 1", out.getvalue())
            path.write_text("RuleID,Verdict,Reason\nr,,\n")
            self.assertEqual(summarize_review.summarize(path), 2)


if __name__ == "__main__":
    unittest.main()
