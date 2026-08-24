package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageHelper {
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, prefix: String = "item_img"): String? {
        return try {
            val filename = "${prefix}_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, filename)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            null
        }
    }
}
