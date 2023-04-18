package com.route.routeme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.route.fragment.DocumentFragment
import com.route.routeme.databinding.ActivityMainBinding
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig

/**
 * Created by Ashwani Kumar Singh on 18,April,2023.
 */
class MainActivity:AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private val scanCustomCode = registerForActivityResult(ScanCustomCode(), ::listenBarCodeResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        GetDeeplinkUrl(intent)

        // set Windows Flags to Full Screen
        // using setFlags function

        // set Windows Flags to Full Screen
        // using setFlags function
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


    }


    /**
     * Deeplink/Applink Url
     */
    private fun GetDeeplinkUrl(intent: Intent) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this,
                OnSuccessListener { pendingDynamicLinkData ->
                    Log.i("", "")
                    if (pendingDynamicLinkData == null) {
                        launchScanner()
                        return@OnSuccessListener
                    }
                    val uri = pendingDynamicLinkData.link
                    addRouteListFragment(uri.toString())

                })
            .addOnFailureListener(
                this
            ) {
                launchScanner()
            }
    }

    /**
     * Callback to get scanned Url
     */
    private fun listenBarCodeResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                var scannedUrl = result.content.rawValue
                addRouteListFragment(scannedUrl)
            }
            QRResult.QRUserCanceled -> {
                finish()
                showSnackBar("User canceled")
            }
            QRResult.QRMissingPermission -> {
                showSnackBar("Missing permission")
            }

            is QRResult.QRError -> {
                val msg = "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
                showSnackBar(msg)
            }
        }

    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(binding!!.root, msg, Snackbar.LENGTH_LONG).apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.run {
                maxLines = 5
                setTextIsSelectable(true)
            }
        }.show()
    }


    private fun addRouteListFragment(url: String) {
        val documentFragment = DocumentFragment.getInstance(url)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, documentFragment).commit()
    }

    private fun launchScanner() {
        scanCustomCode.launch(
            ScannerConfig.build {
                setOverlayStringRes(R.string.scan_barcode) // string resource used for the scanner overlay
                setHapticSuccessFeedback(false) // enable (default) or disable haptic feedback when a barcode was detected
                setShowTorchToggle(false) // show or hide (default) torch/flashlight toggle button
                setShowCloseButton(true) // show or hide (default) close button
                setHorizontalFrameRatio(1.0f) // set the horizontal overlay ratio (default is 1 / square frame)
                setUseFrontCamera(false) // use the front camera
            }
        )
    }

}