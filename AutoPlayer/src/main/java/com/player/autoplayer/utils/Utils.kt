package com.player.autoplayer.utils

import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.View

object Utils {
    private val TAG = Utils::class.java.simpleName

    private fun getRectOfView(view: View): Rect {
        val rect = Rect()
        val offset = Point()
        view.getGlobalVisibleRect(rect, offset)
        return rect
    }


    fun isInCenterOfTheScreen(parent: View): Boolean {
        val videoRect = getRectOfView(parent)
        val visibleArea = (videoRect.height() * videoRect.width()).toFloat()
        val viewArea = parent.width * parent.height
        Log.i(
            TAG,
            "isInCenterOfTheScreen: visibleArea: $visibleArea, viewArea: $viewArea, visibleAreaOffset: ${visibleArea / viewArea} "
        )
        return (visibleArea / viewArea) > 0.7
    }
}