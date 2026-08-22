package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_answer_label
import braincup.composeapp.generated.resources.learn_check
import braincup.composeapp.generated.resources.learn_continue
import braincup.composeapp.generated.resources.learn_correct
import braincup.composeapp.generated.resources.learn_incorrect
import braincup.composeapp.generated.resources.learn_lesson_complete_title
import braincup.composeapp.generated.resources.learn_lesson_finish
import braincup.composeapp.generated.resources.learn_lesson_score
import braincup.composeapp.generated.resources.learn_next_lesson
import braincup.composeapp.generated.resources.learn_next_line
import braincup.composeapp.generated.resources.learn_your_answer
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.LearnVisualCanvas
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.NumberPadWithInput
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.ProgressDots
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

/** Answer state of the step currently on screen. */
private sealed interface StepAnswer {
    data object Unanswered : StepAnswer

    data class Answered(val isCorrect: Boolean) : StepAnswer
}

@Composable
fun LearnLessonScreen(
    lessonId: String,
    storage: UserStorage,
    onDone: () -> Unit,
    onNextLesson: (lessonId: String) -> Unit,
    onBack: () -> Unit,
) {
    val lesson = remember(lessonId) { LearnCatalog.lessonById(lessonId) } ?: return
    val nextLessonId = remember(lessonId) {
        val siblings = LearnCatalog.lessons(lesson.topic)
        siblings.getOrNull(siblings.indexOfFirst { it.id == lesson.id } + 1)?.id
    }
    var xpGained by remember(lessonId) { mutableIntStateOf(0) }

    LearnLessonScreenContent(
        lesson = lesson,
        nextLessonId = nextLessonId,
        xpGained = xpGained,
        onLessonCompleted = { xpGained = storage.completeLearnLesson(lesson.id).xpGained },
        onDone = onDone,
        onNextLesson = onNextLesson,
        onBack = onBack,
    )
}

@Composable
fun LearnLessonScreenContent(
    lesson: LearnLesson,
    nextLessonId: String?,
    xpGained: Int,
    onLessonCompleted: () -> Unit,
    onDone: () -> Unit,
    onNextLesson: (lessonId: String) -> Unit,
    onBack: () -> Unit,
) {
    var stepIndex by remember(lesson.id) { mutableIntStateOf(0) }
    var correctCount by remember(lesson.id) { mutableIntStateOf(0) }
    var answer by remember(lesson.id) { mutableStateOf<StepAnswer>(StepAnswer.Unanswered) }
    var selectedOption by remember(lesson.id) { mutableStateOf<Int?>(null) }
    var typedAnswer by remember(lesson.id) { mutableStateOf("") }
    var revealedLines by remember(lesson.id) { mutableIntStateOf(0) }

    val finished = stepIndex >= lesson.steps.size

    // Awards XP exactly once, the first time the learner reaches the end of this lesson.
    LaunchedEffect(lesson.id, finished) {
        if (finished) onLessonCompleted()
    }

    val advance = {
        stepIndex++
        answer = StepAnswer.Unanswered
        selectedOption = null
        typedAnswer = ""
        revealedLines = 0
    }

    AppScaffold(
        title = lesson.title,
        onBack = onBack,
        scrollable = false,
    ) {
        if (finished) {
            LessonCompleteContent(
                lesson = lesson,
                correctCount = correctCount,
                xpGained = xpGained,
                nextLessonId = nextLessonId,
                onDone = onDone,
                onNextLesson = onNextLesson,
                modifier = Modifier.weight(1f),
            )
            return@AppScaffold
        }

        val step = lesson.steps[stepIndex]

        ProgressDots(
            currentIndex = stepIndex,
            total = lesson.steps.size,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (step) {
                is LessonStep.Concept -> ConceptStep(step)
                is LessonStep.Worked -> WorkedStep(step, revealedLines)
                is LessonStep.Choice -> ChoiceStep(
                    step = step,
                    selectedOption = selectedOption,
                    answer = answer,
                    onSelect = { index ->
                        if (answer is StepAnswer.Unanswered) {
                            selectedOption = index
                            val isCorrect = index == step.correctIndex
                            if (isCorrect) correctCount++
                            answer = StepAnswer.Answered(isCorrect)
                        }
                    },
                )

                is LessonStep.Numeric -> NumericStep(
                    step = step,
                    stepIndex = stepIndex,
                    typedAnswer = typedAnswer,
                    answer = answer,
                    onInputChange = { typedAnswer = it },
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        LessonActionBar(
            step = step,
            answer = answer,
            typedAnswer = typedAnswer,
            revealedLines = revealedLines,
            onRevealLine = { revealedLines++ },
            onCheckNumeric = {
                val correct = LearnCatalog.matchesNumericAnswer(
                    typedAnswer,
                    (step as LessonStep.Numeric).answer,
                )
                if (correct) correctCount++
                answer = StepAnswer.Answered(correct)
            },
            onContinue = advance,
        )
    }
}

@Composable
private fun ColumnScope.LessonActionBar(
    step: LessonStep,
    answer: StepAnswer,
    typedAnswer: String,
    revealedLines: Int,
    onRevealLine: () -> Unit,
    onCheckNumeric: () -> Unit,
    onContinue: () -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp + bottomInset, top = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            step is LessonStep.Worked && revealedLines < step.lines.size -> PrimaryActionButton(
                onClick = onRevealLine,
                value = stringResource(Res.string.learn_next_line),
                modifier = Modifier.fillMaxWidth(),
            )

            step is LessonStep.Numeric && answer is StepAnswer.Unanswered -> PrimaryActionButton(
                onClick = { if (typedAnswer.isNotBlank()) onCheckNumeric() },
                value = stringResource(Res.string.learn_check),
                modifier = Modifier.fillMaxWidth(),
            )

            // A choice step is answered by tapping an option, so it shows no button until then.
            step is LessonStep.Choice && answer is StepAnswer.Unanswered -> Unit

            else -> PrimaryActionButton(
                onClick = onContinue,
                value = stringResource(Res.string.learn_continue),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ConceptStep(step: LessonStep.Concept) {
    step.visual?.let {
        LearnVisualCanvas(
            visual = it,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 140.dp)
                .height(120.dp)
                .padding(bottom = 8.dp),
        )
    }
    Text(
        text = step.body,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.widthIn(max = 480.dp),
    )
    step.formula?.let { formula ->
        Spacer(Modifier.height(16.dp))
        FormulaCard(formula)
    }
}

@Composable
private fun FormulaCard(formula: String) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
    ) {
        MathText(
            text = formula,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

@Composable
private fun WorkedStep(step: LessonStep.Worked, revealedLines: Int) {
    step.visual?.let {
        LearnVisualCanvas(
            visual = it,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 8.dp),
        )
    }
    Text(
        text = step.problem,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 480.dp),
    )
    Spacer(Modifier.height(12.dp))
    step.lines.take(revealedLines).forEach { line ->
        PrismCard(
            face = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth().padding(vertical = 4.dp),
        ) {
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
            )
        }
    }
    AnimatedVisibility(visible = revealedLines >= step.lines.size) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.learn_answer_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = step.result,
                style = MaterialTheme.typography.headlineSmall,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ChoiceStep(
    step: LessonStep.Choice,
    selectedOption: Int?,
    answer: StepAnswer,
    onSelect: (Int) -> Unit,
) {
    step.visual?.let {
        LearnVisualCanvas(
            visual = it,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 8.dp),
        )
    }
    Text(
        text = step.question,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 480.dp),
    )
    Spacer(Modifier.height(16.dp))
    step.options.forEachIndexed { index, option ->
        OptionTile(
            label = option,
            state = optionState(index, step.correctIndex, selectedOption, answer),
            onClick = { onSelect(index) },
        )
        Spacer(Modifier.height(8.dp))
    }
    if (answer is StepAnswer.Answered) {
        FeedbackCard(isCorrect = answer.isCorrect, explanation = step.explanation)
    }
}

@Composable
private fun NumericStep(
    step: LessonStep.Numeric,
    stepIndex: Int,
    typedAnswer: String,
    answer: StepAnswer,
    onInputChange: (String) -> Unit,
) {
    step.visual?.let {
        LearnVisualCanvas(
            visual = it,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 8.dp),
        )
    }
    Text(
        text = step.question,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 480.dp),
    )
    if (answer is StepAnswer.Answered) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.learn_your_answer),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = typedAnswer,
            style = MaterialTheme.typography.headlineSmall,
            color = if (answer.isCorrect) SuccessGreen else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        FeedbackCard(isCorrect = answer.isCorrect, explanation = step.explanation)
    } else {
        // Keyed so the pad's internal buffer starts empty on every new question.
        key(stepIndex) {
            NumberPadWithInput(onInputChange = onInputChange)
        }
    }
}

private fun optionState(
    index: Int,
    correctIndex: Int,
    selectedOption: Int?,
    answer: StepAnswer,
): OptionState = when {
    answer is StepAnswer.Unanswered -> OptionState.IDLE
    index == correctIndex -> OptionState.CORRECT
    index == selectedOption -> OptionState.WRONG
    else -> OptionState.MUTED
}

private enum class OptionState { IDLE, CORRECT, WRONG, MUTED }

@Composable
private fun OptionTile(
    label: String,
    state: OptionState,
    onClick: () -> Unit,
) {
    val face = when (state) {
        OptionState.IDLE -> MaterialTheme.colorScheme.surfaceVariant
        OptionState.CORRECT -> SuccessGreen
        OptionState.WRONG -> MaterialTheme.colorScheme.error
        OptionState.MUTED -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (state) {
        OptionState.CORRECT, OptionState.WRONG -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    PrismTile(
        face = face,
        isClickable = state == OptionState.IDLE,
        modifier = Modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .hoverHand(state == OptionState.IDLE),
        onClick = onClick,
    ) {
        MathText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (state == OptionState.MUTED) contentColor.copy(alpha = 0.6f) else contentColor,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}

@Composable
private fun FeedbackCard(isCorrect: Boolean, explanation: String) {
    PrismCard(
        face = if (isCorrect) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth().padding(top = 8.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(if (isCorrect) Res.string.learn_correct else Res.string.learn_incorrect),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) SuccessGreen else Primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun LessonCompleteContent(
    lesson: LearnLesson,
    correctCount: Int,
    xpGained: Int,
    nextLessonId: String?,
    onDone: () -> Unit,
    onNextLesson: (lessonId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.learn_lesson_complete_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.learn_lesson_score, correctCount, lesson.questionCount),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (xpGained > 0) {
            Spacer(Modifier.height(16.dp))
            XpGainedChip(xpGained = xpGained)
        }
        Spacer(Modifier.height(24.dp))
        if (nextLessonId != null) {
            PrimaryActionButton(
                onClick = { onNextLesson(nextLessonId) },
                value = stringResource(Res.string.learn_next_lesson),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }
        PrimaryActionButton(
            onClick = onDone,
            value = stringResource(Res.string.learn_lesson_finish),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@DevicePreviews
@Composable
private fun LearnLessonScreenPreview() {
    ScreenPreviewHost {
        LearnLessonScreenContent(
            lesson = LearnCatalog.lessons(com.inspiredandroid.braincup.learn.MathTopic.GEOMETRY).first(),
            nextLessonId = null,
            xpGained = 0,
            onLessonCompleted = {},
            onDone = {},
            onNextLesson = {},
            onBack = {},
        )
    }
}
