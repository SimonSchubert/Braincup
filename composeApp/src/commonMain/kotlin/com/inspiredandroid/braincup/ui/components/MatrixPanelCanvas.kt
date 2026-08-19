package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inspiredandroid.braincup.games.matrix.CENTERED_SLOT
import com.inspiredandroid.braincup.games.matrix.MatrixEntity
import com.inspiredandroid.braincup.games.matrix.MatrixPanel
import com.inspiredandroid.braincup.games.matrix.SIZE_SCALES
import com.inspiredandroid.braincup.games.tools.composeColor

/**
 * One cell of the matrix: either a single centred figure, or up to four laid out on a 2x2
 * sub-grid so that a rule governing how many there are, or where they sit, has somewhere to show.
 */
@Composable
fun MatrixPanelCanvas(
    panel: MatrixPanel,
    modifier: Modifier = Modifier,
) {
    val single = panel.entities.singleOrNull()
    if (single != null && single.slot == CENTERED_SLOT) {
        Box(modifier, contentAlignment = Alignment.Center) {
            MatrixEntityShape(single)
        }
        return
    }
    val bySlot = panel.entities.associateBy { it.slot }
    Column(modifier) {
        repeat(SUB_GRID) { row ->
            Row(Modifier.weight(1f).fillMaxWidth()) {
                repeat(SUB_GRID) { column ->
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        bySlot[row * SUB_GRID + column]?.let { MatrixEntityShape(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixEntityShape(entity: MatrixEntity) {
    PrismPolygon(
        points = entity.shape.paths,
        face = entity.color.composeColor(),
        rotationDegrees = entity.rotation.toFloat(),
        modifier = Modifier.fillMaxSize(SIZE_SCALES[entity.sizeStep]),
    )
}

private const val SUB_GRID = 2
