package com.player.autoplayer

import android.content.Context
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.player.autoplayer.utils.HelperForExoPlayer
import com.player.autoplayer.utils.PlayerListener
import com.player.autoplayer.utils.Utils


class AutoPlayerManager {
    private val TAG = AutoPlayerManager::class.java.simpleName
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private lateinit var playerView: PlayerView
    var helperForExoPlayer: HelperForExoPlayer? = null

    var autoPlayerId: Int? = null
    var autoPlayPlayer: Boolean = true
    var isMute: Boolean = false
    var useController: Boolean = false
    var loop: Int = Int.MAX_VALUE
    var enableCache: Boolean = false

    constructor(activity: AppCompatActivity) {
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
    }

    fun getPlayerView(): PlayerView {
        return playerView
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        var firstVisibleItem: Int = 0
        var lastVisibleItem: Int = 0
        var visibleCount: Int = 0

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    Log.i(
                        TAG,
                        "onScrollStateChanged: visibleCount: $visibleCount"
                    )
                    val visiblePlayers: HashMap<Int, AutoPlayer> = HashMap()
                    var playerForPlay: View? = null
                    var playerPosition: Int = -1
                    for (i in 0 until visibleCount) {
                        Log.i(
                            TAG,
                            "onScrollStateChanged: for: $i"
                        )
                        val view = recyclerView.getChildAt(i) ?: continue
                        val autoPlayExoPlayer = autoPlayerId?.let { view.findViewById<View>(it) }
                        if (autoPlayExoPlayer != null && autoPlayExoPlayer is AutoPlayer) {
                            Log.i(
                                TAG,
                                "onScrollStateChanged: for: $i autoPlayExoPlayer not null so play"
                            )
                            visiblePlayers[firstVisibleItem + i] = autoPlayExoPlayer
                        }
                    }

                    if (visiblePlayers.count() == 1) {
                        playerPosition = visiblePlayers.keys.first()
                        playerForPlay = visiblePlayers[playerPosition]
                    } else {
                        run getPlayer@{
                            visiblePlayers.forEach {
                                if (Utils.isInCenterOfTheScreen(it.value)) {
                                    playerPosition = it.key
                                    playerForPlay = visiblePlayers[playerPosition]
                                    return@getPlayer
                                }
                            }
                        }
                    }


                    if (playerForPlay != null) {
                        Log.i(
                            TAG,
                            "playerForPlay is not null, and current player and player for play is same: ${currentPlayingViewPosition == playerPosition}"
                        )
                        if (currentPlayingViewPosition != playerPosition) {
                            currentPlayingViewPosition = playerPosition
                            play(playerForPlay!!)
                        }
                    } else {
                        Log.i(
                            TAG,
                            "playerForPlay is null, and current player is null: ${currentPlayingViewPosition < 0}"
                        )
                        if (currentPlayingViewPosition >= 0) {
                            for (i in 0 until visibleCount) {
                                helperForExoPlayer?.stop()
                                playerView.getPlayerParent()?.removePlayer()
                            }
                            currentPlayingViewPosition = -1
                        }
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            visibleCount = (lastVisibleItem - firstVisibleItem) + 1
        }
    }

    private val childAttachHandler = android.os.Handler(Looper.getMainLooper())
    private val childAttachRunnable = object : Runnable {
        var mAttachedRecyclerView: RecyclerView? = null
        override fun run() {
            if (mAttachedRecyclerView != null)
                onScrollListener.onScrollStateChanged(
                    recyclerView = mAttachedRecyclerView!!,
                    newState = RecyclerView.SCROLL_STATE_IDLE
                )
        }
    }

    private val onChildAttachStateChangeListener =
        object : RecyclerView.OnChildAttachStateChangeListener {
            var attachedRecyclerView: RecyclerView? = null
            override fun onChildViewDetachedFromWindow(view: View) {
                releasePlayer(view)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                childAttachHandler.removeCallbacks(childAttachRunnable)
                childAttachHandler.postDelayed(childAttachRunnable.apply {
                    mAttachedRecyclerView = attachedRecyclerView
                }, 500)
            }
        }


    /**
     * Used to attach recycler view to this library.
     * Call this after setting LayoutManager to your recycler view
     */
    fun attachRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager != null) {
            recyclerView.removeOnScrollListener(onScrollListener)
            recyclerView.removeOnChildAttachStateChangeListener(onChildAttachStateChangeListener)

            recyclerView.addOnScrollListener(onScrollListener)
            recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener.apply {
                attachedRecyclerView = recyclerView
            })
        } else {
            throw(RuntimeException("call attachRecyclerView() after setting RecyclerView.layoutManager to your RecyclerView."))
        }
    }


    fun setup() {
        playerView = PlayerView(getContext())
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        playerView.useController = useController
        helperForExoPlayer = HelperForExoPlayer(
            mContext = getContext(),
            playerView = playerView,
            enableCache = enableCache,
            loopVideo = loop > 0,
            loopCount = loop
        )
        helperForExoPlayer!!.setPlayerListener(false, object : PlayerListener {
            override fun onPlayerStart() {
                super.onPlayerStart()
                playerView.getPlayerParent()?.hidePlaceholderView(0)
                playerView.getPlayerParent()?.playerListener?.onPlayerStart()
            }

            override fun onPlayerBuffering(isBuffering: Boolean) {
                super.onPlayerBuffering(isBuffering)
                playerView.getPlayerParent()?.playerListener?.onPlayerBuffering(isBuffering)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                super.onPlayerError(error)
                playerView.getPlayerParent()?.playerListener?.onPlayerError(error)
            }

            override fun onPlayerReady() {
                super.onPlayerReady()
                playerView.getPlayerParent()?.playerListener?.onPlayerReady()
            }

            override fun onPlayerProgress(positionMs: Long) {
                super.onPlayerProgress(positionMs)
                playerView.getPlayerParent()?.playerListener?.onPlayerProgress(positionMs)
            }

            override fun onPlayerStop() {
                super.onPlayerStop()
                playerView.getPlayerParent()?.playerListener?.onPlayerStop()
            }

            override fun onPlayerToggleControllerVisible(isVisible: Boolean) {
                super.onPlayerToggleControllerVisible(isVisible)
                playerView.getPlayerParent()?.playerListener?.onPlayerToggleControllerVisible(
                    isVisible
                )
            }
        })
        playerView.tag = this


        if (activity != null) {
            helperForExoPlayer?.makeLifeCycleAware(activity!!)
        } else if (fragment != null) {
            helperForExoPlayer?.makeLifeCycleAware(fragment!!)
        }
    }

    var currentPlayingViewPosition: Int = -1

    @Synchronized
    private fun play(view: View) {
        val autoPlayExoPlayer = autoPlayerId?.let { view.findViewById<View>(it) }

        if (autoPlayExoPlayer != null && autoPlayExoPlayer is AutoPlayer) {
            if (autoPlayExoPlayer.playerView == null) {

                playerView.getPlayerParent()?.removePlayer()
                autoPlayExoPlayer.addPlayer(playerView)
                if (autoPlayExoPlayer.url?.isNotBlank() == true) {
                    autoPlayExoPlayer.isMute = isMute
                    if (isMute) {
                        helperForExoPlayer?.mute()
                    } else {
                        helperForExoPlayer?.unMute()
                    }

                    helperForExoPlayer?.setUrl(autoPlayExoPlayer.url!!, autoPlayPlayer)
                }
                playerView.getPlayerParent()?.playerListener?.onPlayerReady()
            }
        }
    }

    private fun releasePlayer(view: View) {
        val autoPlayExoPlayer = autoPlayerId?.let { view.findViewById<View>(it) }
        if (autoPlayExoPlayer != null && autoPlayExoPlayer is AutoPlayer) {
            if (autoPlayExoPlayer.playerView != null) {
                helperForExoPlayer?.stop()
                autoPlayExoPlayer.removePlayer()
            }
        }
    }

    private fun PlayerView.getPlayerParent(): AutoPlayer? {
        if (this.parent != null && this.parent is AutoPlayer) {
            return this.parent as AutoPlayer
        }
        return null
    }

    private fun getContext(): Context {
        return if (activity != null) {
            activity!!
        } else {
            fragment!!.requireContext()
        }
    }

}