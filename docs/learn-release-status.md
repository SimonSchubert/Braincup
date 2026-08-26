# Learn Math: first release status

Working document for the Learn Math section's first release. It survives between Claude
sessions: read it before touching anything under `learn/`, and update it as work lands.

Last updated: 2026-08-26 (parked; both topics authored; clarity- and correctness-passed; render check outstanding)

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
   explanations actually explain (see the house style below), spelling consistent with the rest
   of the section.
9. **Seen rendering.** Opened once in the running app or a Paparazzi render, on a narrow
   screen, not just read in the source.

Status values used in the tables: `ready` / `review` (reworked, readiness pass not done) /
`rework` (still grade-slice content) / `todo` (does not exist yet).

### House style for an `explanation` (item 8)

An answer explanation says **why**, never **what**. Both places it renders already show the
answer, so a line that restates it teaches nothing:

* In a lesson it appears **only after a correct answer** (`LearnLessonScreen.kt`: `FeedbackCard`
  on `Correct`, a generic `RetryNote` on a miss), sitting under the solved formula in green.
* In a test it appears on the `ReviewCard`, under "your answer" and "correct answer" - and that
  card **draws no figure**. So an explanation that only describes the picture ("Eight rows of
  nine.", "45 squares out of a hundred.") is read with nothing to look at.

Rules:

1. One sentence, naming the rule or the step that reaches the answer. Target 45-90 characters
   when rewriting.
2. Never describe only the figure. A test explanation has to stand on its own words.
3. Close a tempting distractor in the same sentence rather than adding a second one:
   "2 parts out of the 9 in the bar, not out of the 7 the ratio names."
4. **Length is not the test.** "Every jump adds ten, so 50 + 10 = 60." is 37 characters and is a
   good explanation. Rewrite what restates; leave short reasoning alone.
5. **No `{a:}` / `{b:}` in a quiz explanation.** Tinting matches a number to the colour the
   figure draws it in, and the review card has no figure. Enforced by
   `LearnCatalogTest.everyStepAndQuestionExplains`.

A unit `summary` may list what its three lessons cover, but should not open by repeating lesson
1's title: the two lines sit directly above each other on `LearnUnitScreen`.

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

**5. Clarity pass over all ten units, 2026-08-26.** Item 8 re-read across every unit against the
house style in section 3. The teaching prose came out clean; the thinness was all in the answer
explanations, and worst in the tests.

| Surface | n | median chars, before -> after | under 45 chars, before -> after |
|---|---|---|---|
| `Concept.body` | 79 | 105 -> 106 | 0 -> 0 |
| lesson `explanation` | 89 | 57 -> 65 | 29 -> 12 |
| quiz `explanation` | 60 | **44 -> 60** | **32 -> 9** |

61 explanations rewritten, every one landing inside the 45-90 band. The 21 short ones left alone
all reason rather than restate ("Every jump adds ten, so 50 + 10 = 60."), which is why no
minimum-length assertion was added. Also in this pass:

| What | Where | Fix |
|---|---|---|
| The quiz screen rendered the same notation two ways: option tiles went through `MathText`, but the prompt, the review prompt and the review explanation used a plain `Text`. A learner saw `72 / 9 = ?` directly above an option tile reading `4.5 × 10^4`. 23 authored Arithmetic quiz strings affected, and Geometry's too. | `LearnQuizScreen.kt` question prompt, `ReviewCard` prompt and explanation | All three now `MathText(fractionSlash = true)`, matching the option tiles at `:162`. |
| "Surds collect the way like terms do in algebra." Algebra is parked, so a v1 learner has no route to "like terms". | `arithmetic-surds-arithmetic`, step 1 | Now "Matching roots collect the way matching units do", with a metres example. |
| The longest body in the topic packed four ideas into one step and carried a comma splice at "7.07 is a rounding of that length, the root sign is the length itself". | `arithmetic-surds`, step 2 | Trimmed to one idea; the next step already carries the exact-versus-rounded point. |
| "A nought in the middle of a number counts... which is the whole difference between 0.00308 and 308 000." Both have 3 s.f., so the contrast it invites is empty, and trailing noughts are a separate case. | `arithmetic-bounds-significant`, step 5 | Now contrasts a middle nought with a leading one, which is the distinction the step's own formula names. |
| Five unit summaries opened by repeating lesson 1's title, and those two lines sit directly above each other on `LearnUnitScreen`. | `fractions`, `decimals`, `negatives`, `surds`, `bounds` | Reworded. The three that echo a *later* lesson read as a list of the three and were left alone. |

New guard: `LearnCatalogTest.everyStepAndQuestionExplains` - every explanation non-blank, and no
quiz explanation carries `{a:}` / `{b:}` markup, because `ReviewCard` draws no figure for a tint
to refer to.

Checked and found not to be a problem: neither `unit.summary` (`LearnUnitScreen.kt:109-113`) nor
`lesson.summary` (`LessonRow`) is clamped with `maxLines`, so no summary can be ellipsised. The
`maxLines = 2` in `LearnComponents.kt` is on the sub-topic row's *title*, and that row shows no
summary at all.

**Render check, partly done.** Paparazzi renders of `arithmetic-fractions`, `-surds` and
`-bounds` sub-topic screens, the `-multiplication` and `-standard-form` test screens, and an
`arithmetic-surds` lesson step confirmed: summaries wrap fully and are never ellipsised, the
lesson-1 stutter is gone, and option tiles render `4.5 × 10^4`. **Still unseen:** the
`FeedbackCard` and the `ReviewCard`, which is where 61 of the 66 edits land - both need a
learner to answer something first, so Paparazzi cannot reach them. Walk one lesson and one full
test in the running app to close item 9. Note `10^4` renders with a literal caret in both
lessons and tests: `formatMathSymbols` has no superscript rule, which is pre-existing.

**Not done here:** Geometry's 12 sub-topics have not had this pass. They were authored to the
other eight readiness points but never against the explanation house style, and they inherit the
same figure-less `ReviewCard`.

---

**6. Correctness audit of both topics, 2026-08-26.** Every one of the 528 steps was extracted and
every one of the **327 answerable items** checked by hand, with the plain arithmetic auto-verified
as well.

**The maths came out clean: no wrong answers, no arithmetic slips, no false statements.** The
awkward cases are all right - trapezium is used in the exclusive UK sense throughout, "exactly two
equal sides" sidesteps the isosceles/equilateral overlap, `(x, y) -> (-y, x)` is the correct
quarter-turn anticlockwise, area and volume scale by k² and k³, and the cyclic-quadrilateral
question at `2190` picks the right opposite pair.

Every defect found was a **figure contradicting correct words**, or a term used before it existed:

| What | Where | Fix |
|---|---|---|
| "A triangle has angles of 40 and 75 degrees" was drawn with `RightTriangle`, which stamps a square right-angle marker unconditionally (`ShapeVisuals.kt`). The picture asserts a right angle the question rules out, and reading it gives 50. | `geometry-angles-adding` | New `Triangle(TriKind.SCALENE)`. |
| Cyclic quadrilaterals were never drawn. The Concept steps used `Polygon(sides = 4)`, which is **regular**, so they showed a square; all three question steps used `AngleFigure(supplement = true)` - two angles on a straight line, no quadrilateral and no circle. The lesson's own definition, "all four corners sitting on the circle", was never shown. | `geometry-circle-theorems-cyclic`, and the test | New `CyclicQuad`. |
| Rhombus, parallelogram, trapezium and kite were all drawn as squares or rectangles, for the same `Polygon`-is-regular reason. A rhombus question showed a square. | `geometry-quadrilaterals-*` | New `Quadrilateral` + `QuadKind`. |
| "A triangle has exactly two equal sides. What is it called?" showed an equilateral triangle. | `geometry-quadrilaterals-triangles` | `Triangle(TriKind.ISOSCELES)`. |
| "Octagon" was the correct answer to "which name belongs to this shape", but the naming Concept taught only triangle, quadrilateral, pentagon and hexagon. At g12 it was reachable only by elimination. | `g12-geometry-flat-shapes` | The Concept now names the octagon too. |
| Two area explanations read the grid the wrong way round: "6 rows of 3" over `AreaGrid(cols = 6, rows = 3)`, which draws 3 rows of 6. Same class as the multiplication defect in note 2. | `g35-measurement-area`, and the test | "3 rows of 6." / "3 rows of 7." |
| Place-value vocabulary two lessons early: "takes one off the loose ones, not off the rods" sat in lesson 1 over a `NumberLine`, and rods arrive in lesson 3. | `g12-arithmetic-counting` | Reworded to the number line the step actually draws. |
| The only inexact pi answer in the circles unit: 3.14 x 70 = 219.8 offered as "220 cm", where the other eight are exact. | `geometry-circles` | Diameter 50 cm, so 157 cm exactly. |
| Two right triangles stated 90/35/55 but were drawn `a = 5, b = 3` and `a = 4, b = 3`, which render 90/31/59 and 90/37/53. | `angles` test, `geometry-circle-theorems-tangents` | `a = 7, b = 5` gives 90/35.5/54.5. |
| Every `Plot` in the transformations unit carried a decorative `Curve.Linear(m = 0.5f)`. On "reflect this point in the y-axis" a stray line through the origin invites reflecting in *that* line. | `geometry-transformations-*`, 14 figures | `Plot.curve` is now optional; those figures draw bare axes. |

Three figure variants were added and rendered before use: `Triangle`/`TriKind`,
`Quadrilateral`/`QuadKind` (equal sides ticked, parallel sides chevroned, the two marks offset so
they do not smudge together) and `CyclicQuad`, whose corners sit at deliberately uneven gaps so
the opposite-angle rule cannot be read as a fact about rectangles.

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
* **`Polygon` only builds *regular* shapes.** `Polygon(sides = 4)` is a square and
  `Polygon(sides = 3)` is equilateral, so it is the wrong figure for a rhombus, a
  parallelogram, a trapezium, a kite, an isosceles or a scalene triangle. Use
  `Quadrilateral(QuadKind.X)` or `Triangle(TriKind.X)` for those; `Polygon` is for questions
  that are only about counting sides and corners.
* **`RightTriangle` always stamps its right-angle marker**, whatever `labels` says. Never use
  it for a triangle whose given angles are not a right angle plus two others.
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
| 2026-08-26 | Every certificate wired to Play Games and Game Center: 22 store-only achievements, one per sub-topic, ids derived from the unit id in the new `learn/LearnStoreAchievements.kt` and guarded by `LearnStoreAchievementsTest`. No in-app `Achievements` entries and no new strings, so nothing to localize. `UserStorage.restoreLearnCertificates` means a certificate now survives a reinstall, which it never did before. Icons 49-70 generated in gold. All 44 created on 2026-08-27 via the new `scripts/store_achievements.rb`, which drives both store APIs directly (fastlane has no achievement support at all). Game Center is complete, icons included, at 10 points each and 600/1000 used; the cap turned out to be a non-issue. Play Games has all 22 with their ids written back into `play_games.xml`, at 5 XP, the minimum Play accepts. The Play Games icons had to be attached by hand in Play Console, because the Games Configuration API has no image upload method any more; done 2026-08-27, and verified by reading the config back: all 22 have a published icon, each a pixel-exact match for its `media/achievements/png/` source. Nothing outstanding on either store. Full detail and copy in `media/achievements/README.md`. |
| 2026-08-26 | Scope set: ship Arithmetic + Geometry, park six. Parking method, perimeter/area move and Geometry split decided. This document created. |
| 2026-08-26 | Correctness audit of all 528 steps across both topics. The maths is clean: 327 answerable items checked, no wrong answers or false statements. Ten defect classes fixed, every one a figure contradicting correct words - most seriously a 40/75/65 triangle drawn with a right-angle marker, and a cyclic-quadrilateral lesson that never once drew a quadrilateral in a circle. Added `Triangle`, `Quadrilateral` and `CyclicQuad` figure variants, since `Polygon` only builds regular shapes and so drew a square for every rhombus and a square for every cyclic quadrilateral. `Plot.curve` made optional to clear the stray line off the transformations figures. |
| 2026-08-26 | Arithmetic clarity pass, all ten units: 61 answer explanations rewritten from restatement to reason (quiz median 44 -> 60 chars, sub-45 count 32 -> 9). `LearnQuizScreen`'s question prompt and both `ReviewCard` lines switched to `MathText`, so `×` and `÷` finally match the lessons. Three content defects fixed: a parked-Algebra reference in surds, an overlong four-idea body, and a significant-figures contrast that was empty. Five stuttering unit summaries reworded. House style for readiness item 8 written into section 3 and guarded by a new `everyStepAndQuestionExplains` test. Geometry has not had this pass. |
| 2026-08-26 | Geometry reworked, 4 grade slices into 12 sub-topics, one band per commit. g12 into Flat and Solid shapes; g35 into Angles, Triangles and quadrilaterals, and Symmetry; g68 into Pythagoras, Circles, and Volume and surface area; g910 into Similarity, Transformations and Circle theorems. Corrected `canCaptionItsResult` for `RightTriangle`, which had been counting seven unlabelled triangles that gave nothing away. RATCHET reached 0 and was deleted along with the `reworkedUnits` hatch. Section total: 22 sub-topics, 66 lessons. Only the render check remains. |
| 2026-08-26 | Arithmetic split 6 -> 10: `ratio-and-percent` became Negative numbers, Ratio and proportion and Percentages; `standard-form-and-surds` became Standard form, Surds and Rounding and bounds. Twelve new lessons and six new tests written; original lesson ids kept. All 10 sub-topics now `ready` bar the batched render check. Found that `RightTriangle` cannot set `reveal = false`, which blocks the Pythagoras rework (see section 5). |
| 2026-08-26 | Readiness pass: `arithmetic-multiplication`, `arithmetic-fractions` and `arithmetic-decimals` reviewed and marked ready, four more defects fixed. 4 of 6 Arithmetic sub-topics done; units 5 and 6 are blocked on open decision 1. |
| 2026-08-26 | Readiness pass started. `arithmetic-counting` reviewed and marked ready: four defects fixed (see section 4). Corrected a wrong lead in this document: low Tinted / Formula-led counts are a property of the figures a unit uses, not a sign of unfinished work. |
| 2026-08-26 | Restructure committed as `b2a56c22`; `learn-parked` branched there. Parking executed: 6 content files, 6 `MathTopic` entries, 6 tile sketches and 540 locale strings removed; perimeter and area moved into Geometry as `geometry-perimeter-and-area`; previews and tests repointed at shipped topics; `RATCHET` 50 -> 15 and verified tight at exactly 15. `desktopTest` green apart from the by-design `NurikabeMeasureTest.measure`. Geometry rework not started. |
