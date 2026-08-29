package com.inspiredandroid.braincup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import braincup.composeapp.generated.resources.learn_revealed_answer
import braincup.composeapp.generated.resources.learn_show_answer
import braincup.composeapp.generated.resources.learn_take_test
import braincup.composeapp.generated.resources.learn_test_intro
import braincup.composeapp.generated.resources.learn_try_again
import braincup.composeapp.generated.resources.learn_try_again_hint
import braincup.composeapp.generated.resources.learn_your_answer
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.CatalogText
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnLesson
import com.inspiredandroid.braincup.learn.LessonStep
import com.inspiredandroid.braincup.learn.isNotation
import com.inspiredandroid.braincup.learn.resolve
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.NumberPadWithInput
import com.inspiredandroid.braincup.ui.components.PrismCard
import com.inspiredandroid.braincup.ui.components.ProgressDots
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import com.inspiredandroid.braincup.ui.components.XpGainedChip
import com.inspiredandroid.braincup.ui.components.learn.FigureRoles
import com.inspiredandroid.braincup.ui.components.learn.LearnAnswerCard
import com.inspiredandroid.braincup.ui.components.learn.LearnContentWidth
import com.inspiredandroid.braincup.ui.components.learn.LearnFigurePanel
import com.inspiredandroid.braincup.ui.components.learn.LearnFormulaCard
import com.inspiredandroid.braincup.ui.components.learn.LearnOptionState
import com.inspiredandroid.braincup.ui.components.learn.LearnOptionTile
import com.inspiredandroid.braincup.ui.components.learn.LearnPrimaryButton
import com.inspiredandroid.braincup.ui.components.learn.LearnResultColumn
import com.inspiredandroid.braincup.ui.components.learn.LearnStepColumn
import com.inspiredandroid.braincup.ui.components.learn.LearnText
import com.inspiredandroid.braincup.ui.components.learn.VisualAnswer
import com.inspiredandroid.braincup.ui.components.learn.roles
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.PrimaryContainer
import com.inspiredandroid.braincup.ui.theme.SuccessGreenOnContainer
import org.jetbrains.compose.resources.stringResource

/**
 * How far the learner has got with the question on screen.
 *
 * A wrong answer is not an end state: the step stays open, the miss is marked, and they try again.
 * [Missed] carries every option already ruled out so they all stay struck through, and [firstTry]
 * remembers whether the first attempt landed, which is what the lesson score counts.
 *
 * Nothing moves on until the answer is on the screen, so a lesson cannot be walked through
 * without ever meeting one - but after [RevealAfterMisses] misses it can arrive as [Revealed]
 * rather than [Correct], because a learner who cannot get there has to be able to read the answer
 * and carry on.
 */
sealed interface LessonAnswer {
    data object Unanswered : LessonAnswer

    /** Options (or typed values) tried and rejected so far, in order. Never empty. */
    data class Missed(val attempts: List<String>) : LessonAnswer

    data class Correct(val firstTry: Boolean) : LessonAnswer

    /**
     * The learner asked for the answer instead of finding it. [attempts] is what they had already
     * ruled out, so those stay struck through beside the one that turns out to be right.
     *
     * The step then reads exactly as a solved one - the sum finishes, the figure marks the value,
     * the explanation opens - because that is the teaching. It only does not count: the score
     * counts answers that landed first time, and this one did not land at all.
     */
    data class Revealed(val attempts: List<String>) : LessonAnswer
}

/**
 * Whether the answer is on the screen, however it got there. What follows from it is the same
 * either way: the sum resolves, the options settle, the explanation opens and Continue appears.
 */
val LessonAnswer.isResolved: Boolean
    get() = this is LessonAnswer.Correct || this is LessonAnswer.Revealed

/** Whatever this answer has already ruled out, so a miss stays struck through after a reveal. */
private val LessonAnswer.ruledOut: List<String>
    get() = when (this) {
        is LessonAnswer.Missed -> attempts
        is LessonAnswer.Revealed -> attempts
        else -> emptyList()
    }

/**
 * How many misses on one question before the way out appears.
 *
 * A step stays open until it is answered, which is what stops a lesson being walked through with
 * nothing worked out. On the number pad that had no floor under it: a learner who cannot get there
 * has ten thousand things to type and no way forward but the back arrow, which drops the lesson.
 * Two misses is where trying again has stopped being trying.
 */
private const val RevealAfterMisses = 2

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
    onCorrectAnswer: () -> Unit,
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
        onCorrectAnswer = onCorrectAnswer,
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
    /** Fired the moment an answer lands, so the device confirms it the way a game does. */
    onCorrectAnswer: () -> Unit = {},
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
            onCorrectAnswer()
            LessonAnswer.Correct(firstTry = previous.isEmpty())
        } else {
            LessonAnswer.Missed(previous + attempt)
        }
    }

    /** Hands the answer over. Never scored: the question was not worked out, it was read. */
    fun reveal() {
        answer = LessonAnswer.Revealed((answer as? LessonAnswer.Missed)?.attempts.orEmpty())
    }

    AppScaffold(
        title = stringResource(lesson.title),
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
                is LessonStep.Choice -> {
                    // Answers are recorded as the text the learner tapped, so the options are
                    // resolved once here and the step below works in plain strings.
                    val options = step.options.map { it.resolve() }
                    ChoiceStep(
                        step = step,
                        options = options,
                        answer = answer,
                        onSelect = { index ->
                            if (!answer.isResolved) {
                                submit(options[index], index == step.correctIndex)
                            }
                        },
                    )
                }

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
            // Offered once trying again has stopped being trying. Nothing before that: a first
            // miss is a slip, and a way out on the screen from the start is a way past the step.
            onReveal = { reveal() }.takeIf {
                (answer as? LessonAnswer.Missed)?.attempts?.size?.let { it >= RevealAfterMisses } == true
            },
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
    /** Non-null once the step has been missed often enough to offer the answer. */
    onReveal: (() -> Unit)? = null,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp + bottomInset, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            step is LessonStep.Worked && revealedLines < step.lines.size -> LearnPrimaryButton(
                onClick = onRevealLine,
                value = stringResource(Res.string.learn_next_line),
            )

            // Continue appears only once the answer is on the screen. Until then the step stays
            // open, so a lesson cannot be walked through without ever meeting one.
            step is LessonStep.Numeric && !answer.isResolved -> LearnPrimaryButton(
                onClick = { if (typedAnswer.isNotBlank()) onCheckNumeric() },
                value = stringResource(
                    if (answer is LessonAnswer.Missed) Res.string.learn_try_again else Res.string.learn_check,
                ),
            )

            // A choice step is answered by tapping an option, so it shows no button of its own.
            step is LessonStep.Choice && !answer.isResolved -> Unit

            else -> LearnPrimaryButton(
                onClick = onContinue,
                value = stringResource(Res.string.learn_continue),
            )
        }

        if (onReveal != null) {
            // Under the primary action and in the quiet face the rest of the app gives up in, not
            // beside it: this is the way out of a step, not one of two things being chosen between.
            Spacer(Modifier.height(8.dp))
            TextPrismButton(
                onClick = onReveal,
                value = stringResource(Res.string.learn_show_answer),
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
    // A revealed answer is still the answer, so the figure marks it the way it marks a found one.
    // What was guessed on the way there is no longer what the picture is about.
    is LessonAnswer.Correct, is LessonAnswer.Revealed ->
        correctValue.trim().toIntOrNull()?.let { VisualAnswer(it, correct = true) }

    is LessonAnswer.Missed -> attempts.last().trim().toIntOrNull()?.let { VisualAnswer(it, correct = false) }
}

@Composable
private fun ConceptStep(step: LessonStep.Concept) {
    step.visual?.let {
        LearnFigurePanel(it, modifier = Modifier.padding(bottom = 16.dp))
    }
    LearnText(
        notation = false,
        text = step.body.resolve(),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = LearnContentWidth),
    )
    step.formula?.let { formula ->
        Spacer(Modifier.height(16.dp))
        LearnFormulaCard(formula.resolve(), roles = step.visual?.roles())
    }
}

@Composable
private fun ColumnScope.WorkedStep(step: LessonStep.Worked, revealedLines: Int) {
    val problem = step.problem.resolve()
    val result = step.result.resolve()
    val lines = step.lines
    val worked = revealedLines >= lines.size
    // A problem asked as an equation finishes where it was asked: the answer lands on the question
    // mark, exactly as it does on a question step, rather than being restated underneath. A problem
    // asked in words has no question mark to land on, so that one still answers on its own line.
    val finishesInPlace = problem.trimEnd().endsWith("= ?")
    // The sum leads, above the figure and in the same card the teaching steps give their formula:
    // it is the thing being worked out, and the diagram under it is the picture of that sum.
    LearnFormulaCard(
        if (worked && finishesInPlace) problem.replace("?", "{c:$result}") else problem,
        roles = step.visual?.roles(),
    )
    Spacer(Modifier.height(16.dp))
    step.visual?.let {
        LearnFigurePanel(it, modifier = Modifier.padding(bottom = 16.dp))
    }
    lines.take(revealedLines).forEach { line ->
        PrismCard(
            face = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth().padding(vertical = 4.dp),
        ) {
            LearnText(
                text = line.resolve(),
                // The same measure a Concept step's prose reads at. A worked line is teaching text
                // doing the same job, and the display face is heavy enough that a step down in
                // size reads as a footnote rather than as the explanation. A line that is an
                // equation rather than a sentence takes the number face, like every other equation
                // in the section.
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                notation = line.isNotation,
            )
        }
    }
    AnimatedVisibility(visible = worked && !finishesInPlace) {
        Column {
            Spacer(Modifier.height(8.dp))
            LearnAnswerCard(stringResource(Res.string.learn_answer_label), result)
        }
    }
}

@Composable
private fun ColumnScope.ChoiceStep(
    step: LessonStep.Choice,
    options: List<String>,
    answer: LessonAnswer,
    onSelect: (Int) -> Unit,
) {
    val correct = options[step.correctIndex]
    step.visual?.let {
        LearnFigurePanel(
            visual = it,
            modifier = Modifier.padding(bottom = 16.dp),
            answer = answer.visualAnswer(correct),
        )
    }
    val (formula, prose) = questionHeadingParts(step.formula, step.question)
    QuestionHeading(
        formula = formula,
        question = prose,
        solved = correct.takeIf { answer.isResolved },
        roles = step.visual?.roles(),
    )
    Spacer(Modifier.height(16.dp))

    val missed = answer.ruledOut
    options.forEachIndexed { index, option ->
        LearnOptionTile(
            label = option,
            state = when {
                answer.isResolved && index == step.correctIndex -> LearnOptionState.CORRECT
                option in missed -> LearnOptionState.WRONG
                answer.isResolved -> LearnOptionState.DIMMED
                else -> LearnOptionState.NORMAL
            },
            onClick = { onSelect(index) },
            notation = step.options[index].isNotation,
        )
        Spacer(Modifier.height(8.dp))
    }
    when (answer) {
        is LessonAnswer.Correct -> FeedbackCard(step.explanation.resolve(), revealed = false)
        is LessonAnswer.Revealed -> FeedbackCard(step.explanation.resolve(), revealed = true)
        is LessonAnswer.Missed -> RetryNote()
        LessonAnswer.Unanswered -> Unit
    }
}

/**
 * How a question step's heading splits: the equation that leads on the formula card, and the prose
 * underneath saying how to read the picture.
 *
 * A step usually carries both. Some carry only a question, and when that question is itself
 * notation - "√7 x √7 = ?" - it is the sum being asked, not a caption on the figure above it, so
 * it takes the card and leaves no prose behind. Set as loose text it came out in the display face
 * at the supporting size: an equation in Bungee, smaller than every equation around it.
 *
 * Which is which comes off the catalog's own types, never off the characters. See
 * [isNotation][com.inspiredandroid.braincup.learn.isNotation].
 */
@Composable
private fun questionHeadingParts(
    formula: CatalogText?,
    question: CatalogText,
): Pair<String?, String?> = if (formula == null && question.isNotation) {
    question.resolve() to null
} else {
    formula?.resolve() to question.resolve()
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
private fun QuestionHeading(
    formula: String?,
    question: String?,
    solved: String? = null,
    roles: FigureRoles? = null,
) {
    Column(
        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (formula != null) {
            LearnFormulaCard(
                if (solved == null) formula else formula.replace("?", "{c:$solved}"),
                roles = roles,
            )
            if (question != null) Spacer(Modifier.height(10.dp))
        }
        if (question != null) {
            LearnText(
                notation = false,
                text = question,
                // With a formula above it the prose is a supporting line; without one it is the
                // question. Both read at one measure and it is the colour that demotes the
                // supporting one, because shrinking the display face as well left it too small to
                // read comfortably.
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
    val (formula, prose) = questionHeadingParts(step.formula, step.question)
    val revealed = answer is LessonAnswer.Revealed
    QuestionHeading(
        formula = formula,
        question = prose,
        solved = step.answer.takeIf { answer.isResolved },
        roles = step.visual?.roles(),
    )
    if (answer.isResolved) {
        // Only when the question had nowhere to resolve. A formula ending in "= ?" already
        // finishes in front of the learner, in the answer green, so reading the same number back
        // to them underneath it is the number twice and the card stack once too often.
        if (formula?.trimEnd()?.endsWith("= ?") != true) {
            Spacer(Modifier.height(12.dp))
            // After a reveal there is no answer of theirs to read back: the pad holds whatever
            // they last got wrong, so the card carries the answer itself.
            LearnAnswerCard(
                label = stringResource(
                    if (revealed) Res.string.learn_answer_label else Res.string.learn_your_answer,
                ),
                value = if (revealed) step.answer else typedAnswer,
            )
        }
        Spacer(Modifier.height(8.dp))
        FeedbackCard(step.explanation.resolve(), revealed = revealed)
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
 * Shown once the answer is on the screen. There is no wrong-answer variant: a miss leaves the step
 * open and shows [RetryNote] instead, so this card only ever explains an answer the learner is
 * now looking at - one they found, or, after [RevealAfterMisses] misses, one they asked for. Only
 * the heading tells those apart, because the explanation is the same teaching either way.
 *
 * Face and ink are both brand-pinned rather than taken from the scheme. Material You resolves
 * `primaryContainer` to whatever the device wallpaper suggests, which on some phones is a pale
 * grey, and the text underneath it was inheriting the ambient near-white content colour: white on
 * pale grey. Pinning both halves of the pair keeps them legible on every device and theme.
 */
@Composable
private fun FeedbackCard(explanation: String, revealed: Boolean) {
    PrismCard(
        face = PrimaryContainer,
        modifier = Modifier.widthIn(max = LearnContentWidth).fillMaxWidth().padding(top = 8.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(
                    if (revealed) Res.string.learn_revealed_answer else Res.string.learn_correct,
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                // Green, because green means "the answer" everywhere else in the section: the tile
                // that was just tapped, the value the figure marks, the `{c:}` the formula
                // resolves to. Saying "Correct!" in the card's own purple made this the one place
                // correctness had a colour of its own. A revealed answer is still the answer, so
                // it keeps the colour and only changes what the heading claims.
                color = SuccessGreenOnContainer,
            )
            Spacer(Modifier.height(4.dp))
            LearnText(
                notation = false,
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
            LearnPrimaryButton(
                onClick = { onNextLesson(nextLessonId) },
                value = stringResource(Res.string.learn_next_lesson),
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
            LearnPrimaryButton(
                onClick = onTakeTest,
                value = stringResource(Res.string.learn_take_test),
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
