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
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.learn.LearnVisualCanvas
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Every distinct figure in the catalog, on its own panel.
 *
 * The full-screen sweep shows each figure in context but buries it in a 2424px frame, which makes
 * a whole-catalog comparison of one figure family impractical and leaves label crowding hard to
 * measure. These frames are the panel and nothing else, at the same measures a lesson draws it,
 * so they can be scanned as well as read.
 *
 * Figures are deduplicated on the `LearnVisual` value itself, and named after the first step that
 * uses them, so a defect found here maps straight back to a lesson.
 */
@RunWith(Parameterized::class)
class LearnFigureRenderTest(
    private val family: String,
) {
    companion object {
        private val FigureWidth = 420.dp
        private val FigureHeight = 180.dp

        /** Every figure in the catalog, first use wins, grouped by the variant that draws it. */
        private val catalog: Map<String, List<Pair<String, LearnVisual>>> by lazy {
            val seen = LinkedHashMap<LearnVisual, String>()
            LearnCatalog.allLessons.forEach { lesson ->
                lesson.steps.forEachIndexed { index, step ->
                    step.visual?.let { seen.putIfAbsent(it, "${lesson.id}_s${index + 1}") }
                }
            }
            LearnCatalog.allUnits.forEach { unit ->
                unit.quiz.questions.forEachIndexed { index, question ->
                    question.visual?.let { seen.putIfAbsent(it, "${unit.id}_q${index + 1}") }
                }
            }
            seen.entries
                .map { (visual, owner) -> owner to visual }
                .groupBy { (_, visual) -> visual::class.simpleName ?: "Other" }
        }

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun families(): List<Array<Any>> = catalog.keys.sorted().map { arrayOf<Any>(it) }

    }

    @get:Rule
    val paparazzi = learnPaparazzi()

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @Test
    fun panels() {
        catalog.getValue(family).forEachIndexed { index, (owner, visual) ->
            // A viewport just deep enough for the panel, so the frame is the figure and the scan
            // does not have to find it inside a screen.
            paparazzi.learnSnap("${index.toString().padStart(3, '0')}_$owner", heightPx = 560) {
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
                        )
                    }
                }
            }
        }
    }
}
