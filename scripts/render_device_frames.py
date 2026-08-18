#!/usr/bin/env python3
"""Wrap recorded desktop-sized app screenshots in macOS and Chrome window frames.

Source images come from `DesktopFrameScreenshotTest` (Paparazzi records them into
screenshotTests/src/test/snapshots/images). Outputs land in media/ and are the images the
README and the Flatpak metainfo point at.

Run via `./gradlew :screenshotTests:updateDesktopScreenshots`, or directly once the snapshots
exist. Requires Pillow.
"""

from __future__ import annotations

import math
import re
import sys
from pathlib import Path
from urllib.parse import unquote

try:
    from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont
except ImportError:
    sys.exit("Pillow is required: python3 -m pip install pillow")

ROOT = Path(__file__).resolve().parent.parent
SNAPSHOTS = ROOT / "screenshotTests" / "src" / "test" / "snapshots" / "images"
MEDIA = ROOT / "media"
FONTS = ROOT / "composeApp" / "src" / "commonMain" / "composeResources" / "font"
INDEX_HTML = ROOT / "composeApp" / "src" / "wasmJsMain" / "resources" / "index.html"

WINDOW_TITLE = "Braincup"
WEB_URL = "braincup.app"
CORNER_RADIUS = 24

MARGIN_X = 112
MARGIN_TOP = 76
MARGIN_BOTTOM = 148
SHADOW_BLUR = 26
SHADOW_OFFSET_Y = 30
SHADOW_OPACITY = 0.42

# (output file, DesktopFrameScreenshotTest method, frame, macOS title bar variant)
OUTPUTS = [
    ("screen_mac_01.png", "mainMenu", "mac", "dark"),
    ("screen_mac_02.png", "gameSherlockCalculation", "mac", "light"),
    ("screen_web_01.png", "mainMenu", "web", None),
    ("screen_web_02.png", "gamePathFinder", "web", None),
]

SS = 3  # supersampling factor for the hand-drawn chrome


def font(name: str, size: int) -> ImageFont.FreeTypeFont:
    return ImageFont.truetype(str(FONTS / name), size)


def snapshot_for(test_name: str) -> Path:
    matches = sorted(SNAPSHOTS.glob(f"*_DesktopFrameScreenshotTest_{test_name}.png"))
    if not matches:
        sys.exit(
            f"No snapshot for DesktopFrameScreenshotTest.{test_name} in {SNAPSHOTS}.\n"
            "Record it first: ./gradlew :screenshotTests:updateDesktopScreenshots",
        )
    return matches[0]


def rounded_mask(width: int, height: int, radius: int, ss: int = 4) -> Image.Image:
    mask = Image.new("L", (width * ss, height * ss), 0)
    ImageDraw.Draw(mask).rounded_rectangle(
        [0, 0, width * ss - 1, height * ss - 1],
        radius=radius * ss,
        fill=255,
    )
    return mask.resize((width, height), Image.LANCZOS)


def draw_layer(width: int, height: int, paint) -> Image.Image:
    """Paint into a supersampled RGBA layer and downsample it, so edges and text stay smooth."""
    layer = Image.new("RGBA", (width * SS, height * SS), (0, 0, 0, 0))
    paint(ImageDraw.Draw(layer), SS)
    return layer.resize((width, height), Image.LANCZOS)


# --- favicon, rasterized from the inline SVG in index.html -------------------------------------

CMD_RE = re.compile(r"([MmLlCcHhVvZz])([^MmLlCcHhVvZz]*)")
NUM_RE = re.compile(r"[-+]?(?:\d*\.\d+|\d+\.?)(?:[eE][-+]?\d+)?")


def cubic(p0, p1, p2, p3, steps: int = 16):
    for i in range(1, steps + 1):
        t = i / steps
        u = 1 - t
        yield (
            u**3 * p0[0] + 3 * u * u * t * p1[0] + 3 * u * t * t * p2[0] + t**3 * p3[0],
            u**3 * p0[1] + 3 * u * u * t * p1[1] + 3 * u * t * t * p2[1] + t**3 * p3[1],
        )


def parse_path(d: str) -> list[list[tuple[float, float]]]:
    subpaths: list[list[tuple[float, float]]] = []
    current: list[tuple[float, float]] = []
    x = y = 0.0
    start = (0.0, 0.0)
    for letter, raw in CMD_RE.findall(d):
        nums = [float(n) for n in NUM_RE.findall(raw)]
        relative = letter.islower()
        cmd = letter.upper()
        if cmd == "M":
            for i in range(0, len(nums) - 1, 2):
                px, py = nums[i], nums[i + 1]
                if relative:
                    px, py = x + px, y + py
                if i == 0:
                    if len(current) > 1:
                        subpaths.append(current)
                    current = [(px, py)]
                    start = (px, py)
                else:
                    current.append((px, py))
                x, y = px, py
        elif cmd in ("L", "H", "V"):
            step = 2 if cmd == "L" else 1
            for i in range(0, len(nums), step):
                if cmd == "L":
                    px, py = nums[i], nums[i + 1]
                    if relative:
                        px, py = x + px, y + py
                elif cmd == "H":
                    px, py = (x + nums[i]) if relative else nums[i], y
                else:
                    px, py = x, (y + nums[i]) if relative else nums[i]
                current.append((px, py))
                x, y = px, py
        elif cmd == "C":
            for i in range(0, len(nums) - 5, 6):
                pts = []
                for j in range(3):
                    px, py = nums[i + j * 2], nums[i + j * 2 + 1]
                    pts.append((x + px, y + py) if relative else (px, py))
                current.extend(cubic((x, y), *pts))
                x, y = pts[2]
        elif cmd == "Z":
            current.append(start)
            x, y = start
    if len(current) > 1:
        subpaths.append(current)
    return subpaths


def favicon(size: int, ss: int = 8) -> Image.Image:
    html = INDEX_HTML.read_text(encoding="utf-8")
    match = re.search(r'<link rel="icon"[^>]*href="data:image/svg\+xml,([^"]+)"', html)
    if not match:
        sys.exit(f"No inline SVG favicon found in {INDEX_HTML}")
    svg = unquote(match.group(1))
    view_x, view_y, view_w, _ = (float(v) for v in re.search(r"viewBox='([^']+)'", svg).group(1).split())
    stroke = re.search(r"stroke='([^']+)'", svg).group(1)
    stroke_width = float(re.search(r"stroke-width='([^']+)'", svg).group(1))

    scale = size * ss / view_w
    icon = Image.new("RGBA", (size * ss, size * ss), (0, 0, 0, 0))
    draw = ImageDraw.Draw(icon)
    for d in re.findall(r"<path d='([^']+)'", svg):
        for subpath in parse_path(d):
            points = [((px - view_x) * scale, (py - view_y) * scale) for px, py in subpath]
            draw.line(points, fill=stroke, width=max(1, round(stroke_width * scale)), joint="curve")
    return icon.resize((size, size), Image.LANCZOS)


# --- chrome -----------------------------------------------------------------------------------

MAC_TITLE_BAR = 57
MAC_VARIANTS = {
    "light": {"bar": "#F4F3F3", "border": "#D9D9D9", "text": "#3B3B3B"},
    "dark": {"bar": "#3A3A3C", "border": "#2A2A2C", "text": "#E6E6E6"},
}
TRAFFIC_LIGHTS = ("#EC6A5E", "#F4BF4F", "#61C554")


def mac_chrome(width: int, variant: str) -> Image.Image:
    colors = MAC_VARIANTS[variant]

    def paint(draw: ImageDraw.ImageDraw, s: int) -> None:
        draw.rectangle([0, 0, width * s, MAC_TITLE_BAR * s], fill=colors["bar"])
        draw.rectangle([0, (MAC_TITLE_BAR - 1) * s, width * s, MAC_TITLE_BAR * s], fill=colors["border"])
        for index, color in enumerate(TRAFFIC_LIGHTS):
            circle(draw, (27.5 + 40 * index) * s, MAC_TITLE_BAR / 2 * s, 7 * s, color)
        draw.text(
            (width * s / 2, MAC_TITLE_BAR / 2 * s),
            WINDOW_TITLE,
            font=font("rubik_semibold.ttf", round(21 * s)),
            fill=colors["text"],
            anchor="mm",
        )

    return draw_layer(width, MAC_TITLE_BAR, paint)


TAB_STRIP = 80
TOOLBAR = 93
BROWSER_CHROME = TAB_STRIP + TOOLBAR
STRIP_BG = "#1F2020"
TOOLBAR_BG = "#3C3C3C"
FIELD_BG = "#282828"
ICON = "#C9C9C9"
ICON_DIM = "#7A7A7A"
TEXT = "#E3E3E3"
FAVICON_SIZE = 28


def circle(draw: ImageDraw.ImageDraw, cx: float, cy: float, r: float, fill: str) -> None:
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=fill)


def chevron(draw: ImageDraw.ImageDraw, cx: float, cy: float, size: float, w: float, fill: str) -> None:
    draw.line(
        [(cx - size, cy - size / 2), (cx, cy + size / 2), (cx + size, cy - size / 2)],
        fill=fill,
        width=round(w),
        joint="curve",
    )


def arrow(draw: ImageDraw.ImageDraw, cx: float, cy: float, size: float, w: float, fill: str, back: bool) -> None:
    tip = cx - size if back else cx + size
    tail = cx + size if back else cx - size
    head = size * 0.62
    draw.line([(tail, cy), (tip, cy)], fill=fill, width=round(w))
    draw.line(
        [(tip + (head if back else -head), cy - head), (tip, cy), (tip + (head if back else -head), cy + head)],
        fill=fill,
        width=round(w),
        joint="curve",
    )


def reload_icon(draw: ImageDraw.ImageDraw, cx: float, cy: float, r: float, w: float, fill: str) -> None:
    """Ring with a gap at the top, closed off by an arrowhead on the right of the gap."""
    gap_start = -50
    draw.arc([cx - r, cy - r, cx + r, cy + r], start=gap_start, end=240, fill=fill, width=round(w))
    angle = math.radians(gap_start)
    radial = (math.cos(angle), math.sin(angle))
    tangent = (math.sin(angle), -math.cos(angle))
    px, py = cx + r * radial[0], cy + r * radial[1]
    span, reach = r * 0.55, r * 0.85
    draw.polygon(
        [
            (px + radial[0] * span, py + radial[1] * span),
            (px - radial[0] * span, py - radial[1] * span),
            (px + tangent[0] * reach, py + tangent[1] * reach),
        ],
        fill=fill,
    )


def star_icon(draw: ImageDraw.ImageDraw, cx: float, cy: float, r: float, w: float, fill: str) -> None:
    points = []
    for i in range(10):
        angle = math.radians(-90 + i * 36)
        radius = r if i % 2 == 0 else r * 0.44
        points.append((cx + radius * math.cos(angle), cy + radius * math.sin(angle)))
    draw.line(points + [points[0]], fill=fill, width=round(w), joint="curve")


def info_icon(draw: ImageDraw.ImageDraw, cx: float, cy: float, r: float, w: float, fill: str) -> None:
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=fill, width=round(w))
    circle(draw, cx, cy - r * 0.42, w * 0.75, fill)
    draw.line([(cx, cy - r * 0.06), (cx, cy + r * 0.5)], fill=fill, width=round(w))


def browser_chrome(width: int) -> Image.Image:
    tab_w = 464
    tab_x = 172
    strip_center = TAB_STRIP / 2
    bar_center = TAB_STRIP + TOOLBAR / 2
    field_top, field_bottom = TAB_STRIP + 15, TAB_STRIP + 77
    field_radius = (field_bottom - field_top) / 2
    url_left = 240
    url_right = width - 76

    def paint(draw: ImageDraw.ImageDraw, s: int) -> None:
        draw.rectangle([0, 0, width * s, TAB_STRIP * s], fill=STRIP_BG)
        draw.rectangle([0, TAB_STRIP * s, width * s, BROWSER_CHROME * s], fill=TOOLBAR_BG)

        for index, color in enumerate(TRAFFIC_LIGHTS):
            circle(draw, (40 + 46 * index) * s, strip_center * s, 13 * s, color)

        draw.rounded_rectangle(
            [tab_x * s, 8 * s, (tab_x + tab_w) * s, (TAB_STRIP + 20) * s],
            radius=12 * s,
            fill=TOOLBAR_BG,
            corners=(True, True, False, False),
        )
        draw.text(
            ((tab_x + 66) * s, strip_center * s),
            WINDOW_TITLE,
            font=font("rubik_regular.ttf", round(23 * s)),
            fill="#E8E8E8",
            anchor="lm",
        )
        close_x = (tab_x + tab_w - 32) * s
        for dx, dy in ((-1, -1), (-1, 1)):
            draw.line(
                [(close_x + dx * 9 * s, strip_center * s + dy * 9 * s), (close_x - dx * 9 * s, strip_center * s - dy * 9 * s)],
                fill=ICON,
                width=round(2.2 * s),
            )
        plus_x = (tab_x + tab_w + 40) * s
        draw.line([(plus_x - 11 * s, strip_center * s), (plus_x + 11 * s, strip_center * s)], fill=ICON, width=round(2.2 * s))
        draw.line([(plus_x, strip_center * s - 11 * s), (plus_x, strip_center * s + 11 * s)], fill=ICON, width=round(2.2 * s))

        draw.rounded_rectangle(
            [(width - 68) * s, 14 * s, (width - 12) * s, 66 * s],
            radius=10 * s,
            fill=TOOLBAR_BG,
        )
        chevron(draw, (width - 40) * s, strip_center * s, 9 * s, 2.2 * s, ICON)

        arrow(draw, 44 * s, bar_center * s, 12 * s, 2.6 * s, ICON, back=True)
        arrow(draw, 116 * s, bar_center * s, 12 * s, 2.6 * s, ICON_DIM, back=False)
        reload_icon(draw, 179 * s, bar_center * s, 12 * s, 2.6 * s, ICON)

        draw.rounded_rectangle(
            [url_left * s, field_top * s, url_right * s, field_bottom * s],
            radius=field_radius * s,
            fill=FIELD_BG,
        )
        info_icon(draw, (url_left + 40) * s, bar_center * s, 13 * s, 2.2 * s, ICON)
        draw.text(
            ((url_left + 76) * s, bar_center * s),
            WEB_URL,
            font=font("rubik_regular.ttf", round(26 * s)),
            fill=TEXT,
            anchor="lm",
        )
        star_icon(draw, (url_right - 48) * s, bar_center * s, 12 * s, 2.2 * s, ICON)

        for dy in (-14, 0, 14):
            circle(draw, (width - 44) * s, (bar_center + dy) * s, 3.2 * s, ICON)

    chrome = draw_layer(width, BROWSER_CHROME, paint)
    icon = favicon(FAVICON_SIZE)
    chrome.alpha_composite(icon, (tab_x + 20, round(strip_center - FAVICON_SIZE / 2)))
    return chrome


# --- assembly ---------------------------------------------------------------------------------


def compose(content: Image.Image, chrome: Image.Image) -> Image.Image:
    window_w = content.width
    window_h = chrome.height + content.height
    window = Image.new("RGBA", (window_w, window_h), (0, 0, 0, 0))
    window.paste(content.convert("RGBA"), (0, chrome.height))
    window.alpha_composite(chrome, (0, 0))

    mask = rounded_mask(window_w, window_h, CORNER_RADIUS)
    window.putalpha(ImageChops.multiply(window.getchannel("A"), mask))

    canvas_w = window_w + MARGIN_X * 2
    canvas_h = window_h + MARGIN_TOP + MARGIN_BOTTOM
    shadow_alpha = Image.new("L", (canvas_w, canvas_h), 0)
    shadow_alpha.paste(mask, (MARGIN_X, MARGIN_TOP + SHADOW_OFFSET_Y))
    shadow_alpha = shadow_alpha.filter(ImageFilter.GaussianBlur(SHADOW_BLUR))
    shadow_alpha = shadow_alpha.point(lambda v: round(v * SHADOW_OPACITY))

    canvas = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
    shadow = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 255))
    shadow.putalpha(shadow_alpha)
    canvas.alpha_composite(shadow)
    canvas.alpha_composite(window, (MARGIN_X, MARGIN_TOP))
    return canvas


def main() -> None:
    MEDIA.mkdir(parents=True, exist_ok=True)
    for out_name, test_name, frame, variant in OUTPUTS:
        content = Image.open(snapshot_for(test_name))
        chrome = mac_chrome(content.width, variant) if frame == "mac" else browser_chrome(content.width)
        image = compose(content, chrome)
        target = MEDIA / out_name
        image.save(target)
        print(f"Wrote media/{out_name} ({image.width}x{image.height}) from {test_name} in a {frame} frame")


if __name__ == "__main__":
    main()
