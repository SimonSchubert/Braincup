package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_ages
import braincup.composeapp.generated.resources.learn_rules_guide_title
import braincup.composeapp.generated.resources.learn_shape_guide_title
import braincup.composeapp.generated.resources.learn_unit_progress
import com.inspiredandroid.braincup.learn.GuideSection
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.MathTopic
import com.inspiredandroid.braincup.ui.components.learn.RulesGuideGlyphs
import com.inspiredandroid.braincup.ui.components.learn.ShapeGuideGlyphs
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
        progress = if (unitsTotal > 0) certificates.toFloat() / unitsTotal else 0f,
        // The trophy marks a finished topic, so it waits for the last sub-topic's certificate.
        showMedal = unitsTotal > 0 && certificates >= unitsTotal,
        onClick = { onClick(topic) },
        modifier = modifier,
    )
}

/** The glyph strip inside a guide button, sized to sit on one line of its label. */
private val GuideGlyphHeight = 14.dp

/**
 * The button that opens a topic's guide, in the top right of the topic's screen.
 *
 * Deliberately not a row on the ladder: every row there is a sub-topic with lessons behind it and
 * a certificate to earn, and a guide is neither. It sits in the bar as a button, shaped like the
 * app's other bar buttons and carrying a few of the things it opens onto: the shapes for Geometry,
 * the operators for Arithmetic.
 */
@Composable
fun LearnGuideButton(
    topic: MathTopic,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (topic) {
        MathTopic.GEOMETRY -> GuideButton(
            label = stringResource(Res.string.learn_shape_guide_title),
            onClick = onClick,
            modifier = modifier,
        ) {
            ShapeGuideGlyphs(modifier = Modifier.height(GuideGlyphHeight))
        }

        MathTopic.ARITHMETIC -> GuideButton(
            label = stringResource(Res.string.learn_rules_guide_title),
            onClick = onClick,
            modifier = modifier,
        ) {
            RulesGuideGlyphs(modifier = Modifier.height(GuideGlyphHeight))
        }
    }
}

@Composable
private fun GuideButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyphs: @Composable () -> Unit,
) {
    // The button shares the top bar with the topic's title, and at a large font scale there is no
    // room for both labels: the glyphs already say which guide this is, so the word is dropped and
    // handed to the screen reader instead of being ellipsised down to a single letter.
    val labelFits = !isLargeFontScale()
    PrismTile(
        face = Primary,
        modifier = modifier
            .padding(end = 8.dp)
            .defaultMinSize(minHeight = 36.dp)
            .semantics { contentDescription = label }
            .hoverHand(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            glyphs()
            if (labelFits) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
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
    /** This rung's age band, counting from 1, among the [bands] its topic covers. */
    band: Int,
    bands: Int,
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
                        band = band,
                        bands = bands,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(unit.title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    // Two lines hold the longest name at the default size; at a large one they hold
                    // about half of it, so the cap rises with the text rather than truncating it.
                    maxLines = if (isLargeFontScale()) 4 else 2,
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
    progress: Float? = null,
) {
    // Not locked to a square, and the preview keeps a full one: a topic sits beside the untimed
    // games on the menu and is the same kind of thing, so it carries its progress the same way.
    PrismTile(
        face = Primary,
        modifier = modifier.hoverHand(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
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
                    .padding(start = 8.dp, top = 6.dp, bottom = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (progress != null) {
                    PrismProgressBar(
                        progress = { progress },
                        trackColor = Color.Black.copy(alpha = 0.22f),
                        fillColor = Color.White,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                    )
                }
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

/** The heading over a run of guide entries, shared by the shape guide and the rules guide. */
@Composable
fun GuideSectionHeader(
    section: GuideSection<*>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = stringResource(section.title),
            style = MaterialTheme.typography.titleSmall,
            color = Primary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(section.blurb),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
