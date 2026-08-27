#!/usr/bin/env python3
"""Build a browsable index of the Learn section render check.

`./gradlew :screenshotTests:renderLearnScreens` lays roughly 1600 frames out under
screenshotTests/build/learn-render/, one folder per sub-topic. That is far too many to page
through in a file browser, and far too many megabytes to inline into a single self-contained
page, so this writes an index.html next to them that points at them on disk.

Run via the Gradle task, or directly once the frames exist. No dependencies.
"""

from __future__ import annotations

import html
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RENDER = ROOT / "screenshotTests" / "build" / "learn-render"

# Folders that are not a sub-topic, in the order they should appear.
SPECIAL_ORDER = ["_section", "_figurephase"]

FIGURE_FAMILIES = {
    "AngleFigure", "AreaGrid", "ArrayDots", "BarChart", "CircleFigure", "CyclicQuad",
    "DecimalGrid", "Fraction", "NumberLine", "PlaceValue", "Plot", "Polygon", "Quadrilateral",
    "RatioBar", "RightTriangle", "Solid", "Steps", "Symmetry", "TenFrame", "Triangle",
}


def group_of(folder: str) -> str:
    if folder in SPECIAL_ORDER:
        return "Section"
    if folder in FIGURE_FAMILIES:
        return "Figures"
    if folder.endswith(("-dark", "-oled")):
        return "Themes"
    if folder.startswith("arithmetic-"):
        return "Arithmetic"
    if folder.startswith("geometry-"):
        return "Geometry"
    return "Other"


GROUP_ORDER = ["Section", "Arithmetic", "Geometry", "Themes", "Figures", "Other"]

# Frame names are ordered so a sub-topic reads as a walkthrough; these turn the prefix into a
# heading a human can scan.
STAGES = [
    (re.compile(r"^0\d_"), "Sub-topic screen"),
    (re.compile(r"^11\d"), "Lesson 1"),
    (re.compile(r"^12\d"), "Lesson 2"),
    (re.compile(r"^13\d"), "Lesson 3"),
    (re.compile(r"^2\d_"), "Lesson complete"),
    (re.compile(r"^3\d_"), "Unit test"),
    (re.compile(r"^4\d_"), "Test result"),
    (re.compile(r"^5\d_"), "Certificate"),
]


def stage_of(name: str) -> str:
    for pattern, label in STAGES:
        if pattern.match(name):
            return label
    return ""


CSS = """
:root {
  --bg: #f6f6f7; --fg: #16161a; --muted: #6b6b76; --line: #dcdce2;
  --card: #ffffff; --accent: #ed7354; --shadow: 0 1px 3px rgba(0,0,0,.10);
}
@media (prefers-color-scheme: dark) {
  :root { --bg:#141417; --fg:#ececed; --muted:#9a9aa4; --line:#2c2c33; --card:#1c1c21;
          --shadow: 0 1px 3px rgba(0,0,0,.5); }
}
* { box-sizing: border-box; }
body { margin:0; background:var(--bg); color:var(--fg);
       font:15px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,sans-serif; }
header { position:sticky; top:0; z-index:10; background:var(--bg);
         border-bottom:1px solid var(--line); padding:14px 20px 12px; }
h1 { margin:0 0 2px; font-size:17px; letter-spacing:-.01em; }
.sub { color:var(--muted); font-size:13px; }
.controls { display:flex; gap:10px; align-items:center; margin-top:10px; flex-wrap:wrap; }
input[type=search] { flex:1 1 240px; min-width:200px; padding:7px 11px; border:1px solid var(--line);
        border-radius:7px; background:var(--card); color:var(--fg); font-size:14px; }
label.size { color:var(--muted); font-size:13px; display:flex; gap:7px; align-items:center; }
nav { padding:10px 20px 0; display:flex; flex-wrap:wrap; gap:5px; }
nav a { font-size:12px; color:var(--muted); text-decoration:none; padding:3px 8px;
        border:1px solid var(--line); border-radius:99px; background:var(--card); white-space:nowrap; }
nav a:hover { color:var(--accent); border-color:var(--accent); }
nav .grouplabel { font-size:11px; text-transform:uppercase; letter-spacing:.09em;
        color:var(--muted); width:100%; margin:9px 0 2px; }
main { padding:8px 20px 80px; }
section { margin-top:30px; scroll-margin-top:132px; }
section > h2 { font-size:15px; margin:0 0 3px; }
section > h2 .count { color:var(--muted); font-weight:400; font-size:13px; margin-left:7px; }
.stage { font-size:11px; text-transform:uppercase; letter-spacing:.09em; color:var(--muted);
         margin:16px 0 7px; border-top:1px solid var(--line); padding-top:9px; }
.grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(var(--w,190px),1fr)); gap:13px; }
figure { margin:0; background:var(--card); border:1px solid var(--line); border-radius:9px;
         overflow:hidden; box-shadow:var(--shadow); }
figure img { display:block; width:100%; height:auto; background:#fff; }
figcaption { font-size:11px; color:var(--muted); padding:5px 7px; word-break:break-all;
             font-family:ui-monospace,SFMono-Regular,Menlo,monospace; }
figure.hidden, section.hidden, .stage.hidden { display:none; }
"""

JS = """
const search = document.getElementById('q');
const size = document.getElementById('size');
size.addEventListener('input', () => {
  document.documentElement.style.setProperty('--w', size.value + 'px');
});
search.addEventListener('input', () => {
  const q = search.value.trim().toLowerCase();
  document.querySelectorAll('section').forEach(sec => {
    let shown = 0;
    sec.querySelectorAll('figure').forEach(fig => {
      const hit = !q || fig.dataset.name.includes(q) || sec.id.toLowerCase().includes(q);
      fig.classList.toggle('hidden', !hit);
      if (hit) shown++;
    });
    sec.classList.toggle('hidden', shown === 0);
    sec.querySelectorAll('.stage').forEach(st => {
      let next = st.nextElementSibling;
      const any = next && next.querySelector('figure:not(.hidden)');
      st.classList.toggle('hidden', !any);
    });
  });
});
"""


def main() -> int:
    if not RENDER.is_dir():
        sys.exit(f"No renders at {RENDER}. Run ./gradlew :screenshotTests:renderLearnScreens first.")

    folders = sorted(p for p in RENDER.iterdir() if p.is_dir())
    if not folders:
        sys.exit(f"No sub-topic folders under {RENDER}.")

    by_group: dict[str, list[Path]] = {}
    for f in folders:
        by_group.setdefault(group_of(f.name), []).append(f)

    total = sum(len(list(f.glob("*.png"))) for f in folders)

    nav: list[str] = []
    body: list[str] = []
    for group in GROUP_ORDER:
        if group not in by_group:
            continue
        nav.append(f'<div class="grouplabel">{html.escape(group)}</div>')
        for folder in by_group[group]:
            frames = sorted(folder.glob("*.png"), key=lambda p: p.name)
            if not frames:
                continue
            name = folder.name
            nav.append(f'<a href="#{html.escape(name)}">{html.escape(name)}</a>')
            body.append(f'<section id="{html.escape(name)}">')
            body.append(
                f'<h2>{html.escape(name)}<span class="count">{len(frames)} frames</span></h2>'
            )
            stage = None
            open_grid = False
            for frame in frames:
                this_stage = stage_of(frame.name)
                if this_stage != stage:
                    if open_grid:
                        body.append("</div>")
                    stage = this_stage
                    if stage:
                        body.append(f'<div class="stage">{html.escape(stage)}</div>')
                    body.append('<div class="grid">')
                    open_grid = True
                rel = f"{name}/{frame.name}"
                body.append(
                    f'<figure data-name="{html.escape(frame.stem.lower())}">'
                    f'<a href="{html.escape(rel)}" target="_blank">'
                    f'<img loading="lazy" src="{html.escape(rel)}" alt="{html.escape(frame.stem)}"></a>'
                    f"<figcaption>{html.escape(frame.stem)}</figcaption></figure>"
                )
            if open_grid:
                body.append("</div>")
            body.append("</section>")

    page = f"""<!doctype html>
<html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Learn render check</title>
<style>{CSS}</style></head>
<body>
<header>
  <h1>Learn render check</h1>
  <div class="sub">{total} frames across {len(folders)} folders &middot; click any frame for full size</div>
  <div class="controls">
    <input id="q" type="search" placeholder="Filter by frame or sub-topic name, e.g. 'choice_correct' or 'circles'">
    <label class="size">width <input id="size" type="range" min="120" max="520" value="190"></label>
  </div>
</header>
<nav>{''.join(nav)}</nav>
<main>{''.join(body)}</main>
<script>{JS}</script>
</body></html>
"""
    out = RENDER / "index.html"
    out.write_text(page, encoding="utf-8")
    print(f"Wrote {out} ({total} frames, {len(folders)} folders)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
