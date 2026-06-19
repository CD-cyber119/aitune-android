package com.pengge.aitune.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pengge.aitune.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    currentThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeChanged: (ThemeMode) -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showKeys by remember { mutableStateOf(false) }

    val sectionTitleColor = MaterialTheme.colorScheme.primary
    val sectionTitleColor2 = MaterialTheme.colorScheme.secondary
    val sectionTitleColor3 = MaterialTheme.colorScheme.tertiary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                color = sectionTitleColor
            )

            // DeepSeek
            OutlinedTextField(
                value = uiState.deepSeekKey,
                onValueChange = { viewModel.saveDeepSeekKey(it) },
                label = { Text("DeepSeek API Key") },
                placeholder = { Text("***") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = sectionTitleColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = sectionTitleColor,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                visualTransformation = if (showKeys) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showKeys = !showKeys }) {
                        Icon(
                            if (showKeys) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showKeys) "隐藏" else "显示",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            // MiMo Key
            OutlinedTextField(
                value = uiState.mimoKey,
                onValueChange = { viewModel.saveMimoKey(it) },
                label = { Text("小米 MiMo TTS API Key") },
                placeholder = { Text("***") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = sectionTitleColor2,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = sectionTitleColor2,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                visualTransformation = if (showKeys) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // ====== 第二部分：语音设置 ======
            Text(
                "语音设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = sectionTitleColor2
            )

            Text(
                "TTS 模式",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = uiState.ttsMode == "system",
                    onClick = { viewModel.saveTTSMode("system") },
                    label = { Text("系统TTS") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = sectionTitleColor2.copy(alpha = 0.2f),
                        selectedLabelColor = sectionTitleColor2
                    )
                )
                FilterChip(
                    selected = uiState.ttsMode == "mimo",
                    onClick = { viewModel.saveTTSMode("mimo") },
                    label = { Text("小米MiMo") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = sectionTitleColor2.copy(alpha = 0.2f),
                        selectedLabelColor = sectionTitleColor2
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "DJ 语体风格",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            selectedContainerColor = sectionTitleColor.copy(alpha = 0.2f),
                            selectedLabelColor = sectionTitleColor
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // ====== 主题切换 ======
            Text(
                "主题模式",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = sectionTitleColor3
            )

            Text(
                "外观",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("system" to "跟随系统", "light" to "橘子海", "dark" to "深夜电台").forEach { (id, name) ->
                    FilterChip(
                        selected = uiState.themeMode == id,
                        onClick = {
                            viewModel.saveThemeMode(id)
                            onThemeChanged(
                                when (id) {
                                    "light" -> ThemeMode.LIGHT
                                    "dark" -> ThemeMode.DARK
                                    else -> ThemeMode.SYSTEM
                                }
                            )
                        },
                        label = { Text(name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = sectionTitleColor3.copy(alpha = 0.2f),
                            selectedLabelColor = sectionTitleColor3
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // ====== 第三部分：Spotify ======
            Text(
                "Spotify 配置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = sectionTitleColor3
            )

            OutlinedTextField(
                value = uiState.spotifyClientId,
                onValueChange = { viewModel.saveSpotifyClientId(it) },
                label = { Text("Client ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = sectionTitleColor3,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = sectionTitleColor3,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = uiState.spotifyClientSecret,
                onValueChange = { viewModel.saveSpotifyClientSecret(it) },
                label = { Text("Client Secret") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = sectionTitleColor3,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = sectionTitleColor3,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
