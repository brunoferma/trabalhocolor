package com.devventure.colormyviews

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Função salva o Bitmap gerado da view como um file em um diretório privado, utilizando getExternalFilesDir(),
 * portanto os arquivos serão removidos quando o app for desinstalado
 * @param imageBitmap imagem em bitmap a ser salva
 * @param filename nome do arquivo a ser salvo
 * @param context contexto da activity
 * @return Uri
 */
fun Context.saveScreenshot(imageBitmap: Bitmap, filename: String , context: Context): Uri {
    val dirPath = applicationContext.filesDir
    val file = File(dirPath, "$filename.jpg")
    val fileOutputStream = FileOutputStream(file)
    try {
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
        fileOutputStream.apply {
            flush()
            close()
        }
        return FileProvider.getUriForFile(
            context,
            "com.devventure.colormyviews.provider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return Uri.EMPTY
    }
}