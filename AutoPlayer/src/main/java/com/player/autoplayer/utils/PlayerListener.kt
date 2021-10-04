package com.player.autoplayer.utils

import com.google.android.exoplayer2.ExoPlaybackException

interface PlayerListener {
    fun onPlayerReady() {}
    fun onPlayerStart() {}
    fun onPlayerStop() {}
    fun onPlayerProgress(positionMs: Long) {}
    fun onPlayerError(error: ExoPlaybackException?) {}
    fun onPlayerBuffering(isBuffering: Boolean) {}
    fun onPlayerToggleControllerVisible(isVisible: Boolean) {}
}