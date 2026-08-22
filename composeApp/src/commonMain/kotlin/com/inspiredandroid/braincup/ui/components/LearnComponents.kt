package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_certificate_grade_bronze
import braincup.composeapp.generated.resources.learn_certificate_grade_gold
import braincup.composeapp.generated.resources.learn_certificate_grade_silver
import braincup.composeapp.generated.resources.learn_lesson_progress
import com.inspiredandroid.braincup.learn.CertificateGrade
import com.inspiredandroid.braincup.learn.LearnVisual
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val BronzeMedal = Color(0xFFCD7F32)
private val SilverMedal = Color(0xFFB8BFC6)

/** Medal colour for a certificate tier, used by the badge, the tile and the certificate page. */
fun CertificateGrade.medalColor(): Color = when (this) {
    CertificateGrade.BRONZE -> BronzeMedal
    CertificateGrade.SILVER -> SilverMedal
    CertificateGrade.GOLD -> MedalGold
}

fun CertificateGrade.labelRes(): StringResource = when (this) {
    CertificateGrade.BRONZE -> Res.string.learn_certificate_grade_bronze
    CertificateGrade.SILVER -> Res.string.learn_certificate_grade_silver
    CertificateGrade.GOLD -> Res.string.learn_certificate_grade_gold
}

/** The sketch that stands in for a topic on its tiles. */
fun MathTopic.tileVisual(): LearnVisual = when (this) {
    MathTopic.ARITHMETIC -> LearnVisual.NUMBER_LINE
    MathTopic.MEASUREMENT -> LearnVisual.RULER
    MathTopic.GEOMETRY -> LearnVisual.RIGHT_TRIANGLE
    MathTopic.DATA -> LearnVisual.BAR_CHART
    MathTopic.ALGEBRA -> LearnVisual.BALANCE_SCALE
    MathTopic.TRIGONOMETRY -> LearnVisual.UNIT_CIRCLE
    MathTopic.FUNCTIONS -> LearnVisual.PARABOLA
    MathTopic.CALCULUS -> LearnVisual.AREA_UNDER_CURVE
}

/** A small trophy in the tier's metal, shown wherever a certificate has been earned. */
@Composable
fun CertificateMedal(
    grade: CertificateGrade,
    modifier: Modifier = Modifier,
) {
    PrismTrophy(
        tint = grade.medalColor(),
        modifier = modifier,
    )
}

/**
 * Topic entry used both in the main menu's Learn section and on the Learn screen itself: the
 * topic's sketch over its accent colour, with the lesson progress and any certificate earned.
 */
@Composable
fun LearnTopicTile(
    topic: MathTopic,
    lessonsCompleted: Int,
    lessonsTotal: Int,
    grade: CertificateGrade?,
    onClick: (MathTopic) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = Primary,
        modifier = modifier
            .aspectRatio(1f)
            .hoverHand(),
        onClick = { onClick(topic) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(topic.accentColor)),
                contentAlignment = Alignment.Center,
            ) {
                MaterialTheme(colorScheme = LightColorScheme) {
                    LearnVisualCanvas(
                        visual = topic.tileVisual(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                    )
                }
                if (grade != null) {
                    CertificateMedal(
                        grade = grade,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(22.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(topic.titleRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(Res.string.learn_lesson_progress, lessonsCompleted, lessonsTotal),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                )
            }
        }
    }
}

/** Section heading used above the Learn tiles on the main menu. */
@Composable
fun LearnSectionHeader(
    title: String,
    subtitle: String,
    trailing: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
