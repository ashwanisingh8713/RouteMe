package com.ar.navigation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ar.common.helpers.*
import com.ar.common.samplerender.SampleRender
import com.ar.navigation.helpers.ARCoreSessionLifecycleHelper
import com.ar.navigation.util.KotlinUtil
import com.google.ar.core.Config
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*
import com.route.routeme.databinding.ActivityArRendererBinding
import com.route.viewmodel.RoutePointViewModel
import kotlin.math.roundToInt

/**
 * Created by Ashwani Kumar Singh on 11,March,2023.
 */

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3D model.
 */
class ArRenderingActivity : AppCompatActivity() {

  val snackbarHelper = SnackbarHelper()

  lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
//  lateinit var view: HelloArView
  private lateinit var renderer: HelloArRenderer
  private lateinit var binding: ActivityArRendererBinding
  private var mRulerWidth : Int = 0
  private val instantPlacementSettings = InstantPlacementSettings()
  val depthSettings = DepthSettings()

  private lateinit var model: RoutePointViewModel

  private var routeId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    instantPlacementSettings.onCreate(this)
    binding = ActivityArRendererBinding.inflate(layoutInflater)

    model = ViewModelProvider(this@ArRenderingActivity)[RoutePointViewModel::class.java]

    if (intent.extras != null) {
      routeId = intent.extras!!.getString("id")!!
    }

    // Init Making API Request
//        model.loadRoutesDocument();
    model.getDocumentAppClipRouteIdBody(routeId)

    model.routesDocument.observe(this, Observer {routesData->
      Log.i("", "$routesData")

      // Set up the Hello AR renderer.
      renderer = HelloArRenderer(this, routesData)
      lifecycle.addObserver(renderer)

      // Set up Hello AR UI.
      setContentView(binding.root)

      // Sets up an example renderer using our HelloARRenderer.
      SampleRender(binding.surfaceview, renderer, assets)

      depthSettings.onCreate(this)

    })

    // Setup ARCore session lifecycle helper and configuration.
    arCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
    // If Session creation or Session.resume() fails, display a message and log detailed
    // information.
    arCoreSessionHelper.exceptionCallback = { exception ->
        val message =
          when (exception) {
            is UnavailableUserDeclinedInstallationException ->
              "Please install Google Play Services for AR"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            is CameraNotAvailableException -> "Camera not available. Try restarting the app."
            else -> "Failed to create AR session: $exception"
          }
        snackbarHelper.showError(this, message)
      }

    // Configure session features, including: Lighting Estimation, Depth mode, Instant Placement.
    arCoreSessionHelper.beforeSessionResume = ::configureSession
    lifecycle.addObserver(arCoreSessionHelper)



  }

  // Configure the session, using Lighting Estimation, and Depth mode.
  private fun configureSession(session: Session) {
    session.configure(
      session.config.apply {
        lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

        // Depth API is used if it is configured in Hello AR's settings.
        depthMode =
          if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            Config.DepthMode.AUTOMATIC
          } else {
            Config.DepthMode.DISABLED
          }

        // Instant Placement is used if it is configured in Hello AR's settings.
        instantPlacementMode =
          if (instantPlacementSettings.isInstantPlacementEnabled) {
            InstantPlacementMode.LOCAL_Y_UP
          } else {
            InstantPlacementMode.DISABLED
          }
      }
    )
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    results: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, results)
    if (!CameraPermissionHelper.hasCameraPermission(this)) {
      // Use toast instead of snackbar here since the activity will exit.
      Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
        .show()
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(this)
      }
      finish()
    }
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
  }

  fun showProgressBar() {
    runOnUiThread{binding.progressBar.visibility = View.VISIBLE}

  }

  fun hideProgressBar() {
    runOnUiThread { binding.progressBar.visibility = View.GONE }
  }

  ///////////////////////////////////////////////////////////////////////////////////////
  ////               Distance Calculation       ////////////////////////////////
  companion object {
    const val DESTINATION = 1
  }

  var totalDistanceCM = 840   // In CM
  fun coveredDistanceMtr(coveredDistanceMtr: Float) {

  }







}
