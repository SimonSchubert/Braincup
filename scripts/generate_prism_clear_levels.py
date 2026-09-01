#!/usr/bin/env python3
"""Generate and verify Prism Clear catalog levels.

The Kotlin engine (composeApp/.../games/PrismClearGame.kt) is mirrored here so boards can be
searched for offline. `--self-check` replays every shipped level's solution through this mirror,
which is what proves the mirror still matches the engine before any generated board is trusted.
"""

from __future__ import annotations

import argparse
import random
import re
import sys
import time
from collections import Counter
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
CATALOG = REPO / "composeApp/src/commonMain/kotlin/com/inspiredandroid/braincup/games/PrismClearLevels.kt"

# Index = PrismTileType ordinal; character = the catalog's board encoding.
CHARS = "RGBPO"
EMPTY = -1

Board = tuple[int, ...]
Swap = tuple[int, int]

# ── Engine mirror (PrismClearGame.kt) ──


def find_matches(cells: Board, rows: int, cols: int) -> set[int]:
    matched: set[int] = set()
    for r in range(rows):
        c = 0
        while c < cols:
            t = cells[r * cols + c]
            if t == EMPTY:
                c += 1
                continue
            end = c + 1
            while end < cols and cells[r * cols + end] == t:
                end += 1
            if end - c >= 3:
                matched.update(r * cols + x for x in range(c, end))
            c = end
    for c in range(cols):
        r = 0
        while r < rows:
            t = cells[r * cols + c]
            if t == EMPTY:
                r += 1
                continue
            end = r + 1
            while end < rows and cells[end * cols + c] == t:
                end += 1
            if end - r >= 3:
                matched.update(y * cols + c for y in range(r, end))
            r = end
    return matched


def apply_gravity(cells: list[int], rows: int, cols: int) -> None:
    for c in range(cols):
        column = [cells[r * cols + c] for r in range(rows) if cells[r * cols + c] != EMPTY]
        empty = rows - len(column)
        for r in range(rows):
            cells[r * cols + c] = EMPTY if r < empty else column[r - empty]


def resolve_cascades(cells: list[int], rows: int, cols: int) -> int:
    """Settles the board in place; returns how many tiles were cleared."""
    cleared = 0
    while True:
        matches = find_matches(tuple(cells), rows, cols)
        if not matches:
            return cleared
        cleared += len(matches)
        for i in matches:
            cells[i] = EMPTY
        apply_gravity(cells, rows, cols)


def is_legal_swap(cells: Board, rows: int, cols: int, a: int, b: int) -> bool:
    if a == b:
        return False
    n = rows * cols
    if not (0 <= a < n and 0 <= b < n):
        return False
    if abs(a // cols - b // cols) + abs(a % cols - b % cols) != 1:
        return False
    if cells[a] == EMPTY or cells[b] == EMPTY:
        return False
    copy = list(cells)
    copy[a], copy[b] = copy[b], copy[a]
    return bool(find_matches(tuple(copy), rows, cols))


def all_legal_swaps(cells: Board, rows: int, cols: int) -> list[Swap]:
    out: list[Swap] = []
    for i in range(rows * cols):
        if cells[i] == EMPTY:
            continue
        r, c = divmod(i, cols)
        if c + 1 < cols and is_legal_swap(cells, rows, cols, i, i + 1):
            out.append((i, i + 1))
        if r + 1 < rows and is_legal_swap(cells, rows, cols, i, i + cols):
            out.append((i, i + cols))
    return out


def has_any_valid_swap(cells: Board, rows: int, cols: int) -> bool:
    return bool(all_legal_swaps(cells, rows, cols))


def play(cells: Board, rows: int, cols: int, a: int, b: int) -> tuple[Board, int]:
    nxt = list(cells)
    nxt[a], nxt[b] = nxt[b], nxt[a]
    cleared = resolve_cascades(nxt, rows, cols)
    return tuple(nxt), cleared


def is_empty(cells: Board) -> bool:
    return all(c == EMPTY for c in cells)


def counts_are_multiples_of_three(cells: Board) -> bool:
    counts = Counter(c for c in cells if c != EMPTY)
    return all(v % 3 == 0 for v in counts.values())


def verify_solves(start: Board, rows: int, cols: int, swaps: list[Swap]) -> bool:
    sim = start
    for a, b in swaps:
        if is_empty(sim):
            return True
        if not is_legal_swap(sim, rows, cols, a, b):
            return False
        sim, _ = play(sim, rows, cols, a, b)
    return is_empty(sim)


# ── Board <-> text ──


def parse_board(board: str, rows: int, cols: int) -> Board:
    clean = "".join(board.split())
    if len(clean) != rows * cols:
        raise ValueError(f"board length {len(clean)} != {rows * cols}")
    return tuple(EMPTY if ch == "." else CHARS.index(ch) for ch in clean)


def render_board(cells: Board, rows: int, cols: int) -> str:
    lines = []
    for r in range(rows):
        lines.append("".join("." if c == EMPTY else CHARS[c] for c in cells[r * cols:(r + 1) * cols]))
    return "\n".join(lines)


# ── Catalog parser (self-check only) ──

LEVEL_RE = re.compile(
    r'level\(\s*(\d+),\s*(\d+),\s*(\d+),\s*(\d+),\s*"""(.*?)""",\s*listOf\((.*?)\),\s*\),',
    re.DOTALL,
)
PAIR_RE = re.compile(r"(\d+)\s+to\s+(\d+)")


def read_catalog(path: Path = CATALOG) -> list[dict]:
    src = path.read_text()
    levels = []
    for m in LEVEL_RE.finditer(src):
        levels.append(
            {
                "id": int(m.group(1)),
                "rows": int(m.group(2)),
                "cols": int(m.group(3)),
                "budget": int(m.group(4)),
                "board": m.group(5),
                "solution": [(int(a), int(b)) for a, b in PAIR_RE.findall(m.group(6))],
            }
        )
    return levels


def self_check(path: Path = CATALOG) -> list[str]:
    """Returns a list of problems; empty means the mirror agrees with every shipped level."""
    problems = []
    levels = read_catalog(path)
    if not levels:
        return [f"no levels parsed from {path}"]
    for lvl in levels:
        tag = f"L{lvl['id']}"
        try:
            cells = parse_board(lvl["board"], lvl["rows"], lvl["cols"])
        except ValueError as exc:
            problems.append(f"{tag}: {exc}")
            continue
        if not counts_are_multiples_of_three(cells):
            problems.append(f"{tag}: colour counts not all multiples of three")
        if not has_any_valid_swap(cells, lvl["rows"], lvl["cols"]):
            problems.append(f"{tag}: stuck on deal")
        if not verify_solves(cells, lvl["rows"], lvl["cols"], lvl["solution"]):
            problems.append(f"{tag}: catalog solution does not empty the board")
    return problems


# ── Solver ──

SOLVED, UNSOLVABLE, UNKNOWN = "solved", "unsolvable", "unknown"


class Solver:
    """DFS over the state DAG. The board only ever loses tiles, so a visited set prunes hard.

    A state is memoised as unsolvable only when its whole subtree was explored inside the budget;
    a state abandoned on budget stays unknown so a later, cheaper visit can still settle it.
    """

    def __init__(self, rows: int, cols: int, node_budget: int = 400_000) -> None:
        self.rows = rows
        self.cols = cols
        self.node_budget = node_budget
        self.dead: set[Board] = set()
        self.nodes = 0

    def solve(self, cells: Board) -> tuple[str, list[Swap]]:
        self.nodes = 0
        return self._dfs(cells)

    def _dfs(self, cells: Board) -> tuple[str, list[Swap]]:
        if is_empty(cells):
            return SOLVED, []
        if cells in self.dead:
            return UNSOLVABLE, []
        self.nodes += 1
        if self.nodes > self.node_budget:
            return UNKNOWN, []

        moves = all_legal_swaps(cells, self.rows, self.cols)
        if not moves:
            self.dead.add(cells)
            return UNSOLVABLE, []

        # Biggest cascade first: reaches an empty board in fewer swaps, so the stored line is short.
        scored = []
        for a, b in moves:
            nxt, cleared = play(cells, self.rows, self.cols, a, b)
            scored.append((-cleared, a, b, nxt))
        scored.sort()

        exhaustive = True
        for _, a, b, nxt in scored:
            status, tail = self._dfs(nxt)
            if status == SOLVED:
                return SOLVED, [(a, b)] + tail
            if status == UNKNOWN:
                exhaustive = False
        if exhaustive:
            self.dead.add(cells)
            return UNSOLVABLE, []
        return UNKNOWN, []


# ── Reverse construction ──
#
# A randomly dealt full board is almost never clearable: with no refill, every colour has to be
# walked into a line before the board runs out of material, and a shuffle leaves residues that no
# sequence of swaps can reach. So packed boards are built backwards instead. Starting from a
# position one swap away from empty, each step inserts a fresh line of three under reverse gravity
# and then un-swaps one of its tiles, which is exactly the inverse of a forward move. Every board
# produced this way is solvable, and the recorded swaps are its solution.


def stacks_of(cells: Board, rows: int, cols: int) -> list[list[int]]:
    """Column contents bottom-to-top. Assumes a settled (bottom-packed) board."""
    return [
        [cells[r * cols + c] for r in range(rows - 1, -1, -1) if cells[r * cols + c] != EMPTY]
        for c in range(cols)
    ]


def cells_of(stacks: list[list[int]], rows: int, cols: int) -> Board:
    cells = [EMPTY] * (rows * cols)
    for c, stack in enumerate(stacks):
        for depth, tile in enumerate(stack):
            cells[(rows - 1 - depth) * cols + c] = tile
    return tuple(cells)


def terminal_positions(rows: int, cols: int) -> list[tuple[Board, Swap]]:
    """Boards that one swap empties: two interlocked triples, laid flat or stacked."""
    out = []
    for x in range(len(CHARS)):
        for y in range(len(CHARS)):
            if x == y:
                continue
            for c0 in range(cols - 5):
                cells = [EMPTY] * (rows * cols)
                base = (rows - 1) * cols + c0
                for offset, tile in enumerate([x, x, y, x, y, y]):
                    cells[base + offset] = tile
                out.append((tuple(cells), (base + 2, base + 3)))
            if rows >= 6:
                for c in range(cols):
                    stacks = [[] for _ in range(cols)]
                    stacks[c] = [x, x, y, x, y, y]
                    cells = cells_of(stacks, rows, cols)
                    out.append((cells, ((rows - 3) * cols + c, (rows - 4) * cols + c)))
    return out


def _insertions(stacks: list[list[int]], rows: int, cols: int, targets: list[int]) -> list[list[tuple[int, int]]]:
    """Every way to add a line of three, as per-column (column, insert depth) lists."""
    out = []
    for r in range(rows):
        depth = rows - 1 - r
        for c0 in range(cols - 2):
            group = [(c, depth) for c in range(c0, c0 + 3)]
            if all(depth <= len(stacks[c]) and len(stacks[c]) < targets[c] for c, _ in group):
                out.append(group)
    for c in range(cols):
        if len(stacks[c]) + 3 > targets[c]:
            continue
        for r in range(rows - 2):
            depth = rows - 3 - r
            if depth <= len(stacks[c]):
                out.append([(c, depth), (c, depth), (c, depth)])
    return out


def _reverse_step(
    cells: Board,
    rows: int,
    cols: int,
    targets: list[int],
    rng: random.Random,
) -> tuple[Board, Swap] | None:
    stacks = stacks_of(cells, rows, cols)
    options = _insertions(stacks, rows, cols, targets)
    rng.shuffle(options)
    # Fill the columns that are furthest from their target first, or the last few lines have
    # nowhere legal to go and the whole build has to restart.
    options.sort(key=lambda g: -sum(targets[c] - len(stacks[c]) for c, _ in g))
    cutoff = max(1, len(options) // 3)
    for group in options[:cutoff] if rng.random() < 0.7 else options:
        for colour in rng.sample(range(len(CHARS)), len(CHARS)):
            grown = [list(s) for s in stacks]
            if len(group) == 3 and group[0][0] == group[1][0] == group[2][0]:
                c, depth = group[0]
                grown[c][depth:depth] = [colour, colour, colour]
            else:
                for c, depth in group:
                    grown[c].insert(depth, colour)
            pre = cells_of(grown, rows, cols)
            line = {i for i, t in enumerate(pre) if t == colour}
            matches = find_matches(pre, rows, cols)
            if not matches or not matches <= line:
                continue
            moves = [(p, q) for p in matches for q in _neighbours(p, rows, cols) if pre[q] != EMPTY and q not in matches]
            rng.shuffle(moves)
            for p, q in moves:
                undone = list(pre)
                undone[p], undone[q] = undone[q], undone[p]
                board = tuple(undone)
                if find_matches(board, rows, cols):
                    continue
                replayed, _ = play(board, rows, cols, p, q)
                if replayed == cells:
                    return board, (p, q)
    return None


def _neighbours(i: int, rows: int, cols: int) -> list[int]:
    r, c = divmod(i, cols)
    out = []
    if r > 0:
        out.append(i - cols)
    if r + 1 < rows:
        out.append(i + cols)
    if c > 0:
        out.append(i - 1)
    if c + 1 < cols:
        out.append(i + 1)
    return out


def build_backwards(
    rows: int,
    cols: int,
    targets: list[int],
    rng: random.Random,
    restarts: int = 40,
) -> tuple[Board, list[Swap]] | None:
    total = sum(targets)
    for _ in range(restarts):
        cells, final_swap = rng.choice(terminal_positions(rows, cols))
        heights = [len(s) for s in stacks_of(cells, rows, cols)]
        if any(h > t for h, t in zip(heights, targets)):
            continue
        swaps = [final_swap]
        while sum(1 for t in cells if t != EMPTY) < total:
            step = _reverse_step(cells, rows, cols, targets, rng)
            if step is None:
                break
            cells, swap = step
            swaps.insert(0, swap)
        if sum(1 for t in cells if t != EMPTY) == total:
            return cells, swaps
    return None


# ── Difficulty metrics ──


def random_playout_rate(cells: Board, rows: int, cols: int, trials: int, rng: random.Random) -> float:
    wins = 0
    for _ in range(trials):
        board = cells
        while True:
            if is_empty(board):
                wins += 1
                break
            moves = all_legal_swaps(board, rows, cols)
            if not moves:
                break
            a, b = rng.choice(moves)
            board, _ = play(board, rows, cols, a, b)
    return wins / trials


def greedy_solves(cells: Board, rows: int, cols: int, biggest_first: bool) -> bool:
    """Plays the most (or least) rewarding legal swap every turn. A good level defeats both."""
    board = cells
    while True:
        if is_empty(board):
            return True
        moves = all_legal_swaps(board, rows, cols)
        if not moves:
            return False
        best = None
        for a, b in moves:
            nxt, cleared = play(board, rows, cols, a, b)
            key = -cleared if biggest_first else cleared
            if best is None or key < best[0]:
                best = (key, nxt)
        board = best[1]


def measure(
    cells: Board,
    rows: int,
    cols: int,
    solver: Solver,
    rng: random.Random,
    trials: int,
    trap_node_budget: int = 40_000,
) -> dict | None:
    status, solution = solver.solve(cells)
    if status != SOLVED:
        return None
    openings = all_legal_swaps(cells, rows, cols)
    traps = 0
    # Proving a 48-tile position dead can cost seconds, so openings get a smaller budget than the
    # deal. An opening the search cannot settle counts as survivable, which under-reports the trap
    # ratio rather than inflating it.
    full_budget = solver.node_budget
    solver.node_budget = trap_node_budget
    for a, b in openings:
        nxt, _ = play(cells, rows, cols, a, b)
        child_status, _ = solver.solve(nxt)
        if child_status == UNSOLVABLE:
            traps += 1
    solver.node_budget = full_budget
    return {
        "solution": solution,
        "solution_len": len(solution),
        "deal_swaps": len(openings),
        "trap_ratio": traps / len(openings) if openings else 0.0,
        "random_rate": random_playout_rate(cells, rows, cols, trials, rng),
        "greedy_big": greedy_solves(cells, rows, cols, True),
        "greedy_small": greedy_solves(cells, rows, cols, False),
    }


# ── Generation ──


def column_targets(rows: int, cols: int, tiles: int, rng: random.Random) -> list[int]:
    """Per-column heights summing to `tiles`, as level as the grid allows."""
    if tiles > rows * cols:
        raise ValueError(f"{tiles} tiles do not fit in {rows}x{cols}")
    targets = [tiles // cols] * cols
    for c in rng.sample(range(cols), tiles % cols):
        targets[c] += 1
    return targets


def generate(
    rows: int,
    cols: int,
    tiles: int,
    seed: int,
    attempts: int,
    trials: int,
    node_budget: int,
    time_budget: float,
    min_deal_swaps: int,
    min_trap_ratio: float,
    max_random_rate: float,
    min_solution_len: int,
    trap_node_budget: int = 40_000,
) -> list[dict]:
    rng = random.Random(seed)
    found = []
    started = time.time()
    for _ in range(attempts):
        if time.time() - started > time_budget:
            break
        built = build_backwards(rows, cols, column_targets(rows, cols, tiles, rng), rng)
        if built is None:
            continue
        cells, construction = built
        if not verify_solves(cells, rows, cols, construction):
            raise AssertionError("reverse construction produced an unsolvable board")
        if len(all_legal_swaps(cells, rows, cols)) < min_deal_swaps:
            continue
        if greedy_solves(cells, rows, cols, True) or greedy_solves(cells, rows, cols, False):
            continue
        stats = measure(
            cells, rows, cols, Solver(rows, cols, node_budget), rng, trials, trap_node_budget
        )
        if stats is None:
            # The board is solvable by construction, so this only means the search ran out of
            # budget before proving it. Fall back to the built line rather than dropping it.
            continue
        if stats["solution_len"] < min_solution_len:
            continue
        if stats["trap_ratio"] < min_trap_ratio:
            continue
        if stats["random_rate"] > max_random_rate:
            continue
        stats["cells"] = cells
        stats["rows"] = rows
        stats["cols"] = cols
        found.append(stats)
    return found


def emit_kotlin(level_id: int, stats: dict, note: str) -> str:
    board = render_board(stats["cells"], stats["rows"], stats["cols"])
    indented = "\n".join(" " * 12 + line for line in board.splitlines())
    pairs = ", ".join(f"{a} to {b}" for a, b in stats["solution"])
    return (
        f"        // L{level_id} — {note}\n"
        f"        level(\n"
        f"            {level_id},\n"
        f"            {stats['rows']},\n"
        f"            {stats['cols']},\n"
        f"            {stats['solution_len']},\n"
        f'            """\n{indented}\n            """,\n'
        f"            listOf({pairs}),\n"
        f"        ),"
    )


def describe(stats: dict) -> str:
    return (
        f"{stats['rows']}x{stats['cols']} tiles={sum(1 for c in stats['cells'] if c != EMPTY)} "
        f"swaps={stats['solution_len']} openings={stats['deal_swaps']} "
        f"traps={stats['trap_ratio']:.0%} random={stats['random_rate']:.1%}"
    )


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--self-check", action="store_true", help="verify the shipped catalog")
    ap.add_argument("--rows", type=int, default=8)
    ap.add_argument("--cols", type=int, default=6)
    ap.add_argument("--tiles", type=int, default=None, help="defaults to a fully packed board")
    ap.add_argument("--seed", type=int, default=1)
    ap.add_argument("--attempts", type=int, default=4000)
    ap.add_argument("--trials", type=int, default=400)
    ap.add_argument("--node-budget", type=int, default=400_000)
    ap.add_argument("--time-budget", type=float, default=300.0)
    ap.add_argument("--min-deal-swaps", type=int, default=8)
    ap.add_argument("--min-trap-ratio", type=float, default=0.5)
    ap.add_argument("--max-random-rate", type=float, default=0.0)
    ap.add_argument("--min-solution-len", type=int, default=8)
    ap.add_argument("--emit", type=int, default=0, help="first level id to print Kotlin for")
    ap.add_argument("--top", type=int, default=5, help="how many candidates to keep")
    args = ap.parse_args()

    if args.self_check:
        problems = self_check()
        if problems:
            for p in problems:
                print(f"FAIL {p}")
            return 1
        print(f"OK {len(read_catalog())} catalog levels verified against the engine mirror")
        return 0

    tiles = args.tiles if args.tiles is not None else args.rows * args.cols
    if tiles % 3 != 0:
        print(f"tile count {tiles} is not a multiple of three", file=sys.stderr)
        return 2

    found = generate(
        rows=args.rows,
        cols=args.cols,
        tiles=tiles,
        seed=args.seed,
        attempts=args.attempts,
        trials=args.trials,
        node_budget=args.node_budget,
        time_budget=args.time_budget,
        min_deal_swaps=args.min_deal_swaps,
        min_trap_ratio=args.min_trap_ratio,
        max_random_rate=args.max_random_rate,
        min_solution_len=args.min_solution_len,
    )
    found.sort(key=lambda s: (s["trap_ratio"], s["deal_swaps"], s["solution_len"]), reverse=True)
    found = found[: args.top]
    for i, stats in enumerate(found):
        print(f"--- candidate {i}: {describe(stats)}")
        print(render_board(stats["cells"], stats["rows"], stats["cols"]))
        if args.emit:
            print(emit_kotlin(args.emit + i, stats, "generated"))
    if not found:
        print("no candidate met the gates")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
