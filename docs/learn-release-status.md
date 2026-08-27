# Learn Math: first release status

Working document for the Learn Math section's first release. It survives between Claude
sessions: read it before touching anything under `learn/`, and update it as work lands.

Last updated: 2026-08-27 (render check closed out; see section 7)

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

## 7. Render check (item 9)

Run `./gradlew :screenshotTests:renderLearnScreens`. It records the whole section through
`screenshotTests/src/test/kotlin/.../screenshots/learn/` and lays ~1570 frames out under
`screenshotTests/build/learn-render/`, one folder per sub-topic, plus `index.html` to page
through them. `-PlearnOnly='LearnUnitRenderTest.*[geometry-circles]'` re-renders one sub-topic;
`layoutLearnScreens` re-files without re-rendering.

Three defaulted seams in `composeApp` make the answered states reachable, which is what closes
the gap this document recorded as unreachable: `LessonScreenState` (`LearnLessonScreen.kt`),
`QuizScreenState` (`LearnQuizScreen.kt`) and `LearnVisualCanvas`'s `inspectionPhase`. The app
always passes the defaults; only previews and this harness pass anything else. `FeedbackCard`,
`RetryNote`, the lesson result, the test result and the unfolded `ReviewCard` list are all
rendered now, as is the second phase of a two-phase figure.

`LearnFigureRenderTest` additionally renders every distinct figure in the catalog on its own
panel, grouped by variant, which is what makes a whole-family comparison practical.

### Defects found

| What | Where | Fix |
|---|---|---|
| Axis labels ran into each other: "14" touching "15", "-20" touching "-18", "-5" touching "-4". The fit test that decides whether every tick can be numbered measured the *minor* label style, but a called-out value is drawn at `AccentLabelFactor` and bold, so it is the widest label on the line. `tickStep > 1` skipped the test entirely, because it makes every tick a major. | `drawNumberLine`, 7 figures across `arithmetic-counting`, `-negatives` and `-bounds` | Fit test now measures the widest style the line can draw, and a plain label that would touch a called-out one is dropped rather than overprinted. Verified against all 38 `NumberLine` figures. |

A second defect, and the decision taken on it:

| What | Where | Fix |
|---|---|---|
| A test prompt, its options and its review line all rendered in the number face, because the clarity pass routed all three through `MathText` so notation would match the option tiles. The side effect was that pure prose - "What comes next?", "Which number is smaller, 62 or 26?", "They are equal" - lost the display face, so the same wording read one way in a lesson and another in a test. | `LearnQuizScreen.kt` prompt, both `ReviewCard` lines, and `LearnOptionTile` | New `String.readsAsNotation()` (`MathText.kt`) - true when the string carries an operator, or contains no word at all - drives a `LearnText` that picks the face per string. `9 + 6 = ?`, `41`, `1/5` and `4.5 x 10^4` stay in Rubik; `What comes next?` and `They are equal` take the display face. Every non-Learn `MathText` call site is pure notation, so nothing outside the section moves. |

A third defect, found by eye in the index rather than by any scan:

| What | Where | Fix |
|---|---|---|
| `RightTriangle(showSquares = true)` drew the whole figure at 40% of the size it could be - a 144px island in a 410px canvas - with the side labels printed on top of the square labels, so "12" and "144" overlapped and "9" and "81" overlapped. The sizing reserved room for a square on *both* legs along *each* axis, `(a + a)` across and `(b + b)` down. The squares actually sit on the far side of one leg each, so the figure spans `(a + b)` in both directions. | `drawRightTriangle`, worst on `geometry-pythagoras-using` step 2 (`a = 12, b = 9`); 7 figures use `showSquares` | Extent corrected to `(a + b)`, the whole span centred rather than the triangle alone, and the side labels moved inside the triangle - the one part of the figure that is always empty - instead of into the middle of a square that carries its own number. 2.5x larger, and every label legible. |

### The colour code

Decided 2026-08-27, after the render check showed the section had no colour code at all: the
figure painted the given, the working and the answer in one identical orange, while the formula
beside it turned the answer green. Two colours were each carrying two meanings - orange was the
brand *and* every called-out value, green was "the second group in a figure" *and* "correct".

Three roles now, and nothing else:

| Role | Colour | Where it appears |
|---|---|---|
| **Given** - what the question hands you | `Primary` `#ED7354` | the start value on a figure, a formula's first operand, `{a:}` in prose |
| **Structure** - the scaffolding | `onSurfaceVariant` | a formula's operators and `=`, ordinary axis labels |
| **Working** - the step you take | `WorkingBlue` `#4478C2` | hop arcs and their labels, the second group in a two-accent figure, `{b:}` in prose and in a formula's second operand |
| **Answer** - and nothing else | `SuccessGreen` `#5C8E58` | the value a figure marks once it is right, the resolved `{c:}` in a solved formula, the correct option tile |

The working is the *movement*, not the places it pauses. A value a hop touches down on - the 10 in
`15 - 9`, reached by hopping back 5 - stays an ordinary axis label. Colouring it too turned a
three-colour code back into a scatter of highlights, so it was tried and reverted.

**Operators are structure, not content.** While `-` and `=` were printed in the same orange as an
untagged number, orange read as "the colour a formula card is printed in" rather than "the number
the question handed you". `withFormulaColors` (MathText.kt) now colours a formula per token: values
take their role, operators take the muted tone. A minus is part of the number when it signs one and
structure when it subtracts, which is the distinction `spaceSubtraction` already draws - after
`formatMathSymbols` a subtracting minus always has space around it and a sign never does. So
"15 - 9 = 6" reads orange, grey, blue, grey, green, and every colour on the card means something.

**A test question is a formula too.** `LearnText(roleColors = true)` renders a quiz prompt and the
review card's prompt through the same per-token colouring a lesson's formula card uses, and 13
binary prompts were tagged the same way, so "9 + 6 = ?" reads orange-blue over a ten-frame drawn
orange-blue. Answer options deliberately stay out of it: an option is a choice, not a given, and it
already says what it is by turning green. The review card's "correct answer" line is now green
whether or not the learner got it - green means "the answer" everywhere, and printing it in the
card's own ink after a miss made the one line they most need the quietest thing on the card.

**A formula's second operand is the working.** In "15 - 9 = 6" the 15 is what the question hands
you, the 9 is what you do to it - the same quantity the -4 and -5 hops add up to - and the 6 is the
answer. 48 formulas and 13 quiz prompts were tagged `{b:}` on their second operand for this; the tagger
required whitespace around the operator, which is the same tight-versus-spaced rule
`formatMathSymbols` uses to tell a fraction from a division, so `1/2 = 4/8` was left alone while
`28 / 7 = 4` was not. 105 formulas that are not a plain `given op working = answer` - sequences,
chains like `15 - 8 = 15 - 5 - 3`, `17 / 5 = 3 r 2` - were left untouched.

`{c:}` is new and is never authored: it is substituted at render time when a question resolves, so
"15 - 9 = ?" finishes as "15 - 9 = **6**" in the same green the option tile turns and the same
green the number line marks the 6 in. One number, one colour, three places.

Blue rather than a second warm tone: green had to be freed for correctness, and orange against
blue is the one accent pair that survives every kind of colour blindness, which matters in a
section that carries meaning in colour and already ships an accessible palette. The tone is picked
for its worst case - 4.09:1 on the light figure panel, 3.74:1 on warm dark, 4.71:1 on OLED - which
is a better floor than `SuccessGreen` (3.52) or `Primary` (2.68, on that same light panel).

Worth knowing: `Primary` on the light figure panel is **2.68:1**, under the 3.0 floor for
graphics. That is pre-existing and unrelated to this change, but it is the weakest colour in the
section and it is the one carrying the givens.

### One box per idea

A lesson step is a stack of cards - formula, figure, working line, feedback - and the test was
printing its equation as loose text under the figure, so the same question read as a caption in
one place and as the question in the other. `LearnFormulaCard` moved out of `LearnLessonScreen`
into the shared components and both screens now use it: notation goes in a card, prose stays plain
underneath, in a lesson and in a test alike.

Two readouts that floated between cards are now `LearnAnswerCard`, and one of them stopped being
drawn at all. A `Numeric` step whose formula ends in `= ?` already finishes in front of the learner
in the answer green, so the "your answer" line under it was the same number twice; it now shows
only when the question had nowhere to resolve, which is the rule `Worked` steps already followed.

Left deliberately unboxed: the prose question under a formula (it is a supporting line, not a
second question), and the `ReviewCard`'s prompt, which is already inside a card of its own.

### The code inside a figure

A figure's own annotations carry the same roles as the formula beside it, so the two read as one
thing:

* `ArrayDots` labels its rows in the given and its columns in the working - "4 rows" orange and
  "7 in each" blue against `4 x 7` on the card. A **split** array is a different figure: there the
  two row bands carry the two colours and the column count runs through both, so it stays chrome.
* `Steps` keeps the plain accent rather than the working blue. A sequence has no given to step
  from - every term is the same kind of thing - and `hopArc` is shared with `drawNumberLine`, so
  colouring number-line hops blue had silently flipped all 48 `Steps` figures with it, while their
  own formulas stayed orange. `hopArc` now takes a colour.
* `AngleFigure`'s supplement pair draws both arcs on **one** radius, so together they read as the
  single half turn the caption adds up to; two radii made them look like two unrelated angles that
  happened to share an arm. The arm is drawn *after* the arcs, because it is the same colour as the
  first of them and the arc end was blending into it rather than stopping against it. Its caption
  uses the new `VisualScope.labelRuns`, so "130 + 50 = 180" prints each number in the colour of the
  arc it counts.
* Two-accent figures - `TenFrame`, `Fraction`, `RatioBar`, a split `ArrayDots` - use orange and
  blue for "the two quantities", which is the same pair meaning "given and working" one level up.
  Blue is a clear gain here over the green these used to draw: an equivalence pair like 1/2 over
  4/8 no longer looks like one of the two is the correct one.

### Test figures that drew their own answer

`questionFiguresDoNotCaptionTheirAnswer` checks that a figure does not *label* the answer. It
cannot see one that simply *draws* it. Scanning all 132 test questions for a figure whose own
numbers come to the accepted answer turned up nine leaks, and the scan itself is now a test -
`testFiguresDoNotDrawTheirOwnAnswer` - so the class cannot come back.

The guard reads each figure for what a learner can take off it without answering anything: the
total a ten-frame or an array counts out, the tick a hop lands on, the cell count of an area grid,
the terms of a ladder, and the row count an array *prints in words*. A figure that draws several
of the options is showing the field rather than the answer - which is exactly what a "which of
these is largest" question needs - so only a figure that comes to the correct option and to none
of the others is a leak. Two questions are exempt by name, because counting the array is the
method they intend: "How many dots are here?" and "A tray holds 8 rows of 6 buns".

Lessons are deliberately out of scope. A lesson step's figure sits beside prose teaching the
method, and drawing the hops there *is* the teaching; a test is the one place the learner supplies
the whole of the answer.

| Unit | Question | Drew | Now |
|---|---|---|---|
| `arithmetic-counting` | `9 + 6 = ?` | `TenFrame(9, added = 6)` - ten dots and five, already regrouped | `TenFrame(9, added = 0)`: the nine it is handed, one gap left in the frame |
| `arithmetic-counting` | `14 - 6 = ?` | `NumberLine(start = 14, hopSteps = [-4, -2])` - hops land on 8 | same line, `start = 14` and no hops: where the count starts |
| `arithmetic-multiplication` | `6 x 9 = ?` | `ArrayDots(6 x 9, split = 5)` - 54 countable dots, captioned "5 rows" and "1 more" | `Steps(0, 9, 18, 27)`: the nines set off, three of the six |
| `arithmetic-multiplication` | `72 / 9 = ?` | `ArrayDots(8 x 9)` - the figure prints "8 rows", and 8 is the answer | `Steps(18, 27, 36, 45)`: the nines climbing, stopping short of 72 |
| `arithmetic-multiplication` | `23 / 4 = ?` | `ArrayDots(5 x 4, leftover = 3)` - "5 rows" in words with the remainder beside it, which is "5 r 3" written out | `Steps(0, 4, 8, 12)`: the fours climbing, stopping short of 23 |
| `arithmetic-negatives` | `-6 + 9 = ?` | `NumberLine(start = -6, hopSteps = [6, 3])` - hops land on 3 | `start = -6`, no hops |
| `arithmetic-negatives` | `-3 - 8 = ?` | `NumberLine(start = -3, hopSteps = [-8])` - hops land on -11 | `start = -3`, no hops |
| `arithmetic-negatives` | `7 - (-5) = ?` | `NumberLine(start = 7, hopSteps = [5])` - hops land on 12 | `start = 7`, no hops |
| `geometry-similarity` | scale factor 3, area factor? | `AreaGrid(3 x 3)` - nine cells to count under sides labelled with the factor itself: the k² rule, worked | `AreaGrid(4 x 2)`: what an area is made of, at a size no option can be read off |

**Two of the nine were not in the original scan**, and are worse than countable: `72 / 9` and
`23 / 4` were drawn as arrays, and an array prints its own row count in words - which for a
division *is* the answer. The first scan compared the figure's product against the answer, so it
saw 72 and 23 and let both through. The guard now reads the row count too.

### A question figure that drew nothing

`NumberLine(from = -10, to = 5, reveal = false)` had no `start`, no `jump` and no `hopSteps`, so it
rendered a bare axis labelled -10, -5, 0 and 5. The step asks which of -10, -6, -1 and 0 is the
largest; the figure marked none of them, and -6 and -1 were not even numbered.
`everyStepAndQuestionHasAVisual` passed because the visual is non-null.

`LearnVisual.NumberLine` now takes a `compare` list: the values a question is choosing between,
each given a tall tick, a dot on the axis and a number in the called-out size, whatever the fit
test decides about the rest of the line. They take the **ordinary ink**, not a role colour - a
candidate is not a given and not yet an answer, and the one that turns out to be right is the
option tile, not the axis. The same bare figure appeared twice, in `g68-arithmetic-negatives`
step 3 and in the `arithmetic-negatives` test; both now list their four candidates.

### Subtraction hops

Decided 2026-08-27: **an arrowhead on every hop**, at the end it lands on. Drawn without one, a hop
back is the same upward curve in the same place as a hop forward, and the minus sign on the label
was the only thing saying the count goes the other way - a lot to hang on one glyph in a figure
whose whole job is to show the movement. One change in `hopArc`, so all 38 `NumberLine` figures and
all 48 `Steps` figures take it.

One consequence, worth knowing before authoring: a `Steps` ladder lays its terms out in list order
at even spacing, not by value, so a **descending** ladder now draws right-pointing arrows over
falling numbers. Nineteen of the catalog's twenty-two ladders already ascend; write them that way.
The two division tests reworked above were authored descending and flipped for exactly this.

Not fixed by this, and accepted: on the bridge-through-ten steps the arcs still read "-3" then "-5"
left to right, the reverse of the order the prose works them in. The animation plays them in order
and the arrowheads now say which way each one goes; numbering the hops on top of that is more
furniture than the figure can carry.

### Known and left alone

| What | Where | Note |
|---|---|---|
| `Primary` `#ED7354` measures **2.68:1** on the light figure panel, under the 3.0 floor for graphics, and it is the colour carrying every given. | app-wide, `Primary` | Decided 2026-08-27: **left as it is**. Pre-existing, shipped on every surface, and a brand colour is not something a render check gets to move. Recorded here so the next accessibility pass starts from a measurement rather than a hunch. |
| `PlaceValue` draws rods at `alpha = 0.4f` and loose ones at `0.55f`, so they render `#F2C1B5` / `#A2BD9F` against the `#ED7354` / `#5C8E58` that `TenFrame` and `NumberLine` use at full strength. A `{a:30}` tint therefore prints full-strength orange beside pale pink rods, which is the one thing the tinting exists not to do. | `NumberVisuals.kt`, `drawPlaceValue` | Deliberate, so the block outlines stay legible. Left alone unless the tint mismatch matters more. |

Reviewed so far: `arithmetic-counting` in full, every `NumberLine`, `Steps`, `ArrayDots`,
`TenFrame`, `AreaGrid` and `RightTriangle` figure in the catalog. Section 7 has nothing left
open bar the two rows above, both of which are decisions rather than defects.

---

## 8. Open decisions

| # | Question | Why it matters | State |
|---|---|---|---|
| 1 | Do Arithmetic units 5 and 6 split into six the way Geometry is splitting? | Consistency of the readiness bar, and it rebalances 12 vs 6. | **done 2026-08-26**: split. Arithmetic is 10 units. |
| 2 | Is the 12-unit Geometry ladder right, or should some units merge? | Sets the whole authoring workload. | **done 2026-08-26**: 12 units, authored. |
| 3 | Drop "Halves and quarters", or keep it as a g12 Geometry unit? | Overlaps Arithmetic fractions. | **done 2026-08-26**: drop it. |
| 4 | Does the section ship with `GRADES_11_12` unreachable? | Two dead strings across 45 files, and its subtitle still mentions calculus, which is parked. Both shipped ladders stop at g910. | **open** |

---

## 9. Session log

| Date | What happened |
|---|---|
| 2026-08-27 | Render check closed. Nine test figures were drawing their own answer, not the seven the first scan found: `72 / 9` and `23 / 4` were drawn as arrays, and an array prints its row count in words, which for a division is the answer. All nine reworked to pose the question and stop - a ten-frame that holds the nine it is handed, number lines that mark where the count starts instead of where it finishes, nines and fours ladders in place of countable arrays, an area grid at a size no option can be read off - and the scan is now `testFiguresDoNotDrawTheirOwnAnswer` so the class cannot come back. The bare number line that marked none of its four candidates got `NumberLine.compare`, which numbers and dots them in the ordinary ink; it was in a lesson step and in a test. Hops now end in an arrowhead, so a hop back is no longer the same picture as a hop forward - 38 number lines and 48 ladders. `Primary` at 2.68:1 on the light panel was raised and left alone: brand, not a render defect. |
| 2026-08-27 | Render check started. Paparazzi harness added for the whole section (~1570 frames, `renderLearnScreens`), with three defaulted seams in composeApp so the answered states a still frame could not reach - `FeedbackCard`, `RetryNote`, both results and the `ReviewCard` list - are rendered at last, along with the second phase of a two-phase figure. Three defects found and fixed: number-line axis labels overprinting each other (the fit test measured the narrowest label style and the line then drew the widest); every quiz prompt, option and review line set in the number face, so prose read one way in a lesson and another in a test; and `RightTriangle(showSquares = true)` drawn at 40% size with its labels stacked, because the sizing reserved room for two squares per axis instead of one. Also scanned all 132 test questions for figures a learner can simply count to the answer: seven of them. `arithmetic-counting` reviewed in full and every `NumberLine` figure in the catalog; four items left open in section 7. |
| 2026-08-26 | Every certificate wired to Play Games and Game Center: 22 store-only achievements, one per sub-topic, ids derived from the unit id in the new `learn/LearnStoreAchievements.kt` and guarded by `LearnStoreAchievementsTest`. No in-app `Achievements` entries and no new strings, so nothing to localize. `UserStorage.restoreLearnCertificates` means a certificate now survives a reinstall, which it never did before. Icons 49-70 generated in gold. All 44 created on 2026-08-27 via the new `scripts/store_achievements.rb`, which drives both store APIs directly (fastlane has no achievement support at all). Game Center is complete, icons included, at 10 points each and 600/1000 used; the cap turned out to be a non-issue. Play Games has all 22 with their ids written back into `play_games.xml`, at 5 XP, the minimum Play accepts. The Play Games icons had to be attached by hand in Play Console, because the Games Configuration API has no image upload method any more; done 2026-08-27, and verified by reading the config back: all 22 have a published icon, each a pixel-exact match for its `media/achievements/png/` source. Nothing outstanding on either store. Full detail and copy in `media/achievements/README.md`. |
| 2026-08-26 | Scope set: ship Arithmetic + Geometry, park six. Parking method, perimeter/area move and Geometry split decided. This document created. |
| 2026-08-26 | Correctness audit of all 528 steps across both topics. The maths is clean: 327 answerable items checked, no wrong answers or false statements. Ten defect classes fixed, every one a figure contradicting correct words - most seriously a 40/75/65 triangle drawn with a right-angle marker, and a cyclic-quadrilateral lesson that never once drew a quadrilateral in a circle. Added `Triangle`, `Quadrilateral` and `CyclicQuad` figure variants, since `Polygon` only builds regular shapes and so drew a square for every rhombus and a square for every cyclic quadrilateral. `Plot.curve` made optional to clear the stray line off the transformations figures. |
| 2026-08-26 | Arithmetic clarity pass, all ten units: 61 answer explanations rewritten from restatement to reason (quiz median 44 -> 60 chars, sub-45 count 32 -> 9). `LearnQuizScreen`'s question prompt and both `ReviewCard` lines switched to `MathText`, so `×` and `÷` finally match the lessons. Three content defects fixed: a parked-Algebra reference in surds, an overlong four-idea body, and a significant-figures contrast that was empty. Five stuttering unit summaries reworded. House style for readiness item 8 written into section 3 and guarded by a new `everyStepAndQuestionExplains` test. Geometry has not had this pass. |
| 2026-08-26 | Geometry reworked, 4 grade slices into 12 sub-topics, one band per commit. g12 into Flat and Solid shapes; g35 into Angles, Triangles and quadrilaterals, and Symmetry; g68 into Pythagoras, Circles, and Volume and surface area; g910 into Similarity, Transformations and Circle theorems. Corrected `canCaptionItsResult` for `RightTriangle`, which had been counting seven unlabelled triangles that gave nothing away. RATCHET reached 0 and was deleted along with the `reworkedUnits` hatch. Section total: 22 sub-topics, 66 lessons. Only the render check remains. |
| 2026-08-26 | Arithmetic split 6 -> 10: `ratio-and-percent` became Negative numbers, Ratio and proportion and Percentages; `standard-form-and-surds` became Standard form, Surds and Rounding and bounds. Twelve new lessons and six new tests written; original lesson ids kept. All 10 sub-topics now `ready` bar the batched render check. Found that `RightTriangle` cannot set `reveal = false`, which blocks the Pythagoras rework (see section 5). |
| 2026-08-26 | Readiness pass: `arithmetic-multiplication`, `arithmetic-fractions` and `arithmetic-decimals` reviewed and marked ready, four more defects fixed. 4 of 6 Arithmetic sub-topics done; units 5 and 6 are blocked on open decision 1. |
| 2026-08-26 | Readiness pass started. `arithmetic-counting` reviewed and marked ready: four defects fixed (see section 4). Corrected a wrong lead in this document: low Tinted / Formula-led counts are a property of the figures a unit uses, not a sign of unfinished work. |
| 2026-08-26 | Restructure committed as `b2a56c22`; `learn-parked` branched there. Parking executed: 6 content files, 6 `MathTopic` entries, 6 tile sketches and 540 locale strings removed; perimeter and area moved into Geometry as `geometry-perimeter-and-area`; previews and tests repointed at shipped topics; `RATCHET` 50 -> 15 and verified tight at exactly 15. `desktopTest` green apart from the by-design `NurikabeMeasureTest.measure`. Geometry rework not started. |
