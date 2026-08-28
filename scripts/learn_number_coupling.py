#!/usr/bin/env python3
"""Report which translated Learn sentences quote a number their own step declares.

A lesson step writes its arithmetic in Kotlin - `math("{a:6} + {b:7} = ?")`, its options, its
answer, its figure. Its prose often repeats one of those numbers: "the frame holds ten and {a:7}
are filled, so {b:3} squares are still empty." Change the step and that sentence is wrong, in
English and in every locale that has translated it.

The numbers could not simply be pulled out into placeholders. A number in a lesson sentence is
either the step's arithmetic or a fact being taught - "angles on a straight line add to 180" - and
nothing in the text distinguishes them. Templating the wrong one would let a later edit silently
make the maths wrong, which is worse than re-translating a sentence.

So it is reported instead. Run this before changing a step's numbers to see what else has to
change; `check_localizations.py` then fails the build for any locale left behind.

  ./scripts/learn_number_coupling.py                 every coupled sentence, by lesson
  ./scripts/learn_number_coupling.py --step g12-arithmetic-counting
  ./scripts/learn_number_coupling.py --summary       counts only
"""
from __future__ import annotations

import argparse
import pathlib
import re
import sys
import xml.etree.ElementTree as ET

ROOT = pathlib.Path(__file__).resolve().parents[1]
LEARN = ROOT / "composeApp/src/commonMain/kotlin/com/inspiredandroid/braincup/learn"
STRINGS = ROOT / "composeApp/src/commonMain/composeResources/values/strings.xml"
CONTENT = [LEARN / "content/ArithmeticContent.kt", LEARN / "content/GeometryContent.kt"]

STEP_START = re.compile(r"^\s*(Concept|Worked|Choice|Numeric|QuizQuestion)\(")
LESSON_ID = re.compile(r'^\s*id = "([^"]+)",$')
UNIT_SLUG = re.compile(r'^\s*urlSlug = "([^"]+)",$')
QUIZ_START = re.compile(r"^\s*questions = listOf\($")
RESOURCE = re.compile(r"Res\.(?:string|plurals)\.(\w+)")
NUMBER = re.compile(r"-?\d+(?:[.,]\d+)*")

# What the step itself says the sum is: its notation, the values it fills a template with, the
# answer it compares against, its options, and the numbers its figure is drawn from.
DECLARED = re.compile(
    r'math\("([^"]*)"\)'
    r'|filled\([^)]*?\)'
    r'|answer = "([^"]*)"'
    r"|mathOptions\(([^)]*)\)"
    r"|counted\([^,]+,\s*(\d+)\)"
    r"|visual = \w+\(([^)]*)\)",
)


def catalog_text() -> dict[str, str]:
    root = ET.parse(STRINGS).getroot()
    return {el.get("name"): (el.text or "") for el in root if el.tag == "string"}


def steps() -> list[tuple[str, int, set[str], list[str]]]:
    """Every step as (lesson id, index within it, declared numbers, resource keys)."""
    found: list[tuple[str, int, set[str], list[str]]] = []
    for path in CONTENT:
        lesson, unit, index, keys, numbers = "?", "?", 0, [], set()

        def flush() -> None:
            if keys:
                found.append((lesson, index, numbers, list(keys)))

        for line in path.read_text().split("\n"):
            slug = UNIT_SLUG.match(line)
            if slug:
                unit = slug.group(1)
            match = LESSON_ID.match(line)
            if match:
                # The step still open belongs to the lesson that is ending, not the one starting.
                flush()
                lesson, index, keys, numbers = match.group(1), 0, [], set()
                continue
            if QUIZ_START.match(line):
                flush()
                lesson, index, keys, numbers = f"{unit} test", 0, [], set()
                continue
            if STEP_START.match(line):
                flush()
                index += 1
                keys, numbers = [], set()
            keys += RESOURCE.findall(line)
            for hit in DECLARED.finditer(line):
                numbers |= set(re.findall(r"-?\d+(?:\.\d+)?", hit.group(0)))
        flush()
    return found


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--step", help="only steps of this lesson id")
    parser.add_argument("--summary", action="store_true", help="counts only")
    args = parser.parse_args()

    text = catalog_text()
    coupled = 0
    translated = 0
    for lesson, index, numbers, keys in steps():
        if args.step and args.step != lesson:
            continue
        rows = []
        for key in keys:
            value = text.get(key)
            if not value:
                continue
            translated += 1
            quoted = sorted({n for n in NUMBER.findall(value) if n in numbers})
            if quoted:
                coupled += 1
                rows.append((key, quoted, value))
        if rows and not args.summary:
            print(f"{lesson} step {index}  declares {sorted(numbers, key=len)}")
            for key, quoted, value in rows:
                print(f"    quotes {quoted}")
                print(f"      {key}")
                print(f"      {value}")
            print()

    print(f"{coupled} of {translated} translated catalog sentences quote a number their step declares.")
    print("Changing that step's arithmetic means changing the sentence, and re-translating it.")
    if coupled:
        print("check_localizations.py fails the build for any locale left behind.")


if __name__ == "__main__":
    sys.exit(main())
