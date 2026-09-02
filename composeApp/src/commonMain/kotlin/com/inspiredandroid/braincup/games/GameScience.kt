package com.inspiredandroid.braincup.games

import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.science_color_confusion_paradigm
import braincup.composeapp.generated.resources.science_color_confusion_summary
import braincup.composeapp.generated.resources.science_digit_memory_paradigm
import braincup.composeapp.generated.resources.science_digit_memory_summary
import braincup.composeapp.generated.resources.science_flash_crowd_paradigm
import braincup.composeapp.generated.resources.science_flash_crowd_summary
import braincup.composeapp.generated.resources.science_ghost_grid_paradigm
import braincup.composeapp.generated.resources.science_ghost_grid_summary
import braincup.composeapp.generated.resources.science_mental_flex_paradigm
import braincup.composeapp.generated.resources.science_mental_flex_summary
import braincup.composeapp.generated.resources.science_mental_rotations_paradigm
import braincup.composeapp.generated.resources.science_mental_rotations_summary
import braincup.composeapp.generated.resources.science_n_back_paradigm
import braincup.composeapp.generated.resources.science_n_back_summary
import braincup.composeapp.generated.resources.science_orbit_tracker_paradigm
import braincup.composeapp.generated.resources.science_orbit_tracker_summary
import braincup.composeapp.generated.resources.science_pattern_sequence_paradigm
import braincup.composeapp.generated.resources.science_pattern_sequence_summary
import braincup.composeapp.generated.resources.science_rule_shift_paradigm
import braincup.composeapp.generated.resources.science_rule_shift_summary
import braincup.composeapp.generated.resources.science_tower_of_hanoi_paradigm
import braincup.composeapp.generated.resources.science_tower_of_hanoi_summary
import org.jetbrains.compose.resources.StringResource

/**
 * The published cognitive-psychology task a game implements.
 *
 * The bar for having one is fidelity, not resemblance: the game has to run the paradigm's own
 * manipulation, so that what the cited literature says about the task is actually true of the
 * game. Ghost Grid is Corsi because it is a forward spatial span; Mental Flex is task-switching
 * because every board carries a competing answer on the inactive dimension, which is the whole
 * point of the paradigm. Rule Shift sits beside it without duplicating it: Mental Flex hands the
 * player the rule every round, where Rule Shift never states it and moves it once they have it,
 * so what it measures is induction from a single bit of feedback.
 *
 * Games that merely evoke a paradigm deliberately have none, and the card is a claim, so the
 * omissions are load bearing:
 *  - SCHULTE_TABLE has a thin peer-reviewed base; the validated equivalent is Trail Making.
 *  - VISUAL_MEMORY is object-location binding, adjacent to visual paired-associate tasks but not
 *    one of them.
 *
 * COLOR_CONFUSION was on that list until it was rebuilt. It used to lay nine words out in a grid
 * and ask for the ones whose word matched its ink, which makes the congruent cells the targets and
 * leaves no prepotent response to override; it now runs single trials against a fixed response row
 * and measures the congruency cost, which is the task.
 *
 * See `docs/game-science.md` for the full audit, including the games that were considered and
 * left out.
 */
data class GameScience(
    /** The name researchers use for the task. */
    val paradigmRes: StringResource,
    /** One sentence on what the task measures, in the player's terms. */
    val summaryRes: StringResource,
    /**
     * Author surnames and year of the paper the task is from. Not a [StringResource]: surnames and
     * years are the same in every locale, and a translator has nothing to do to them.
     */
    val citation: String,
)

/** The task this game implements, or null when it does not implement one faithfully. */
val GameType.science: GameScience?
    get() = when (this) {
        GameType.FLASH_CROWD -> GameScience(
            paradigmRes = Res.string.science_flash_crowd_paradigm,
            summaryRes = Res.string.science_flash_crowd_summary,
            citation = "Halberda, Mazzocco & Feigenson, 2008",
        )
        GameType.COLOR_CONFUSION -> GameScience(
            paradigmRes = Res.string.science_color_confusion_paradigm,
            summaryRes = Res.string.science_color_confusion_summary,
            citation = "Stroop, 1935; MacLeod, 1991",
        )
        GameType.GHOST_GRID -> GameScience(
            paradigmRes = Res.string.science_ghost_grid_paradigm,
            summaryRes = Res.string.science_ghost_grid_summary,
            citation = "Corsi, 1972",
        )
        GameType.ORBIT_TRACKER -> GameScience(
            paradigmRes = Res.string.science_orbit_tracker_paradigm,
            summaryRes = Res.string.science_orbit_tracker_summary,
            citation = "Pylyshyn & Storm, 1988",
        )
        GameType.N_BACK -> GameScience(
            paradigmRes = Res.string.science_n_back_paradigm,
            summaryRes = Res.string.science_n_back_summary,
            citation = "Kirchner, 1958; Jaeggi et al., 2008",
        )
        GameType.MENTAL_ROTATIONS -> GameScience(
            paradigmRes = Res.string.science_mental_rotations_paradigm,
            summaryRes = Res.string.science_mental_rotations_summary,
            citation = "Shepard & Metzler, 1971",
        )
        GameType.DIGIT_MEMORY -> GameScience(
            paradigmRes = Res.string.science_digit_memory_paradigm,
            summaryRes = Res.string.science_digit_memory_summary,
            citation = "Brown, 1958; Peterson & Peterson, 1959",
        )
        GameType.PATTERN_SEQUENCE -> GameScience(
            paradigmRes = Res.string.science_pattern_sequence_paradigm,
            summaryRes = Res.string.science_pattern_sequence_summary,
            citation = "Raven, 1938",
        )
        GameType.MENTAL_FLEX -> GameScience(
            paradigmRes = Res.string.science_mental_flex_paradigm,
            summaryRes = Res.string.science_mental_flex_summary,
            citation = "Rogers & Monsell, 1995",
        )
        GameType.RULE_SHIFT -> GameScience(
            paradigmRes = Res.string.science_rule_shift_paradigm,
            summaryRes = Res.string.science_rule_shift_summary,
            citation = "Grant & Berg, 1948",
        )
        GameType.TOWER_OF_HANOI -> GameScience(
            paradigmRes = Res.string.science_tower_of_hanoi_paradigm,
            summaryRes = Res.string.science_tower_of_hanoi_summary,
            citation = "Simon, 1975",
        )
        else -> null
    }
