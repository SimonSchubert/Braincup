package com.inspiredandroid.braincup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.level_label
import braincup.composeapp.generated.resources.xp_progress
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainer
import com.inspiredandroid.braincup.ui.theme.OnPrimaryContainerSubtle
import com.inspiredandroid.braincup.ui.theme.Primary
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerLevelCard(
    totalXp: Int,
    modifier: Modifier = Modifier,
) {
    val level = UserStorage.levelForXp(totalXp)
    val xpIntoLevel = UserStorage.xpIntoLevel(totalXp)
    val xpSpan = UserStorage.xpSpanForLevel(level)
    val targetProgress = if (xpSpan > 0) xpIntoLevel.toFloat() / xpSpan else 0f
    val titleRes = UserStorage.currentTitleRes(level)

    val inInspection = LocalInspectionMode.current
    val target = targetProgress.coerceIn(0f, 1f)
    var lastAnimatedXp by rememberSaveable { mutableIntStateOf(Int.MIN_VALUE) }
    // First-ever render starts the bar at 0 so it can sweep up to the current value.
    // Subsequent renders animate from the previous progress to the new one.
    val animatedProgress = remember {
        Animatable(if (inInspection || lastAnimatedXp == totalXp) target else 0f)
    }
    LaunchedEffect(totalXp) {
        if (inInspection) {
            animatedProgress.snapTo(target)
        } else {
            animatedProgress.animateTo(
                targetValue = target,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            )
        }
        lastAnimatedXp = totalXp
    }

    BrandedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.level_label, level),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = OnPrimaryContainer,
            )
        }

        Spacer(Modifier.height(10.dp))

        XpProgressBar(
            progress = animatedProgress.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(Res.string.xp_progress, xpIntoLevel, xpSpan),
            style = MaterialTheme.typography.labelSmall,
            color = OnPrimaryContainer,
        )
    }
}

@Composable
fun XpProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val track = OnPrimaryContainerSubtle
    val fill = Primary
    Canvas(modifier = modifier) {
        val corner = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(color = track, size = size, cornerRadius = corner)
        val filledWidth = size.width * progress.coerceIn(0f, 1f)
        if (filledWidth > 0f) {
            drawRoundRect(
                color = fill,
                topLeft = Offset.Zero,
                size = Size(filledWidth, size.height),
                cornerRadius = corner,
            )
        }
    }
}
