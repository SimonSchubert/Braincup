package com.inspiredandroid.braincup.screenshots.learn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.phaseCount
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.learn.LearnVisualCanvas
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * The half of a two-phase figure that no other render can reach.
 *
 * A figure with more than one phase cycles them on a timer, and the timer never runs under
 * inspection, so every full-screen render in this sweep freezes on phase 0. Today that is the
 * `NumberLine(thenJump = ...)` figures, whose second phase is the return hop: the part of the
 * story a learner is meant to take away. This renders each phase side by side under its own name.
 *
 * The panel is rebuilt here rather than reused because `LearnFigurePanel` is `internal` to
 * `:composeApp`; the measures are copied from `LearnScreenComponents.kt`.
 */
class LearnFigurePhaseRenderTest {

    private companion object {
        val FigureWidth = 420.dp
        val FigureHeight = 180.dp
    }

    @get:Rule
    val paparazzi = learnPaparazzi()

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @Test
    fun everyPhase() {
        multiPhaseFigures().forEachIndexed { index, (owner, visual) ->
            repeat(visual.phaseCount) { phase ->
                paparazzi.learnSnap("${index.pad()}_${owner}_phase$phase") {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        PrismCard(
                            face = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.widthIn(max = FigureWidth).fillMaxWidth().height(FigureHeight),
                        ) {
                            LearnVisualCanvas(
                                visual = visual,
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                inspectionPhase = phase,
                            )
                        }
                    }
                }
            }
        }
    }

    /** Every figure in the catalog that has more than one phase, tagged with where it is used. */
    private fun multiPhaseFigures(): List<Pair<String, LearnVisual>> = buildList {
        LearnCatalog.allLessons.forEach { lesson ->
            lesson.steps.forEachIndexed { stepIndex, step ->
                step.visual
                    ?.takeIf { it.phaseCount > 1 }
                    ?.let { add("${lesson.id}_s${stepIndex + 1}" to it) }
            }
        }
        LearnCatalog.allUnits.forEach { unit ->
            unit.quiz.questions.forEachIndexed { index, question ->
                question.visual
                    ?.takeIf { it.phaseCount > 1 }
                    ?.let { add("${unit.id}_q${index + 1}" to it) }
            }
        }
    }

    private fun Int.pad(): String = toString().padStart(2, '0')
}
