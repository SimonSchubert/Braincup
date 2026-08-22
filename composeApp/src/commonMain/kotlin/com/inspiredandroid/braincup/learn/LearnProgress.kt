package com.inspiredandroid.braincup.learn

import androidx.compose.runtime.Immutable

/**
 * One unit's state for the menus: how far through its lessons the learner is, and the best test
 * result recorded. [tier] is null until the unit test has been passed at least once.
 */
@Immutable
data class LearnUnitProgress(
    val unit: LearnUnit,
    val lessonsCompleted: Int,
    val bestPercent: Int?,
    val tier: CertificateTier?,
    val earnedEpochDay: Int?,
) {
    val topic: MathTopic = unit.topic

    val lessonsTotal: Int = unit.lessons.size

    val hasCertificate: Boolean = tier != null

    val allLessonsDone: Boolean = lessonsCompleted >= lessonsTotal

    companion object {
        /** Empty progress, for previews and for a unit never opened. */
        fun empty(unit: LearnUnit): LearnUnitProgress = LearnUnitProgress(unit, 0, null, null, null)
    }
}

/** A whole grade band rolled up for the Learn menu and the main-menu tiles. */
@Immutable
data class LearnGradeProgress(
    val level: GradeLevel,
    val lessonsCompleted: Int,
    val lessonsTotal: Int,
    val certificates: Int,
    val unitsTotal: Int,
    /** Best tier earned anywhere in the band, shown as the tile's badge. */
    val bestTier: CertificateTier?,
) {
    val allCertificatesEarned: Boolean = unitsTotal > 0 && certificates >= unitsTotal

    companion object {
        fun empty(level: GradeLevel): LearnGradeProgress {
            val units = LearnCatalog.units(level)
            return LearnGradeProgress(
                level = level,
                lessonsCompleted = 0,
                lessonsTotal = units.sumOf { it.lessons.size },
                certificates = 0,
                unitsTotal = units.size,
                bestTier = null,
            )
        }
    }
}
