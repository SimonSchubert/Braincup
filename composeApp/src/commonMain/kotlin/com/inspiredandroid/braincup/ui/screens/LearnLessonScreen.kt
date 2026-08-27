package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_answer_label
import braincup.composeapp.generated.resources.learn_check
import braincup.composeapp.generated.resources.learn_continue
import braincup.composeapp.generated.resources.learn_correct
import braincup.composeapp.generated.resources.learn_lesson_complete_title
import braincup.composeapp.generated.resources.learn_lesson_score
import braincup.composeapp.generated.resources.learn_next_lesson
import braincup.composeapp.generated.resources.learn_next_line
import braincup.composeapp.generated.resources.learn_take_test
import braincup.composeapp.generated.resources.learn_test_intro
import braincup.composeapp.generated.resources.learn_try_again
import braincup.composeapp.generated.resources.learn_try_again_hint
import braincup.composeapp.generated.resources.learn_your_answer
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.NumberPadWithInput
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.ProgressDots
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.learn.LearnAnswerCard
import com.inspiredandroid.braincup.ui.components.learn.LearnContentWidth
import com.inspiredandroid.braincup.ui.components.learn.LearnFigurePanel
import com.inspiredandroid.braincup.ui.components.learn.LearnFormulaCard
import com.inspiredandroid.braincup.ui.components.learn.LearnOptionState
import com.inspiredandroid.braincup.ui.components.learn.LearnOptionTile
import com.inspiredandroid.braincup.ui.components.learn.LearnResultColumn
import com.inspiredandroid.braincup.ui.components.learn.LearnStepColumn
import com.inspiredandroid.braincup.ui.components.learn.VisualAnswer
import com.inspiredandroid.braincup.ui.components.withGroupColors
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import org.jetbrains.compose.resources.stringResource

/**
 * How far the learner has got with the question on screen.
 *
 * A wrong answer is not an end state: the step stays open, the miss is marked, and they try again.
 * Nothing moves on until [Correct], so a lesson can never be walked past without understanding it.
 * [Missed] carries every option already ruled out so they all stay struck through, and [firstTry]
 * remembers whether the first attempt landed, which is what the lesson score counts.
 */
sealed interface LessonAnswer {
    data object Unanswered : LessonAnswer

    /** Options (or typed values) tried and rejected so far, in order. Never empty. */
    data class Missed(val attempts: List<String>) : LessonAnswer

    data class Correct(val firstTry: Boolean) : LessonAnswer
}

/**
 * Where a lesson opens.
 *
 * The app always starts a lesson at [Start] and walks it from there; nothing in the running app
 * ever passes anything else. It exists so a preview or a screenshot render can open the screen
 * part-way through, which is the only way to see the states a learner has to answer their way
 * into: the solved formula, the [FeedbackCard], the retry note and the lesson result.
 */
@Immutable
data class LessonScreenState(
    val stepIndex: Int = 0,
    /** How many lines of a [LessonStep.Worked] have been turned over. */
    val revealedLines: Int = 0,
    val answer: LessonAnswer = LessonAnswer.Unanswered,
    /** What is in the number pad's buffer on a [LessonStep.Numeric]. */
    val typedAnswer: String = "",
    /** Questions answered right first time so far, which is what the lesson result reports. */
    val correctCount: Int = 0,
) {
    companion object {
        val Start = LessonScreenState()
    }
}

@Composable
fun LearnLessonScreen(
    lessonId: String,
    storage: UserStorage,
    onNextLesson: (lessonId: String) -> Unit,
    onTakeTest: () -> Unit,
    onBack: () -> Unit,
) {
    val lesson = remember(lessonId) { LearnCatalog.lessonById(lessonId) } ?: return
    val nextLessonId = remember(lessonId) {
        val siblings = LearnCatalog.unitOfLesson(lesson)?.lessons.orEmpty()
        siblings.getOrNull(siblings.indexOfFirst { it.id == lesson.id } + 1)?.id
    }
    var xpGained by remember(lessonId) { mutableIntStateOf(0) }

    LearnLessonScreenContent(
        lesson = lesson,
        nextLessonId = nextLessonId,
        xpGained = xpGained,
        onLessonCompleted = { xpGained = storage.completeLearnLesson(lesson.id).xpGained },
        onNextLesson = onNextLesson,
        onTakeTest = onTakeTest,
        onBack = onBack,
    )
}

@Composable
fun LearnLessonScreenContent(
    lesson: LearnLesson,
    nextLessonId: String?,
    xpGained: Int,
    onLessonCompleted: () -> Unit,
    onNextLesson: (lessonId: String) -> Unit,
    onBack: () -> Unit,
    onTakeTest: (() -> Unit)? = null,
    initialState: LessonScreenState = LessonScreenState.Start,
) {
    var stepIndex by remember(lesson.id) { mutableIntStateOf(initialState.stepIndex) }
    var correctCount by remember(lesson.id) { mutableIntStateOf(initialState.correctCount) }
    var answer by remember(lesson.id) { mutableStateOf(initialState.answer) }
    var typedAnswer by remember(lesson.id) { mutableStateOf(initialState.typedAnswer) }
    var revealedLines by remember(lesson.id) { mutableIntStateOf(initialState.revealedLines) }

    val finished = stepIndex >= lesson.steps.size

    // Awards XP exactly once, the first time the learner reaches the end of this lesson.
    LaunchedEffect(lesson.id, finished) {
        if (finished) onLessonCompleted()
    }

    val advance = {
        stepIndex++
        answer = LessonAnswer.Unanswered
        typedAnswer = ""
        revealedLines = 0
    }

    /** Records an attempt. The score counts only answers that landed first time. */
    fun submit(attempt: String, isCorrect: Boolean) {
        val previous = (answer as? LessonAnswer.Missed)?.attempts.orEmpty()
        answer = if (isCorrect) {
            if (previous.isEmpty()) correctCount++
            LessonAnswer.Correct(firstTry = previous.isEmpty())
        } else {
            LessonAnswer.Missed(previous + attempt)
        }
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
                onNextLesson = onNextLesson,
                onTakeTest = onTakeTest,
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

        LearnStepColumn {
            when (step) {
                is LessonStep.Concept -> ConceptStep(step)
                is LessonStep.Worked -> WorkedStep(step, revealedLines)
                is LessonStep.Choice -> ChoiceStep(
                    step = step,
                    answer = answer,
                    onSelect = { index ->
                        if (answer !is LessonAnswer.Correct) {
                            submit(step.options[index], index == step.correctIndex)
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
                submit(typedAnswer, correct)
            },
            onContinue = advance,
        )
    }
}

@Composable
private fun ColumnScope.LessonActionBar(
    step: LessonStep,
    answer: LessonAnswer,
    typedAnswer: String,
    revealedLines: Int,
    onRevealLine: () -> Unit,
    onCheckNumeric: () -> Unit,
    onContinue: () -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // The bar spans the window, the button inside it does not: stretched edge to edge on a desktop
    // window it stops reading as a button at all.
    val button = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp + bottomInset, top = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            step is LessonStep.Worked && revealedLines < step.lines.size -> PrimaryActionButton(
                onClick = onRevealLine,
                value = stringResource(Res.string.learn_next_line),
                modifier = button,
            )

            // Continue appears only once the question is right. Until then the step stays open, so
            // a lesson cannot be walked through without ever working an answer out.
            step is LessonStep.Numeric && answer !is LessonAnswer.Correct -> PrimaryActionButton(
                onClick = { if (typedAnswer.isNotBlank()) onCheckNumeric() },
                value = stringResource(
                    if (answer is LessonAnswer.Missed) Res.string.learn_try_again else Res.string.learn_check,
                ),
                modifier = button,
            )

            // A choice step is answered by tapping an option, so it shows no button of its own.
            step is LessonStep.Choice && answer !is LessonAnswer.Correct -> Unit

            else -> PrimaryActionButton(
                onClick = onContinue,
                value = stringResource(Res.string.learn_continue),
                modifier = button,
            )
        }
    }
}

/**
 * The value the figure should point at: the one the learner last put forward. Only numbers reach
 * the figure, so an option like "the orange one" simply leaves it unmarked.
 */
private fun LessonAnswer.visualAnswer(correctValue: String): VisualAnswer? = when (this) {
    LessonAnswer.Unanswered -> null
    is LessonAnswer.Correct -> correctValue.trim().toIntOrNull()?.let { VisualAnswer(it, correct = true) }
    is LessonAnswer.Missed -> attempts.last().trim().toIntOrNull()?.let { VisualAnswer(it, correct = false) }
}

@Composable
private fun ConceptStep(step: LessonStep.Concept) {
    step.visual?.let {
        LearnFigurePanel(it, modifier = Modifier.padding(bottom = 16.dp))
    }
    LessonText(
        text = step.body,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = LearnContentWidth),
    )
    step.formula?.let { formula ->
        Spacer(Modifier.height(16.dp))
        LearnFormulaCard(formula)
    }
}

/**
 * Lesson prose, with any `{a:...}` and `{b:...}` numbers tinted to match the dots they name in the
 * figure above: the "6" of "6 needs 4 more" is the same orange as the six dots already in the
 * frame, and the "4" the same green as the ones arriving. Untagged text renders as plain [Text].
 */
@Composable
private fun LessonText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text.withGroupColors(),
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
    )
}

@Composable
private fun ColumnScope.WorkedStep(step: LessonStep.Worked, revealedLines: Int) {
    val worked = revealedLines >= step.lines.size
    // A problem asked as an equation finishes where it was asked: the answer lands on the question
    // mark, exactly as it does on a question step, rather than being restated underneath. A problem
    // asked in words has no question mark to land on, so that one still answers on its own line.
    val finishesInPlace = step.problem.trimEnd().endsWith("= ?")
    // The sum leads, above the figure and in the same card the teaching steps give their formula:
    // it is the thing being worked out, and the diagram under it is the picture of that sum.
    LearnFormulaCard(
        if (worked && finishesInPlace) step.problem.replace("?", "{c:${step.result}}") else step.problem,
    )
    Spacer(Modifier.height(16.dp))
    step.visual?.let {
        LearnFigurePanel(it, modifier = Modifier.padding(bottom = 16.dp))
    }
    step.lines.take(revealedLines).forEach { line ->
        PrismCard(
            face = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth().padding(vertical = 4.dp),
        ) {
            LessonText(
                text = line,
                // The same measure a Concept step's prose reads at. A worked line is teaching text
                // doing the same job, and the display face is heavy enough that a step down in
                // size reads as a footnote rather than as the explanation.
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
            )
        }
    }
    AnimatedVisibility(visible = worked && !finishesInPlace) {
        Column {
            Spacer(Modifier.height(8.dp))
            LearnAnswerCard(stringResource(Res.string.learn_answer_label), step.result)
        }
    }
}

@Composable
private fun ColumnScope.ChoiceStep(
    step: LessonStep.Choice,
    answer: LessonAnswer,
    onSelect: (Int) -> Unit,
) {
    step.visual?.let {
        LearnFigurePanel(
            visual = it,
            modifier = Modifier.padding(bottom = 16.dp),
            answer = answer.visualAnswer(step.options[step.correctIndex]),
        )
    }
    QuestionHeading(
        formula = step.formula,
        question = step.question,
        solved = (answer as? LessonAnswer.Correct)?.let { step.options[step.correctIndex] },
    )
    Spacer(Modifier.height(16.dp))

    val missed = (answer as? LessonAnswer.Missed)?.attempts.orEmpty()
    step.options.forEachIndexed { index, option ->
        LearnOptionTile(
            label = option,
            state = when {
                answer is LessonAnswer.Correct && index == step.correctIndex -> LearnOptionState.CORRECT
                option in missed -> LearnOptionState.WRONG
                answer is LessonAnswer.Correct -> LearnOptionState.MUTED
                else -> LearnOptionState.IDLE
            },
            onClick = { onSelect(index) },
        )
        Spacer(Modifier.height(8.dp))
    }
    when (answer) {
        is LessonAnswer.Correct -> FeedbackCard(step.explanation)
        is LessonAnswer.Missed -> RetryNote()
        LessonAnswer.Unanswered -> Unit
    }
}

/**
 * The question itself. When the step carries a formula that leads, in the same card the teaching
 * steps use for theirs, and the prose drops underneath to say how to read the picture.
 *
 * [solved] is the answer, once the learner has it. It takes the place of the question mark in the
 * formula, so the sum they were asked finishes in front of them rather than being restated
 * somewhere else, and it arrives as `{c:}` - the answer green the correct option turns and the
 * figure marks the value in, so one number reads the same in all three places.
 */
@Composable
private fun QuestionHeading(formula: String?, question: String, solved: String? = null) {
    Column(
        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (formula != null) {
            LearnFormulaCard(if (solved == null) formula else formula.replace("?", "{c:$solved}"))
            Spacer(Modifier.height(10.dp))
        }
        LessonText(
            text = question,
            // With a formula above it the prose is a supporting line; without one it is the
            // question. Both read at one measure and it is the colour that demotes the supporting
            // one, because shrinking the display face as well left it too small to read comfortably.
            style = MaterialTheme.typography.titleMedium,
            color = if (formula == null) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
        )
    }
}

/** Shown after a miss. It says to try again and nothing else, so no hint leaks with it. */
@Composable
private fun RetryNote() {
    Text(
        text = stringResource(Res.string.learn_try_again_hint),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.error,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = LearnContentWidth).padding(top = 8.dp),
    )
}

@Composable
private fun NumericStep(
    step: LessonStep.Numeric,
    stepIndex: Int,
    typedAnswer: String,
    answer: LessonAnswer,
    onInputChange: (String) -> Unit,
) {
    step.visual?.let {
        LearnFigurePanel(
            visual = it,
            modifier = Modifier.padding(bottom = 16.dp),
            answer = answer.visualAnswer(step.answer),
        )
    }
    QuestionHeading(
        formula = step.formula,
        question = step.question,
        solved = (answer as? LessonAnswer.Correct)?.let { step.answer },
    )
    if (answer is LessonAnswer.Correct) {
        // Only when the question had nowhere to resolve. A formula ending in "= ?" already
        // finishes in front of the learner, in the answer green, so reading the same number back
        // to them underneath it is the number twice and the card stack once too often.
        if (step.formula?.trimEnd()?.endsWith("= ?") != true) {
            Spacer(Modifier.height(12.dp))
            LearnAnswerCard(stringResource(Res.string.learn_your_answer), typedAnswer)
        }
        Spacer(Modifier.height(8.dp))
        FeedbackCard(step.explanation)
    } else {
        // Keyed so the pad's buffer starts empty on a new question, and again after a miss so the
        // rejected number is cleared rather than needing backspacing away.
        val missCount = (answer as? LessonAnswer.Missed)?.attempts?.size ?: 0
        key(stepIndex, missCount) {
            NumberPadWithInput(onInputChange = onInputChange)
        }
        if (answer is LessonAnswer.Missed) {
            Spacer(Modifier.height(8.dp))
            RetryNote()
        }
    }
}

/**
 * Shown once the learner gets there. There is no wrong-answer variant any more: a miss leaves the
 * step open and shows [RetryNote] instead, so this card only ever confirms and explains.
 *
 * Face and ink are both brand-pinned rather than taken from the scheme. Material You resolves
 * `primaryContainer` to whatever the device wallpaper suggests, which on some phones is a pale
 * grey, and the text underneath it was inheriting the ambient near-white content colour: white on
 * pale grey. Pinning both halves of the pair keeps them legible on every device and theme.
 */
@Composable
private fun FeedbackCard(explanation: String) {
    PrismCard(
        face = PrimaryContainer,
        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth().padding(top = 8.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.learn_correct),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OnPrimaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            LessonText(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = OnPrimaryContainer,
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
    onNextLesson: (lessonId: String) -> Unit,
    onTakeTest: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    LearnResultColumn(
        title = stringResource(Res.string.learn_lesson_complete_title),
        score = stringResource(Res.string.learn_lesson_score, correctCount, lesson.questionCount),
        scoreStyle = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
        // A lesson result is short enough to sit in the middle of the screen; a test result is
        // not, because the review list unfolds under it.
        verticalArrangement = Arrangement.Center,
    ) {
        if (xpGained > 0) {
            Spacer(Modifier.height(16.dp))
            XpGainedChip(xpGained = xpGained)
        }
        val quiz = if (nextLessonId == null && onTakeTest != null) {
            remember(lesson.id) { LearnCatalog.unitOfLesson(lesson)?.quiz }
        } else {
            null
        }
        if (nextLessonId != null) {
            Spacer(Modifier.height(24.dp))
            PrimaryActionButton(
                onClick = { onNextLesson(nextLessonId) },
                value = stringResource(Res.string.learn_next_lesson),
                modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
            )
        } else if (quiz != null && onTakeTest != null) {
            // The last lesson of a sub-topic ends where the sub-topic itself does: at its test.
            // Finishing here and being sent back to the unit screen to find that button is a step
            // the learner never needs to take.
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.learn_test_intro, quiz.total),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            PrimaryActionButton(
                onClick = onTakeTest,
                value = stringResource(Res.string.learn_take_test),
                modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun LearnLessonScreenPreview() {
    ScreenPreviewHost {
        LearnLessonScreenContent(
            lesson = LearnCatalog.allLessons.first(),
            nextLessonId = null,
            xpGained = 0,
            onLessonCompleted = {},
            onNextLesson = {},
            onBack = {},
        )
    }
}
