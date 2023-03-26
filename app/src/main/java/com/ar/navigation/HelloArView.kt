package com.ar.navigation

import android.opengl.GLSurfaceView
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ar.common.helpers.InstantPlacementSettings
import com.ar.common.helpers.SnackbarHelper
import com.route.routeme.R


/**
 * Created by Ashwani Kumar Singh on 8,March,2023.
 */

/** Contains UI elements for Hello AR. */
class HelloArView(
    val activity: ArRenderingActivity,
    private val instantPlacementSettings: InstantPlacementSettings
) : DefaultLifecycleObserver {
    val root = View.inflate(activity, R.layout.activity_ar_renderer, null)
    val mSurfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)

    val session
        get() = activity.arCoreSessionHelper.session

    val snackbarHelper = SnackbarHelper()

    override fun onResume(owner: LifecycleOwner) {
        mSurfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        mSurfaceView.onPause()
    }

}
