package com.inspiredandroid.braincup.learn

import androidx.compose.runtime.Immutable

/**
 * One unit's state for the menus: how far through its lessons the learner is, and whether its test
 * has been certified. [earnedEpochDay] is null until the unit test has been passed flawlessly.
 */
@Immutable
data class LearnUnitProgress(
    val unit: LearnUnit,
    val lessonsCompleted: Int,
    val earnedEpochDay: Int?,
) {
    val topic: MathTopic = unit.topic

    val lessonsTotal: Int = unit.lessons.size

    val hasCertificate: Boolean = earnedEpochDay != null

    val allLessonsDone: Boolean = lessonsCompleted >= lessonsTotal

    companion object {
        /** Empty progress, for previews and for a unit never opened. */
        fun empty(unit: LearnUnit): LearnUnitProgress = LearnUnitProgress(unit, 0, null)
    }
}

/** A whole topic rolled up for the Learn menu and the main-menu tiles. */
@Immutable
data class LearnTopicProgress(
    val topic: MathTopic,
    val lessonsCompleted: Int,
    val lessonsTotal: Int,
    val certificates: Int,
    val unitsTotal: Int,
) {
    /** Every sub-topic certified, which is what the topic tile's trophy marks. */
    val allCertificatesEarned: Boolean = unitsTotal > 0 && certificates >= unitsTotal

    companion object {
        fun empty(topic: MathTopic): LearnTopicProgress {
            val units = LearnCatalog.units(topic)
            return LearnTopicProgress(
                topic = topic,
                lessonsCompleted = 0,
                lessonsTotal = units.sumOf { it.lessons.size },
                certificates = 0,
                unitsTotal = units.size,
            )
        }
    }
}
