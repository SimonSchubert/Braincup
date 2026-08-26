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
import braincup.composeapp.generated.resources.learn_try_again
import braincup.composeapp.generated.resources.learn_try_again_hint
import braincup.composeapp.generated.resources.learn_your_answer
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.MathText
import com.inspiredandroid.braincup.ui.components.NumberPadWithInput
import com.inspiredandroid.braincup.ui.components.PrimaryActionButton
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.PrismTile
import com.inspiredandroid.braincup.ui.components.ProgressDots
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.hoverHand
import com.inspiredandroid.braincup.ui.components.learn.LearnVisualCanvas
import com.inspiredandroid.braincup.ui.components.learn.VisualAnswer
import com.inspiredandroid.braincup.ui.components.withGroupColors
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreen
import org.jetbrains.compose.resources.stringResource

/**
 * How wide lesson content is allowed to grow. Everything on the step shares one measure so a
 * desktop window does not stretch the prose, the options and the diagram to three different
 * widths; the diagram is capped tighter still, because its figures are laid out from the canvas
 * width and a very wide canvas draws counters and hops far larger than the text beside them.
 */
private val LessonContentWidth = 480.dp
private val LessonVisualWidth = 420.dp
private val LessonVisualHeight = 180.dp

/**
 * How far the learner has got with the question on screen.
 *
 * A wrong answer is not an end state: the step stays open, the miss is marked, and they try again.
 * Nothing moves on until [Correct], so a lesson can never be walked past without understanding it.
 * [Missed] carries every option already ruled out so they all stay struck through, and [firstTry]
 * remembers whether the first attempt landed, which is what the lesson score counts.
 */
private sealed interface StepAnswer {
    data object Unanswered : StepAnswer

    /** Options (or typed values) tried and rejected so far, in order. Never empty. */
    data class Missed(val attempts: List<String>) : StepAnswer

    data class Correct(val firstTry: Boolean) : StepAnswer
}

/** The value the learner last put forward, for the figure to mark, or null before any attempt. */
private fun StepAnswer.lastAttempt(): String? = when (this) {
    StepAnswer.Unanswered -> null
    is StepAnswer.Missed -> attempts.last()
    is StepAnswer.Correct -> null
}

@Composable
fun LearnLessonScreen(
    lessonId: String,
    storage: UserStorage,
    onNextLesson: (lessonId: String) -> Unit,
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
) {
    var stepIndex by remember(lesson.id) { mutableIntStateOf(0) }
    var correctCount by remember(lesson.id) { mutableIntStateOf(0) }
    var answer by remember(lesson.id) { mutableStateOf<StepAnswer>(StepAnswer.Unanswered) }
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
        typedAnswer = ""
        revealedLines = 0
    }

    /** Records an attempt. The score counts only answers that landed first time. */
    fun submit(attempt: String, isCorrect: Boolean) {
        val previous = (answer as? StepAnswer.Missed)?.attempts.orEmpty()
        answer = if (isCorrect) {
            if (previous.isEmpty()) correctCount++
            StepAnswer.Correct(firstTry = previous.isEmpty())
        } else {
            StepAnswer.Missed(previous + attempt)
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
                    answer = answer,
                    onSelect = { index ->
                        if (answer !is StepAnswer.Correct) {
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
    answer: StepAnswer,
    typedAnswer: String,
    revealedLines: Int,
    onRevealLine: () -> Unit,
    onCheckNumeric: () -> Unit,
    onContinue: () -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // The bar spans the window, the button inside it does not: stretched edge to edge on a desktop
    // window it stops reading as a button at all.
    val button = Modifier.widthIn(max = LessonContentWidth).fillMaxWidth()
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
            step is LessonStep.Numeric && answer !is StepAnswer.Correct -> PrimaryActionButton(
                onClick = { if (typedAnswer.isNotBlank()) onCheckNumeric() },
                value = stringResource(
                    if (answer is StepAnswer.Missed) Res.string.learn_try_again else Res.string.learn_check,
                ),
                modifier = button,
            )

            // A choice step is answered by tapping an option, so it shows no button of its own.
            step is LessonStep.Choice && answer !is StepAnswer.Correct -> Unit

            else -> PrimaryActionButton(
                onClick = onContinue,
                value = stringResource(Res.string.learn_continue),
                modifier = button,
            )
        }
    }
}

/**
 * The step's diagram, on a panel of its own so it reads as a figure rather than as marks floating
 * on the page, and so the tap-to-replay target has a visible edge.
 */
@Composable
private fun LessonVisual(
    visual: LearnVisual,
    modifier: Modifier = Modifier,
    answer: VisualAnswer? = null,
) {
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .widthIn(max = LessonVisualWidth)
            .fillMaxWidth()
            .height(LessonVisualHeight),
    ) {
        LearnVisualCanvas(
            visual = visual,
            modifier = Modifier.fillMaxSize().padding(12.dp),
            answer = answer,
        )
    }
}

/**
 * The value the figure should point at: the one the learner last put forward. Only numbers reach
 * the figure, so an option like "the orange one" simply leaves it unmarked.
 */
private fun StepAnswer.visualAnswer(correctValue: String): VisualAnswer? = when (this) {
    StepAnswer.Unanswered -> null
    is StepAnswer.Correct -> correctValue.trim().toIntOrNull()?.let { VisualAnswer(it, correct = true) }
    is StepAnswer.Missed -> attempts.last().trim().toIntOrNull()?.let { VisualAnswer(it, correct = false) }
}

@Composable
private fun ConceptStep(step: LessonStep.Concept) {
    step.visual?.let {
        LessonVisual(it, modifier = Modifier.padding(bottom = 16.dp))
    }
    LessonText(
        text = step.body,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = LessonContentWidth),
    )
    step.formula?.let { formula ->
        Spacer(Modifier.height(16.dp))
        FormulaCard(formula)
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
private fun FormulaCard(formula: String) {
    // The formula is the line the step is teaching, so it carries the brand colour and full-weight
    // ink rather than the muted tone the supporting cards use.
    PrismCard(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.widthIn(max = LessonContentWidth).fillMaxWidth(),
    ) {
        MathText(
            text = formula,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = Primary,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            fractionSlash = true,
        )
    }
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
    FormulaCard(
        if (worked && finishesInPlace) step.problem.replace("?", "{b:${step.result}}") else step.problem,
    )
    Spacer(Modifier.height(16.dp))
    step.visual?.let {
        LessonVisual(it, modifier = Modifier.padding(bottom = 16.dp))
    }
    step.lines.take(revealedLines).forEach { line ->
        PrismCard(
            face = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = LessonContentWidth).fillMaxWidth().padding(vertical = 4.dp),
        ) {
            LessonText(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
            )
        }
    }
    AnimatedVisibility(visible = worked && !finishesInPlace) {
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
private fun ColumnScope.ChoiceStep(
    step: LessonStep.Choice,
    answer: StepAnswer,
    onSelect: (Int) -> Unit,
) {
    step.visual?.let {
        LessonVisual(
            visual = it,
            modifier = Modifier.padding(bottom = 16.dp),
            answer = answer.visualAnswer(step.options[step.correctIndex]),
        )
    }
    QuestionHeading(
        formula = step.formula,
        question = step.question,
        solved = (answer as? StepAnswer.Correct)?.let { step.options[step.correctIndex] },
    )
    Spacer(Modifier.height(16.dp))

    val missed = (answer as? StepAnswer.Missed)?.attempts.orEmpty()
    step.options.forEachIndexed { index, option ->
        OptionTile(
            label = option,
            state = when {
                answer is StepAnswer.Correct && index == step.correctIndex -> OptionState.CORRECT
                option in missed -> OptionState.WRONG
                answer is StepAnswer.Correct -> OptionState.MUTED
                else -> OptionState.IDLE
            },
            onClick = { onSelect(index) },
        )
        Spacer(Modifier.height(8.dp))
    }
    when (answer) {
        is StepAnswer.Correct -> FeedbackCard(step.explanation)
        is StepAnswer.Missed -> RetryNote()
        StepAnswer.Unanswered -> Unit
    }
}

/**
 * The question itself. When the step carries a formula that leads, in the same card the teaching
 * steps use for theirs, and the prose drops underneath to say how to read the picture.
 *
 * [solved] is the answer, once the learner has it. It takes the place of the question mark in the
 * formula, so the sum they were asked finishes in front of them rather than being restated
 * somewhere else, and it arrives in the same green the correct option turns.
 */
@Composable
private fun QuestionHeading(formula: String?, question: String, solved: String? = null) {
    Column(
        modifier = Modifier.widthIn(max = LessonContentWidth).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (formula != null) {
            FormulaCard(if (solved == null) formula else formula.replace("?", "{b:$solved}"))
            Spacer(Modifier.height(10.dp))
        }
        LessonText(
            text = question,
            // With a formula above it the prose is a supporting line; without one it is the question.
            style = if (formula == null) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
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
        modifier = Modifier.widthIn(max = LessonContentWidth).padding(top = 8.dp),
    )
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
        LessonVisual(
            visual = it,
            modifier = Modifier.padding(bottom = 16.dp),
            answer = answer.visualAnswer(step.answer),
        )
    }
    QuestionHeading(
        formula = step.formula,
        question = step.question,
        solved = (answer as? StepAnswer.Correct)?.let { step.answer },
    )
    if (answer is StepAnswer.Correct) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.learn_your_answer),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = typedAnswer,
            style = MaterialTheme.typography.headlineSmall,
            color = SuccessGreen,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        FeedbackCard(step.explanation)
    } else {
        // Keyed so the pad's buffer starts empty on a new question, and again after a miss so the
        // rejected number is cleared rather than needing backspacing away.
        val missCount = (answer as? StepAnswer.Missed)?.attempts?.size ?: 0
        key(stepIndex, missCount) {
            NumberPadWithInput(onInputChange = onInputChange)
        }
        if (answer is StepAnswer.Missed) {
            Spacer(Modifier.height(8.dp))
            RetryNote()
        }
    }
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
            .widthIn(max = LessonContentWidth)
            .fillMaxWidth()
            .hoverHand(state == OptionState.IDLE),
        onClick = { if (state == OptionState.IDLE) onClick() },
    ) {
        MathText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (state == OptionState.MUTED) contentColor.copy(alpha = 0.6f) else contentColor,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            fractionSlash = true,
        )
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
        modifier = Modifier.widthIn(max = LessonContentWidth).fillMaxWidth().padding(top = 8.dp),
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
                style = MaterialTheme.typography.bodyMedium,
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
        if (nextLessonId != null) {
            Spacer(Modifier.height(24.dp))
            PrimaryActionButton(
                onClick = { onNextLesson(nextLessonId) },
                value = stringResource(Res.string.learn_next_lesson),
                modifier = Modifier.widthIn(max = LessonContentWidth).fillMaxWidth(),
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
