package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageHelper {

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
        return try {
            val directory = File(context.filesDir, "item_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "item_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val file = File(directory, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBase64ToInternalStorage(context: Context, base64String: String): String? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size) ?: return null
            saveBitmapToInternalStorage(context, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
