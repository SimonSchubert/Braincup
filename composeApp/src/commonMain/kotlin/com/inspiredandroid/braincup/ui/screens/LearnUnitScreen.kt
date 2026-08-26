package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_ages
import braincup.composeapp.generated.resources.learn_certificate_awarded
import braincup.composeapp.generated.resources.learn_certificate_view
import braincup.composeapp.generated.resources.learn_lesson_number
import braincup.composeapp.generated.resources.learn_lesson_progress
import braincup.composeapp.generated.resources.learn_lessons_header
import braincup.composeapp.generated.resources.learn_retake_test
import braincup.composeapp.generated.resources.learn_take_test
import braincup.composeapp.generated.resources.learn_test_intro
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnUnitProgress
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.CertificateMedal
import com.inspiredandroid.braincup.ui.components.ChunkyCheck
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.learn.LearnContentWidth
import com.inspiredandroid.braincup.ui.components.learn.learnContainerColors
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.stringResource

@Composable
fun LearnUnitScreen(
    unit: LearnUnit,
    storage: UserStorage,
    onLessonSelected: (lessonId: String) -> Unit,
    onTakeTest: () -> Unit,
    onViewCertificate: () -> Unit,
    onBack: () -> Unit,
) {
    val completed = remember(storage, unit) { storage.getCompletedLearnLessonIds().toImmutableSet() }
    val progress = remember(storage, unit) { storage.getLearnUnitProgress(unit) }
    LearnUnitScreenContent(
        progress = progress,
        completedLessonIds = completed,
        onLessonSelected = onLessonSelected,
        onTakeTest = onTakeTest,
        onViewCertificate = onViewCertificate,
        onBack = onBack,
    )
}

@Composable
fun LearnUnitScreenContent(
    progress: LearnUnitProgress,
    completedLessonIds: ImmutableSet<String>,
    onLessonSelected: (lessonId: String) -> Unit,
    onTakeTest: () -> Unit,
    onViewCertificate: () -> Unit,
    onBack: () -> Unit,
) {
    val unit = progress.unit
    val lessons = unit.lessons
    val quiz = unit.quiz

    AppScaffold(
        title = unit.title,
        onBack = onBack,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "header") {
                Column(
                    modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        // Topic first, then the age band this rung is normally taught at: the
                        // learner arrived here from the topic's ladder, not from a school year.
                        text = stringResource(unit.topic.titleRes) + " · " +
                            stringResource(Res.string.learn_ages, unit.level.ageRange),
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = unit.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            Res.string.learn_lesson_progress,
                            progress.lessonsCompleted,
                            progress.lessonsTotal,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (progress.hasCertificate) {
                item(key = "certificate") {
                    EarnedCertificateCard(
                        onClick = onViewCertificate,
                        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
                    )
                }
            }

            item(key = "lessons_header") {
                Text(
                    text = stringResource(Res.string.learn_lessons_header),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .widthIn(max = LearnContentWidth)
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }

            itemsIndexed(lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
                LessonRow(
                    lesson = lesson,
                    index = index,
                    isCompleted = lesson.id in completedLessonIds,
                    onClick = { onLessonSelected(lesson.id) },
                    modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
                )
            }

            item(key = "test") {
                Column(
                    modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.learn_test_intro, quiz.total),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    PrimaryActionButton(
                        onClick = onTakeTest,
                        value = stringResource(
                            if (progress.hasCertificate) Res.string.learn_retake_test else Res.string.learn_take_test,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun EarnedCertificateCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.hoverHand().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CertificateMedal(modifier = Modifier.size(32.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.learn_certificate_awarded),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = stringResource(Res.string.learn_certificate_view),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun LessonRow(
    lesson: LearnLesson,
    index: Int,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (face, ink) = learnContainerColors(isCompleted)
    PrismCard(
        face = face,
        modifier = modifier.hoverHand().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.learn_lesson_number, index + 1),
                    style = MaterialTheme.typography.labelSmall,
                    color = ink.copy(alpha = 0.75f),
                )
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = ink,
                )
                Text(
                    text = lesson.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = ink.copy(alpha = 0.75f),
                )
            }
            if (isCompleted) {
                ChunkyCheck(
                    color = SuccessGreen,
                    modifier = Modifier.padding(start = 8.dp).size(20.dp),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LearnUnitScreenPreview() {
    val unit = LearnCatalog.units(MathTopic.ARITHMETIC).first()
    ScreenPreviewHost {
        LearnUnitScreenContent(
            progress = LearnUnitProgress(
                unit = unit,
                lessonsCompleted = 1,
                earnedEpochDay = 20_000,
            ),
            completedLessonIds = persistentSetOf(unit.lessons.first().id),
            onLessonSelected = {},
            onTakeTest = {},
            onViewCertificate = {},
            onBack = {},
        )
    }
}
