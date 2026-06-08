# Wordle word lists

Two files per language:

- `answers_<tag>.txt` — secret-word candidates (common/fair words).
- `guesses_<tag>.txt` — every guess the keyboard may submit (a superset of answers).

Format guarantees (enforced on generation and re-filtered on load):

- UPPERCASE, one word per line, no blank lines.
- Exactly 5 characters, all within that language's alphabet (see `WordleLanguage.kt`).
- Accent policy is applied at authoring time: French accents are stripped to their base
  letter; German keeps Ä/Ö/Ü and maps ß → SS.
- Deduplicated and sorted.

Regenerate with `python3 scripts/generate_wordle_lists.py`.

## Sources

| Tag | Answers | Guesses | Source |
|-----|---------|---------|--------|
| en | 1,510 | 12,167 | NYT Wordle lists (cfreshman gists) filtered to [WordNet](https://wordnet.princeton.edu/) nouns + [en_50k](https://github.com/hermitdave/FrequencyWords) frequency ≥200 |
| de | 944 | 4,713 | [wordle-de](https://github.com/caco3/wordle-de) targets that are common nouns ([german-nouns](https://github.com/gambolputty/german-nouns) + [german-categorized-wordlist](https://github.com/ynsrc/german-categorized-wordlist)), [de_50k](https://github.com/hermitdave/FrequencyWords) frequency ≥200; other words for guesses |
| fr | 1,144 | 7,980 | [wordle_solver_french](https://github.com/cestpasphoto/wordle_solver_french) words filtered to dominant nouns ([french-words](https://github.com/frodonh/french-words)) + [fr_50k](https://github.com/hermitdave/FrequencyWords) frequency ≥200 |
| nl | 1,229 | 9,723 | [OpenTaal wordlist](https://github.com/OpenTaal/opentaal-wordlist) filtered to nouns ([dutch-plurals](https://github.com/CentreForDigitalHumanities/dutch-plurals)) + [nl_50k](https://github.com/hermitdave/FrequencyWords) frequency ≥200 (BSD-3-Clause) |

## Adding or expanding a language

1. Add a `WordleLanguage` entry in
   `commonMain/kotlin/com/inspiredandroid/braincup/games/wordle/WordleLanguage.kt`.
2. Add `answers_<tag>.txt` and `guesses_<tag>.txt` here, then record the source in this file.
3. Currently shipped: en, de, fr, nl. Every other locale hides the Wordle tile until a
   list + config entry is added (see `WordleLanguages.resolve`).