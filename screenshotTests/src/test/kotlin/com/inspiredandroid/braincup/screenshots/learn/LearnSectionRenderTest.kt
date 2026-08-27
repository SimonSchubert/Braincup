package com.inspiredandroid.braincup.screenshots.learn

import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnTopicProgress
import com.inspiredandroid.braincup.learn.LearnUnitProgress
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.screens.LearnMenuScreenContent
import com.inspiredandroid.braincup.ui.screens.LearnShapeGuideScreen
import com.inspiredandroid.braincup.ui.screens.LearnTopicScreenContent
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * The chrome around the sub-topics: the Learn menu, the two topic ladders and the shape guide.
 *
 * Each is rendered twice where progress changes what is on screen, because an empty ladder and a
 * half-finished one lay out differently and only one of them is what a returning learner sees.
 */
class LearnSectionRenderTest {

    @get:Rule
    val paparazzi = learnPaparazzi()

    @OptIn(ExperimentalResourceApi::class)
    @Before
    fun setup() {
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @Test
    fun menu() {
        paparazzi.learnSnap("00_menu_fresh") {
            LearnMenuScreenContent(
                progress = MathTopic.entries.map { LearnTopicProgress.empty(it) }.toImmutableList(),
                onTopicSelected = {},
                onBack = {},
            )
        }
        // Arithmetic part-done, Geometry finished: the tile's progress line and its trophy.
        paparazzi.learnSnap("01_menu_in_progress") {
            val progress = MathTopic.entries.map { topic ->
                val empty = LearnTopicProgress.empty(topic)
                when (topic) {
                    MathTopic.ARITHMETIC -> empty.copy(lessonsCompleted = 11, certificates = 3)
                    else -> empty.copy(
                        lessonsCompleted = empty.lessonsTotal,
                        certificates = empty.unitsTotal,
                    )
                }
            }
            LearnMenuScreenContent(
                progress = progress.toImmutableList(),
                onTopicSelected = {},
                onBack = {},
            )
        }
    }

    @Test
    fun topics() {
        MathTopic.entries.forEachIndexed { index, topic ->
            val units = LearnCatalog.units(topic)
            paparazzi.learnSnap("1${index}0_topic_${topic.id}_fresh", heightPx = LearnTallPx) {
                LearnTopicScreenContent(
                    topic = topic,
                    progress = units.map { LearnUnitProgress.empty(it) }.toImmutableList(),
                    onUnitSelected = {},
                    onGuide = {},
                    onBack = {},
                )
            }
            // Every state a sub-topic row can be in, on one screen: untouched, part-done,
            // all lessons done but not certified, and certified.
            paparazzi.learnSnap("1${index}1_topic_${topic.id}_in_progress", heightPx = LearnTallPx) {
                val progress = units.mapIndexed { position, unit ->
                    when (position % 4) {
                        0 -> LearnUnitProgress(unit, unit.lessons.size, 20_000)
                        1 -> LearnUnitProgress(unit, unit.lessons.size, null)
                        2 -> LearnUnitProgress(unit, 1, null)
                        else -> LearnUnitProgress.empty(unit)
                    }
                }
                LearnTopicScreenContent(
                    topic = topic,
                    progress = progress.toImmutableList(),
                    onUnitSelected = {},
                    onGuide = {},
                    onBack = {},
                )
            }
        }
    }

    @Test
    fun shapeGuide() {
        // Reachable only from the Geometry ladder, and the one Learn screen with no state at all.
        paparazzi.learnSnap("20_shape_guide", heightPx = LearnGuidePx) {
            LearnShapeGuideScreen(onBack = {})
        }
    }
}
