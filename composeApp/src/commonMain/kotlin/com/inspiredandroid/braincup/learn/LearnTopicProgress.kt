package com.inspiredandroid.braincup.learn

import androidx.compose.runtime.Immutable

/**
 * A topic's state for the menus: how far through its lessons the learner is, and the best test
 * result they have recorded. [grade] is null until the topic test has been passed at least once.
 */
@Immutable
data class LearnTopicProgress(
    val topic: MathTopic,
    val lessonsCompleted: Int,
    val bestPercent: Int?,
    val grade: CertificateGrade?,
    val earnedEpochDay: Int?,
) {
    val lessonsTotal: Int = LearnCatalog.lessons(topic).size

    val hasCertificate: Boolean = grade != null

    val allLessonsDone: Boolean = lessonsCompleted >= lessonsTotal

    companion object {
        /** Empty progress, for previews and for a topic never opened. */
        fun empty(topic: MathTopic): LearnTopicProgress = LearnTopicProgress(topic, 0, null, null, null)
    }
}
