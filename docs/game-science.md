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
| Color Confusion | Stroop Task | Stroop, 1935; MacLeod, 1991 | One word per trial, answered on its ink from a fixed colour row, against a scheduled mix of congruent and incongruent trials. The congruency cost is measured and reported. See below. |
| Flash Crowd | Non-Symbolic Number Comparison | Halberda, Mazzocco & Feigenson, 2008 | Ratio-driven difficulty, with dot area and dot size both controlled against count. See below. |
| Ghost Grid | Corsi Block-Tapping Task | Corsi, 1972 | Forward spatial span, reproduced in order. Standard. |
| Orbit Tracker | Multiple Object Tracking | Pylyshyn & Storm, 1988 | Textbook structure: cue targets, identical distractors, independent motion, select all. |
| N-Back | N-Back Task | Kirchner, 1958; Jaeggi et al., 2008 | The published block at the published pace: continuous machine-paced stream, one target decision per item, 6 targets in 20 scored trials, controlled lure rate, and n carried between plays as a level ladder. See below. |
| Mental Rotations | Mental Rotation Task | Shepard & Metzler, 1971 | Chiral figures with mirror foils, exact 24-rotation lattice, fairness checks that reject achiral figures and same-orientation pairs. The most faithful in the app. |
| Digit Memory | Brown-Peterson Task | Brown, 1958; Peterson & Peterson, 1959 | Encode, then an unrelated task genuinely occupying the same workspace, then recall. |
| Pattern Sequence | Matrix Reasoning | Raven, 1938 | The Raven's item format: read the rule along the rows, pick the completing panel. |
| Mental Flex | Task-Switching Paradigm | Rogers & Monsell, 1995 | Cue-based rule switch with a competing answer on the inactive dimension on every board, so a player still running the old rule lands on the other match rather than at random. Real switch and repeat trials. |
| Rule Shift | Wisconsin Card Sorting Test | Grant & Berg, 1948 | Sorting rule never stated and never cued, learned from a single bit of feedback, and moved silently once the player demonstrates it. Untimed, and shortened so the shifts stay frequent: 36 cards, six categories, four-correct criterion. See below. |
| Tower of Hanoi | Tower of Hanoi | Simon, 1975 | Standard planning measure; the puzzle requires moves that undo progress. |

## Does not show the card, and why

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

## N-Back's lure rate

Worth its own note, because it is the part of the task that is easiest to ship without and hardest
to notice missing.

N-Back used to flash a sequence and then probe a single serial position ("which shape was at
position 3?"), which is a span task carrying the name of a different paradigm. It now runs the
stream, with the published numbers: 500ms on a 3000ms step, 20 scored trials after the n priming
ones, 6 targets, and a block cleared on fewer than 3 errors, counting misses and false alarms
alike. The first n items cannot be targets and a response on them is a false alarm, as in the
standard scoring.

The piece that carries the paradigm's actual claim is the **lure**: an item that repeats what was
n-1 or n+1 back. A lure is familiar but wrong, so rejecting one needs the position in the memory
window and not just recognition. Without lures, "does this feel recent" answers the task, and
n-back stops measuring the updating and interference resolution it is cited for.

Lures cannot simply be seeded and left, because with a six-shape palette an unconstrained
non-target lands on a neighbour position about two trials in five, which would swamp the four
planned ones and make the interference load an accident of the draw. So non-target items exclude
the n-1 and n+1 items as well as the n item, and the lure rate becomes a controlled quantity. It is
still a rate rather than an exact count: a lure slot degrades to an ordinary non-target when both
neighbour positions happen to hold the target shape. `NBackGameTest.almostEveryPlannedLureLands`
asserts the achievable bar, that the mean lands above 85% of the planned four, and
`aBlockHasTheTextbookTargetAndLureCounts` asserts no block ever exceeds them.

Targets are spread one per equal share of the block rather than drawn freely, because clustered
targets make a block a coin flip. The shares are computed per block rather than from a fixed width,
since 20 trials do not divide by 6: a truncated width would leave the last two scored slots
unreachable, a dead tail a player could learn to stop attending to.
`everyScoredSlotCanHoldATarget` pins that.

### Why it is a level game

The run clock is why this took two attempts. Every timed game in the app gets 60 seconds, and one
block at the reference pace runs past a minute, so a faithful block did not fit even once, let
alone the twice that adapting n needs. The first version compressed the block to 9 scored trials
and the step to 1800ms to make an adaptive run fit, and the block length was the real cost:
adapting on 9 trials with 3 targets is a far noisier estimate than 20 with 6.

Making it a level game removes the constraint rather than working around it. One level is one block
at that level's n, and the level *is* n, so "Level 3" is 3-back. Clearing a block unlocks the next
and the stored level carries between plays, which is the adaptive procedure of Jaeggi et al. (2008)
with the session boundary moved: their protocol is blocks with n carried between them, which is a
level ladder already. The score, the highest n reached, is also what that literature reports as its
outcome measure.

The one thing the level form gives up is the downward step. A block that is not cleared leaves the
stored level alone and is replayed, where the reference would drop n after a bad enough block.
Retrying the same level serves the same purpose within a game's idiom.

### Feedback

Nothing is marked mid-stream except the player's own taps, which turn the Match button green or
red. Misses are deliberately silent. A miss is only known once its window closes, which is the
instant the next item appears, so marking one puts a salient event on top of the item that most
needs encoding and makes the following miss more likely. The block result, matches found out of
matches present and mistakes made, is shown at the end, which is where the reference puts its
feedback too. It sits on the finish screen rather than on one of its own, next to the buttons that
act on it: a result the player reads and then loses before they can respond to it is not feedback.

Misses and wrong taps are counted together as mistakes, because that is what the clearing rule
counts. Reporting only wrong taps would tell a player who let a match go by that they made none.

## Rule Shift's three departures

Worth its own note, because all three are administration parameters rather than the manipulation,
and a reader who knows the test will notice them.

It is **untimed**, which is worth stating plainly because every other non-puzzle game in this app is
not. The WCST has no clock in any published administration; speed is not what it measures, and a
timed version would pay for fast guessing over working the rule out. An earlier draft ran on the
app's standard sixty-second timer, which was an unstated deviation and the one most damaging to the
card, since it changed what the score rewards. The deck ends the run instead.

**A category is four consecutive correct over a 36-card run, not ten over 64.** This is the one
deviation made for the game rather than for the measure, and it is the largest. At the standard
criterion roughly nine trials in ten only confirm a rule the player is already holding: the run
takes over two minutes and almost all of it is filler. The shifts are what the paradigm is about, so
the run is sized to pack them in - six categories inside 36 cards puts a rule change roughly every
six taps. Four consecutive correct still demonstrates the rule rather than luck, since guessing a
category is under 1% per attempt. A draft did run the validated WCST-64 (Kongs, Thompson, Iverson &
Heaton, 2000) at the standard ten-correct criterion, and it was accurate and dull.

**The deck holds only 24 of the 64 distinct cards.** Key card `i` carries count `i`, colour `i` and
shape `i`, so a stimulus points at one key card by number, one by colour and one by form. Only the
cards whose three indices are all different are dealt, and they repeat to fill the run. On the rest,
two rules agree, the trial's feedback cannot separate them, and an error cannot be classified as
perseverative or not. The standard deck includes those cards and absorbs them in scoring.

**The next rule is drawn at random from the two not in force**, rather than cycling
colour -> form -> number. The fixed order is how the test is administered, not what it measures, and
a player who learns it stops having to probe at all, which is the part being measured.

Nothing on screen counts the streak or the categories. Both are derivable from the feedback, but
rendering either would light up on the exact trial the rule moves and announce it, and being
surprised is the task. The cards left are shown, since the shrinking deck is visible in the real
test.

The card still stands: the manipulation is intact - the rule is never stated, is learned from one
bit of feedback, and moves silently once held, with perseverative errors countable. What changed is
how many trials that takes, which is a parameter, not the thing being claimed.

One framing point, because it decides how the game is described rather than how it is built: on
Miyake et al. (2000) the WCST loads most strongly on *shifting*, which is what Mental Flex already
covers. Rule Shift is not a second Mental Flex, and the difference is not flexibility but where the
rule comes from - Mental Flex hands it over every round, Rule Shift never states it. What is
untouched elsewhere in the app is induction from one bit of feedback, so that is what the card
claims.

## Color Confusion's rebuild, and the three things it does not do

Worth its own note, because the game that used to carry this name was the app's clearest case of
naming a paradigm it did not run.

The old mechanic laid nine colour words out in a grid and asked for every cell whose word matched
its ink. That makes the *congruent* cells the targets. Nothing has to be overridden to find them,
since reading the word and reading the ink point at the same answer, and a task with no conflict in
it has no interference to measure. It was a visual search, and a decent one; it was not Stroop. It
now shows one word at a time and asks for its ink from a fixed row of four colour swatches, which is
the manual form the task is usually run in.

**The clock is not a deviation here**, which is worth saying because the section above argues the
opposite for Rule Shift. The Stroop is administered timed and scored as items completed - Golden's
(1978) version allows 45 seconds a card - so a sixty-second run whose score is correct answers is the
procedure rather than the app's format imposed on it. Speed is the measure in one of these tasks and
the enemy of it in the other.

Three things it does not do, and all three make the number it reports *smaller* than the ones in the
literature rather than larger:

**Responses are manual, not spoken.** Naming the colour aloud is what Stroop did and what most of
the literature reports. Tapping a swatch keyed to a colour is the standard computerised substitute
and reliably yields a smaller effect (MacLeod, 1991). Nothing about a phone can fix that.

**The list is mixed, not blocked.** Golden's version is three uniform cards: words, colour patches,
incongruent items. Interleaving the conditions shrinks the effect, but a blocked design cannot
produce a within-run comparison at all, and one run is all a player gives the game.

**There is no neutral condition,** so the reported number is a *congruency cost* and not
interference. Interference proper is incongruent minus neutral, and congruent trials are themselves
faster than neutral ones, so incongruent minus congruent bundles that speed-up in with the cost. A
neutral condition would need either a row of Xs, which reads as filler and gets answered differently,
or colour-unrelated words, which would need a word list in all 44 locales; and a sixty-second run has
too few trials to divide three ways in any case. The finish screen calls it a congruency cost, which
is what it is.

### The congruency schedule

Congruency is dealt from a shuffled bag of five holding two congruent trials, rather than drawn per
trial. Over the forty-odd trials a minute holds, an independent draw would make the mix an accident
of the run, and would let one condition come out in a streak long enough to be most of one side of
the comparison. The bag makes the ratio exact but for the part-dealt bag at the end, and
`ColorConfusionGameTest` asserts it bag by bag rather than in aggregate, which is the stronger claim.

Two in five rather than half is a trade, not a free choice: the effect grows with the share of
congruent trials, so an even split would report a slightly larger number. The incongruent condition
is the one the task is about, and a run this short has trials to spare on one side only.

The ink also never repeats on consecutive trials. A repeat would let a player hold one swatch and
collect points without reading anything, and response repetitions carry a speed-up of their own that
would land unevenly across the two conditions.

Only correct trials contribute a time, since an error time is a time to the wrong decision and not
the quantity the effect is defined over. The medians are taken rather than the means, so one
distracted trial cannot move the reading, and the effect is withheld entirely until each condition
has five correct trials behind it.

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
