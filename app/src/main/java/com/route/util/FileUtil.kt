package com.route.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.route.routeme.BuildConfig
import java.io.File
import java.time.LocalDateTime


/**
 * Created by Ashwani Kumar Singh on 03,March,2023.
 */
object FileUtil {

    private var file: File? = null

    fun createFile(context: Context) {
        if (file == null) {
            file = File(context.cacheDir, "ApiRequest.txt")
            var iscreate = file?.createNewFile()
            Log.i("", "")
        }
    }

    @JvmStatic
    fun writeFile(requestContent: String) {
        val current = LocalDateTime.now()
        val contentWithTime = "\n $current ::  $requestContent"
        file?.appendText(contentWithTime)

    }

    fun shareLogFile(activity: FragmentActivity) {
        if (file != null) {
            val uri: Uri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.type = "application/txt"
            share.putExtra(Intent.EXTRA_STREAM, uri)
//            share.setPackage("com.whatsapp")
            activity.startActivity(share)
        }
    }
}