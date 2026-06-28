#!/usr/bin/env python3
"""Verify composeResources string keys exist for every supported locale.

Reads the canonical locale list from androidApp/src/main/res/xml/locales_config.xml
and compares composeApp/.../composeResources/values*/strings.xml against the base
English file (values/strings.xml).

Exit code 0 when all locales are complete; 1 when any keys or locale files are missing.

Examples:
  ./scripts/check_localizations.py
  ./scripts/check_localizations.py --json
  ./scripts/check_localizations.py --locale de
  ./gradlew checkLocalizations
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

STRING_NAME_PATTERN = re.compile(r'<string\s+name="([^"]+)"')
LOCALE_NAME_PATTERN = re.compile(r'<locale\s+android:name="([^"]+)"')

DEFAULT_RESOURCES_DIR = (
    Path(__file__).resolve().parents[1]
    / "composeApp/src/commonMain/composeResources"
)
DEFAULT_LOCALES_CONFIG = (
    Path(__file__).resolve().parents[1]
    / "androidApp/src/main/res/xml/locales_config.xml"
)


@dataclass
class LocaleReport:
    locale: str
    folder: str
    file_exists: bool
    key_count: int = 0
    missing_keys: list[str] = field(default_factory=list)
    extra_keys: list[str] = field(default_factory=list)


@dataclass
class CheckResult:
    ok: bool
    base_locale: str
    base_key_count: int
    supported_locales: list[str]
    locale_reports: list[LocaleReport]
    missing_keys_in_base: list[str]
    missing_locale_files: list[str]

    def issue_count(self) -> int:
        count = len(self.missing_keys_in_base)
        count += len(self.missing_locale_files)
        for report in self.locale_reports:
            count += len(report.missing_keys) + len(report.extra_keys)
        return count


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Check that all string resources are localized for supported languages.",
    )
    parser.add_argument(
        "--resources-dir",
        type=Path,
        default=DEFAULT_RESOURCES_DIR,
        help="composeResources directory (default: composeApp/.../composeResources)",
    )
    parser.add_argument(
        "--locales-config",
        type=Path,
        default=DEFAULT_LOCALES_CONFIG,
        help="Android locales_config.xml with supported locale codes",
    )
    parser.add_argument(
        "--locale",
        action="append",
        dest="locales",
        metavar="LOCALE",
        help="Only check these locale codes (repeatable). Default: all supported locales.",
    )
    parser.add_argument(
        "--json",
        action="store_true",
        help="Print machine-readable JSON (for agents and CI parsers)",
    )
    parser.add_argument(
        "--quiet",
        action="store_true",
        help="Only print errors and summary (no success lines)",
    )
    return parser.parse_args()


def load_supported_locales(locales_config: Path) -> list[str]:
    if not locales_config.is_file():
        raise SystemExit(f"Locales config not found: {locales_config}")

    text = locales_config.read_text(encoding="utf-8")
    locales = LOCALE_NAME_PATTERN.findall(text)
    if not locales:
        raise SystemExit(f"No locales found in {locales_config}")
    return locales


def locale_folder(locale: str) -> str:
    return "values" if locale == "en" else f"values-{locale}"


def strings_file(resources_dir: Path, locale: str) -> Path:
    return resources_dir / locale_folder(locale) / "strings.xml"


def extract_string_keys(path: Path) -> set[str]:
    text = path.read_text(encoding="utf-8")
    return set(STRING_NAME_PATTERN.findall(text))


def check_localizations(
    resources_dir: Path,
    supported_locales: list[str],
) -> CheckResult:
    base_locale = "en"
    base_file = strings_file(resources_dir, base_locale)
    if not base_file.is_file():
        raise SystemExit(f"Base strings file not found: {base_file}")

    base_keys = extract_string_keys(base_file)
    all_locale_keys: set[str] = set()
    locale_reports: list[LocaleReport] = []
    missing_locale_files: list[str] = []

    for locale in supported_locales:
        path = strings_file(resources_dir, locale)
        folder = locale_folder(locale)
        if not path.is_file():
            missing_locale_files.append(locale)
            locale_reports.append(
                LocaleReport(
                    locale=locale,
                    folder=folder,
                    file_exists=False,
                ),
            )
            continue

        locale_keys = extract_string_keys(path)
        all_locale_keys |= locale_keys
        locale_reports.append(
            LocaleReport(
                locale=locale,
                folder=folder,
                file_exists=True,
                key_count=len(locale_keys),
                missing_keys=sorted(base_keys - locale_keys),
                extra_keys=sorted(locale_keys - base_keys),
            ),
        )

    missing_keys_in_base = sorted(all_locale_keys - base_keys)

    ok = (
        not missing_locale_files
        and not missing_keys_in_base
        and all(not report.missing_keys and not report.extra_keys for report in locale_reports)
    )

    return CheckResult(
        ok=ok,
        base_locale=base_locale,
        base_key_count=len(base_keys),
        supported_locales=supported_locales,
        locale_reports=locale_reports,
        missing_keys_in_base=missing_keys_in_base,
        missing_locale_files=missing_locale_files,
    )


def print_human_report(result: CheckResult, quiet: bool) -> None:
    if not quiet:
        print(
            f"Base locale ({result.base_locale}): {result.base_key_count} keys "
            f"in {locale_folder(result.base_locale)}/strings.xml",
        )
        print(f"Supported locales: {', '.join(result.supported_locales)}")
        print()

    if result.missing_locale_files:
        print("Missing locale files:")
        for locale in result.missing_locale_files:
            folder = locale_folder(locale)
            print(f"  [{locale}] {folder}/strings.xml does not exist")
        print()

    if result.missing_keys_in_base:
        print(f"Missing keys in base ({result.base_locale}):")
        for key in result.missing_keys_in_base:
            print(f"  {key}")
        print()

    for report in result.locale_reports:
        if not report.file_exists:
            continue

        issues: list[str] = []
        if report.missing_keys:
            issues.append(f"{len(report.missing_keys)} missing")
        if report.extra_keys:
            issues.append(f"{len(report.extra_keys)} extra")

        if not issues:
            if not quiet:
                print(f"[{report.locale}] OK ({report.key_count} keys)")
            continue

        print(f"[{report.locale}] {', '.join(issues)}:")
        for key in report.missing_keys:
            print(f"  - missing: {key}")
        for key in report.extra_keys:
            print(f"  - extra: {key}")
        print()

    if result.ok:
        print("All strings are localized for every supported language.")
    else:
        print(f"Localization check failed ({result.issue_count()} issue(s)).")


def print_json_report(result: CheckResult) -> None:
    payload = {
        "ok": result.ok,
        "base_locale": result.base_locale,
        "base_key_count": result.base_key_count,
        "supported_locales": result.supported_locales,
        "missing_locale_files": result.missing_locale_files,
        "missing_keys_in_base": result.missing_keys_in_base,
        "locales": [
            {
                "locale": report.locale,
                "folder": report.folder,
                "file_exists": report.file_exists,
                "key_count": report.key_count,
                "missing_keys": report.missing_keys,
                "extra_keys": report.extra_keys,
            }
            for report in result.locale_reports
        ],
        "issue_count": result.issue_count(),
    }
    print(json.dumps(payload, indent=2, ensure_ascii=False))


def main() -> int:
    args = parse_args()

    if not args.resources_dir.is_dir():
        raise SystemExit(f"Resources directory not found: {args.resources_dir}")

    supported_locales = load_supported_locales(args.locales_config)
    if args.locales:
        unknown = sorted(set(args.locales) - set(supported_locales))
        if unknown:
            raise SystemExit(
                f"Unknown locale(s): {', '.join(unknown)}. "
                f"Supported: {', '.join(supported_locales)}",
            )
        supported_locales = [locale for locale in supported_locales if locale in args.locales]

    result = check_localizations(args.resources_dir, supported_locales)

    if args.json:
        print_json_report(result)
    else:
        print_human_report(result, quiet=args.quiet)

    return 0 if result.ok else 1


if __name__ == "__main__":
    sys.exit(main())