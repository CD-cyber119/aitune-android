package com.pengge.aitune.ui.radio

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/** 状态指示器 — 显示当前电台状态（思考/说话/播放/错误） */
@Composable
fun StateIndicator(
    statusLabel: String,
    isLoading: Boolean = false,
    isPlaying: Boolean = false,
    isError: Boolean = false
) {
    val indicatorColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isLoading -> MaterialTheme.colorScheme.tertiary
            isPlaying -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        label = "stateIndicatorColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(indicatorColor)
        )
        Text(
            text = statusLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
