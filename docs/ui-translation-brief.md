# App UI: translation brief

How to write player-facing strings (menus, game names, descriptions, demos, settings, IQ test,
achievements). The Learn catalog has its own brief in `docs/learn-translation-brief.md` and its
own pipeline; do not mix the two.

German (`values-de/strings.xml`) is the worked UI reference for register and for not translating
word for word. Match that, not the English syntax.

## Informal address

English has no T–V distinction. Every locale that does must pick the informal form and keep it:

| | Informal | Not this |
|---|---|---|
| German | du, Tippe, Schau, Merk dir | Sie, Tippen Sie, Beobachten Sie |
| French | tu, Touche, Regarde | vous, Touchez, Regardez |
| Spanish | tú, Toca, Mira | usted, Toque |
| Dutch | je, Tik, Kijk | u, Tikt u |
| Italian | tu, Tocca | lei, Tocchi |
| Portuguese | tu, Toca | você where the rest of the file is tu |
| Russian | ты, Нажми | Вы, Нажмите |

Imperatives use that form. Do not mix formal and informal in one locale. Player-facing French
UI is *tu*; do not reintroduce *vous*.

Settings rows that describe what a toggle does ("Play music and sound effects.") can stay
impersonal. Certificates and legal lines can stay formal. Everything that talks *to* the player
does not.

## Rewrite, don't translate

A string that keeps English word order is wrong even when every word is in the target language.

German tells:

* A hanging separable prefix after a long phrase: "Tippe sie jetzt in der gleichen Reihenfolge an."
  Native: "Tippe sie jetzt in derselben Reihenfolge nach."
* "teilen" for English "share" (a trait, a shape). Native: "haben gemeinsam" / "dieselbe Form haben."
* "Beobachte" for English "Watch" in a game prompt. Native: "Schau" / "Merk dir."
* "in der gleichen Reihenfolge" for "in the same order as before." Native: "in derselben Reihenfolge."
* "eintippen" for tapping a sequence on screen. Native: "nachtippen."
* "wahr machen" for "make the equation true." Native: "damit die Gleichung stimmt."

The test: would a native speaker say this out loud to a friend sitting next to the phone? If it
only works as a subtitle under the English, rewrite it.

## Length and names

Keep close to the English length. Demo captions and `_title` keys sit on small cards.

Game names follow the three tiers in `values/strings.xml` (translate / loanword / leave in English).
Do not invent a new name for a game that already has one in that locale unless the current name is
wrong (a collision with a different real-world word, leftover English in a translated sentence).

Achievement descriptions are "Gewinne die Goldmedaille in \<game name\>": use the local game name,
not the English one. Wordle and N-Back stay English everywhere.

Science notes (`science_*`): paradigm names are what researchers in that language call the task,
summaries address the player with the same informal *you*, citation lines stay untranslated.

## File conventions

Same as the Learn brief: raw apostrophes, no backslash escapes, no em-dashes (spaced hyphen if
you need a break). `%1$s` / `%1$d` slots survive, all of them.
