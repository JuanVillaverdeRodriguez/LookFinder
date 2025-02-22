package inditex.tech.lookfinder

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


        fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {
        // Crear un archivo en el almacenamiento interno
        val file = File(context.filesDir, fileName)

        try {
            // Abrir un FileOutputStream para escribir el archivo
            val fos: OutputStream = FileOutputStream(file)
            // Comprimir y guardar la imagen en el archivo
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Retornar la ruta del archivo guardado
        return file.absolutePath
    }

