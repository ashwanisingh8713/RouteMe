package com.ar.navigation

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ar.common.helpers.DisplayRotationHelper
import com.ar.common.helpers.TrackingStateHelper
import com.ar.common.samplerender.*
import com.ar.common.samplerender.arcore.BackgroundRenderer
import com.ar.common.samplerender.arcore.PlaneRenderer
import com.ar.common.samplerender.arcore.SpecularCubemapFilter
import com.google.ar.core.*
import com.ar.navigation.pathmodel.RouteAnchor
import com.ar.navigation.util.KotlinUtil
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.NotYetAvailableException
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Created by Ashwani Kumar Singh on 11,March,2023.
 */


/** Renders the HelloAR application using our example Renderer. */
class HelloArRenderer(val activity: ArRenderingActivity) :
    SampleRender.Renderer, DefaultLifecycleObserver {
    companion object {
        val TAG = "HelloArRenderer"

        // See the definition of updateSphericalHarmonicsCoefficients for an explanation of these
        // constants.
        private val sphericalHarmonicFactors =
            floatArrayOf(
                0.282095f,
                -0.325735f,
                0.325735f,
                -0.325735f,
                0.273137f,
                -0.273137f,
                0.078848f,
                -0.273137f,
                0.136569f
            )

        private val Z_NEAR = 0.1f

            private val Z_FAR = 5f
//        private val Z_FAR = 10.1f

        // Assumed distance from the device camera to the surface on which user will try to place
        // objects.
        // This value affects the apparent scale of objects while the tracking method of the
        // Instant Placement point is SCREENSPACE_WITH_APPROXIMATE_DISTANCE.
        // Values in the [0.2, 2.0] meter range are a good choice for most AR experiences. Use lower
        // values for AR experiences where users are expected to place objects on surfaces close to the
        // camera. Use larger values for experiences where the user will likely be standing and trying
        // to
        // place an object on the ground or floor in front of them.
        val APPROXIMATE_DISTANCE_METERS = 2.0f

        val CUBEMAP_RESOLUTION = 16
        val CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES = 32
    }

    lateinit var render: SampleRender
    lateinit var planeRenderer: PlaneRenderer
    lateinit var backgroundRenderer: BackgroundRenderer
    lateinit var virtualSceneFramebuffer: Framebuffer
    var hasSetTextureNames = false

    // Point Cloud
    lateinit var pointCloudVertexBuffer: VertexBuffer
    lateinit var pointCloudMesh: Mesh
    lateinit var pointCloudShader: Shader

    // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
    // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
    private var lastPointCloudTimestamp: Long = 0

    // Virtual object (ARCore pawn)
    private lateinit var virtualObjectMesh: Mesh
    private lateinit var virtualObjectShader: Shader
    private lateinit var virtualObjectAlbedoTexture: Texture

    // Environmental HDR
    private lateinit var dfgTexture: Texture
    private lateinit var cubemapFilter: SpecularCubemapFilter

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16) // view x model

    private val modelViewProjectionMatrix = FloatArray(16) // projection x view x model

    private val sphericalHarmonicsCoefficients = FloatArray(9 * 3)
    private val viewInverseMatrix = FloatArray(16)
    private val worldLightDirection = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
    private val viewLightDirection = FloatArray(4) // view x world light direction

    val session
        get() = activity.arCoreSessionHelper.session

    private val displayRotationHelper = DisplayRotationHelper(activity)
    private val trackingStateHelper = TrackingStateHelper(activity)

    override fun onResume(owner: LifecycleOwner) {
        displayRotationHelper.onResume()
        hasSetTextureNames = false
        Log.i("", "")

    }

    override fun onPause(owner: LifecycleOwner) {
        displayRotationHelper.onPause()
    }

    override fun onSurfaceCreated(render: SampleRender) {
        // Prepare the rendering objects.
        // This involves reading shaders and 3D model files, so may throw an IOException.
        try {
            planeRenderer = PlaneRenderer(render)
            backgroundRenderer = BackgroundRenderer(render)
            virtualSceneFramebuffer = Framebuffer(render, /*width=*/ 1, /*height=*/ 1)

            cubemapFilter =
                SpecularCubemapFilter(
                    render,
                    CUBEMAP_RESOLUTION,
                    CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES
                )
            // Load environmental lighting values lookup table
            dfgTexture =
                Texture(
                    render,
                    Texture.Target.TEXTURE_2D,
                    Texture.WrapMode.CLAMP_TO_EDGE,
                    /*useMipmaps=*/ false
                )
            // The dfg.raw file is a raw half-float texture with two channels.
            val dfgResolution = 64
            val dfgChannels = 2
            val halfFloatSize = 2

            val buffer: ByteBuffer =
                ByteBuffer.allocateDirect(dfgResolution * dfgResolution * dfgChannels * halfFloatSize)
            activity.assets.open("models/dfg.raw").use { it.read(buffer.array()) }

            // SampleRender abstraction leaks here.
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dfgTexture.textureId)
            GLError.maybeThrowGLException("Failed to bind DFG texture", "glBindTexture")
            GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D,
                /*level=*/ 0,
                GLES30.GL_RG16F,
                /*width=*/ dfgResolution,
                /*height=*/ dfgResolution,
                /*border=*/ 0,
                GLES30.GL_RG,
                GLES30.GL_HALF_FLOAT,
                buffer
            )
            GLError.maybeThrowGLException("Failed to populate DFG texture", "glTexImage2D")

            // Point cloud
            pointCloudShader =
                Shader.createFromAssets(
                    render,
                    "shaders/point_cloud.vert",
                    "shaders/point_cloud.frag",
                    /*defines=*/ null
                )
                    .setVec4(
                        "u_Color",
                        floatArrayOf(31.0f / 255.0f, 188.0f / 255.0f, 210.0f / 255.0f, 1.0f)
                    )
                    .setFloat("u_PointSize", 5.0f)

            // four entries per vertex: X, Y, Z, confidence
            pointCloudVertexBuffer =
                VertexBuffer(render, /*numberOfEntriesPerVertex=*/ 4, /*entries=*/ null)
            val pointCloudVertexBuffers = arrayOf(pointCloudVertexBuffer)
            pointCloudMesh =
                Mesh(
                    render,
                    Mesh.PrimitiveMode.POINTS, /*indexBuffer=*/
                    null,
                    pointCloudVertexBuffers
                )

            // Virtual object to render (ARCore pawn)
            virtualObjectAlbedoTexture =
                Texture.createFromAsset(
                    render,
                    "models/pawn_albedo.png",
                    Texture.WrapMode.CLAMP_TO_EDGE,
                    Texture.ColorFormat.SRGB
                )


            val virtualObjectPbrTexture =
                Texture.createFromAsset(
                    render,
                    "models/pawn_roughness_metallic_ao.png",
                    Texture.WrapMode.CLAMP_TO_EDGE,
                    Texture.ColorFormat.LINEAR
                )
            virtualObjectMesh = Mesh.createFromAsset(render, "models/pawn.obj")
            virtualObjectShader =
                Shader.createFromAssets(
                    render,
                    "shaders/environmental_hdr.vert",
                    "shaders/environmental_hdr.frag",
                    mapOf("NUMBER_OF_MIPMAP_LEVELS" to cubemapFilter.numberOfMipmapLevels.toString())
                )
                    .setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
                    .setTexture(
                        "u_RoughnessMetallicAmbientOcclusionTexture",
                        virtualObjectPbrTexture
                    )
                    .setTexture("u_Cubemap", cubemapFilter.filteredCubemapTexture)
                    .setTexture("u_DfgTexture", dfgTexture)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read a required asset file", e)
            showError("Failed to read a required asset file: $e")
        }
    }

    override fun onSurfaceChanged(render: SampleRender, width: Int, height: Int) {
        displayRotationHelper.onSurfaceChanged(width, height)
        virtualSceneFramebuffer.resize(width, height)
    }

    override fun onDrawFrame(render: SampleRender) {
        val session = session ?: return


        // Texture names should only be set once on a GL thread unless they change. This is done during
        // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
        // initialized during the execution of onSurfaceCreated.
        if (!hasSetTextureNames) {
            session.setCameraTextureNames(intArrayOf(backgroundRenderer.cameraColorTexture.textureId))
            hasSetTextureNames = true
        }

        // -- Update per-frame state

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session)

        // Obtain the current frame from ARSession. When the configuration is set to
        // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
        // camera frame rate.
        val frame =
            try {
                session.update()
            } catch (e: CameraNotAvailableException) {
                Log.e(TAG, "Camera not available during onDrawFrame", e)
                showError("Camera not available. Try restarting the app.")
                return
            }

        val camera = frame.camera

        // Update BackgroundRenderer state to match the depth settings.
        try {
            backgroundRenderer.setUseDepthVisualization(
                render,
                activity.depthSettings.depthColorVisualizationEnabled()
            )
            backgroundRenderer.setUseOcclusion(
                render,
                activity.depthSettings.useDepthForOcclusion()
            )
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read a required asset file", e)
            showError("Failed to read a required asset file: $e")
            return
        }

        // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
        // used to draw the background camera image.
        backgroundRenderer.updateDisplayGeometry(frame)
        val shouldGetDepthImage =
            activity.depthSettings.useDepthForOcclusion() ||
                    activity.depthSettings.depthColorVisualizationEnabled()
        if (camera.trackingState == TrackingState.TRACKING && shouldGetDepthImage) {
            try {
                val depthImage = frame.acquireDepthImage16Bits()
                backgroundRenderer.updateCameraDepthTexture(depthImage)
                depthImage.close()
            } catch (e: NotYetAvailableException) {
                // This normally means that depth data is not available yet. This is normal so we will not
                // spam the logcat with this.
            }
        }

        addDisplayAnchor(camera.displayOrientedPose)

        // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
        trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

        // Show a message based on whether tracking has failed, if planes are detected, and if the user
        // has placed any objects.
        val message: String? = when {
            camera.trackingState == TrackingState.PAUSED && camera.trackingFailureReason == TrackingFailureReason.NONE -> {
//                mDisplayedAnchors.clear()
                "Searching for surfaces..."
            }
            camera.trackingState == TrackingState.PAUSED ->
                TrackingStateHelper.getTrackingFailureReasonString(camera)
            session.hasTrackingPlane() && mHoldingAnchors.isEmpty() -> {
                loadAllAnchors(session)
                addDisplayAnchor(camera.displayOrientedPose)
                "Tap on a surface to place an object."
            }
            session.hasTrackingPlane() && mDisplayedAnchors.isNotEmpty() -> null
            else -> "Searching for surfaces..."
        }
        if (message == null) {
            activity.view.snackbarHelper.hide(activity)
        } else {
            activity.view.snackbarHelper.showMessage(activity, message)
        }

        // -- Draw background
        if (frame.timestamp != 0L) {
            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
            // drawing possible leftover data from previous sessions if the texture is reused.
            backgroundRenderer.drawBackground(render)
        }

        // If not tracking, don't draw 3D objects.
        if (camera.trackingState == TrackingState.PAUSED) {
            return
        }

        // Get projection matrix.
        camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR)

        // Get camera matrix and draw.
        camera.getViewMatrix(viewMatrix, 0)
        frame.acquirePointCloud().use { pointCloud ->
            if (pointCloud.timestamp > lastPointCloudTimestamp) {
                pointCloudVertexBuffer.set(pointCloud.points)
                lastPointCloudTimestamp = pointCloud.timestamp
            }
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            pointCloudShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
            render.draw(pointCloudMesh, pointCloudShader)
        }

        // Visualize planes.
        planeRenderer.drawPlanes(
            render,
            session.getAllTrackables<Plane>(Plane::class.java),
            camera.displayOrientedPose,
            projectionMatrix
        )

        // Visualize anchors created by touch.
        render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f)

        for (routeAnchor in mDisplayedAnchors) {
            val anchor = routeAnchor.anchor!!
            if (anchor.trackingState == TrackingState.TRACKING) {
//                Log.i("Ashwani", "makeVisible :: true")
                anchor.pose.toMatrix(modelMatrix, 0)
                // Calculate model/view/projection matrices
                Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
                Matrix.multiplyMM(
                    modelViewProjectionMatrix,
                    0,
                    projectionMatrix,
                    0,
                    modelViewMatrix,
                    0
                )
                // Update shader properties and draw
                virtualObjectShader.setMat4("u_ModelView", modelViewMatrix)
                virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix)

                virtualObjectShader.setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
                render.draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer)
            } else {
//                Log.i("Ashwani", "makeVisible :: false")
            }
        }

        // Compose the virtual scene with the background.
        backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR)
    }

    /** Checks if we detected at least one plane. */
    private fun Session.hasTrackingPlane() =
        getAllTrackables(Plane::class.java).any { it.trackingState == TrackingState.TRACKING }


    private fun showError(errorMessage: String) {
        activity.view.snackbarHelper.showError(activity, errorMessage)
    }

    private fun showMsg(errorMessage: String) {
        activity.view.snackbarHelper.showMsg(activity, errorMessage)
    }

    // It holds displayed Anchors
    private var mDisplayedAnchors = mutableListOf<RouteAnchor>()

    // It holds all Anchors
    private var mHoldingAnchors = mutableListOf<RouteAnchor>()
    private var mAllAnchors = 0
    private var mConsolidatedDistance: Float = 0f
    private var mCoveredDistanceByCamera: Float = 0f
    private var mDisplayedAnchorCount = 0
    private var mDistanceFromStartAnchor = 0f;

    private fun addDisplayAnchor(cameraPose: Pose) {

        // To setting camera axis
        val cameraAxis = FloatArray(3)
        cameraAxis[0] = cameraPose.tx()
        cameraAxis[1] = cameraPose.ty()
        cameraAxis[2] = cameraPose.tz()

        if (mHoldingAnchors.isEmpty()) {
            return
        }
        if (mHoldingAnchors.size < mDisplayedAnchors.size) {
            return
        }
        if (mDisplayedAnchors.size == 0) {
            val routeAnchor: RouteAnchor = mHoldingAnchors[0]
            routeAnchor.makeVisible = true
            mDisplayedAnchors.add(routeAnchor)

            val anchorPose = routeAnchor.anchor!!.pose
            val distanceOfCameraNdAnchor = KotlinUtil.calculateDistance(cameraPose, anchorPose)
            mCoveredDistanceByCamera += distanceOfCameraNdAnchor

            mDistanceFromStartAnchor = routeAnchor.distanceCovered

            // Camera Degree
            var angleBtwAnchorCamera = 0.0
            if(mDisplayedAnchorCount-2 >= 0) {
                val previousRouteAnchor: RouteAnchor =
                    mHoldingAnchors[mDisplayedAnchorCount - 2]
                val previousAnchorPose = previousRouteAnchor.anchor!!.pose
                angleBtwAnchorCamera =
                    KotlinUtil.vectorAngle(previousAnchorPose, cameraPose, anchorPose)
            }

            mDisplayedAnchorCount++

            // Logs
            Log.i("Ashwani", "+++++ Added Anchor Count :: $mDisplayedAnchorCount")
            Log.i("Ashwani", "Distance From Start Anchor :: $mDistanceFromStartAnchor")
            Log.i("Ashwani", "Direction For Next Anchor :: ${routeAnchor.directionToNext}")
            Log.i("Ashwani", "Angle Between Added Anchor and Camera :: $angleBtwAnchorCamera")
            Log.i("Ashwani", "Angle Between Added Anchor and Next Anchor :: ${routeAnchor.angle}")
            Log.i("Ashwani", "Anchor Axis :: ${routeAnchor.anchorAxis.contentToString()}")
            Log.i("Ashwani", "Camera Axis :: ${cameraAxis.contentToString()}")
            Log.i("Ashwani", "================================================== :: ")


        } else {
            if(mAllAnchors <= mDisplayedAnchorCount-1) {
                return
            }
            val distanceCoveredByCamera = KotlinUtil.calculateDistance(cameraPose, mHoldingAnchors[0].anchor!!.pose)*constantReduction()
            Log.i("Ashwani", "distanceCoveredByCamera :: $distanceCoveredByCamera")

//            if (mDistanceFromStartAnchor <= distanceCoveredByCamera)
            if (mDistanceFromStartAnchor <= 100)
            {

                val routeAnchor: RouteAnchor = mHoldingAnchors[mDisplayedAnchorCount-1]
                val anchorPose = routeAnchor.anchor!!.pose
                mDistanceFromStartAnchor = routeAnchor.distanceCovered
                routeAnchor.makeVisible = true
                mDisplayedAnchors.add(routeAnchor)

                // Camera Degree
                var angleBtwAnchorCamera = 0.0
                if(mDisplayedAnchorCount-2 >= 0) {
                    val previousRouteAnchor: RouteAnchor =
                        mHoldingAnchors[mDisplayedAnchorCount - 2]
                    val previousAnchorPose = previousRouteAnchor.anchor!!.pose
                    angleBtwAnchorCamera =
                        KotlinUtil.vectorAngle(previousAnchorPose, cameraPose, anchorPose)
                }

                mDisplayedAnchorCount++

                // Logs
                Log.i("Ashwani", "+++++ Added Anchor Count :: $mDisplayedAnchorCount")
                Log.i("Ashwani", "Distance From Start Anchor :: $mDistanceFromStartAnchor")
                Log.i("Ashwani", "Direction For Next Anchor :: ${routeAnchor.directionToNext}")
                Log.i("Ashwani", "Angle Between Added Anchor and Camera :: $angleBtwAnchorCamera")
                Log.i("Ashwani", "Angle Between Added Anchor and Next Anchor :: ${routeAnchor.angle}")
                Log.i("Ashwani", "Anchor Axis :: ${routeAnchor.anchorAxis.contentToString()}")
                Log.i("Ashwani", "Camera Axis :: ${cameraAxis.contentToString()}")
                Log.i("Ashwani", "================================================== :: ")

            } else {
//                Log.i("Ashwani", "ELSE ****************** :: false")
            }

        }


    }

    /**
     * Loads all Anchors of the Path
     */
    private fun loadAllAnchors(session: Session) {
        val pair = KotlinUtil.getRouteAnchors(session)
        mHoldingAnchors = pair.first//.subList(1, 18)
        mConsolidatedDistance = pair.second
        mAllAnchors = mHoldingAnchors.size
        Log.i("Ashwani", "Consolidated Distance:: $mConsolidatedDistance")
    }

    private fun constantReduction(): Float {
//        return 9 / 10f
//        return 3 / 3.8f
        return 4/3f
    }

}

/**
 * Associates an Anchor with the trackable it was attached to. This is used to be able to check
 * whether or not an Anchor originally was attached to an {@link InstantPlacementPoint}.
 */
