#!/usr/bin/env python3
"""Hand the Learn Math catalog to a translator one chunk at a time, and merge the answer back.

The catalog is 1,242 strings and 9 plural sets per locale. Editing that by hand in a 2,000-line
XML file is where ordering drifts, keys get duplicated and colour markers quietly vanish, so a
translator never touches the file: it reads a chunk of English out as JSON and writes the
translation back in as JSON, and this script does the XML.

A locale is translated in place and resumable. `pending` only ever reports keys the locale does
not already have, so an interrupted run picks up where it stopped, and a key that never arrives
falls back to English at runtime rather than showing blank.

Examples:
  ./scripts/learn_translate.py chunks --locale es
  ./scripts/learn_translate.py pending --locale es --chunk geometry-circles > chunk.json
  ./scripts/learn_translate.py merge --locale es --file translated.json
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "composeApp/src/commonMain/composeResources"

# Kept in step with PENDING_TRANSLATION_PREFIXES in check_localizations.py.
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
STRING_NAME_PATTERN = re.compile(r'<string\s+name="([^"]+)"')
PLURALS_PATTERN = re.compile(r'<plurals\s+name="([^"]+)"\s*>(.*?)</plurals>', re.S)
PLURALS_NAME_PATTERN = re.compile(r'<plurals\s+name="([^"]+)"')
PLURAL_ITEM_PATTERN = re.compile(r'<item\s+quantity="([^"]+)"\s*>(.*?)</item>', re.S)

# A grade prefix says when a lesson is met, not what it is about, so it is dropped before grouping:
# `learn_g12_arithmetic_counting_s1_body` and `learn_unit_arithmetic_counting_title` belong to the
# same chunk. Everything a translator has to keep consistent then lands in one chunk together.
GROUP_STRIP = re.compile(r"^learn_(?:unit_)?(?:g\d+_)?")


def is_catalog(key: str) -> bool:
    return key.startswith(CATALOG_PREFIXES)


def chunk_of(key: str) -> str:
    rest = GROUP_STRIP.sub("", key)
    parts = rest.split("_")
    if parts[0] in {"shape", "shapeguide"}:
        return "guide-shapes"
    if parts[0] in {"rule", "rulesguide"}:
        return "guide-rules"
    if parts[0] in {"opt", "t", "shared"}:
        return "shared-phrases"
    return "-".join(parts[:2]) if len(parts) > 1 else parts[0]


def locale_folder(locale: str) -> str:
    if locale == "en":
        return "values"
    parts = locale.split("-")
    if len(parts) == 2 and len(parts[1]) == 2 and parts[1].isalpha():
        return f"values-{parts[0]}-r{parts[1]}"
    return f"values-{locale}"


def strings_path(locale: str) -> Path:
    return RESOURCES / locale_folder(locale) / "strings.xml"


def read_strings(path: Path) -> dict[str, str]:
    return dict(STRING_PATTERN.findall(path.read_text(encoding="utf-8")))


def read_plurals(path: Path) -> dict[str, dict[str, str]]:
    text = path.read_text(encoding="utf-8")
    return {
        name: dict(PLURAL_ITEM_PATTERN.findall(body))
        for name, body in PLURALS_PATTERN.findall(text)
    }


def base_key_order(path: Path) -> list[str]:
    """Every key in the order the base file declares it, plurals included."""
    text = path.read_text(encoding="utf-8")
    found: list[tuple[int, str]] = []
    for match in STRING_NAME_PATTERN.finditer(text):
        found.append((match.start(), match.group(1)))
    for match in PLURALS_NAME_PATTERN.finditer(text):
        found.append((match.start(), f"plurals:{match.group(1)}"))
    return [key for _, key in sorted(found)]


def unescape(text: str) -> str:
    return text.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")


def escape(text: str) -> str:
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def pending_for(locale: str) -> tuple[dict[str, str], dict[str, dict[str, str]]]:
    base = strings_path("en")
    target = strings_path(locale)
    if not target.is_file():
        raise SystemExit(f"No strings file for {locale}: {target}")

    base_strings = read_strings(base)
    base_plurals = read_plurals(base)
    have_strings = set(read_strings(target))
    have_plurals = set(read_plurals(target))

    strings = {
        key: unescape(value)
        for key, value in base_strings.items()
        if is_catalog(key) and key not in have_strings
    }
    plurals = {
        name: {q: unescape(v) for q, v in forms.items()}
        for name, forms in base_plurals.items()
        if is_catalog(name) and name not in have_plurals
    }
    return strings, plurals


def cmd_chunks(args: argparse.Namespace) -> int:
    strings, plurals = pending_for(args.locale)
    counts: dict[str, int] = {}
    for key in strings:
        counts[chunk_of(key)] = counts.get(chunk_of(key), 0) + 1
    for name in plurals:
        counts[chunk_of(name)] = counts.get(chunk_of(name), 0) + 1

    if args.json:
        print(json.dumps({"locale": args.locale, "chunks": counts}, indent=2, ensure_ascii=False))
        return 0

    total = sum(counts.values())
    if not total:
        print(f"[{args.locale}] catalog is complete, nothing pending")
        return 0
    print(f"[{args.locale}] {total} pending across {len(counts)} chunks")
    for chunk, count in sorted(counts.items(), key=lambda item: (-item[1], item[0])):
        print(f"  {count:5}  {chunk}")
    return 0


def cmd_pending(args: argparse.Namespace) -> int:
    strings, plurals = pending_for(args.locale)
    if args.chunk:
        strings = {k: v for k, v in strings.items() if chunk_of(k) == args.chunk}
        plurals = {k: v for k, v in plurals.items() if chunk_of(k) == args.chunk}
        if not strings and not plurals:
            raise SystemExit(f"No pending keys in chunk {args.chunk!r} for {args.locale}")

    payload = {
        "locale": args.locale,
        "chunk": args.chunk or "all",
        "strings": strings,
        "plurals": plurals,
    }
    print(json.dumps(payload, indent=2, ensure_ascii=False))
    return 0


def render_plural(name: str, forms: dict[str, str], indent: str) -> list[str]:
    order = ["zero", "one", "two", "few", "many", "other"]
    ordered = [q for q in order if q in forms] + [q for q in forms if q not in order]
    lines = [f'{indent}<plurals name="{name}">']
    for quantity in ordered:
        lines.append(f'{indent}    <item quantity="{quantity}">{escape(forms[quantity])}</item>')
    lines.append(f"{indent}</plurals>")
    return lines


def cmd_merge(args: argparse.Namespace) -> int:
    payload = json.loads(Path(args.file).read_text(encoding="utf-8"))
    locale = args.locale or payload.get("locale")
    if not locale:
        raise SystemExit("No locale given, and none in the file")

    new_strings: dict[str, str] = payload.get("strings") or {}
    new_plurals: dict[str, dict[str, str]] = payload.get("plurals") or {}
    if not new_strings and not new_plurals:
        raise SystemExit("Nothing to merge: no strings and no plurals in the file")

    base = strings_path("en")
    target = strings_path(locale)
    base_strings = read_strings(base)
    base_plurals = read_plurals(base)
    order = base_key_order(base)
    position = {key: i for i, key in enumerate(order)}

    unknown = sorted(
        [k for k in new_strings if k not in base_strings]
        + [k for k in new_plurals if k not in base_plurals],
    )
    if unknown:
        raise SystemExit("Keys not in the base file: " + ", ".join(unknown[:10]))

    lines = target.read_text(encoding="utf-8").split("\n")
    added = skipped = revised = 0

    def line_index(key: str) -> int | None:
        needle = (
            f'<plurals name="{key[len("plurals:"):]}"'
            if key.startswith("plurals:")
            else f'<string name="{key}"'
        )
        for i, line in enumerate(lines):
            if needle in line:
                return i
        return None

    def replace(key: str, block: list[str]) -> bool:
        """Swap a key already in the file for a new rendering of it, in place."""
        start = line_index(key)
        if start is None:
            return False
        end = start
        if key.startswith("plurals:"):
            while end < len(lines) and "</plurals>" not in lines[end]:
                end += 1
        lines[start : end + 1] = block
        return True

    def insert(key: str, block: list[str]) -> None:
        nonlocal added, skipped, revised
        if line_index(key) is not None:
            if args.force:
                replace(key, block)
                revised += 1
            else:
                skipped += 1
            return
        # Walk back through the base ordering to the nearest key this locale already carries, and
        # land just after it. That keeps a locale file reading in the same order as the base even
        # though older files have drifted and cannot be diffed line for line.
        at = position[key]
        for previous in reversed(order[:at]):
            index = line_index(previous)
            if index is None:
                continue
            end = index
            if previous.startswith("plurals:"):
                while end < len(lines) and "</plurals>" not in lines[end]:
                    end += 1
            lines[end + 1 : end + 1] = block
            added += 1
            return
        raise SystemExit(f"No anchor found for {key} in {target}")

    for key in sorted(new_strings, key=lambda k: position[k]):
        insert(key, [f'    <string name="{key}">{escape(new_strings[key])}</string>'])
    for name in sorted(new_plurals, key=lambda k: position[f"plurals:{k}"]):
        insert(f"plurals:{name}", render_plural(name, new_plurals[name], "    "))

    target.write_text("\n".join(lines), encoding="utf-8")
    note = "".join(
        [f", {revised} revised" if revised else "", f", {skipped} already present" if skipped else ""],
    )
    print(f"[{locale}] merged {added} key(s){note}")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    chunks = sub.add_parser("chunks", help="list the pending chunks for a locale and their sizes")
    chunks.add_argument("--locale", required=True)
    chunks.add_argument("--json", action="store_true")
    chunks.set_defaults(func=cmd_chunks)

    pending = sub.add_parser("pending", help="dump untranslated catalog keys as JSON")
    pending.add_argument("--locale", required=True)
    pending.add_argument("--chunk", help="only this chunk (see `chunks`)")
    pending.set_defaults(func=cmd_pending)

    merge = sub.add_parser("merge", help="merge a translated JSON file into a locale")
    merge.add_argument("--locale")
    merge.add_argument("--file", required=True)
    merge.add_argument(
        "--force",
        action="store_true",
        help="overwrite keys the locale already has, for revising a translation in place",
    )
    merge.set_defaults(func=cmd_merge)

    args = parser.parse_args()
    return args.func(args)


if __name__ == "__main__":
    sys.exit(main())
