#!/usr/bin/env python3
"""Unit tests for Wordle word-list normalization."""

from __future__ import annotations

import unittest

from generate_wordle_lists import normalize_ascii, normalize_de, strip_accents


class NormalizeDeTest(unittest.TestCase):
    def test_preserves_umlauts(self) -> None:
        self.assertEqual(normalize_de("Käfig"), "KÄFIG")
        self.assertEqual(normalize_de("Käfer"), "KÄFER")
        self.assertEqual(normalize_de("Größe"), None)  # 6 chars after ß → SS
        self.assertEqual(normalize_de("Bühne"), "BÜHNE")

    def test_rejects_wrong_length(self) -> None:
        self.assertIsNone(normalize_de("Fuß"))
        self.assertIsNone(normalize_de("Straße"))

    def test_strips_foreign_accents_on_loanwords(self) -> None:
        self.assertEqual(normalize_de("Cafés"), "CAFES")


class NormalizeAsciiTest(unittest.TestCase):
    def test_strips_french_accents(self) -> None:
        self.assertEqual(normalize_ascii("café"), None)  # 4 chars
        self.assertEqual(normalize_ascii("après"), "APRES")
        self.assertEqual(normalize_ascii("étoil"), "ETOIL")


class StripAccentsRegressionTest(unittest.TestCase):
    def test_strips_german_umlauts_without_shielding(self) -> None:
        self.assertEqual(strip_accents("KÄFIG"), "KAFIG")


if __name__ == "__main__":
    unittest.main()