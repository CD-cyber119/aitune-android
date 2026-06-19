package com.pengge.aitune.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pengge.aitune.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showKey by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.ArrowBack, contentDescription = "返回")
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ====== 第一部分：API Key ======
            Text(
                "API 配置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryPink
            )

            // DeepSeek
            OutlinedTextField(
                value = uiState.deepSeekKey,
                onValueChange = { viewModel.saveDeepSeekKey(it) },
                label = { Text("DeepSeek API Key") },
                placeholder = { Text("sk-xxxxxxxx") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = PrimaryPink,
                    cursorColor = TextPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary
                ),
                visualTransformation = if (showKey) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showKey = !showKey }) {
                        Icon(
                            if (showKey) Icons.VisibilityOff else Icons.Visibility,
                            contentDescription = if (showKey) "隐藏" else "显示",
                            tint = TextMuted
                        )
                    }
                }
            )

            // MiMo Key
            OutlinedTextField(
                value = uiState.mimoKey,
                onValueChange = { viewModel.saveMimoKey(it) },
                label = { Text("小米 MiMo TTS API Key") },
                placeholder = { Text("sk-xxxxxxxx") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = PrimaryBlue,
                    cursorColor = TextPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary
                ),
                visualTransformation = if (showKey) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            HorizontalDivider(color = SurfaceVariant)

            // ====== 第二部分：语音设置 ======
            Text(
                "语音设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Text(
                "TTS 模式",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = uiState.ttsMode == "system",
                    onClick = { viewModel.saveTTSMode("system") },
                    label = { Text("系统TTS") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryBlue
                    )
                )
                FilterChip(
                    selected = uiState.ttsMode == "mimo",
                    onClick = { viewModel.saveTTSMode("mimo") },
                    label = { Text("小米MiMo") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryBlue
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "DJ 语体风格",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("casual" to "闲聊", "chill" to "深夜", "story" to "故事", "hype" to "安利").forEach { (id, name) ->
                    FilterChip(
                        selected = uiState.djLanguage == id,
                        onClick = { viewModel.saveDJLanguage(id) },
                        label = { Text(name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryPink.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryPink
                        )
                    )
                }
            }

            HorizontalDivider(color = SurfaceVariant)

            // ====== 第三部分：Spotify ======
            Text(
                "Spotify 配置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AccentGreen
            )

            OutlinedTextField(
                value = uiState.spotifyClientId,
                onValueChange = { viewModel.saveSpotifyClientId(it) },
                label = { Text("Client ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = AccentGreen,
                    cursorColor = TextPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = uiState.spotifyClientSecret,
                onValueChange = { viewModel.saveSpotifyClientSecret(it) },
                label = { Text("Client Secret") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = AccentGreen,
                    cursorColor = TextPrimary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}
