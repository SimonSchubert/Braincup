## v3.8.1 - 2026-10-08

### Features
- Bring the main menu mascot to life: it breathes, sways and taps its foot, and hops when tapped.
  It also reacts to your situation: a delighted hop when you return after a few days away, a
  worried look when tonight's session is all that keeps your streak alive, and a sleepy pose late
  at night.
- The mascot reacts to your answers: a thumbs up for a correct answer and on the session complete
  screen, a slump with its sunglasses slipping for a wrong one, and a celebration for a new best.

### Improvements
- Redesign every game tile on the home screen in one style: raised boards, cards and keys, with
  the number games showing the keys and cards they are played with and every tile at the same
  size and depth.
- Raise the Simon Says pads like the physical toy, seated in a body with a center cap. A pressed
  pad now sinks instead of shrinking.
- Upgrade dependencies.

### Fixes
- Stop the Sliding Puzzle tutorial leaving a ghost tile behind.
- Give the new-best mascot room above the trophy.

## v3.8.0 - 2026-10-04

### Features
- Add Rail Yard, a train routing game: flip the switches so each colored train reaches the station
  of its color. The map grows from three stations up to eight during a run, and trains already on
  the board still count after the clock runs out.
- Add Head Count, a working-memory game: people walk in and out of a house, and when the traffic
  stops you say how many are inside. Its difficulty carries over from your last run.
- Add Flock, a flanker task: swipe the way the middle bird flies while the birds around it point
  the same way or the opposite way. The finish screen reports what the conflicting birds cost you.
- Each new game has its own gold medal on Play Games and Game Center.

### Fixes
- Stop opening Google's account setup on every launch for players without a Google account.
  Signing in from the leaderboard buttons now also restores progress.
- Fix distorted flags on Android 9.
- Loop the background music without a gap.

### Improvements
- Explain the Trio rule with examples, and say why a wrong guess is not a trio.
- Show a found trio on the Trio menu tile.
- Upgrade dependencies.

## v3.7.0 - 2026-09-27

### Features
- Add Checkers (English draughts) next to Chess and Reversi. Play the CPU at Easy, Medium or Hard,
  or pass and play against a friend. Captures are compulsory, a multi-jump is tapped one landing at
  a time, and forty moves each without a capture or a man moving is a draw.
- Add Mini Checkers to Logic: a 6x6 board built around a forcing combination, where every move of
  the line leaves the CPU exactly one reply. Normal asks for a three-move line, Hard for four or
  five with more pieces on the board. It has its own gold medal.
- Add Measurement to Learn Math: measuring length, telling the time, money and change, mass and
  capacity, metric units, and speed, distance and time, over 18 lessons.
- Translate the Learn catalog into Italian, Portuguese, Japanese, Korean, Traditional Chinese,
  Simplified Chinese, Indonesian, Hindi and Russian.
- Split the sound setting into separate music and sound effect switches, so background music can
  be muted while game sounds keep playing.
- Rework color-blind support: right and wrong answers show a tick or cross in every game, not only
  a red or green tint. The color-blind setting adds a pattern to every colored piece, Prism Clear
  tiles show their shapes, Wordle and Nurikabe get marks and hatching, and Settings names the games
  the setting hides.
- Release an aarch64 Linux build alongside x86_64.

### Fixes
- Write numbers with the decimal separator each language uses. Learn Math printed a decimal point
  in every language, including the thirty or so that write a comma.
- Accept every exactly correct answer in Missing Operators, including swapped plus and minus of
  equal terms and answers like 8 / 3 * 3.
- Stop Reset from paying the win again on a solved Mini Checkers or Mini Chess board.
- Correct false Learn teaching on rulers, negatives, quadratic roots and metric conversions.
- Use real plurals for the Daily Challenge, streak and best-tries counts, and stop gluing color,
  shape and direction together in answer feedback, which broke grammar in several languages.
- Stop Knot repeating a color on its largest boards.
- Match the Linux desktop entry to the window class, so taskbars group the window with its launcher.
- Keep a compared value on a Learn number line next to its own tick when the row is crowded.

### Improvements
- Use Taiwan's terms for rows and columns so Path Finder names the right cell.
- Retranslate Color Confusion and Trio after the English rewrites, and rewrite the Play listings to
  the current game catalog in every language.
- Upgrade dependencies.

## v3.6.0 - 2026-09-09

### Features
- Add Reversi on a 6x6 board, against the CPU or pass-and-play on one device. Trap a line of your
  opponent's discs between two of yours to flip them; most discs at the end wins. Against the CPU
  you pick Normal or Hard: Normal grabs whatever discs are going, which is the mistake the game
  punishes, while Hard plays for corners and mobility and works the last twelve squares out exactly.

### Fixes
- Say when a Wordle win was a win. A solve on the sixth row filled the board like a loss and the
  status line read "The word was ...", so the win read as a reveal after a failure. It now says
  "Solved in 6/6" in green, and only a real loss or a give-up keeps the reveal.
- Stop Sherlock Calculation ramping out of reach. The tile pool tops out at six, only a solved
  round steps the difficulty so a run of skips no longer banks rounds nobody solved, and a resumed
  run applies its stored bounds before dealing the first round rather than after it.
- Keep the whole Missing Operators equation on the screen. From round 10 the line ran past a 411dp
  phone and the last slot was cut off; it is now scaled to fit rather than wrapped or scrolled.
- Keep every Learn Math number key on the screen. The pad is pinned above the Check button instead
  of scrolling with the step, so the row holding the 0 no longer falls under the fold, and what is
  typed lands on the question mark in the card that asked for it.
- Stop menu section headings from passing taps through to the tile underneath and launching a game
  nobody aimed at. A drag that starts on the heading still scrolls the grid.

### Improvements
- Flags drops its per-round countdown, so a run is now bounded only by a wrong answer
- Tell the Quick Sum total apart from a flashed term: the flashed number is set well above the type
  scale the rest of the app tops out at, and the total is boxed in a tinted card with a leading "="
- Say on the Chess tile that it can be played against a friend as well as the CPU, which it always
  could
- Give a Learn Math value being typed the colour of the line around it, so it no longer collides
  with the blue that marks a working value on the same line
- Remove unused resources, dead code, unused imports and redundant casts
- Upgrade dependencies

## v3.5.0 - 2026-09-02

### Features
- Add Algebra to Learn Math: seven sub-topics from expressions and variables through to powers and
  roots, with 21 lessons and a certificate for each
- Add Rule Shift, card sorting against a rule nobody tells you. Sort each card onto one of four key
  cards by colour, shape or count; the only feedback is right or wrong, and once you have the rule
  it silently changes. Untimed, like the test it comes from: the run ends after 36 cards or six
  completed categories, and your score is the categories. It shows the research card, based on the
  Wisconsin Card Sorting Test.
- Add launcher shortcuts for the daily challenge and the games you played most recently
- Add five more packed Prism Clear levels, L16 to L20
- Give the Sudoku board a Reset button
- Give Trio a Give Up button, and list every valid trio in its instructions

### Improvements
- Rebuild Color Confusion as the real Stroop task. Instead of picking matching words out of a grid,
  one colour word now appears at a time and you tap the colour of the ink it is printed in, with the
  word usually naming a different one. It shows the research card, based on the Stroop task, and the
  finish screen reports your congruency cost: how much longer you took when the word disagreed with
  the ink. The old grid had congruent cells as the targets, so there was nothing to override and
  nothing to measure. Scores are on a new scale, since a trial is one tap where a grid took several
  seconds, so an old high score no longer compares and the medal will re-earn on your next good run.
- Rebuild N-Back as the real task, at the published numbers: a continuous stream of shapes with a
  Match response on every item, 6 matches in 20 trials, a controlled rate of near-miss lures, and a
  block cleared on fewer than 3 errors. It now shows the research card.
- N-Back is now level based, and the level is n: Level 3 is 3-back. Clearing a block unlocks the
  next one and your level carries between plays. This is what makes the full-length block possible,
  since one runs past the 60 seconds a timed game gets. Scores are now the highest n reached, so an
  old N-Back high score no longer compares and the medal on its tile will re-earn on your next
  clear. Like every level game, it no longer appears in the daily challenge.
- Rewrite the Trio instructions around a trait key and per-trait badges, so the rule is legible
  before the first round rather than after it
- Run the settings, achievement and Learn lists under the gesture bar
- Centre the Sudoku digits inside their tiles, and centre a Learn result in the space it is given
- Draw the tick and cross instead of typing them, so they render the same on every platform
- Translate every new string into all 51 locales
- Upgrade dependencies

### Fixes
- Stop the memorize timer bar asking for a frame every vsync while the quit dialog is open, which
  kept the screen animating and the frame clock awake for as long as the dialog stayed up

## v3.4.0 — 2026-09-01

### Features
- Group the main menu into sections with sticky, colour-coded headings
- Add a "Take Your Time" section for the IQ test, Chess, Sudoku, Matchstick Riddles, Peg Solitaire and Flags, with progress shown on the tile
- Cite the cognitive-psychology task behind eight games on their instructions screen

### Fixes
- Stop Flash Crowd being winnable on cumulative dot area instead of number

### Improvements
- Give the untimed and Learn sections their own accents
- Drop the trailing count from section headings
- Improve German and French translations, and fill the remaining gaps

## v3.3.0 — 2026-08-30

### Features
- Add Mental Rotations game
- Add Mental Flex game
- Add an in-app language picker to Settings, independent of the device language
- Add seven Indian languages: Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam and Punjabi
- Add a shape guide for Geometry and a rules guide for Arithmetic in Learn Math
- Translate the Learn Math catalog into French and Dutch
- Add Play store listings for es-US, fr-CA, pt-PT, zh-HK and six regional English locales

### Fixes
- Stop Learn test figures from giving away their own answers
- Fix Learn screen rendering issues found by rendering every screen

### Improvements
- Move the Learn catalog into strings.xml so it can be localized
- Give Learn's figures the app fonts and its prose a readable line measure
- Colour values consistently between prose and figures
- Improve large text accessibility
- Speed up the menu and game screens
- Unify shared game abstractions
- Upgrade dependencies

## v3.2.0 — 2026-08-27

### Features
- Add Learn Math section (beta) with lessons, tests and certificates, organised by topic and sub-topic
- Award a store achievement for every Learn certificate
- Add 21 new languages: Bulgarian, Catalan, Croatian, Czech, Danish, Estonian, Finnish, Hebrew, Hungarian, Icelandic, Irish, Latvian, Lithuanian, Malay, Norwegian, Romanian, Serbian, Slovak, Slovenian, Swedish, Urdu
- Register Filipino, Urdu and Traditional Chinese on iOS

### Fixes
- Fix N-Back gold cup being unreachable
- Fix the Play locale codes and complete every store listing
- Fix content defects in the counting, multiplication, fractions and decimals lessons
- Fix the web build's canonical URLs

### Improvements
- Update the desktop and PWA icons to the new mascot
- Draw the figures the lessons actually describe, with animated steps
- Share one set of components across the Learn screens
- Cut the web build's blank-page wait
- Clear every compose lint finding
- Upgrade dependencies

## v3.1.0 — 2026-08-19

### Features
- Add IQ test
- Add Persian language support

### Fixes
- Fix Accounts dialog scrim and design token drift

### Improvements
- Improve Prism style UI
- Improve Missing Operator and Sherlock Calculation games
- Improve color sets
- Improve localizations
- Share demo scaffold, level-puzzle layout and reading column across screens
- Upgrade dependencies

## v3.0.0 — 2026-08-18

### Features
- Rebuild Pattern Sequence as a 3x3 matrix reasoning puzzle
- Add license screen
- New mascot app icons across all platforms

### Fixes
- Fix Pattern Sequence button sizes
- Fix Trio gold medal never unlocking on Play Games
- Fix Game Center achievement restore for Simon Says, N-Back and Prism Clear
- Align player account dialog buttons

### Improvements
- Speed up Knot and Nurikabe puzzle generation
- Improve OLED and color blind colors
- Improve mascot and feedback icons
- Improve sound looping
- Remove duplicate resources
- Unify shared game abstractions
- Upgrade dependencies

## v2.33.0 — 2026-08-14

### Features
- Add Trio game
- Add multi-account support
- Add Simon Says sound effects
- Update flags

### Fixes
- Fix Peg Solitaire tile preview to show only the center empty
- Fix Mini Chess AI job leak when a game ends by checkmate

### Improvements
- Improve chess board sizing
- Improve performance
- Upgrade dependencies
- Replace deprecated Kotlin and Gradle APIs

## v2.32.0 — 2026-07-29

### Features
- Add Bulls and Cows game
- Add Missing Operators game

## v2.31.0 — 2026-07-22

### Features
- Add Prism Clear game

## v2.30.0 — 2026-07-20

### Features
- Add Simon Says game
- Add N-Shapes game

### Improvements
- Adjust gold medal scoring logic
- Improve Tower of Hanoi

## v2.29.0 — 2026-07-17

### Features
- Add Bubble Sum game
- Add Peg Solitaire game
- Add Quick Sum game

## v2.28.0 — 2026-07-14

### Features
- Add Tower of Hanoi game
- Chess draws by threefold repetition

### Fixes
- Fix web crash on nested routes
- Fix chess grid colors
- Fix French strings that showed backslash escapes in-game

### Improvements
- Reduce how often triangles rotate in the anomaly puzzle
- Upgrade dependencies

## v2.27.0 — 2026-07-11

### Features
- Achievements now take difficulty level into account

### Fixes
- Fix Wordle and session start landscape UI
- Fix Sherlock calculation landscape layout

### Improvements
- Improve app performance
- Unclutter game screens
- Clean up deprecated APIs

## v2.26.1 — 2026-07-07

### Fixes
- Add a fallback font so non-Latin languages render correctly

### Improvements
- Improve daily challenges rotation
- Disable lenient 9 and 6 answer forms
- Upgrade dependencies

## v2.26.0 — 2026-06-30

### Features
- Sudoku notes (pencil marks)
- Browser URL navigation with a dedicated 404 page on web

### Fixes
- Fix text jiggle on the instructions screen
- Fix German Wordle umlaut normalization

### Improvements
- Sudoku dims numbers once all of them are placed
- Improve overall UI performance
- Improve matchstick puzzle horizontal layout
- Persistent sponsors section
- Polish UI styles and clean up localizations
- Improve web SEO
- Upgrade dependencies

## v2.25.0 — 2026-06-28

### Features
- Match stick puzzle game
- Prism style toggle

### Improvements
- Clearer distinction between normal games and mini games
- Normal sudoku progress persists across sessions

### Fixes
- Fix web build dependency

## v2.24.0 — 2026-06-26

### Features
- Solo chess puzzle game
- Knot untangling puzzle game
- Greek translation

### Improvements
- Lower Android minimum SDK to 24 (Android 7.0 support)
- Updated dependencies

## v2.23.0 — 2026-06-24

### Features
- Animated instructions for game tutorials

### Fixes
- Fixed dialog status bar theme

### Improvements
- Updated localizations
- Updated dependencies

## v2.22.0 — 2026-06-21

### Features
- Cat Queens puzzle game
- Achievements on iOS via Game Center
- Achievements on Android via Play Games

### Fixes
- Skip quit confirmation when the game is already finished

### Improvements
- Refreshed game previews
- Consistent game styling
- Tuned achievement difficulty
- Added missing localizations
- Updated dependencies

## v2.21.0 — 2026-06-16

### Features
- Nurikabe puzzle game
- Shikaku puzzle game

## v2.20.0 — 2026-06-13

### Features
- Number pad input settings
- Confirmation dialog when quitting a game
- New achievements

### Fixes
- Correct rounding when chaining sub-expressions

### Improvements
- New navigation animations
- More readable number font
- Polished achievements screen
- Updated dependencies

## v2.19.0 — 2026-06-09

### Features
- New Wordle game

### Improvements
- Updated dependencies

## v2.18.0 — 2026-06-01

### Features
- New chess game

### Fixes
- Sherlock auto-check no longer triggers on non-numeric input
- Fix HTTP requests on desktop

### Improvements
- Level-based scoring for Sliding Puzzle and Lights Out
- Polished chess board styling

## v2.17.0 — 2026-05-28

### Features
- New standard Sudoku game
- Theme switcher with Light, Dark, and OLED modes

### Improvements
- Improved right-to-left layout support
- Improved color contrast for readability
- Better spacing on small screens
- Added missing localizations
- Updated dependencies

## v2.16.0 — 2026-05-24

### Features
- Add Spot the New game

## v2.15.3 — 2026-05-24

### Features
- Add digit memory game

## v2.15.2 (2026-05-21)

### Improvements
- Polish division and addition screen UI
- Update dependencies

## v2.15.1 — 2026-05-17

### Features
- iOS Game Center achievements and leaderboard support

### Fixes
- Restore flag score on re-reinstall
- Fix visual memory difficulty bug

## v2.15.0 — 2026-05-16

### Features
- Color-blind mode

### Fixes
- Sherlock calculation screen width issue

### Improvements
- Performance improvements
- Improved color confusion button contrast
- New chess scenarios
- UI alignment refinements

## v2.14.1 — 2026-05-14

### Fixes
- Leaderboard open action now works correctly
- Restored XP on fresh installs
- Fixed ProGuard-related crash

## v2.14.0 — 2026-05-14

### Features
- XP leaderboard
- Prism trophies

### Improvements
- Custom shapes now displayed as prisms
- General UX refinements

## v2.13.0 — 2026-05-13

### Features
- New Flag guessing game
- Google Play Services achievements

### Improvements
- Upgraded Compose Multiplatform
- Switched to Bungee font

## v2.12.1 — 2026-05-13

### Fixes
- Excluded chess from daily challenges

### Improvements
- Refined UI alignment
- Upgraded dependencies

## v2.12.0 — 2026-05-08

### Improvements
- Refactored UI from Material design to Prism design system

## v2.11.0 — 2026-05-01

### Features
- Horizontal phone layout support

### Improvements
- Improved Compose reusability
- Added splashscreen dependency

## v2.10.0 — 2026-04-30

### Features
- New Lights Out game
- New Sliding Puzzle game

### Improvements
- Improved Compose rendering performance
- Aligned game preview styles
- Upgraded SDK dependencies
- Polished achievements UI
- Faster audio player startup

## v2.9.0 — 2026-04-29

### Features
- New mini chess game
- Themed icon support on Android

### Improvements
- Polished value comparison UI
- Performance improvements

## v2.8.0 — 2026-04-25

### Features
- New Schulte table game
- New mini Sudoku game

## v2.7.1 — 2026-04-24

### Fixes
- Fix F-Droid build by disabling the AGP "Dependency metadata" signing block

### Improvements
- Upgrade dependencies

## v2.7.0 — 2026-04-22

### Features
- New XP level system to track your progress
- Restored adaptive difficulty across games

### Fixes
- Improved UI responsiveness
- Corrected translations in French, Italian, Turkish, and Ukrainian

### Improvements
- Internal refactor to replace deprecated APIs

## v2.6.2 — 2026-04-21

### Improvements
- Pure black backgrounds for better OLED energy saving
- Smoother Visual Memory animations
- UI and UX polish

## v2.6.1 — 2026-04-20

### Improvements
- Split Android build into `foss` and `playStore` flavors to enable F-Droid publishing

## v2.6.0 — 2026-04-20

### Fixes
- Fixed potential Ghost Grid and Visual Memory crash

### Improvements
- Fraction calculations now ramp up in difficulty as you progress
- Polished UI and UX
- Challenge card has a max width for better readability on large screens
- Performance improvements

## v2.5.0 — 2026-04-19

### Features
- Dark mode that follows system appearance, with Material You dynamic color on Android 12+

### Improvements
- Crisper number pad operator buttons with perfectly centered vector icons

## v2.4.1 — 2026-04-19

### Features
- Grid Solver now features pre-filled Sudoku-style clues with 3x3 and 4x4 grids
- Failed Grid Solver rounds reveal the correct solution on the grid

### Fixes
- Fixed Grid Solver crash when advancing to a larger grid between rounds

### Improvements
- Math calculations display with proper symbols and vertical fractions

## v2.4.0 — 2026-04-16

### Features
- Daily challenge mode with day-streak tracking

### Improvements
- Redesigned session interstitial with progress dots and game descriptions
- Added hand cursor to Ghost Grid cells and Value Comparison buttons
- Added localizations for daily challenge and session screens across all 19 languages
- Upgraded Kotlin, Compose Multiplatform, and other SDKs

## v2.3.0 — 2026-03-25

### Improvements
- Upgrade SDKs
- Migrate to Android module
