package com.inspiredandroid.braincup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.learn_grade_certificates
import braincup.composeapp.generated.resources.learn_grade_intro
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.learn.GradeLevel
import com.inspiredandroid.braincup.learn.LearnCatalog
import com.inspiredandroid.braincup.learn.LearnUnit
import com.inspiredandroid.braincup.learn.LearnUnitProgress
import com.inspiredandroid.braincup.ui.components.AppScaffold
import com.inspiredandroid.braincup.ui.components.LearnUnitTile
import com.inspiredandroid.braincup.ui.screens.games.DevicePreviews
import com.inspiredandroid.braincup.ui.screens.games.ScreenPreviewHost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/** The topics taught at one grade band. Each tile opens that band's unit for the topic. */
@Composable
fun LearnGradeScreen(
    level: GradeLevel,
    storage: UserStorage,
    onUnitSelected: (LearnUnit) -> Unit,
    onBack: () -> Unit,
) {
    val progress = remember(storage, level) { storage.getLearnUnitProgress(level).toImmutableList() }
    LearnGradeScreenContent(
        level = level,
        progress = progress,
        onUnitSelected = onUnitSelected,
        onBack = onBack,
    )
}

@Composable
fun LearnGradeScreenContent(
    level: GradeLevel,
    progress: ImmutableList<LearnUnitProgress>,
    onUnitSelected: (LearnUnit) -> Unit,
    onBack: () -> Unit,
) {
    val certificateCount = remember(progress) { progress.count { it.hasCertificate } }

    AppScaffold(
        title = stringResource(level.titleRes),
        onBack = onBack,
        scrollable = false,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }, contentType = "grade_intro") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(level.subtitleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.learn_grade_intro),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            Res.string.learn_grade_certificates,
                            certificateCount,
                            progress.size,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            items(progress, key = { it.unit.id }, contentType = { "learn_unit" }) { unitProgress ->
                LearnUnitTile(
                    unit = unitProgress.unit,
                    lessonsCompleted = unitProgress.lessonsCompleted,
                    lessonsTotal = unitProgress.lessonsTotal,
                    tier = unitProgress.tier,
                    onClick = onUnitSelected,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun LearnGradeScreenPreview() {
    ScreenPreviewHost {
        LearnGradeScreenContent(
            level = GradeLevel.GRADES_3_5,
            progress = LearnCatalog.units(GradeLevel.GRADES_3_5)
                .map { LearnUnitProgress.empty(it) }
                .toImmutableList(),
            onUnitSelected = {},
            onBack = {},
        )
    }
}
