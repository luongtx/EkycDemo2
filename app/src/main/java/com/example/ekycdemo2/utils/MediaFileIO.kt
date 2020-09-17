package com.example.ekycdemo2.utils

import android.content.Context
import com.example.ekycdemo2.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MediaFileIO {
    companion object {
        fun createMediaFile(ctx: Context): File {
            return File(
                getOutputDirectory(ctx),
                SimpleDateFormat(
                    Constants.FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg"
            )
        }

        private fun getOutputDirectory(ctx: Context): File {
            val mediaDir = ctx.externalMediaDirs.firstOrNull()?.let {
                File(it, ctx.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else ctx.filesDir
        }
    }
}