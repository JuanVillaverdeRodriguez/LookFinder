package inditex.tech.lookfinder.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun getApiResponse(photo: String): String {
    return withContext(Dispatchers.IO) { // Ejecuta en un hilo de fondo
        val url = URL("https://api.inditex.com/pubvsearch/products?image=$photo")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
