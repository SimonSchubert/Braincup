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
import braincup.composeapp.generated.resources.learn_ages
import braincup.composeapp.generated.resources.learn_shape_guide_subtitle
import braincup.composeapp.generated.resources.learn_shape_guide_title
import braincup.composeapp.generated.resources.learn_unit_progress
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.learn.ShapeGuideRowPreview
import com.inspiredandroid.braincup.ui.components.learn.SubTopicRowPreview
import com.inspiredandroid.braincup.ui.components.learn.TopicTilePreview
import com.inspiredandroid.braincup.ui.theme.LightColorScheme
import com.inspiredandroid.braincup.ui.theme.MedalGold
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import org.jetbrains.compose.resources.stringResource

/** A small gold trophy, shown wherever a certificate has been earned. */
@Composable
fun CertificateMedal(modifier: Modifier = Modifier) {
    PrismTrophy(
        tint = MedalGold,
        modifier = modifier,
    )
}

/**
 * Topic entry, used in the main menu's Learn section and on the Learn screen: the topic's sketch
 * over its accent colour, with how many of its sub-topic certificates have been earned.
 */
@Composable
fun LearnTopicTile(
    topic: MathTopic,
    certificates: Int,
    unitsTotal: Int,
    onClick: (MathTopic) -> Unit,
    modifier: Modifier = Modifier,
) {
    LearnTileBody(
        accentColor = topic.accentColor,
        preview = { TopicTilePreview(topic) },
        title = stringResource(topic.titleRes),
        caption = stringResource(Res.string.learn_unit_progress, certificates, unitsTotal),
        // The trophy marks a finished topic, so it waits for the last sub-topic's certificate.
        showMedal = unitsTotal > 0 && certificates >= unitsTotal,
        onClick = { onClick(topic) },
        modifier = modifier,
    )
}

/**
 * The shape guide entry, above Geometry's ladder: the reference of every named shape.
 *
 * It sits with the rungs and is shaped like one, because that is where a learner looking for a
 * shape's name will go. It is set apart with the highlight colour rather than by being moved
 * somewhere else: it teaches nothing and certifies nothing, so it is not a rung.
 */
@Composable
fun LearnShapeGuideRow(
    accentColor: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        // The brand face the topic tiles wear, rather than a highlight container: this is the one
        // row on the ladder that is a button, and it has to look like one in every theme.
        face = Primary,
        modifier = modifier.fillMaxWidth().hoverHand(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(accentColor), PrismSlot),
                contentAlignment = Alignment.Center,
            ) {
                MaterialTheme(colorScheme = LightColorScheme) {
                    ShapeGuideRowPreview(modifier = Modifier.fillMaxSize())
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.learn_shape_guide_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.learn_shape_guide_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * One rung of a topic's ladder.
 *
 * A row rather than a square tile: sub-topics are an ordered sequence, and a row has space for the
 * name and the age band it is normally taught at, which is what decides where to start. Lesson
 * counts stay on the sub-topic's own screen; here they are noise between the titles.
 */
@Composable
fun LearnSubTopicRow(
    unit: LearnUnit,
    position: Int,
    ladderSize: Int,
    hasCertificate: Boolean,
    onClick: (LearnUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth().hoverHand(),
        onClick = { onClick(unit) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(unit.topic.accentColor), PrismSlot),
                contentAlignment = Alignment.Center,
            ) {
                MaterialTheme(colorScheme = LightColorScheme) {
                    SubTopicRowPreview(
                        position = position,
                        ladderSize = ladderSize,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.learn_ages, unit.level.ageRange),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (hasCertificate) {
                CertificateMedal(modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun LearnTileBody(
    accentColor: Long,
    preview: @Composable () -> Unit,
    title: String,
    caption: String,
    showMedal: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrismTile(
        face = Primary,
        modifier = modifier
            .aspectRatio(1f)
            .hoverHand(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(accentColor)),
                contentAlignment = Alignment.Center,
            ) {
                MaterialTheme(colorScheme = LightColorScheme) {
                    preview()
                }
                if (showMedal) {
                    CertificateMedal(
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
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = caption,
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
