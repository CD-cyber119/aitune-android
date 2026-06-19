package com.pengge.aitune.app.di

import android.content.Context
import com.pengge.aitune.data.repository.SettingsRepository
import com.pengge.aitune.domain.api.AIService
import com.pengge.aitune.domain.api.DeepSeekAIService
import com.pengge.aitune.domain.context.ContextBuilder
import com.pengge.aitune.domain.music.ExoMusicPlayer
import com.pengge.aitune.domain.music.MusicPlayer
import com.pengge.aitune.domain.radio.RadioEngine
import com.pengge.aitune.domain.tts.DJTTS
import com.pengge.aitune.domain.tts.MiMoDJTTS
import com.pengge.aitune.domain.tts.SystemDJTTS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepository(context)

    @Provides
    @Singleton
    fun provideAIService(
        settingsRepository: SettingsRepository
    ): AIService = DeepSeekAIService(settingsRepository)

    @Provides
    @Singleton
    fun provideContextBuilder(): ContextBuilder = ContextBuilder()

    @Provides
    @Singleton
    fun provideMusicPlayer(
        @ApplicationContext context: Context
    ): MusicPlayer = ExoMusicPlayer(context)

    @Provides
    @Singleton
    fun provideTTS(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository
    ): DJTTS {
        val ttsMode = runBlocking { settingsRepository.ttsMode.first() }
        return when (ttsMode) {
            "mimo" -> {
                val key = runBlocking { settingsRepository.mimoApiKey.first() }
                val endpoint = runBlocking { settingsRepository.mimoEndpoint.first() }
                if (!key.isNullOrBlank()) {
                    MiMoDJTTS(apiKey = key, endpoint = endpoint)
                } else {
                    SystemDJTTS(context)
                }
            }
            else -> SystemDJTTS(context)
        }
    }

    @Provides
    @Singleton
    fun provideRadioEngine(
        aiService: AIService,
        contextBuilder: ContextBuilder,
        musicPlayer: MusicPlayer,
        ttsEngine: DJTTS
    ): RadioEngine = RadioEngine(
        aiService = aiService,
        contextBuilder = contextBuilder,
        musicPlayer = musicPlayer,
        ttsEngine = ttsEngine
    )
}
