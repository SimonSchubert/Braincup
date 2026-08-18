#!/usr/bin/env python3
"""Render the 1024x500 feature graphic from the in-app mascot drawable.

The mascot comes straight from composeApp/.../drawable/ic_mascot.xml, so re-running this after
the mascot changes keeps every copy of the graphic in sync. It is written to both places that
need it: the fastlane metadata (which the Play listing and the README banner both use) and the
web Open Graph image, which GitHub Pages has to serve from the wasm bundle.

Requires: rsvg-convert on PATH (same dependency as media/achievements/generate.py) and Pillow.
"""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from pathlib import Path

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    sys.exit("Pillow is required: python3 -m pip install pillow")

ROOT = Path(__file__).resolve().parent.parent
MASCOT = ROOT / "composeApp/src/commonMain/composeResources/drawable/ic_mascot.xml"
BUNGEE = ROOT / "composeApp/src/commonMain/composeResources/font/bungee.ttf"

TARGETS = [
    ROOT / "composeApp/src/wasmJsMain/resources/og-image.png",
    ROOT / "fastlane/metadata/android/en-US/images/featureGraphic.png",
]

WIDTH, HEIGHT = 1024, 500
BACKGROUND = "#FFFFFF"
ACCENT = "#ED7354"

MASCOT_BOX = (238, 250, 380)  # center x, center y, height
TEXT_MARGIN = (470, 1004)  # words stay inside this x range
CAP_HEIGHT = 61

# Word plus the x it is centred on. Rows sit on evenly spaced baselines, so the gaps between
# them are identical; the varying indents keep the original banner's scattered feel.
WORDS = [
    ("NUMBERS", 769),
    ("LOGIC", 622),
    ("SKILL", 838),
    ("MATH", 656),
]
FIRST_BASELINE = 143
ROW_STEP = 93

SS = 3  # supersampling for the text


def vector_drawable_to_svg(path: Path) -> str:
    """Android vector drawables are SVG with a different spelling; ic_mascot.xml uses only
    filled paths, so the rename is the whole conversion."""
    xml = path.read_text(encoding="utf-8")
    width = re.search(r'android:viewportWidth="([\d.]+)"', xml).group(1)
    height = re.search(r'android:viewportHeight="([\d.]+)"', xml).group(1)

    paths = []
    for block in re.findall(r"<path\b(.*?)/>", xml, re.DOTALL):
        fill = re.search(r'android:fillColor="([^"]+)"', block)
        data = re.search(r'android:pathData="([^"]+)"', block)
        if not (fill and data):
            continue
        color = fill.group(1)
        if len(color) == 9:  # #AARRGGBB
            color = f"#{color[3:]}"
        paths.append(f'  <path fill="{color}" d="{data.group(1)}"/>')

    if not paths:
        sys.exit(f"No filled paths found in {path}")
    joined = "\n".join(paths)
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}">\n'
        f"{joined}\n"
        "</svg>\n"
    )


def render_mascot(height: int) -> Image.Image:
    svg = vector_drawable_to_svg(MASCOT)
    with tempfile.TemporaryDirectory() as tmp:
        svg_path = Path(tmp) / "mascot.svg"
        png_path = Path(tmp) / "mascot.png"
        svg_path.write_text(svg, encoding="utf-8")
        try:
            subprocess.run(
                ["rsvg-convert", "-h", str(height), "-o", str(png_path), str(svg_path)],
                check=True,
                capture_output=True,
            )
        except FileNotFoundError:
            sys.exit("rsvg-convert not found on PATH (brew install librsvg / port install librsvg)")
        except subprocess.CalledProcessError as error:
            sys.exit(f"rsvg-convert failed: {error.stderr.decode(errors='replace')}")
        return Image.open(png_path).convert("RGBA")


def font_for_cap_height(cap_height: int) -> ImageFont.FreeTypeFont:
    """Bungee's point size is not its cap height, so solve for the size that measures right."""
    size = cap_height
    for _ in range(12):
        font = ImageFont.truetype(str(BUNGEE), size)
        measured = font.getbbox("H")[3] - font.getbbox("H")[1]
        if measured == cap_height:
            return font
        size = max(1, round(size * cap_height / measured))
    return ImageFont.truetype(str(BUNGEE), size)


def fit_words(cap_height: int) -> tuple[ImageFont.FreeTypeFont, list[tuple[str, int, int]]]:
    """Shrink the type until every word sits inside the text margins and none of them touch.

    Words are positioned by baseline, not by bounding box, so the round letters in LOGIC and
    SKILL overshoot the way the type designer intended instead of shifting their row.
    """
    ruler = ImageDraw.Draw(Image.new("RGBA", (1, 1)))
    left_limit, right_limit = TEXT_MARGIN
    while cap_height > 10:
        font = font_for_cap_height(cap_height)
        placed, boxes = [], []
        for index, (word, cx) in enumerate(WORDS):
            baseline = FIRST_BASELINE + index * ROW_STEP
            x0, y0, x1, y1 = ruler.textbbox((0, baseline), word, font=font, anchor="ls")
            left = round(cx - (x0 + x1) / 2)
            placed.append((word, left, baseline))
            boxes.append((x0 + left, y0, x1 + left, y1))
        if all(b[0] >= left_limit and b[2] <= right_limit for b in boxes) and not overlapping(boxes):
            return font, placed
        cap_height -= 1
    sys.exit("Could not fit the words inside the text margins")


def overlapping(boxes: list[tuple[int, int, int, int]]) -> bool:
    for i, a in enumerate(boxes):
        for b in boxes[i + 1:]:
            if a[0] < b[2] and b[0] < a[2] and a[1] < b[3] and b[1] < a[3]:
                return True
    return False


def main() -> None:
    canvas = Image.new("RGBA", (WIDTH, HEIGHT), BACKGROUND)

    cx, cy, mascot_height = MASCOT_BOX
    mascot = render_mascot(mascot_height)
    canvas.alpha_composite(mascot, (round(cx - mascot.width / 2), round(cy - mascot.height / 2)))

    font, placed = fit_words(CAP_HEIGHT)
    text_layer = Image.new("RGBA", (WIDTH * SS, HEIGHT * SS), (0, 0, 0, 0))
    draw = ImageDraw.Draw(text_layer)
    big = ImageFont.truetype(str(BUNGEE), font.size * SS)
    for word, left, baseline in placed:
        draw.text((left * SS, baseline * SS), word, font=big, fill=ACCENT, anchor="ls")
    canvas.alpha_composite(text_layer.resize((WIDTH, HEIGHT), Image.LANCZOS))

    for target in TARGETS:
        target.parent.mkdir(parents=True, exist_ok=True)
        canvas.save(target)
        print(f"Wrote {target.relative_to(ROOT)} ({canvas.width}x{canvas.height})")


if __name__ == "__main__":
    main()
