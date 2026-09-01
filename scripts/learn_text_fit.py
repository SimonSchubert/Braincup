#!/usr/bin/env python3
"""Flag Learn catalog translations long enough to overflow the space they render into.

The catalog draws into cards, small answer buttons and canvas figures with fixed room, so a
translation half again as long as the English does not wrap, it clips. Rendering all ~1,570 frames
in 50 locales to find that out is 78,500 images nobody will look at, so this measures the text
instead and names the handful worth rendering.

Budgets come from German, which is complete, checked, and the most verbose language in the set: a
string longer than the longest German string of its kind has left the range the layout is known to
survive. Advisory, not a build failure - a long string may still fit. Use it to pick what to look
at, not to fail a build.

Examples:
  ./scripts/learn_text_fit.py --locale es
  ./scripts/learn_text_fit.py --all --top 5
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "composeApp/src/commonMain/composeResources"

CATALOG_PREFIXES = (
    "learn_unit_",
    "learn_g12_", "learn_g35_", "learn_g68_", "learn_g910_", "learn_g1112_",
    "learn_algebra_", "learn_arithmetic_", "learn_geometry_",
    "learn_shape_", "learn_shapeguide_",
    "learn_rule_", "learn_rulesguide_",
    "learn_opt_",
    "learn_t_",
    "learn_shared_",
)

STRING_PATTERN = re.compile(r'<string\s+name="([^"]+)"\s*>(.*?)</string>', re.S)

# Longest German string of each kind, plus headroom. A `name` heads a shape-guide entry in one
# line, an `option` sits in a small button, a `title` heads a card: those three are the tight ones.
BUDGETS: dict[str, int] = {
    "name": 24,
    "title": 45,
    "option": 50,
    "fact": 65,
    "summary": 95,
    "line": 95,
    "other": 115,
    "prompt": 120,
    "question": 165,
    "explanation": 205,
    "body": 265,
}

CLASSES: list[tuple[re.Pattern[str], str]] = [
    (re.compile(r"_o\d+$"), "option"),
    (re.compile(r"_l\d+$"), "line"),
    (re.compile(r"_title$"), "title"),
    (re.compile(r"_summary$"), "summary"),
    (re.compile(r"_name$"), "name"),
    (re.compile(r"_prompt$"), "prompt"),
    (re.compile(r"_question$"), "question"),
    (re.compile(r"_explanation$"), "explanation"),
    (re.compile(r"_body$"), "body"),
    (re.compile(r"_fact$"), "fact"),
    (re.compile(r"_rule$"), "rule"),
]


def key_class(key: str) -> str:
    for pattern, name in CLASSES:
        if pattern.search(key):
            return name
    return "other"


def locale_folder(locale: str) -> str:
    if locale == "en":
        return "values"
    parts = locale.split("-")
    if len(parts) == 2 and len(parts[1]) == 2 and parts[1].isalpha():
        return f"values-{parts[0]}-r{parts[1]}"
    return f"values-{locale}"


def read_catalog(locale: str) -> dict[str, str]:
    path = RESOURCES / locale_folder(locale) / "strings.xml"
    if not path.is_file():
        return {}
    return {
        key: value
        for key, value in STRING_PATTERN.findall(path.read_text(encoding="utf-8"))
        if key.startswith(CATALOG_PREFIXES)
    }


def overlong(locale: str, english: dict[str, str]) -> list[tuple[int, int, str, str, str]]:
    """(length, budget, key, text, english) for every string past its budget, longest first."""
    found = []
    for key, value in read_catalog(locale).items():
        if key not in english:
            continue
        budget = BUDGETS.get(key_class(key), BUDGETS["other"])
        text = re.sub(r"\{[abc]:([^}]*)\}", r"\1", value)
        if len(text) > budget:
            found.append((len(text), budget, key, value, english[key]))
    return sorted(found, reverse=True)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--locale", action="append", dest="locales")
    parser.add_argument("--all", action="store_true", help="every locale with catalog content")
    parser.add_argument("--top", type=int, default=10, help="worst N per locale (default 10)")
    args = parser.parse_args()

    english = read_catalog("en")
    if args.all:
        locales = sorted(
            path.parent.name.replace("values-", "").replace("-r", "-")
            for path in RESOURCES.glob("values-*/strings.xml")
        )
    elif args.locales:
        locales = args.locales
    else:
        raise SystemExit("Give --locale or --all")

    total = 0
    for locale in locales:
        found = overlong(locale, english)
        if not found:
            continue
        total += len(found)
        print(f"[{locale}] {len(found)} over budget")
        for length, budget, key, text, source in found[: args.top]:
            print(f"  {length:4} / {budget:<4} {key}")
            print(f"       en: {source}")
            print(f"       {locale}: {text}")
        if len(found) > args.top:
            print(f"  ... and {len(found) - args.top} more")
        print()

    print(f"{total} string(s) over budget across {len(locales)} locale(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
