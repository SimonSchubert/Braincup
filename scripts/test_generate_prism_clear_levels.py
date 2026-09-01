#!/usr/bin/env python3
"""Unit tests for the Prism Clear engine mirror and level builder."""

from __future__ import annotations

import random
import unittest

from generate_prism_clear_levels import (
    EMPTY,
    Solver,
    all_legal_swaps,
    apply_gravity,
    build_backwards,
    column_targets,
    counts_are_multiples_of_three,
    find_matches,
    greedy_solves,
    is_legal_swap,
    parse_board,
    read_catalog,
    render_board,
    self_check,
    verify_solves,
)

RUBY, EMERALD, SAPPHIRE = 0, 1, 2


class EngineMirrorTest(unittest.TestCase):
    """Mirrors the cases in PrismClearGameTest so the two engines stay in step."""

    def test_clears_entire_run_including_length_four(self) -> None:
        cells = (RUBY, RUBY, RUBY, RUBY, EMPTY, EMPTY)
        self.assertEqual({0, 1, 2, 3}, find_matches(cells, 1, 6))

    def test_run_of_two_does_not_match(self) -> None:
        self.assertEqual(set(), find_matches((RUBY, RUBY, EMERALD), 1, 3))

    def test_gravity_packs_to_bottom(self) -> None:
        cells = [RUBY, EMPTY, EMERALD]
        apply_gravity(cells, 3, 1)
        self.assertEqual([EMPTY, RUBY, EMERALD], cells)

    def test_swap_needs_two_occupied_cells(self) -> None:
        cells = (EMPTY, RUBY, RUBY, RUBY)
        self.assertFalse(is_legal_swap(cells, 1, 4, 0, 1))

    def test_swap_must_create_a_match(self) -> None:
        cells = (RUBY, EMERALD, RUBY, SAPPHIRE)
        self.assertFalse(is_legal_swap(cells, 1, 4, 0, 1))
        cells = (RUBY, EMERALD, RUBY, RUBY)
        self.assertTrue(is_legal_swap(cells, 1, 4, 0, 1))

    def test_diagonal_swaps_are_illegal(self) -> None:
        cells = (RUBY, EMERALD, EMERALD, RUBY, RUBY, EMERALD)
        self.assertFalse(is_legal_swap(cells, 2, 3, 0, 4))

    def test_board_round_trips_through_text(self) -> None:
        board = "RBB...\nBRGRGG"
        cells = parse_board(board, 2, 6)
        self.assertEqual(board, render_board(cells, 2, 6))


class CatalogTest(unittest.TestCase):
    """The gate that proves this mirror still agrees with the shipped Kotlin engine."""

    def test_every_catalog_level_verifies(self) -> None:
        self.assertEqual([], self_check())

    def test_catalog_deals_are_clean_and_playable(self) -> None:
        for level in read_catalog():
            with self.subTest(level=level["id"]):
                rows, cols = level["rows"], level["cols"]
                cells = parse_board(level["board"], rows, cols)
                # generateRound does not settle the deal, so a standing match would just sit there.
                self.assertEqual(set(), find_matches(cells, rows, cols))
                self.assertTrue(counts_are_multiples_of_three(cells))
                self.assertTrue(all_legal_swaps(cells, rows, cols))

    def test_packed_levels_are_full_and_hard(self) -> None:
        packed = [lvl for lvl in read_catalog() if lvl["id"] >= 16]
        for level in packed:
            with self.subTest(level=level["id"]):
                rows, cols = level["rows"], level["cols"]
                cells = parse_board(level["board"], rows, cols)
                gaps = sum(1 for c in cells if c == EMPTY)
                self.assertLessEqual(gaps, 1, "packed levels leave at most one gap")
                self.assertGreaterEqual(len(all_legal_swaps(cells, rows, cols)), 10)
                self.assertFalse(greedy_solves(cells, rows, cols, True))
                self.assertFalse(greedy_solves(cells, rows, cols, False))


class BuilderTest(unittest.TestCase):
    def test_column_targets_sum_and_fit(self) -> None:
        targets = column_targets(7, 7, 48, random.Random(0))
        self.assertEqual(48, sum(targets))
        self.assertTrue(all(t <= 7 for t in targets))

    def test_column_targets_rejects_oversized_request(self) -> None:
        with self.assertRaises(ValueError):
            column_targets(4, 4, 18, random.Random(0))

    def test_reverse_construction_is_solvable_by_its_own_line(self) -> None:
        rng = random.Random(7)
        for _ in range(5):
            built = build_backwards(6, 7, [6] * 7, rng)
            self.assertIsNotNone(built)
            cells, swaps = built
            self.assertEqual(42, sum(1 for c in cells if c != EMPTY))
            self.assertEqual(set(), find_matches(cells, 6, 7))
            self.assertTrue(counts_are_multiples_of_three(cells))
            self.assertTrue(verify_solves(cells, 6, 7, swaps))

    def test_solver_agrees_with_construction(self) -> None:
        rng = random.Random(11)
        cells, _ = build_backwards(6, 7, [6] * 7, rng)
        status, solution = Solver(6, 7, 300_000).solve(cells)
        self.assertEqual("solved", status)
        self.assertTrue(verify_solves(cells, 6, 7, solution))

    def test_solver_proves_a_dead_board_unsolvable(self) -> None:
        # Three colours, three tiles each, but no swap can ever line any of them up.
        cells = parse_board("RGB\nGBR\nBRG", 3, 3)
        status, _ = Solver(3, 3, 10_000).solve(cells)
        self.assertEqual("unsolvable", status)


if __name__ == "__main__":
    unittest.main()
