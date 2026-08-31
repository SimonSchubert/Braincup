# Which games implement a real cognitive task

The instructions screen shows a research note for the games that implement a published
cognitive-psychology paradigm faithfully. `GameScience` holds the data; this is the audit behind
it, including what was deliberately left out and why.

The bar is **fidelity, not resemblance**: the game has to run the paradigm's own manipulation, so
that what the cited literature says about the task is actually true of the game. Naming a paradigm
is a claim, and a card on a game that only evokes one is the kind of overclaiming the whole note
is meant to avoid.

## Shows the card

| Game | Paradigm | Citation | Why it qualifies |
|---|---|---|---|
| Flash Crowd | Non-Symbolic Number Comparison | Halberda, Mazzocco & Feigenson, 2008 | Ratio-driven difficulty, with dot area and dot size both controlled against count. See below. |
| Ghost Grid | Corsi Block-Tapping Task | Corsi, 1972 | Forward spatial span, reproduced in order. Standard. |
| Orbit Tracker | Multiple Object Tracking | Pylyshyn & Storm, 1988 | Textbook structure: cue targets, identical distractors, independent motion, select all. |
| Mental Rotations | Mental Rotation Task | Shepard & Metzler, 1971 | Chiral figures with mirror foils, exact 24-rotation lattice, fairness checks that reject achiral figures and same-orientation pairs. The most faithful in the app. |
| Digit Memory | Brown-Peterson Task | Brown, 1958; Peterson & Peterson, 1959 | Encode, then an unrelated task genuinely occupying the same workspace, then recall. |
| Pattern Sequence | Matrix Reasoning | Raven, 1938 | The Raven's item format: read the rule along the rows, pick the completing panel. |
| Mental Flex | Task-Switching Paradigm | Rogers & Monsell, 1995 | Cue-based rule switch with a competing answer on the inactive dimension on every board, so a player still running the old rule lands on the other match rather than at random. Real switch and repeat trials. |
| Tower of Hanoi | Tower of Hanoi | Simon, 1975 | Standard planning measure; the puzzle requires moves that undo progress. |

## Does not show the card, and why

**N-Back** is not n-back. It flashes a sequence and then probes a single serial position ("which
shape was at position 3?"). Real n-back is a continuous stream where you judge whether the current
item matches the one n steps back, which is what loads updating and interference resolution. As
built it is a span task carrying the name of a different paradigm, and that name belongs to the
most-studied working-memory training task there is (Jaeggi et al., 2008). Either rename it, or
implement a continuous stream with a per-item target response and an adaptive n.

**Color Confusion** is not Stroop. Congruent cells are the targets, so nothing has to override the
prepotent reading response and there is no interference to measure. A faithful version shows one
word, asks for the ink colour from a colour-button row, mixes congruent and incongruent trials, and
scores the interference cost.

**Schulte Table** is popular in Russian-language attention literature but has a thin peer-reviewed
base. The validated equivalent is Trail Making (TMT-A, TMT-B, B minus A as the executive index); the
grid here is also 4x4 where the standard is 5x5.

**Visual Memory** is object-location binding, adjacent to visual paired-associate tasks but not a
standard one: guess order is randomised and a single error ends the run.

**Anomaly Puzzle** is incidentally close to feature versus conjunction visual search (Treisman &
Gelade, 1980) - `SAME_SHAPE` and `SAME_COLOR` are feature search, `RANDOM_COLOR_AND_SHAPE` is
conjunction search - but it is not scored as such, so the paradigm's findings do not apply to it.

Every other game is a puzzle or a drill with no specific paradigm behind it, which is fine. They
just do not get a card.

## Flash Crowd's dot sizing

Worth its own note, because it is the one card that rests on a deliberate deviation from the
textbook method.

Number is not the only thing that changes when you add dots. Total ink and average dot size both
move with count, and either answers "which side has more" without any number judgment at all. The
original generator sized every dot independently, which holds average size constant and so made
cumulative area a perfect cue: the game was winnable on blobbiness.

The two cues cannot both be removed. Total area is count times average dot area, so forcing one to
be uninformative makes the other informative. Panamath alternates the two matchings across trials;
measured over the counts and ratios this game generates, that leaves a player betting purely on ink
right 69% of the time and one betting on dot size right 76%.

`FlashCrowdGame.radiiFor` does the continuous version (Gebuis & Reynvoet, 2011) instead: total area
scales with the *square root* of the count, the exact midpoint between the two matchings, and a wide
per-side jitter buries most of the remainder. Both cues land near 60%, and the residual concentrates
where it costs least: about 72% at the 1:2 ratio, where the number judgment is trivial anyway, and
about 54% at the 9:10 ceiling, where it is actually hard.

`FlashCrowdGameTest` measures all of this rather than asserting a constant. It fails if either cue
creeps back above the alternating-design baseline, if the two stop leaking equally, or if the leak
stops shrinking as the ratio narrows.

## The card states provenance, and nothing else

The card says what the game is based on, cites the paper, and says what the task measures. It makes
no claim about what playing it does for the player, and that restraint is the point: every task
above is validated as a **measure**, not as training. Simons et al. (2016, *Psychological Science in
the Public Interest*) is the standard reference - practice reliably improves performance on the
trained task, near transfer is weaker, and far transfer to everyday cognition is largely
unsupported. Naming the paradigm is a fact about the game; promising an effect would not be.

The remaining exposure is not on this card but in the store listing, which says the games are
"designed to sharpen your mind and improve concentration". That is the claim shape the FTC fined
Lumosity $2M over in 2016. Describing the tasks rather than promising effects costs nothing.

## Adding a game to the list

1. Verify the mechanic runs the paradigm's manipulation, not just its surface.
2. Add the paradigm name and summary to `values/strings.xml` next to the other `science_*` keys,
   and leave the other 43 locales for release time (translations are deferred during feature work).
   The name is substituted into `science_note_based_on` ("Based on %1$s"), so author it bare, with
   no leading article.
3. Add the `GameType` arm to `GameScience.science`.
4. Re-record the Paparazzi snapshot for that game's instructions screen.
