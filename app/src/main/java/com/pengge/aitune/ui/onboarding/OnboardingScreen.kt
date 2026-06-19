package com.pengge.aitune.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pengge.aitune.ui.theme.*

/** 三步引导 — 首次使用 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit = {}
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val steps = listOf(
        OnboardingStep(
            icon = Icons.Filled.Radio,
            title = "欢迎来到 Aitune",
            description = "你的个人 AI 电台。\nAI 读懂你的听歌习惯，像真 DJ 一样为你播歌、讲故事。",
            color = PrimaryPink
        ),
        OnboardingStep(
            icon = Icons.Filled.KeyboardVoice,
            title = "配置 API Key",
            description = "先去「设置」页填入 DeepSeek 和 MiMo TTS 的 Key，\n这是 AI 说话和推荐歌曲的动力。",
            color = PrimaryBlue
        ),
        OnboardingStep(
            icon = Icons.Filled.LibraryMusic,
            title = "连接 Spotify",
            description = "登录你的 Spotify Premium 账号，\n选择想听的歌单，电台就准备好了。",
            color = AccentGreen
        )
    )

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 步骤指示器
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentStep) 12.dp else 8.dp)
                            .padding(end = if (index == currentStep) 4.dp else 6.dp)
                    )
                    Surface(
                        modifier = Modifier.size(
                            width = if (index == currentStep) 24.dp else 8.dp,
                            height = 8.dp
                        ),
                        shape = RoundedCornerShape(4.dp),
                        color = if (index <= currentStep) steps[currentStep].color
                        else SurfaceVariant
                    ) {}
                }
            }

            Spacer(Modifier.height(48.dp))

            // 图标
            Icon(
                imageVector = steps[currentStep].icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = steps[currentStep].color
            )

            Spacer(Modifier.height(24.dp))

            // 标题
            Text(
                text = steps[currentStep].title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // 描述
            Text(
                text = steps[currentStep].description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            Spacer(Modifier.weight(1f))

            // 按钮
            Button(
                onClick = {
                    if (currentStep < steps.size - 1) {
                        currentStep++
                    } else {
                        onComplete()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = steps[currentStep].color
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (currentStep < steps.size - 1) {
                    Text("下一步", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                } else {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("开始使用", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }

            // 跳过按钮
            if (currentStep < steps.size - 1) {
                TextButton(onClick = onComplete) {
                    Text("跳过引导", color = TextMuted)
                }
            }
        }
    }
}

private data class OnboardingStep(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)
