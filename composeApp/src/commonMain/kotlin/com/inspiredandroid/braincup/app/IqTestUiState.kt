package com.inspiredandroid.braincup.app

import androidx.compose.runtime.Immutable
import com.inspiredandroid.braincup.api.UserStorage
import com.inspiredandroid.braincup.games.iqtest.IqBand
import com.inspiredandroid.braincup.games.iqtest.TierResult
import com.inspiredandroid.braincup.games.matrix.MatrixPanel
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class IqTestPlayUiState(
    val itemIndex: Int,
    val itemCount: Int,
    val matrix: ImmutableList<MatrixPanel?>,
    val optionRows: ImmutableList<ImmutableList<MatrixOptionCell>>,
    val optionColumns: Int,
    val selectedOption: Int?,
    val isOnLastItem: Boolean,
)

@Immutable
data class IqTestResultUiState(
    val rawScore: Int,
    val itemCount: Int,
    val iq: Int,
    val percentile: Double,
    val band: IqBand,
    val isBelowMeasurableRange: Boolean,
    val tierBreakdown: ImmutableList<TierResult>,
    val durationSeconds: Int,
    val xpGained: Int,
    val levelChange: UserStorage.LevelChange?,
    val isPersonalBest: Boolean,
)

@Immutable
data class IqTestReviewItemUiState(
    val itemIndex: Int,
    val itemCount: Int,
    val matrix: ImmutableList<MatrixPanel?>,
    val optionRows: ImmutableList<ImmutableList<MatrixOptionCell>>,
    val optionColumns: Int,
    val pickedOption: Int?,
    val correctOption: Int,
    val tier: Int,
)
