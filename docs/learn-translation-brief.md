# Learn Math: translation brief

The brief every locale agent works from. One agent owns one language and takes it end to end, so
that the words a learner meets in lesson 1 are still the words they meet in lesson 40.

Scope per locale: **1,242 strings and 9 plural sets**, about 12,300 words of English. German
(`values-de/strings.xml`) and Spanish (`values-es/strings.xml`) are done in full and are the
worked references for anything this brief leaves open.

## Where this stands (2026-08-29)

| | |
|---|---|
| Complete | `de`, `es`, `fr` |
| Partly done, 36% to 52% | `it` `ru` `pt` `zh` `zh-TW` `id` `hi` `ja` `ko` |
| Not started | the remaining 39 locales |

A run of ten agents stopped mid-catalog on an account spend limit, which is why nine locales are
still half done; `fr` was finished on 2026-08-29 by resuming that loop. **Nothing is broken by
that.** A key that never arrived falls back to English at runtime,
`check_localizations.py` counts it as pending rather than failing, and `pending` reports exactly
what is left. Resuming is just running the loop below again for a locale; no cleanup, no
reverting, no re-doing what already landed.

Cost is the real constraint on this job, not correctness. One locale took roughly 30 minutes and
350k tokens end to end. Budget accordingly before starting a wave, and prefer a small wave that
finishes over a large one that gets cut off halfway.

---

## 1. The loop

Never hand-edit the locale XML. Three commands do the whole job:

```bash
./scripts/learn_translate.py chunks  --locale es                      # what is left, by chunk
./scripts/learn_translate.py pending --locale es --chunk geometry-circles > chunk.json
#   ... write the translation into a JSON file of the same shape ...
./scripts/learn_translate.py merge   --locale es --file translated.json
./scripts/check_localizations.py --locale es                          # must pass before moving on
./scripts/learn_text_fit.py      --locale es                          # must report nothing
```

To revise a key you have already merged, put it in a fresh JSON file and merge again with
`--force`. Never edit the XML to fix something.

The JSON is `{"locale": ..., "strings": {key: text}, "plurals": {name: {quantity: text}}}`. Only
the keys you are translating need to be in it. `merge` puts each key where the base file has it,
so chunks may be done in any order.

**Work one chunk at a time and merge after each one.** A locale has about 30 chunks. `pending`
only ever reports what is still missing, so a run that stops halfway resumes cleanly, and a key
that never arrives falls back to English in the app rather than showing blank. Never batch the
whole catalog into one JSON file.

Run `check_localizations.py --locale <yours>` after every merge. Do not start the next chunk
while it reports an issue.

---

## 2. Rules the check enforces

A translation that breaks one of these fails the build, so they are not style advice.

**Numbers stay exactly as they are.** "eight steps left of -3 is -11" translates the words and
keeps `-3` and `-11` untouched, in any order the sentence needs. The figure beside the sentence
draws those same numbers, so a changed digit is a broken question. Spelled-out numbers in the
English prose ("eight", "ten") are words and translate normally.

Three ways that rule bites, all of them found the hard way:

* **The decimal separator does not change.** `0.5` stays `0.5` even in a language that writes
  `0,5`, because the figure beside it draws a point. Your prose has to name it the way the app
  shows it. Spanish says "el punto" and German says "der Punkt" for exactly this reason. Changing
  it fails the check with a number mismatch that reads like a mistranslation.
* **Thousands separators are thin spaces and stay that way.** `3 872 = 3 900`, `45 000`,
  `2 000 000`. Writing `3.872` or `3,872` adds a number and fails.
* **Watch for idioms that swallow a number.** English "to the nearest 10 cm" has an idiomatic
  Spanish form, "a la decena", which deletes the `10` and fails. The wording has to keep the digit
  even when your language would normally absorb it.

**`{a:...}` and `{b:...}` markers survive, one for one.** They colour a run orange (the given) or
blue (the working) to match the figure. Move them with the words they belong to, and translate
what is inside: `{a:six} rods` becomes `{a:sechs} Stangen` in German. What may never change is how
many `{a:` and how many `{b:` the sentence has. Never author `{c:...}`; the app substitutes that
one itself.

**`%1$s` and `%1$d` slots survive, all of them.** They take a value the lesson supplies. Reorder
them freely if the grammar needs it, but every slot in the English has to appear in the
translation, and no new ones.

**Plural sets need an `other` form.** Give your language exactly the categories CLDR defines for
it, from `zero one two few many other`, and never invent one. Every form keeps the same slots as
the English. The 9 catalog plurals count physical things a figure draws (sides, corners, rows,
lines of symmetry) and amounts in answer options (euros, litres, degrees, square cm), so they need
whatever agreement your language uses for small counts, not just singular and plural.

---

## 3. Quality bar

**Your language has already fixed some of this vocabulary, and you do not get to re-choose it.**
About 106 `learn_*` strings in your file are UI chrome and figure captions that shipped long ago:
solid names, `learn_fig_*` captions, level subtitles. Grep them out and read them before you start.
Whatever word they use for vertex, edge, face, shape or ratio is the word the catalog has to use,
because the two appear on screen together.

**Fix the rest of your terminology before you translate a word of it.** Write down your language's term for
each of these, then use it everywhere without variation: number line, hop, step, sum, digit, tens
and ones, place value, remainder, fraction, numerator, denominator, decimal, percent, ratio,
factor, multiple, square number, square root, surd, upper and lower bound, angle, vertex, side,
edge, face, perimeter, area, volume, radius, diameter, circumference, chord, tangent, symmetry,
reflection, rotation, translation, enlargement, scale factor, congruent, similar, hypotenuse. The
catalog is a ladder a learner climbs in order, and a word that changes halfway up reads as a new
concept.

**Aim at the reader, not the English.** These are lessons for someone meeting the idea for the
first time. Use the register a school textbook in your language would use, address the learner the
way the rest of the app already does (check the existing UI strings in your file), and prefer the
plain everyday word over the formal one wherever your curriculum allows it.

**Keep it close to the English length.** These strings render into cards and onto canvas figures
with fixed room. A translation half again as long as the English will overflow. Short keys are the
tight ones: anything ending `_o1`.. `_o4` is an answer option that sits in a small button, and
`_title` and `_summary` head a card. Prose bodies and explanations have more room but still read
better short.

**Do not translate word for word.** An explanation that narrates a calculation should narrate it
the way your language narrates a calculation.

**Watch for a term that collides with a common word.** Spanish "razón" is both *ratio* and
*reason*, which made one sentence in the ratio lesson unreadable until it was reworded. When your
chosen term doubles as an everyday word, reword the sentence around it rather than swapping the
term.

---

## 3a. Notes on the English source

Answers to things the Spanish pilot had to work out by reading `learn/content`. Take them as
given rather than deriving them again.

* **`learn_shared_right` ("Right") and `learn_shared_right_2` ("right")** are different concepts
  under near-identical text. `learn_shared_right` is a **triangle type**: it appears beside
  equilateral, isosceles and scalene, so it is your language's word in "right triangle".
  `learn_shared_right_2` is an **angle type**, the 90 degree one.
* **`learn_shared_curved_edges` ("Curved edges")** is an option about a flat shape, beside options
  that say "sides". It means the outline curves, not a solid's edge.
* **`learn_geometry_transformations_enlargements_s2_body`** teaches a quirk of the English word
  "enlargement": a scale factor below 1 still counts as one. In most languages the term has no such
  quirk, so re-aim the sentence at your own term rather than translating the English point.
* **`learn_opt_euro`** has "%1$d euro" in both plural forms in English, which is a source
  oversight, not a style. Inflect it properly for your language.
* **`learn_t_doubled` ("(%1$s + %2$s) doubled.")** is a verbless fragment. Give it whatever finite
  form your language needs.
* **`learn_geometry_area_compound_s3_result`** writes "16 square cm" in words where the figure
  beside it draws `cm²`. Follow the figure.

---

## 4. File conventions

* Raw apostrophes. Escape only `&`, `<` and `>`, and `merge` does that for you.
* No em-dashes anywhere, in any language. The English uses a spaced hyphen where it needs a break,
  and so should you unless your language has its own convention.
* `merge` writes the XML, so indentation and key placement are not your problem.

---

## 5. Done

A locale is finished when all three report clean:

```bash
./scripts/learn_translate.py chunks --locale <yours>      # "catalog is complete, nothing pending"
./scripts/check_localizations.py --locale <yours>         # exit 0
./scripts/learn_text_fit.py --locale <yours>              # 0 strings over budget
```

Then report the glossary you fixed, anything the brief did not cover that you had to decide, and
any English source string that looked wrong. Do not commit; the parent session does that.
