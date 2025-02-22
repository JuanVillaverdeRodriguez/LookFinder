package inditex.tech.lookfinder.api

import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

fun postImageWithLimit(urlString: String, imageFile: File) {
    val maxSize = 7 * 1024 * 1024 // 7MB en bytes

    if (!imageFile.exists()) {
        println("Error: El archivo no existe.")
        return
    }

    if (imageFile.length() > maxSize) {
        println("Error: La imagen es demasiado grande. Tamaño máximo permitido: 7MB.")
        return
    }

    val boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
    val lineEnd = "\r\n"
    val twoHyphens = "--"

    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true
    connection.doInput = true
    connection.useCaches = false
    connection.setRequestProperty("Connection", "Keep-Alive")
    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

    val outputStream = DataOutputStream(BufferedOutputStream(connection.outputStream, 8192))

    try {
        // Escribe la cabecera del archivo
        outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"${imageFile.name}\"$lineEnd")
        outputStream.writeBytes("Content-Type: image/jpeg$lineEnd")
        outputStream.writeBytes(lineEnd)

        // Envía el archivo en fragmentos
        val fileInputStream = BufferedInputStream(FileInputStream(imageFile), 8192)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        fileInputStream.close()

        // Cierra multipart/form-data
        outputStream.writeBytes(lineEnd)
        outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")
        outputStream.flush()
        outputStream.close()

        // Lee la respuesta del servidor
        val responseCode = connection.responseCode
        val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

        println("Código de respuesta: $responseCode")
        println("Respuesta del servidor: $responseMessage")
    } catch (e: Exception) {
        println("Error al enviar la imagen: ${e.message}")
    } finally {
        connection.disconnect()
    }
}

//--------------------------------------------
//OBTENER RUTA DE FOTO DESDE GALERIA
//--------------------------------------------
/*
const val PICK_IMAGE_REQUEST = 1

fun openGallery() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    startActivityForResult(intent, PICK_IMAGE_REQUEST)
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
        val imageUri: Uri? = data.data
        if (imageUri != null) {
            val file = File(getRealPathFromURI(imageUri))
            postImageWithLimit("https://miapi.com/upload", file) // Llamamos a la función de subida
        }
    }
}

// Convertir Uri a File Path (Funciona con imágenes de la galería)
fun getRealPathFromURI(contentUri: Uri): String {
    var filePath = ""
    val cursor = contentResolver.query(contentUri, null, null, null, null)
    if (cursor != null) {
        cursor.moveToFirst()
        val index = cursor.getColumnIndex("_data")
        filePath = if (index >= 0) cursor.getString(index) else ""
        cursor.close()
    }
    return filePath
}
*/