package com.pengge.aitune.ui.nowplaying

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pengge.aitune.ui.theme.*
import com.pengge.aitune.ui.radio.StateIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    onOpenSettings: () -> Unit = {},
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aitune") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "设置")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))

            // 专辑封面
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceColor),
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.albumArtUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = uiState.albumArtUrl,
                        contentDescription = "专辑封面",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Filled.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // 状态指示器
            StateIndicator(
                statusLabel = uiState.statusLabel,
                isLoading = uiState.isLoading,
                isPlaying = uiState.isPlaying,
                isError = uiState.isError
            )

            Spacer(Modifier.height(16.dp))

            // 歌名
            Text(
                text = uiState.trackTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // 歌手
            Text(
                text = uiState.trackArtist,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(24.dp))

            // 进度条
            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryPink,
                trackColor = SurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // 播放控制按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = { viewModel.skip() }) {
                    Icon(
                        Icons.Filled.SkipPrevious,
                        contentDescription = "上一首",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.pause() },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryPink)
                ) {
                    Icon(
                        if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "暂停" else "播放",
                        tint = TextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(onClick = { viewModel.skip() }) {
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = "下一首",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // DJ 串词字幕
            AnimatedVisibility(
                visible = uiState.djSubtitle != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    color = SurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = uiState.djSubtitle ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 启动按钮（空闲状态时）
            if (uiState.statusLabel == "就绪") {
                Button(
                    onClick = { viewModel.startRadio() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .height(48.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("启动电台", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
