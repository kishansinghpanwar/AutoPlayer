package com.player.autoplayer.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.player.autoplayer.R
import java.io.File

class HelperForExoPlayer(
    val mContext: Context,
    private val playerView: PlayerView,
    enableCache: Boolean = true,
    private val loopVideo: Boolean = false,
    val loopCount: Int = Integer.MAX_VALUE
) :
    LifecycleObserver {

    private var mPlayer: SimpleExoPlayer
    var cacheSizeInMb: Long = 500

    var isProgressRequired: Boolean = false

    companion object {
        private var simpleCache: SimpleCache? = null
        var mLoadControl: DefaultLoadControl? = null
        var mDataSourceFactory: DataSource.Factory? = null
        var mCacheEnabled = false
    }

    init {
        if (mCacheEnabled != enableCache || mDataSourceFactory == null) {


            mDataSourceFactory = null

            mDataSourceFactory = DefaultDataSourceFactory(
                mContext,
                Util.getUserAgent(mContext, mContext.getString(R.string.app_name)),
                DefaultBandwidthMeter()
            )

            // The LoadControl that controls when the MediaSource buffers for more media, and how much media is buffered.
            // the LoadControl is injected when the SimpleExoPlayer is created.
            val builder = DefaultLoadControl.Builder()
            builder.setAllocator(DefaultAllocator(true, 2 * 1024 * 1024))
            builder.setBufferDurationsMs(5000, 5000, 5000, 5000)
            builder.setPrioritizeTimeOverSizeThresholds(true)
            mLoadControl = builder.createDefaultLoadControl()

            if (enableCache) {
                val cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSizeInMb * 1024 * 1024)
                val file = File(mContext.cacheDir, "media")

                if (simpleCache == null)
                    simpleCache = SimpleCache(file, cacheEvictor)

                mDataSourceFactory = CacheDataSourceFactory(
                    simpleCache,
                    mDataSourceFactory,
                    FileDataSourceFactory(),
                    CacheDataSinkFactory(simpleCache, (2 * 1024 * 1024).toLong()),
                    CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                    null
                )
            }
        }
        mCacheEnabled = enableCache

        mPlayer = ExoPlayerFactory.newSimpleInstance(
            mContext,
            DefaultRenderersFactory(mContext),
            DefaultTrackSelector(),
            mLoadControl
        )
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.setRewindIncrementMs(15000)
        playerView.setFastForwardIncrementMs(15000)
        playerView.player = mPlayer

    }

    private var mediaSource: MediaSource? = null
    private var isPreparing = false
    private var url: String = ""


    fun setUrl(url: String, autoPlay: Boolean = false) {
        if (lifecycle?.currentState == Lifecycle.State.RESUMED) {
            this.url = url
            mediaSource = buildMediaSource(Uri.parse(url))
            loopIfNecessary()
            mPlayer.playWhenReady = autoPlay
            isPreparing = true
            mPlayer.prepare(mediaSource)
        }
    }

    var lifecycle: Lifecycle? = null
    fun makeLifeCycleAware(activity: AppCompatActivity) {
        lifecycle = activity.lifecycle
        activity.lifecycle.addObserver(this)
    }

    fun makeLifeCycleAware(fragment: Fragment) {
        lifecycle = fragment.lifecycle
        fragment.lifecycle.addObserver(this)
    }

    /**
     * Trim or clip media to given start and end milliseconds,
     * Ensure you must call this method after the [setUrl] method call.
     *
     * @param start starting time in millisecond
     * @param end ending time in millisecond
     */
    fun clip(start: Long, end: Long) {
        if (mediaSource != null) {
            mediaSource = ClippingMediaSource(mediaSource, start * 1000, end * 1000)
            loopIfNecessary()
        }
        mPlayer.prepare(mediaSource)
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return when (val type = Util.inferContentType(uri)) {
            C.TYPE_SS -> SsMediaSource.Factory(mDataSourceFactory).createMediaSource(uri)
            C.TYPE_DASH -> DashMediaSource.Factory(mDataSourceFactory).createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(mDataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ExtractorMediaSource.Factory(mDataSourceFactory)
                .createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    /**
     * Looping if the user set if looping is necessary.
     */
    private fun loopIfNecessary() {
        if (loopVideo) {
            mediaSource = LoopingMediaSource(mediaSource, loopCount)
        }
    }

    fun play() {
        mPlayer.playWhenReady = true
    }

    fun pause() {
        mPlayer.playWhenReady = false
    }

    fun stop() {
        mPlayer.stop()
    }

    fun seekTo(positionMs: Long) {
        mPlayer.seekTo(positionMs)
    }


    private val durationHandler = Handler(Looper.getMainLooper())
    private var durationRunnable: Runnable? = null

    private fun startTimer() {
        if (isProgressRequired) {
            durationRunnable?.run {
                durationHandler.postDelayed(this, 17)
            }

        }
    }

    private fun stopTimer() {
        if (isProgressRequired) {
            durationRunnable?.run {
                durationHandler.removeCallbacks(this)
            }
        }
    }

    /**
     * Returns SimpleExoPlayer instance, you can use it for your own implementation.
     */
    fun getPlayer(): SimpleExoPlayer {
        return mPlayer
    }

    /**
     * Used to set the different quality URLs of existing video/audio.
     */
    fun setQualityUrl(qualityUrl: String) {
        val currentPosition = mPlayer.currentPosition
        mediaSource = buildMediaSource(Uri.parse(qualityUrl))
        loopIfNecessary()
        mPlayer.prepare(mediaSource)
        mPlayer.seekTo(currentPosition)
    }

    /**
     * The normal speed of all videos is 1f and double the speed would be 2f.
     */
    fun setSpeed(speed: Float) {
        val param = PlaybackParameters(speed)
        mPlayer.playbackParameters = param
    }

    /**
     * Returns whether the player is playing or not.
     */
    fun isPlaying(): Boolean {
        return mPlayer.playWhenReady
    }

    /**
     * Mute the player
     */
    fun mute() {
        mPlayer.volume = 0f
    }

    /**
     * Unmute the player
     */
    fun unMute() {
        mPlayer.volume = 1f
    }


    //Life Cycle Event
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        mPlayer.playWhenReady = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        simpleCache?.release()
        simpleCache = null
        mPlayer.playWhenReady = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        mPlayer.playWhenReady = true
    }


    /**
     * Listener that used for most popular callbacks
     */
    fun setPlayerListener(progressRequired: Boolean = false, playerListener: PlayerListener) {
        this.isProgressRequired = progressRequired
        mPlayer.addListener(object : Player.EventListener {

            override fun onPlayerError(error: ExoPlaybackException?) {
                playerListener.onPlayerError(error)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (isPreparing && playbackState == Player.STATE_READY) {
                    isPreparing = false
                    playerListener.onPlayerReady()
                }
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        playerListener.onPlayerBuffering(true)
                    }
                    Player.STATE_READY -> {
                        playerListener.onPlayerBuffering(false)
                        if (playWhenReady) {
                            startTimer()
                            playerListener.onPlayerStart()
                        } else {
                            stopTimer()
                            playerListener.onPlayerStop()
                        }
                    }
                    Player.STATE_IDLE -> {
                        stopTimer()
                        playerListener.onPlayerBuffering(false)
                        playerListener.onPlayerError(null)
                    }
                    Player.STATE_ENDED -> {
                        playerListener.onPlayerBuffering(false)
                        stopTimer()
                        playerListener.onPlayerStop()
                    }
                }
            }
        })

        playerView.setControllerVisibilityListener { visibility ->
            playerListener.onPlayerToggleControllerVisible(visibility == View.VISIBLE)
        }

        if (progressRequired) {
            durationRunnable = Runnable {
                playerListener.onPlayerProgress(mPlayer.currentPosition)
                if (mPlayer.playWhenReady) {
                    durationRunnable?.run {
                        durationHandler.postDelayed(this, 500)
                    }
                }
            }
        }
    }

}