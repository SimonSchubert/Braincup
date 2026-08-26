# Learn Math: first release status

Working document for the Learn Math section's first release. It survives between Claude
sessions: read it before touching anything under `learn/`, and update it as work lands.

Last updated: 2026-08-26 (parking done, Geometry rework not started)

---

## 1. Release scope

The first release ships **two topics**: Arithmetic and Geometry. The other six are parked.

| Topic | v1 | Sub-topics | State |
|---|---|---|---|
| Arithmetic | **ships** | 6 | reworked, needs a readiness pass |
| Geometry | **ships** | 5 today, ~12 after the split | grade slices, full rework needed |
| Measurement | parked | 0 | perimeter + area moved into Geometry, rest cut |
| Data & Probability | parked | 3 | grade slices |
| Algebra | parked | 7 | fully reworked, parked anyway |
| Trigonometry | parked | 2 | grade slices |
| Pre-calculus | parked | 2 | grade slices |
| Calculus | parked | 1 | grade slices |

Decisions taken (2026-08-26):

* **Park by deleting to a branch**, not by a runtime flag. Master carries only shipped
  content. Accepted cost: the parked content stops compiling against master, so a
  `LearnVisual` or `LessonStep` change will need porting when a topic is unparked.
* **Perimeter and area move from Measurement into Geometry.** Without them v1 would jump
  from naming shapes straight to Pythagoras, and area is what a learner meets next.
  Length, time, money and metric units stay parked.
* **Geometry splits into real sub-topics**, one coherent subject per unit, the way Algebra
  reads. It does not stay as four grade slices with three unrelated lessons each.
* Algebra is fully reworked and is parked regardless. It is the reference for what a
  finished ladder looks like: read `AlgebraContent.kt` on the `learn-parked` branch when
  authoring Geometry.

---

## 2. Parking: how it is done

`learn-parked` is a **frozen snapshot branch**, not a live fork. It points at the last
master commit that still contained all eight topics. Nothing is ever committed onto it.
Unparking is a file checkout out of that ref, not a merge.

### Steps

- [x] 1. Commit the in-flight topic-first restructure to master (89 files, currently
      uncommitted). The branch needs a commit to point at, and this one is a coherent unit
      of work on its own.
- [x] 2. `git branch learn-parked` at that commit. Never commit to it again.
- [x] 3. Move `Perimeter` and `Area of rectangles` out of `MeasurementContent.kt` into
      `GeometryContent.kt` before deleting the file.
- [x] 4. `git rm` the six parked content files:
      `AlgebraContent.kt`, `MeasurementContent.kt`, `DataContent.kt`,
      `TrigonometryContent.kt`, `FunctionsContent.kt`, `CalculusContent.kt`.
- [x] 5. Remove the six parked entries and their imports from `MathTopic.kt`, and their
      rows from `LearnCatalog.byTopic`.
- [x] 6. Delete the 12 parked topic strings from `values/strings.xml` **and all 44
      `values-*/strings.xml`**: `learn_topic_{measurement,data,algebra,trigonometry,functions,calculus}`
      and each `_subtitle`. That is 540 lines. Nothing else in the section is localized:
      lesson prose lives in Kotlin.
- [x] 7. Fix the references the deletion breaks:
      - `LearnCertificateScreen.kt:175` preview uses `MathTopic.TRIGONOMETRY`
      - `LearnTopicScreen.kt:123` preview uses `MathTopic.ALGEBRA`
      - `LearnUnitScreen.kt:274` preview uses `MathTopic.ALGEBRA`
      - `UserStorageLearnTest.kt` uses `MEASUREMENT`, `TRIGONOMETRY` and `ALGEBRA`
- [x] 8. `LearnCatalogTest`: `reworked` becomes `setOf(MathTopic.ARITHMETIC)` and
      `RATCHET` drops from **50 to 15**. See section 5 for why 15.
- [x] 9. Leave `GradeLevel.GRADES_11_12` and its two strings in place even though no
      shipped unit uses it, so parked content still compiles when restored.

Done 2026-08-26 in `b2a56c22` (restructure, the branch point) and the parking commit on top.
Also cut, and needed back when a topic is unparked: the six `TopicTilePreview` sketches in
`LearnTilePreviews.kt` (`RulerPreview`, `BarChartPreview`, `UnknownSlotPreview`,
`UnitCirclePreview`, `LinearPlotPreview`, `TangentPreview`) and their `when` arms.

### Unparking, later

```
git checkout learn-parked -- composeApp/src/commonMain/kotlin/com/inspiredandroid/braincup/learn/content/AlgebraContent.kt
git show learn-parked:composeApp/src/commonMain/composeResources/values/strings.xml | grep learn_topic_algebra
```

Then re-add the `MathTopic` entry, the `LearnCatalog.byTopic` row, the two strings across
45 files, and expect to port the content forward if the models moved. Check
`git diff learn-parked..master -- .../learn/LearnModels.kt .../learn/LearnVisual.kt`
first: that diff is the size of the porting job.

---

## 3. What "ready" means

A sub-topic is `ready` when all nine hold. The bar is set by the counting and
multiplication passes, which are the ones to copy.

1. **One coherent subject.** The unit teaches one thing and its three lessons are three
   steps into that thing, not three different subjects sharing a grade band.
2. **Shape.** 3 lessons x 6 steps, and a test of at least 6 questions.
   *Enforced by `everyTopicHasSubTopicsWithLessonsAndATest`.*
3. **Every step and question carries a figure.**
   *Enforced by `everyStepAndQuestionHasAVisual`.*
4. **No spoiler figures.** No question figure captions the answer it is asking for
   (`reveal = false` on every question figure).
   *Enforced strictly once the unit id is listed in `reworkedUnits`.*
5. **Formula leads.** A question step whose question is an equation carries `formula`
   ("7 x 3 = ?"), and the prose drops to a supporting line that only says how to read the
   picture. *Partially enforced by `questionFormulasAskRatherThanTell`.*
6. **Tinted numbers.** `{a:N}` / `{b:N}` markup in prose wherever a number in the text is
   the same number the figure draws in an accent colour.
7. **The right figure, not a borrowed one.** If the nearest variant captions itself wrongly
   for this subject, extend the variant or add one. Ratio got its own `RatioBar` because a
   `Fraction` bar captions 1 : 4 as "1/5", which is the exact confusion the lesson exists
   to clear up.
8. **Content checked.** Answers correct, distractors plausible rather than obviously wrong,
   explanations actually explain, spelling consistent with the rest of the section.
9. **Seen rendering.** Opened once in the running app or a Paparazzi render, on a narrow
   screen, not just read in the source.

Status values used in the tables: `ready` / `review` (reworked, readiness pass not done) /
`rework` (still grade-slice content) / `todo` (does not exist yet).

---

## 4. Arithmetic (ships)

All six are reworked and carry zero spoiler figures. What is left is the readiness pass,
mostly items 1, 5, 6 and 8.

| # | Unit id | Title | Level | Q | Tinted | Formula-led | Status |
|---|---|---|---|---|---|---|---|
| 1 | `arithmetic-counting` | Counting and first sums | g12 | 10 | 4 | 7/10 | **`ready`** * |
| 2 | `arithmetic-multiplication` | Multiplication and division | g35 | 7 | 6 | 7/7 | `review` |
| 3 | `arithmetic-fractions` | Fractions | g35 | 9 | 2 | 4/9 | `review` |
| 4 | `arithmetic-decimals` | Decimals | g35 | 9 | 3 | 4/9 | `review` |
| 5 | `arithmetic-ratio-and-percent` | Negatives, ratio and percent | g68 | 9 | 11 | 5/9 | `review` |
| 6 | `arithmetic-standard-form-and-surds` | Standard form, surds and bounds | g910 | 7 | 0 | 2/7 | `review` |

\* Items 1 to 8 pass. Item 9, the render check, is batched for the whole topic at the end of
the pass rather than done per unit.

**Do not read the Tinted and Formula-led columns as scores.** Both are decided by which
figures and questions a unit uses, not by how finished it is:

* `{a:}` / `{b:}` tinting only makes sense against a figure with **two** accents, which means
  `TenFrame`, `PlaceValue`, `Fraction`, `RatioBar` and friends. `NumberLine`, `Steps`,
  `AreaGrid` and `RightTriangle` draw with one accent, so prose that points at them is
  correctly left untinted. Unit 6 uses only single-accent figures, which is why it shows 0.
  Unit 1's tinting sits entirely in its `TenFrame` and `PlaceValue` lessons for the same
  reason, and that is right.
* A leading `formula` belongs on a question that **is** an equation. A question like "what
  number is built here" has no equation to lead with, so a ratio below 1 is expected.

The one real weak spot found so far:

* **Units 5 and 6 fail readiness item 1.** "Negatives, ratio and percent" is three subjects,
  and so is "Standard form, surds and bounds". Under the split rule just adopted for
  Geometry they should become six units. Open decision, see section 7.

### Pass notes

**1. `arithmetic-counting`, reviewed 2026-08-26.** Four defects found and fixed; all 25
answers and explanations otherwise checked and correct.

| What | Where | Fix |
|---|---|---|
| "What comes after 25?" listed 26 as an option and marked it wrong. The figure asks for the next term of a step-five sequence, so a learner counting on is punished for reading the prompt literally. | test, Q6 | Prompt now asks for the next jump. |
| "Which number is smaller, 62 or 26?" drew only 26. `PlaceValue`'s own KDoc says a step asking which of two numbers is larger has to show both. | test, Q5 | `compare = 2 to 6` so both are drawn, and the explanation now reasons about the rods rather than restating the answer. |
| "How many loose ones are here?" was explained with "Five rods and three cubes make 53", which answers a different question. | test, Q4 | Explanation now explains the 3. |
| "These two have the same rods, so the loose ones decide." is a statement; the step never asked anything. | `g12-arithmetic-tens`, step 6 | Now asks which is larger, keeping the hint. |

---

## 5. Geometry (ships)

Today: four grade slices, 15 spoiler figures between them. That 15 is the whole of the
post-parking `RATCHET`, so the ratchet doubles as the release progress meter and reaches 0
exactly when Geometry is done.

### Today

| Unit id | Title | Level | Spoilers | Lessons |
|---|---|---|---|---|
| `geometry-shapes` | Flat and solid shapes | g12 | 1 | Flat shapes / Solid shapes / Halves and quarters |
| `geometry-angles-and-symmetry` | Angles, quadrilaterals and symmetry | g35 | 3 | Angles and turns / Sorting quadrilaterals / Symmetry |
| `geometry-perimeter-and-area` | Perimeter and area | g35 | 0 | Perimeter / Area of rectangles / Shapes made of rectangles |
| `geometry-pythagoras-and-circles` | Pythagoras, circles and volume | g68 | 9 | Pythagoras / Circles / Volume of prisms |
| `geometry-similarity-and-proof` | Similarity, transformations and proof | g910 | 2 | Similar figures / Transformations / Circle theorems |

None carry tinted numbers, and not one question step leads with a formula (0 of 43).

`geometry-perimeter-and-area` is the moved Measurement material, not a reworked sub-topic. Its
two Perimeter and Area lessons came across unchanged, keeping lesson ids
`g35-measurement-perimeter` and `g35-measurement-area` so progress survives. A third lesson,
`geometry-area-compound`, was written to fill the unit out, the metric-units lesson stayed behind
on the branch, and the two spoiling `AreaGrid` figures ("which unit belongs to an area", "which
measurement is an area") were closed with `reveal = false`, which is why the unit contributes 0
to the ratchet. It still needs the same rework as the other four.

### Target ladder

Proposed, confirm before authoring. Each unit is 3 lessons + a 6-question test.

| # | Unit id | Title | Level | Source | Status |
|---|---|---|---|---|---|
| 1 | `geometry-flat-shapes` | Flat shapes | g12 | split from `geometry-shapes` | `rework` |
| 2 | `geometry-solid-shapes` | Solid shapes | g12 | split from `geometry-shapes` | `rework` |
| 3 | `geometry-angles` | Angles and turns | g35 | split from `geometry-angles-and-symmetry` | `rework` |
| 4 | `geometry-quadrilaterals` | Triangles and quadrilaterals | g35 | split from `geometry-angles-and-symmetry` | `rework` |
| 5 | `geometry-symmetry` | Symmetry | g35 | split from `geometry-angles-and-symmetry` | `rework` |
| 6 | `geometry-perimeter-and-area` | Perimeter and area | g35 | **moved, unit exists** | `rework` |
| 7 | `geometry-pythagoras` | Pythagoras' theorem | g68 | split from `geometry-pythagoras-and-circles` | `rework` |
| 8 | `geometry-circles` | Circles | g68 | split from `geometry-pythagoras-and-circles` | `rework` |
| 9 | `geometry-volume` | Volume and surface area | g68 | split from `geometry-pythagoras-and-circles` | `rework` |
| 10 | `geometry-similarity` | Similarity and scale | g910 | split from `geometry-similarity-and-proof` | `rework` |
| 11 | `geometry-transformations` | Transformations | g910 | split from `geometry-similarity-and-proof` | `rework` |
| 12 | `geometry-circle-theorems` | Circle theorems | g910 | split from `geometry-similarity-and-proof` | `rework` |

Notes on the split:

* **"Halves and quarters" is dropped.** It is fraction material and Arithmetic teaches
  fractions properly at g35. Confirm before deleting it.
* **Keep existing lesson ids** where a lesson survives the split, because progress is keyed
  on them. `g12-geometry-flat-shapes` stays `g12-geometry-flat-shapes` even as its unit
  becomes `geometry-flat-shapes`. New lessons use `geometry-<subtopic>-<lesson>`.
* **Unit ids do change**, so certificates already earned against `geometry-shapes` are lost.
  Acceptable pre-release, not acceptable after.
* Twelve Geometry units against six Arithmetic units is lopsided. That resolves either by
  merging some Geometry units or by splitting Arithmetic units 5 and 6, see section 7.

---

## 6. Working order

1. Park (section 2), leaving master green with two topics.
2. Move perimeter and area into Geometry and confirm the target ladder.
3. Author Geometry one sub-topic at a time, top of the ladder down. After each one lands,
   add its unit id to `reworkedUnits` in `LearnCatalogTest` and lower `RATCHET` by that
   unit's spoiler count. Never raise `RATCHET`.
4. Once all twelve are in, replace `reworkedUnits` with `MathTopic.GEOMETRY` in `reworked`
   and assert `RATCHET == 0`.
5. Run the Arithmetic readiness pass, unit by unit, against section 3.
6. Only then localize: no new UI strings are expected, so this should be a no-op. Verify
   with a diff of `values/strings.xml` against the parking commit.

---

## 7. Open decisions

| # | Question | Why it matters | State |
|---|---|---|---|
| 1 | Do Arithmetic units 5 and 6 split into six the way Geometry is splitting? | Consistency of the readiness bar, and it rebalances 12 vs 6. Costs six new units of authoring and breaks two unit ids. | **open** |
| 2 | Is the 12-unit Geometry ladder right, or should some units merge? | Sets the whole authoring workload. | **open** |
| 3 | Drop "Halves and quarters", or keep it as a g12 Geometry unit? | Overlaps Arithmetic fractions. | **open** |
| 4 | Does the section ship with `GRADES_11_12` unreachable? | Two dead strings across 45 files, and the level picker copy mentions calculus. | **open** |

---

## 8. Session log

| Date | What happened |
|---|---|
| 2026-08-26 | Scope set: ship Arithmetic + Geometry, park six. Parking method, perimeter/area move and Geometry split decided. This document created. |
| 2026-08-26 | Readiness pass started. `arithmetic-counting` reviewed and marked ready: four defects fixed (see section 4). Corrected a wrong lead in this document: low Tinted / Formula-led counts are a property of the figures a unit uses, not a sign of unfinished work. |
| 2026-08-26 | Restructure committed as `b2a56c22`; `learn-parked` branched there. Parking executed: 6 content files, 6 `MathTopic` entries, 6 tile sketches and 540 locale strings removed; perimeter and area moved into Geometry as `geometry-perimeter-and-area`; previews and tests repointed at shipped topics; `RATCHET` 50 -> 15 and verified tight at exactly 15. `desktopTest` green apart from the by-design `NurikabeMeasureTest.measure`. Geometry rework not started. |
