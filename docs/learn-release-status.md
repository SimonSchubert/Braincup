# Learn Math: first release status

Working document for the Learn Math section's first release. It survives between Claude
sessions: read it before touching anything under `learn/`, and update it as work lands.

Last updated: 2026-08-26 (parked; both topics authored; render check outstanding)

---

## 1. Release scope

The first release ships **two topics**: Arithmetic and Geometry. The other six are parked.

| Topic | v1 | Sub-topics | State |
|---|---|---|---|
| Arithmetic | **ships** | 10 | all ready bar the render check |
| Geometry | **ships** | 12 | all ready bar the render check |
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

| # | Unit id | Title | Level | Lessons | Test | Status |
|---|---|---|---|---|---|---|
| 1 | `arithmetic-counting` | Counting and first sums | g12 | 3 | 6 | **`ready`** * |
| 2 | `arithmetic-multiplication` | Multiplication and division | g35 | 3 | 6 | **`ready`** * |
| 3 | `arithmetic-fractions` | Fractions | g35 | 3 | 6 | **`ready`** * |
| 4 | `arithmetic-decimals` | Decimals | g35 | 3 | 6 | **`ready`** * |
| 5 | `arithmetic-negatives` | Negative numbers | g68 | 3 | 6 | **`ready`** * |
| 6 | `arithmetic-ratio` | Ratio and proportion | g68 | 3 | 6 | **`ready`** * |
| 7 | `arithmetic-percent` | Percentages | g68 | 3 | 6 | **`ready`** * |
| 8 | `arithmetic-standard-form` | Standard form | g910 | 3 | 6 | **`ready`** * |
| 9 | `arithmetic-surds` | Surds | g910 | 3 | 6 | **`ready`** * |
| 10 | `arithmetic-bounds` | Rounding and bounds | g910 | 3 | 6 | **`ready`** * |

The two compound units are gone. `arithmetic-ratio-and-percent` became units 5 to 7 and
`arithmetic-standard-form-and-surds` became units 8 to 10, each with two lessons and a
six-question test written for it. The original lesson ids were kept, so the only progress
lost is the two old unit certificates, which is acceptable before release and not after.

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

### Pass notes

**1. `arithmetic-counting`, reviewed 2026-08-26.** Four defects found and fixed; all 25
answers and explanations otherwise checked and correct.

| What | Where | Fix |
|---|---|---|
| "What comes after 25?" listed 26 as an option and marked it wrong. The figure asks for the next term of a step-five sequence, so a learner counting on is punished for reading the prompt literally. | test, Q6 | Prompt now asks for the next jump. |
| "Which number is smaller, 62 or 26?" drew only 26. `PlaceValue`'s own KDoc says a step asking which of two numbers is larger has to show both. | test, Q5 | `compare = 2 to 6` so both are drawn, and the explanation now reasons about the rods rather than restating the answer. |
| "How many loose ones are here?" was explained with "Five rods and three cubes make 53", which answers a different question. | test, Q4 | Explanation now explains the 3. |
| "These two have the same rods, so the loose ones decide." is a statement; the step never asked anything. | `g12-arithmetic-tens`, step 6 | Now asks which is larger, keeping the hint. |

**2. `arithmetic-multiplication`, reviewed 2026-08-26.** Two defects, both fixed. The cleanest
unit of the four so far.

| What | Where | Fix |
|---|---|---|
| A word problem led with `38 / 5 = ?`, whose value is 7.6, while only the 7 whole crates were accepted. A learner who answers the equation on screen is marked wrong. | `arithmetic-multiplication-division`, step 6 | Formula dropped so the word problem leads on its own. This is why the unit now reads 6/7 rather than 7/7. |
| A division answer was explained as "Nine rows of eight" over an array drawn as eight rows of nine. | test, Q4 | Explanation reads the array the way it is drawn. |

**3. `arithmetic-fractions`, reviewed 2026-08-26.** One defect, fixed. All 15 answers otherwise
correct.

| What | Where | Fix |
|---|---|---|
| In a lesson called Naming the parts, a step asked `8 - 3 = ?` over a fraction bar and wanted the answer 5. It is a whole-number subtraction sitting in a fractions lesson, and it invites exactly the confusion between the two numbers that the lesson exists to prevent. | `g35-arithmetic-fractions`, step 5 | Now asks how many eighths are left, leading with `8/8 - 3/8 = ?/8`. The answer stays 5, so it is still typeable on the number pad. |

**4. `arithmetic-decimals`, reviewed 2026-08-26.** One defect, fixed. All 15 answers otherwise
correct.

| What | Where | Fix |
|---|---|---|
| "35 hundredths are already there, and 4 tenths finish the square off" over `0.75 - 0.4 = 0.35`. Those add to 0.75, not to a full square, so the prose contradicted its own formula. | `arithmetic-decimals-add`, step 4 | Prose now says the tenths bring it back up to 0.75. |

Two smaller things looked at and deliberately left alone: `arithmetic-decimals` test Q6 draws
only the minuend of `0.7 - 0.35`, and its "smallest of these" step draws two of the three
numbers it lists, because `DecimalGrid` holds at most two squares.

---

## 5. Geometry (ships)

Reworked from four grade slices into twelve sub-topics, band by band. Every one is three
lessons and a six-question test, and every one is held strictly by
`questionFiguresDoNotCaptionTheirAnswer`.

| # | Unit id | Title | Level | Status |
|---|---|---|---|---|
| 1 | `geometry-flat-shapes` | Flat shapes | g12 | **`ready`** * |
| 2 | `geometry-solid-shapes` | Solid shapes | g12 | **`ready`** * |
| 3 | `geometry-angles` | Angles and turns | g35 | **`ready`** * |
| 4 | `geometry-quadrilaterals` | Triangles and quadrilaterals | g35 | **`ready`** * |
| 5 | `geometry-symmetry` | Symmetry | g35 | **`ready`** * |
| 6 | `geometry-perimeter-and-area` | Perimeter and area | g35 | **`ready`** * |
| 7 | `geometry-pythagoras` | Pythagoras' theorem | g68 | **`ready`** * |
| 8 | `geometry-circles` | Circles | g68 | **`ready`** * |
| 9 | `geometry-volume` | Volume and surface area | g68 | **`ready`** * |
| 10 | `geometry-similarity` | Similarity and scale | g910 | **`ready`** * |
| 11 | `geometry-transformations` | Transformations | g910 | **`ready`** * |
| 12 | `geometry-circle-theorems` | Circle theorems | g910 | **`ready`** * |

\* Items 1 to 8 pass. Item 9, the render check, is outstanding for the whole section.

**The ratchet is gone.** `RATCHET` went 50 (before parking) to 15, to 14 as g12 landed, to 7
when the `RightTriangle` predicate was corrected, to 6 after g35, to 0 after g68. With
nothing left to spare, `questionFiguresDoNotCaptionTheirAnswer` now holds the whole catalog
strictly and the `reworkedUnits` escape hatch has been deleted with it.

### Figure limits found while authoring

Worth knowing before writing another lesson, and all three cost a rewrite when hit:

* **`RightTriangle` has no `reveal`.** It hides its answer through `labels = false` (no
  numbers at all) or `unknown = Side.X` (a question mark in place of the side being asked
  for). `canCaptionItsResult()` now matches that instead of blanket-counting every triangle.
  Its `angle` label is not covered by `unknown`, so do not ask for a labelled angle over one.
* **`AngleFigure` coerces its angle into 1..179.** It cannot draw a straight or a reflex
  angle. Teach reflex through the partner that is left of a full turn, which it can show.
* **`CircleFigure` has no sector.** Its `centreAngle` draws a centre angle and the angle at
  the circumference on the same arc, which is circle-theorem material. There is no way to
  draw "a quarter of a circle" as a shaded slice.

## 6. Working order

1. Park (section 2), leaving master green with two topics.
2. Move perimeter and area into Geometry and confirm the target ladder.
3. ~~Author Geometry~~ done, band by band, 4 slices into 12 sub-topics.
4. ~~Drop the ratchet~~ done; the whole catalog is held strictly.
5. ~~Arithmetic readiness pass~~ done, and the two compound units split, 6 into 10.
6. **Render check (item 9), outstanding.** 22 sub-topics, 66 lessons, 396 steps and 132 test
   questions have been read and tested but not seen on a screen. Walk the section in the
   running app on a narrow window, or render it through Paparazzi. This is the one thing
   between here and a release candidate.
7. Localize last: no new UI strings were added, so expect a no-op. Verify with a diff of
   `values/strings.xml` against the parking commit.

---

## 7. Open decisions

| # | Question | Why it matters | State |
|---|---|---|---|
| 1 | Do Arithmetic units 5 and 6 split into six the way Geometry is splitting? | Consistency of the readiness bar, and it rebalances 12 vs 6. | **done 2026-08-26**: split. Arithmetic is 10 units. |
| 2 | Is the 12-unit Geometry ladder right, or should some units merge? | Sets the whole authoring workload. | **done 2026-08-26**: 12 units, authored. |
| 3 | Drop "Halves and quarters", or keep it as a g12 Geometry unit? | Overlaps Arithmetic fractions. | **done 2026-08-26**: drop it. |
| 4 | Does the section ship with `GRADES_11_12` unreachable? | Two dead strings across 45 files, and its subtitle still mentions calculus, which is parked. Both shipped ladders stop at g910. | **open** |

---

## 8. Session log

| Date | What happened |
|---|---|
| 2026-08-26 | Scope set: ship Arithmetic + Geometry, park six. Parking method, perimeter/area move and Geometry split decided. This document created. |
| 2026-08-26 | Geometry reworked, 4 grade slices into 12 sub-topics, one band per commit. g12 into Flat and Solid shapes; g35 into Angles, Triangles and quadrilaterals, and Symmetry; g68 into Pythagoras, Circles, and Volume and surface area; g910 into Similarity, Transformations and Circle theorems. Corrected `canCaptionItsResult` for `RightTriangle`, which had been counting seven unlabelled triangles that gave nothing away. RATCHET reached 0 and was deleted along with the `reworkedUnits` hatch. Section total: 22 sub-topics, 66 lessons. Only the render check remains. |
| 2026-08-26 | Arithmetic split 6 -> 10: `ratio-and-percent` became Negative numbers, Ratio and proportion and Percentages; `standard-form-and-surds` became Standard form, Surds and Rounding and bounds. Twelve new lessons and six new tests written; original lesson ids kept. All 10 sub-topics now `ready` bar the batched render check. Found that `RightTriangle` cannot set `reveal = false`, which blocks the Pythagoras rework (see section 5). |
| 2026-08-26 | Readiness pass: `arithmetic-multiplication`, `arithmetic-fractions` and `arithmetic-decimals` reviewed and marked ready, four more defects fixed. 4 of 6 Arithmetic sub-topics done; units 5 and 6 are blocked on open decision 1. |
| 2026-08-26 | Readiness pass started. `arithmetic-counting` reviewed and marked ready: four defects fixed (see section 4). Corrected a wrong lead in this document: low Tinted / Formula-led counts are a property of the figures a unit uses, not a sign of unfinished work. |
| 2026-08-26 | Restructure committed as `b2a56c22`; `learn-parked` branched there. Parking executed: 6 content files, 6 `MathTopic` entries, 6 tile sketches and 540 locale strings removed; perimeter and area moved into Geometry as `geometry-perimeter-and-area`; previews and tests repointed at shipped topics; `RATCHET` 50 -> 15 and verified tight at exactly 15. `desktopTest` green apart from the by-design `NurikabeMeasureTest.measure`. Geometry rework not started. |
