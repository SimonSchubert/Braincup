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

# The Learn catalog moved out of Kotlin and into strings.xml on 2026-08-28 - roughly 2,500 keys of
# lesson text, all of it still English. Translating it is its own piece of work, so these are
# counted and reported but do not fail the check: without this every locale would read as broken
# and the UI chrome the check exists to guard would be buried under them.
PENDING_TRANSLATION_PREFIXES = (
    'learn_unit_',
    'learn_g12_', 'learn_g35_', 'learn_g68_', 'learn_g910_', 'learn_g1112_',
    'learn_arithmetic_', 'learn_geometry_',
    'learn_shape_', 'learn_shapeguide_',
    'learn_rule_', 'learn_rulesguide_',
    'learn_opt_',
    'learn_t_',
    'learn_shared_',
)


def is_pending(key: str) -> bool:
    # Plural keys are compared under a "plurals:" prefix so they cannot collide with a string of
    # the same name; the catalog test applies to the name underneath it either way.
    return key.removeprefix("plurals:").startswith(PENDING_TRANSLATION_PREFIXES)


# Numbers inside a lesson sentence - "eight steps left of -3 is -11" - are the lesson, and the
# figure beside them draws the same ones. A translation may move them but never change them, so
# rather than templating three hundred explanations into placeholders the numbers are simply
# compared: same multiset in, same multiset out.
NUMBER_PATTERN = re.compile(r'-?\d+(?:[.,]\d+)*')


def numbers_in(text: str) -> list[str]:
    return sorted(NUMBER_PATTERN.findall(text))


# An answer option is never a sentence a translator should meet with a number already baked into
# it. "enlargement by 2" and "enlargement by 3" are one sentence and two numbers: the sentence
# belongs here, the numbers belong in `learn/content` beside the figure that draws them. This has
# been got wrong three times by hand, so it is checked rather than reviewed.
OPTION_KEY_PATTERN = re.compile(r"^learn_.*_o\d+$")


def options_with_numbers(values: dict[str, str]) -> list[str]:
    return sorted(
        key
        for key, value in values.items()
        if OPTION_KEY_PATTERN.match(key) and NUMBER_PATTERN.search(value)
    )


def repeated_texts(values: dict[str, str]) -> dict[str, list[str]]:
    """Catalog texts stored under more than one key, which is one text translated many times."""
    seen: dict[str, list[str]] = {}
    for key, value in values.items():
        if is_pending(key) and CONTENT_KEY_PATTERN.search(key):
            seen.setdefault(value, []).append(key)
    return {text: sorted(keys) for text, keys in seen.items() if len(keys) > 1}


# Step and question content, which is where a repeat means the same sentence twice. A lesson title
# keeps its own key even when it reads like an option.
CONTENT_KEY_PATTERN = re.compile(
    r"_(o\d+|l\d+|prompt|question|explanation|body|problem|result|formula|name|fact|rule|meaning|example)$"
)


def duplicate_families(values: dict[str, str]) -> dict[str, list[str]]:
    """Catalog strings that are the same sentence once their numbers are blanked out."""
    families: dict[str, list[str]] = {}
    for key, value in values.items():
        if not is_pending(key) or not NUMBER_PATTERN.search(value):
            continue
        families.setdefault(NUMBER_PATTERN.sub("#", value), []).append(key)
    return {shape: sorted(keys) for shape, keys in families.items() if len(keys) > 1}


def extract_values(path: Path) -> dict[str, str]:
    text = path.read_text(encoding="utf-8")
    return dict(re.findall(r'<string\s+name="([^"]+)"\s*>(.*?)</string>', text, re.S))
PLURALS_NAME_PATTERN = re.compile(r'<plurals\s+name="([^"]+)"')
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
    pending_keys: list[str] = field(default_factory=list)
    changed_numbers: list[str] = field(default_factory=list)


@dataclass
class CheckResult:
    ok: bool
    base_locale: str
    base_key_count: int
    supported_locales: list[str]
    locale_reports: list[LocaleReport]
    missing_keys_in_base: list[str]
    missing_locale_files: list[str]
    numbered_options: list[str] = field(default_factory=list)
    repeated_sentences: dict[str, list[str]] = field(default_factory=dict)
    repeated_texts: dict[str, list[str]] = field(default_factory=dict)

    def issue_count(self) -> int:
        count = len(self.missing_keys_in_base)
        count += len(self.missing_locale_files)
        for report in self.locale_reports:
            count += len(report.missing_keys) + len(report.extra_keys) + len(report.changed_numbers)
        count += len(self.numbered_options) + len(self.repeated_sentences) + len(self.repeated_texts)
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
    if locale == "en":
        return "values"
    # BCP 47 region subtags (zh-TW) map to Android/Compose values-zh-rTW.
    parts = locale.split("-")
    if len(parts) == 2 and len(parts[1]) == 2 and parts[1].isalpha():
        return f"values-{parts[0]}-r{parts[1]}"
    return f"values-{locale}"


def strings_file(resources_dir: Path, locale: str) -> Path:
    return resources_dir / locale_folder(locale) / "strings.xml"


def extract_string_keys(path: Path) -> set[str]:
    """Every key a locale has to carry, plural sets included.

    A <plurals> is one key with several forms; which forms a locale needs is its own grammar's
    business, so only the name is compared. Prefixing it keeps a plural from ever looking
    interchangeable with a <string> of the same name.
    """
    text = path.read_text(encoding="utf-8")
    keys = set(STRING_NAME_PATTERN.findall(text))
    keys |= {f"plurals:{name}" for name in PLURALS_NAME_PATTERN.findall(text)}
    return keys


def check_localizations(
    resources_dir: Path,
    supported_locales: list[str],
) -> CheckResult:
    base_locale = "en"
    base_file = strings_file(resources_dir, base_locale)
    if not base_file.is_file():
        raise SystemExit(f"Base strings file not found: {base_file}")

    base_keys = extract_string_keys(base_file)
    base_values = extract_values(base_file)
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
        absent = base_keys - locale_keys
        locale_values = extract_values(path)
        # Catalog keys only. Elsewhere a locale may legitimately spell a number out
        # ("Move 1 matchstick" -> "Verschiebe ein Streichholz") or write a grade band its own way.
        changed_numbers = sorted(
            key
            for key, value in locale_values.items()
            if is_pending(key) and key in base_values and numbers_in(value) != numbers_in(base_values[key])
        )
        locale_reports.append(
            LocaleReport(
                locale=locale,
                folder=folder,
                file_exists=True,
                key_count=len(locale_keys),
                missing_keys=sorted(k for k in absent if not is_pending(k)),
                extra_keys=sorted(locale_keys - base_keys),
                pending_keys=sorted(k for k in absent if is_pending(k)),
                changed_numbers=changed_numbers,
            ),
        )

    missing_keys_in_base = sorted(all_locale_keys - base_keys)

    numbered_options = options_with_numbers(base_values)
    repeated_sentences = duplicate_families(base_values)
    repeats = repeated_texts(base_values)

    ok = (
        not numbered_options
        and not repeated_sentences
        and not repeats
        and not missing_locale_files
        and not missing_keys_in_base
        and all(
            not report.missing_keys and not report.extra_keys and not report.changed_numbers
            for report in locale_reports
        )
    )

    return CheckResult(
        ok=ok,
        base_locale=base_locale,
        base_key_count=len(base_keys),
        supported_locales=supported_locales,
        locale_reports=locale_reports,
        missing_keys_in_base=missing_keys_in_base,
        missing_locale_files=missing_locale_files,
        numbered_options=numbered_options,
        repeated_sentences=repeated_sentences,
        repeated_texts=repeats,
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

    if result.numbered_options:
        print("Answer options carrying a number, which belongs in learn/content instead:")
        for key in result.numbered_options:
            print(f"  {key}")
        print()

    if result.repeated_sentences:
        print("The same sentence stored more than once with different numbers:")
        for shape, keys in sorted(result.repeated_sentences.items()):
            print(f"  {shape!r}")
            for key in keys:
                print(f"      {key}")
        print()

    if result.repeated_texts:
        print("The same text stored under more than one key:")
        for text, keys in sorted(result.repeated_texts.items()):
            print(f"  {text!r}")
            for key in keys:
                print(f"      {key}")
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
        if report.changed_numbers:
            issues.append(f"{len(report.changed_numbers)} with changed numbers")

        if not issues:
            if not quiet:
                pending = f", {len(report.pending_keys)} Learn keys pending" if report.pending_keys else ""
                print(f"[{report.locale}] OK ({report.key_count} keys{pending})")
            continue

        print(f"[{report.locale}] {', '.join(issues)}:")
        for key in report.missing_keys:
            print(f"  - missing: {key}")
        for key in report.extra_keys:
            print(f"  - extra: {key}")
        for key in report.changed_numbers:
            print(f"  - numbers changed: {key}")
        print()

    pending = sum(len(r.pending_keys) for r in result.locale_reports)
    if result.ok:
        print("All strings are localized for every supported language.")
        if pending:
            locales = sum(1 for r in result.locale_reports if r.pending_keys)
            print(
                f"{pending // max(locales, 1)} Learn catalog keys are still English in "
                f"{locales} locales, and are not counted as failures.",
            )
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
        "pending_translation": sum(len(r.pending_keys) for r in result.locale_reports),
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