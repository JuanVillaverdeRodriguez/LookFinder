package inditex.tech.lookfinder.api

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL

const val API_URL = "https://lookfinderserver-production.up.railway.app/upload/"

suspend fun postImage(imageFile: File) {
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

    val url = URL(API_URL)
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