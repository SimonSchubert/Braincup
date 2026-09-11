#!/usr/bin/env python3
"""Unit tests for the stale-source comparison in check_localizations.py."""

from __future__ import annotations

import unittest

from check_localizations import english_at, parse_value_history, stale_source_keys


class ParseValueHistoryTest(unittest.TestCase):
    def test_records_adds_and_rewrites_oldest_first(self) -> None:
        log = "\n".join(
            [
                "COMMIT 100",
                "+    <string name=\"game_color_confusion_desc\">Tap matching cells.</string>",
                "COMMIT 200",
                "-    <string name=\"game_color_confusion_desc\">Tap matching cells.</string>",
                "+    <string name=\"game_color_confusion_desc\">Tap the ink.</string>",
            ],
        )
        history = parse_value_history(log)
        self.assertEqual(
            history["game_color_confusion_desc"],
            [(100, "Tap matching cells."), (200, "Tap the ink.")],
        )

    def test_skips_learn_catalog_keys_and_no_op_rewrites(self) -> None:
        log = "\n".join(
            [
                "COMMIT 100",
                "+    <string name=\"learn_unit_title\">Counting</string>",
                "+    <string name=\"game_trio_desc\">Share a count.</string>",
                "COMMIT 200",
                "-    <string name=\"game_trio_desc\">Share a count.</string>",
                "+    <string name=\"game_trio_desc\">Share a count.</string>",
            ],
        )
        history = parse_value_history(log)
        self.assertNotIn("learn_unit_title", history)
        self.assertEqual(history["game_trio_desc"], [(100, "Share a count.")])


class EnglishAtTest(unittest.TestCase):
    def test_picks_the_value_that_was_current_at_t(self) -> None:
        history = {
            "k": [(10, "old"), (30, "new")],
        }
        self.assertIsNone(english_at(history, "k", 9))
        self.assertEqual(english_at(history, "k", 10), "old")
        self.assertEqual(english_at(history, "k", 29), "old")
        self.assertEqual(english_at(history, "k", 30), "new")


class StaleSourceKeysTest(unittest.TestCase):
    def test_flags_a_locale_that_still_translates_old_english(self) -> None:
        english_now = {"game_color_confusion_desc": "Tap the ink."}
        english_history = {
            "game_color_confusion_desc": [(10, "Tap matching cells."), (20, "Tap the ink.")],
        }
        locale_now = {"game_color_confusion_desc": "Tippe passende Zellen."}
        locale_history = {"game_color_confusion_desc": [(15, "Tippe passende Zellen.")]}
        self.assertEqual(
            stale_source_keys(english_now, english_history, locale_now, locale_history),
            ["game_color_confusion_desc"],
        )

    def test_passes_when_the_locale_was_rewritten_after_english(self) -> None:
        english_now = {"game_color_confusion_desc": "Tap the ink."}
        english_history = {
            "game_color_confusion_desc": [(10, "Tap matching cells."), (20, "Tap the ink.")],
        }
        locale_now = {"game_color_confusion_desc": "Tippe die Tinte an."}
        locale_history = {
            "game_color_confusion_desc": [
                (15, "Tippe passende Zellen."),
                (25, "Tippe die Tinte an."),
            ],
        }
        self.assertEqual(
            stale_source_keys(english_now, english_history, locale_now, locale_history),
            [],
        )

    def test_working_tree_rewrite_counts_as_catching_up(self) -> None:
        english_now = {"game_color_confusion_desc": "Tap the ink."}
        english_history = {
            "game_color_confusion_desc": [(10, "Tap matching cells."), (20, "Tap the ink.")],
        }
        locale_now = {"game_color_confusion_desc": "Tippe die Tinte an."}
        locale_history = {"game_color_confusion_desc": [(15, "Tippe passende Zellen.")]}
        self.assertEqual(
            stale_source_keys(english_now, english_history, locale_now, locale_history),
            [],
        )

    def test_locale_written_before_english_landed_is_not_stale_if_source_matches(self) -> None:
        english_now = {"science_ghost_grid_paradigm": "Corsi Block-Tapping Task"}
        english_history = {
            "science_ghost_grid_paradigm": [(20, "Corsi Block-Tapping Task")],
        }
        locale_now = {"science_ghost_grid_paradigm": "Corsi-Block-Tapping-Test"}
        locale_history = {"science_ghost_grid_paradigm": [(19, "Corsi-Block-Tapping-Test")]}
        self.assertEqual(
            stale_source_keys(english_now, english_history, locale_now, locale_history),
            [],
        )

    def test_ignores_learn_catalog_keys(self) -> None:
        english_now = {"learn_unit_title": "New title"}
        english_history = {"learn_unit_title": [(10, "Old title"), (20, "New title")]}
        locale_now = {"learn_unit_title": "Alter Titel"}
        locale_history = {"learn_unit_title": [(15, "Alter Titel")]}
        self.assertEqual(
            stale_source_keys(english_now, english_history, locale_now, locale_history),
            [],
        )


if __name__ == "__main__":
    unittest.main()
