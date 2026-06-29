#!/usr/bin/env python3
"""Regenerate Wordle answer + guess lists for en, de, fr, nl."""

from __future__ import annotations

import csv
import io
import json
import re
import tarfile
import unicodedata
import urllib.request
from collections import defaultdict
from pathlib import Path

OUT = Path(__file__).resolve().parents[1] / "composeApp/src/commonMain/composeResources/files/wordle"

URLS = {
    "en_answers": "https://gist.githubusercontent.com/cfreshman/a03ef2cba789d8cf00c08f767e0fad7b/raw",
    "en_guesses": "https://gist.githubusercontent.com/cfreshman/cdcdf777450c5b5301e439061d29694c/raw",
    "en_frequency": "https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/en/en_50k.txt",
    "en_wordnet": "https://wordnetcode.princeton.edu/wn3.1.dict.tar.gz",
    "de_targets": "https://raw.githubusercontent.com/caco3/wordle-de/main/target-words.json",
    "de_others": "https://raw.githubusercontent.com/caco3/wordle-de/main/other-words.json",
    "de_frequency": "https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/de/de_50k.txt",
    "de_nouns": "https://raw.githubusercontent.com/gambolputty/german-nouns/main/german_nouns/nouns.csv",
    "de_ynsrc_wordlists": (
        "https://github.com/ynsrc/german-categorized-wordlist/releases/download/v1.0.1/"
        "wordlists-v1.0.1.tar.gz"
    ),
    "fr_words": "https://raw.githubusercontent.com/cestpasphoto/wordle_solver_french/main/dict_fr_5.json",
    "fr_frequency": "https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/fr/fr_50k.txt",
    "fr_pos_words": "https://raw.githubusercontent.com/frodonh/french-words/master/french.txt",
    "nl_words": "https://raw.githubusercontent.com/OpenTaal/opentaal-wordlist/master/wordlist.txt",
    "nl_frequency": "https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/nl/nl_50k.txt",
    "nl_nouns": "https://raw.githubusercontent.com/CentreForDigitalHumanities/dutch-plurals/main/output.tsv",
}

ANSWER_MIN_FREQUENCY = 200

EN_ANSWER_BLOCKLIST = frozenset(
    {
        "ABOUT",
        "AFTER",
        "ALIVE",
        "ALONE",
        "ALONG",
        "AMONG",
        "BEFORE",
        "BEING",
        "BELOW",
        "COULD",
        "DOING",
        "EARLY",
        "EVERY",
        "GOING",
        "HAPPY",
        "LUCKY",
        "MIGHT",
        "NEVER",
        "OTHER",
        "OVER",
        "READY",
        "SHOULD",
        "SINCE",
        "STILL",
        "THEIR",
        "THESE",
        "THOSE",
        "THERE",
        "UNDER",
        "UNTIL",
        "WHERE",
        "WHICH",
        "WHILE",
        "WOULD",
    }
)

DE_ANSWER_BLOCKLIST = frozenset(
    {
        "ALLEM",
        "ALLEN",
        "ALLER",
        "ALLES",
        "BITTE",
        "DANKE",
        "DIESE",
        "EINEM",
        "EINEN",
        "EINES",
        "GEHEN",
        "HABEN",
        "IMMER",
        "JETZT",
        "NICHT",
        "SAGEN",
        "SCHON",
        "SEHEN",
        "WARUM",
        "WEISS",
    }
)

FR_ANSWER_BLOCKLIST = frozenset(
    {
        "ALLER",
        "ALLEZ",
        "ALORS",
        "APRES",
        "AUSSI",
        "AVAIT",
        "AVANT",
        "AVOIR",
        "CELLE",
        "CELUI",
        "CETTE",
        "CEUX",
        "COMME",
        "DONT",
        "ENTRE",
        "ETAIT",
        "ETANT",
        "FAIRE",
        "LEQUEL",
        "LEUR",
        "LEURS",
        "MERCI",
        "NOTRE",
        "QUAND",
        "QUOI",
        "VOTRE",
    }
)

FR_MIN_NOUN_LEXIQUE_FREQUENCY = 1.0
FR_VERB_DOMINANCE_FACTOR = 5.0
FR_VERB_DOMINANCE_MIN = 50.0


def fetch(url: str) -> str:
    with urllib.request.urlopen(url, timeout=60) as resp:
        return resp.read().decode("utf-8")


def fetch_bytes(url: str) -> bytes:
    with urllib.request.urlopen(url, timeout=120) as resp:
        return resp.read()


def strip_accents(text: str) -> str:
    """Strip combining marks via NFD. Do not use directly for German — Ä/Ö/Ü decompose to Mn."""
    normalized = unicodedata.normalize("NFD", text)
    return "".join(c for c in normalized if unicodedata.category(c) != "Mn")


_UMLAUT_MARKERS = (("\uE000", "Ä"), ("\uE001", "Ö"), ("\uE002", "Ü"))


def _shield_german_umlauts(text: str) -> str:
    for marker, letter in _UMLAUT_MARKERS:
        text = text.replace(letter, marker)
    return text


def _unshield_german_umlauts(text: str) -> str:
    for marker, letter in _UMLAUT_MARKERS:
        text = text.replace(marker, letter)
    return text


def write_list(path: Path, words: set[str]) -> None:
    path.write_text("\n".join(sorted(words)) + "\n", encoding="utf-8")


def normalize_de(word: str) -> str | None:
    w = word.upper().replace("ß", "SS")
    w = _unshield_german_umlauts(strip_accents(_shield_german_umlauts(w)))
    alphabet = set("ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ")
    if len(w) != 5 or not all(c in alphabet for c in w):
        return None
    return w


def normalize_ascii(word: str) -> str | None:
    w = strip_accents(word).upper()
    if len(w) != 5 or not re.fullmatch(r"[A-Z]{5}", w):
        return None
    return w


def load_frequency_map(url: str, normalize) -> dict[str, int]:
    frequencies: dict[str, int] = {}
    for line in fetch(url).splitlines():
        parts = line.split()
        if len(parts) < 2:
            continue
        normalized = normalize(parts[0])
        if normalized is None:
            continue
        count = int(parts[1])
        frequencies[normalized] = max(frequencies.get(normalized, 0), count)
    return frequencies


def is_excluded_by_pos(
    word: str,
    *,
    nouns: set[str],
    non_nouns: set[str],
    verbs: set[str],
    adjectives: set[str],
    blocklist: frozenset[str],
) -> bool:
    if word in blocklist:
        return True
    if word not in nouns:
        return True
    if word in non_nouns and word not in nouns:
        return True
    if word in verbs and word not in nouns:
        return True
    if word in adjectives and word not in nouns:
        return True
    return False


def load_wordnet_indexes() -> tuple[set[str], set[str], set[str], set[str]]:
    archive = fetch_bytes(URLS["en_wordnet"])
    indexes: dict[str, set[str]] = {}
    with tarfile.open(fileobj=io.BytesIO(archive), mode="r:gz") as tar:
        for member in ("dict/index.noun", "dict/index.verb", "dict/index.adj", "dict/index.adv"):
            extracted = tar.extractfile(member)
            if extracted is None:
                raise FileNotFoundError(f"Missing WordNet member: {member}")
            words: set[str] = set()
            for line in io.TextIOWrapper(extracted, encoding="latin-1"):
                if not line or line[0].isspace():
                    continue
                parts = line.split()
                if len(parts) < 2:
                    continue
                normalized = normalize_ascii(parts[0].replace("_", " "))
                if normalized is not None:
                    words.add(normalized)
            indexes[member] = words
    return (
        indexes["dict/index.noun"],
        indexes["dict/index.verb"],
        indexes["dict/index.adj"],
        indexes["dict/index.adv"],
    )


def load_en() -> tuple[set[str], set[str]]:
    answers = {normalize_ascii(w) for w in fetch(URLS["en_answers"]).splitlines() if w.strip()}
    guesses = {normalize_ascii(w) for w in fetch(URLS["en_guesses"]).splitlines() if w.strip()}
    answers.discard(None)
    guesses.discard(None)
    nouns, verbs, adjectives, adverbs = load_wordnet_indexes()
    frequencies = load_frequency_map(URLS["en_frequency"], normalize_ascii)
    answers = {
        word
        for word in answers
        if frequencies.get(word, 0) >= ANSWER_MIN_FREQUENCY
        and not is_excluded_by_pos(
            word,
            nouns=nouns,
            non_nouns=adverbs,
            verbs=verbs,
            adjectives=adjectives,
            blocklist=EN_ANSWER_BLOCKLIST,
        )
    }
    guesses |= answers
    return answers, guesses


def is_pure_german_noun(pos: str | None) -> bool:
    if not pos:
        return False
    tags = {tag.strip() for tag in pos.split(",")}
    disallowed = {
        "Affix",
        "Suffix",
        "Toponym",
        "Nachname",
        "Gebundenes Lexem",
        "adjektivische Deklination",
        "Eigenname",
        "Vorname",
    }
    if tags & disallowed:
        return False
    return "Substantiv" in tags


def load_de_noun_forms() -> set[str]:
    reader = csv.DictReader(io.StringIO(fetch(URLS["de_nouns"])))
    noun_columns = [name for name in reader.fieldnames or [] if name.startswith("nominativ")]
    plural_columns = [name for name in reader.fieldnames or [] if "plural" in name]
    forms: set[str] = set()
    for row in reader:
        if not is_pure_german_noun(row.get("pos")):
            continue
        for column in noun_columns + plural_columns:
            normalized = normalize_de(row.get(column, "") or "")
            if normalized is not None:
                forms.add(normalized)
        normalized = normalize_de(row.get("lemma", "") or "")
        if normalized is not None:
            forms.add(normalized)
    return forms


def load_ynsrc_wordlist(member: str, archive: bytes, normalize) -> set[str]:
    words: set[str] = set()
    with tarfile.open(fileobj=io.BytesIO(archive), mode="r:gz") as tar:
        extracted = tar.extractfile(member)
        if extracted is None:
            raise FileNotFoundError(f"Missing ynsrc wordlist member: {member}")
        for line in io.TextIOWrapper(extracted, encoding="utf-8"):
            normalized = normalize(line.strip())
            if normalized is not None:
                words.add(normalized)
    return words


def load_de_pos_filters() -> tuple[set[str], set[str], set[str], set[str]]:
    archive = fetch_bytes(URLS["de_ynsrc_wordlists"])
    ynsrc_nouns = (
        load_ynsrc_wordlist("v1/noun-der.txt", archive, normalize_de)
        | load_ynsrc_wordlist("v1/noun-die.txt", archive, normalize_de)
        | load_ynsrc_wordlist("v1/noun-das.txt", archive, normalize_de)
        | load_ynsrc_wordlist("v1/noun-plural.txt", archive, normalize_de)
    )
    non_nouns: set[str] = set()
    for member in (
        "v1/pronoun.txt",
        "v1/article.txt",
        "v1/adverb.txt",
        "v1/conjunction.txt",
        "v1/particle.txt",
        "v1/interjection.txt",
    ):
        non_nouns |= load_ynsrc_wordlist(member, archive, normalize_de)
    verbs = load_ynsrc_wordlist("v1/verb.txt", archive, normalize_de)
    adjectives = load_ynsrc_wordlist("v1/adjective.txt", archive, normalize_de)
    return ynsrc_nouns, non_nouns, verbs, adjectives


def is_excluded_de_answer(
    word: str,
    *,
    ynsrc_nouns: set[str],
    non_nouns: set[str],
    verbs: set[str],
    adjectives: set[str],
) -> bool:
    if word in DE_ANSWER_BLOCKLIST:
        return True
    if word in non_nouns and word not in ynsrc_nouns:
        return True
    if word in verbs and word not in ynsrc_nouns:
        return True
    if word in adjectives and word not in ynsrc_nouns:
        return True
    return False


def load_de() -> tuple[set[str], set[str]]:
    targets = json.loads(fetch(URLS["de_targets"]))["data"]
    others = json.loads(fetch(URLS["de_others"]))["data"]
    frequencies = load_frequency_map(URLS["de_frequency"], normalize_de)
    noun_forms = load_de_noun_forms()
    ynsrc_nouns, non_nouns, verbs, adjectives = load_de_pos_filters()
    candidates = {normalize_de(w) for w in targets}
    candidates.discard(None)
    answers = {
        word
        for word in candidates
        if word in noun_forms
        and frequencies.get(word, 0) >= ANSWER_MIN_FREQUENCY
        and not is_excluded_de_answer(
            word,
            ynsrc_nouns=ynsrc_nouns,
            non_nouns=non_nouns,
            verbs=verbs,
            adjectives=adjectives,
        )
    }
    guesses = {normalize_de(w) for w in targets + others}
    guesses.discard(None)
    return answers, guesses


def load_fr_pos_filters() -> tuple[set[str], set[str], set[str], set[str], dict[str, float], dict[str, float]]:
    tags_by_word: dict[str, set[str]] = defaultdict(set)
    noun_frequencies: dict[str, float] = defaultdict(float)
    verb_frequencies: dict[str, float] = defaultdict(float)
    for line in fetch(URLS["fr_pos_words"]).splitlines():
        parts = line.split("\t")
        if len(parts) < 2:
            continue
        normalized = normalize_ascii(parts[0])
        if normalized is None:
            continue
        pos = parts[1]
        tags_by_word[normalized].add(pos)
        try:
            frequency = float(parts[3]) if len(parts) > 3 and parts[3] else 0.0
        except ValueError:
            frequency = 0.0
        if pos == "NOM" or pos.startswith("NOM"):
            noun_frequencies[normalized] = max(noun_frequencies[normalized], frequency)
        if pos == "VER" or pos.startswith("VER"):
            verb_frequencies[normalized] = max(verb_frequencies[normalized], frequency)

    nouns: set[str] = set()
    verbs: set[str] = set()
    adjectives: set[str] = set()
    non_nouns: set[str] = set()
    for word, tags in tags_by_word.items():
        if any(tag == "NOM" or tag.startswith("NOM") for tag in tags):
            nouns.add(word)
        if any(tag == "VER" or tag.startswith("VER") for tag in tags):
            verbs.add(word)
        if any(tag.startswith("ADJ") for tag in tags):
            adjectives.add(word)
        if any(tag.startswith(prefix) for tag in tags for prefix in ("PRO", "ART", "ADV", "CON", "PRE", "ONO", "AUX")):
            non_nouns.add(word)
    return nouns, non_nouns, verbs, adjectives, noun_frequencies, verb_frequencies


def is_excluded_fr_answer(
    word: str,
    *,
    noun_frequencies: dict[str, float],
    verb_frequencies: dict[str, float],
    nouns: set[str],
    non_nouns: set[str],
    verbs: set[str],
    adjectives: set[str],
) -> bool:
    if is_excluded_by_pos(
        word,
        nouns=nouns,
        non_nouns=non_nouns,
        verbs=verbs,
        adjectives=adjectives,
        blocklist=FR_ANSWER_BLOCKLIST,
    ):
        return True
    if noun_frequencies.get(word, 0.0) < FR_MIN_NOUN_LEXIQUE_FREQUENCY:
        return True
    verb_frequency = verb_frequencies.get(word, 0.0)
    noun_frequency = noun_frequencies.get(word, 0.0)
    if verb_frequency >= max(noun_frequency * FR_VERB_DOMINANCE_FACTOR, FR_VERB_DOMINANCE_MIN):
        return True
    return False


def load_fr() -> tuple[set[str], set[str]]:
    words = json.loads(fetch(URLS["fr_words"]))["words"]
    guesses: set[str] = set()
    for word in words:
        normalized = normalize_ascii(word)
        if normalized is not None:
            guesses.add(normalized)
    nouns, non_nouns, verbs, adjectives, noun_frequencies, verb_frequencies = load_fr_pos_filters()
    frequencies = load_frequency_map(URLS["fr_frequency"], normalize_ascii)
    answers = {
        word
        for word in guesses
        if frequencies.get(word, 0) >= ANSWER_MIN_FREQUENCY
        and not is_excluded_fr_answer(
            word,
            noun_frequencies=noun_frequencies,
            verb_frequencies=verb_frequencies,
            nouns=nouns,
            non_nouns=non_nouns,
            verbs=verbs,
            adjectives=adjectives,
        )
    }
    return answers, guesses


def load_nl_noun_forms() -> set[str]:
    forms: set[str] = set()
    for line in fetch(URLS["nl_nouns"]).splitlines():
        if "N(" not in line:
            continue
        prefix = line.split("(", 1)[0]
        for chunk in re.findall(r"[A-Za-zÀ-ÿ]+", prefix):
            normalized = normalize_ascii(chunk)
            if normalized is not None:
                forms.add(normalized)
    return forms


def load_nl() -> tuple[set[str], set[str]]:
    guesses: set[str] = set()
    for line in fetch(URLS["nl_words"]).splitlines():
        normalized = normalize_ascii(line.strip())
        if normalized is not None:
            guesses.add(normalized)
    noun_forms = load_nl_noun_forms()
    frequencies = load_frequency_map(URLS["nl_frequency"], normalize_ascii)
    answers = {
        word
        for word in guesses
        if word in noun_forms
        and frequencies.get(word, 0) >= ANSWER_MIN_FREQUENCY
    }
    return answers, guesses


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    loaders = {
        "en": load_en,
        "de": load_de,
        "fr": load_fr,
        "nl": load_nl,
    }
    for tag, loader in loaders.items():
        answers, guesses = loader()
        write_list(OUT / f"answers_{tag}.txt", answers)
        write_list(OUT / f"guesses_{tag}.txt", guesses)
        print(f"{tag}: {len(answers)} answers, {len(guesses)} guesses")


if __name__ == "__main__":
    main()