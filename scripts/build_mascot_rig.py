#!/usr/bin/env python3
"""Split the traced ic_mascot vector into animatable parts.

The drawable groups paths by fill colour, so the glasses frame and the toe caps share one
path. This script breaks every colour layer into subpaths, keeps holes (opposite winding)
with the shape they cut, assigns each shape to a body part by position, and writes the
result as absolute cubic curves to MascotRigData.kt. Layer order is preserved, so the rest
pose draws exactly like the original drawable.

Usage: scripts/build_mascot_rig.py
"""
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "composeApp/src/commonMain/composeResources/drawable/ic_mascot.xml"
TARGET = ROOT / "composeApp/src/commonMain/kotlin/com/inspiredandroid/braincup/ui/components/mascot/MascotRigData.kt"
ANDROID = "{http://schemas.android.com/apk/res/android}"

GLASSES_COLORS = {"#FEFEFF", "#4A403A", "#C2AEFD", "#2B2622"}
LENS_COLORS = {"#4A403A", "#2B2622"}
MOUTH_COLOR = "#A82D0C"
LEG_TOP = 630
SHOE_TOP = 712
MIDLINE_X = 340
# The shading the glasses cast along their lower edge; it has to travel with them when they move.
GLASSES_SHADOW_BOX = (90, 320, 680, 440)


def tokenize(data):
    return re.findall(r"[A-Za-z]|-?(?:\d+\.?\d*|\.\d+)(?:e-?\d+)?", data)


def parse_subpaths(data):
    """Returns subpaths as [start, (c1, c2, end), ...] in absolute coordinates."""
    tokens = tokenize(data)
    i = 0
    cmd = None
    cur = (0.0, 0.0)
    start = (0.0, 0.0)
    last_ctrl = None
    subpaths = []

    def num():
        nonlocal i
        value = float(tokens[i])
        i += 1
        return value

    while i < len(tokens):
        if tokens[i].isalpha():
            cmd = tokens[i]
            i += 1
        rel = cmd.islower()
        op = cmd.upper()
        ox, oy = cur if rel else (0.0, 0.0)
        if op == "M":
            cur = (ox + num(), oy + num())
            start = cur
            subpaths.append([cur])
            cmd = "l" if rel else "L"
            last_ctrl = None
        elif op == "C":
            c1 = (ox + num(), oy + num())
            c2 = (ox + num(), oy + num())
            end = (ox + num(), oy + num())
            subpaths[-1].append((c1, c2, end))
            cur, last_ctrl = end, c2
        elif op == "S":
            c1 = (2 * cur[0] - last_ctrl[0], 2 * cur[1] - last_ctrl[1]) if last_ctrl else cur
            c2 = (ox + num(), oy + num())
            end = (ox + num(), oy + num())
            subpaths[-1].append((c1, c2, end))
            cur, last_ctrl = end, c2
        elif op in "LHV":
            if op == "L":
                end = (ox + num(), oy + num())
            elif op == "H":
                end = ((cur[0] if rel else 0) + num(), cur[1])
            else:
                end = (cur[0], (cur[1] if rel else 0) + num())
            subpaths[-1].append((cur, end, end))
            cur, last_ctrl = end, None
        elif op == "Z":
            cur = start
        else:
            sys.exit(f"unsupported command {cmd}")
    return subpaths


def points(sub):
    yield sub[0]
    for seg in sub[1:]:
        yield from seg


def bbox(sub):
    xs, ys = zip(*points(sub))
    return min(xs), min(ys), max(xs), max(ys)


def contains(outer, inner):
    return outer[0] <= inner[0] and outer[1] <= inner[1] and outer[2] >= inner[2] and outer[3] >= inner[3]


def signed_area(sub):
    pts = [sub[0]] + [seg[2] for seg in sub[1:]]
    return sum(a[0] * b[1] - b[0] * a[1] for a, b in zip(pts, pts[1:] + pts[:1])) / 2


def classify(color, box):
    cx, cy = (box[0] + box[2]) / 2, (box[1] + box[3]) / 2
    side = "LEFT" if cx < MIDLINE_X else "RIGHT"
    if cy > SHOE_TOP:
        return f"{side}_SHOE"
    if cy > LEG_TOP:
        return f"{side}_LEG"
    if color == MOUTH_COLOR:
        return "MOUTH"
    if color in GLASSES_COLORS:
        return "LENS" if color in LENS_COLORS else "GLASSES"
    if contains(GLASSES_SHADOW_BOX, box):
        return "GLASSES_SHADOW"
    return "BODY"


def fmt(v):
    return f"{v:.1f}".rstrip("0").rstrip(".")


def main():
    root = ET.parse(SOURCE).getroot()
    shapes = []
    for element in root.iter("path"):
        color = element.get(ANDROID + "fillColor")
        subs = parse_subpaths(element.get(ANDROID + "pathData"))
        boxes = [bbox(s) for s in subs]
        areas = [signed_area(s) for s in subs]
        parts = []
        for index, box in enumerate(boxes):
            # A hole winds against its parent; under the nonzero rule it only cuts while both
            # shapes are drawn together, so it must land in the same part.
            parent = next(
                (
                    p
                    for p in range(index)
                    if boxes[p] != box and contains(boxes[p], box) and areas[p] * areas[index] < 0
                ),
                None,
            )
            parts.append(parts[parent] if parent is not None else classify(color, box))
        grouped = {}
        for sub, part in zip(subs, parts):
            grouped.setdefault(part, []).append(sub)
        for part, members in grouped.items():
            data = " ".join(
                "M " + " ".join(fmt(c) for p in points(sub) for c in p) for sub in members
            )
            shapes.append((part, color, data))

    lines = [
        "// Generated by scripts/build_mascot_rig.py from drawable/ic_mascot.xml. Do not edit.",
        "package com.inspiredandroid.braincup.ui.components.mascot",
        "",
        "internal const val MASCOT_VIEWPORT_WIDTH = %sf" % root.get(ANDROID + "viewportWidth"),
        "internal const val MASCOT_VIEWPORT_HEIGHT = %sf" % root.get(ANDROID + "viewportHeight"),
        "",
        "internal val mascotRigShapes: List<MascotShapeSource> = listOf(",
    ]
    for part, color, data in shapes:
        lines.append(f'    MascotShapeSource(MascotPart.{part}, 0xFF{color[1:]}, "{data}"),')
    lines += [")", ""]
    TARGET.parent.mkdir(parents=True, exist_ok=True)
    TARGET.write_text("\n".join(lines))
    counts = {}
    for part, _, _ in shapes:
        counts[part] = counts.get(part, 0) + 1
    print(f"{len(shapes)} shapes -> {TARGET.relative_to(ROOT)}: {counts}")


if __name__ == "__main__":
    main()
