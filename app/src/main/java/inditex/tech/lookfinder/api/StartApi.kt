package inditex.tech.lookfinder.api

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

suspend fun getApiResponse(photo: String, token: String): String {
    return withContext(Dispatchers.IO) {
        Log.d("API", "FOTO_EN_SI: $photo")

        val encodedPhoto = URLEncoder.encode(photo, "UTF-8")
        val url = URL("https://api.inditex.com/pubvsearch/products?image=$encodedPhoto")
        val connection = url.openConnection() as HttpURLConnection

        Log.d("API", "URL_FOTO: $url")


        try {
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            val response = StringBuilder()

            connection.inputStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    Log.d("API", line!!)
                    response.append(line).append("\n")
                }
            }
            response.toString()
        }
        finally {
            connection.disconnect()
        }

    }
}

suspend fun getNewToken(): String {
    return withContext(Dispatchers.IO) {
        val url = URL("https://auth.inditex.com:443/openam/oauth2/itxid/itxidmp/access_token")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Authorization",
                "Basic " + Base64.encodeToString(
                    "oauth-mkplace-oauthvbrgtfvchwexehqpbvpropro:vFR0pl6gqDKef{ij".toByteArray(),
                    Base64.NO_WRAP
                )
            )
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true  // Permitir envío de datos

            // Datos del body
            val postData = "grant_type=client_credentials&scope=technology.catalog.read"
            OutputStreamWriter(connection.outputStream).use { it.write(postData) }

            // Verifica si la respuesta es correcta (200 OK)
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }

                try {
                    val jsonObject = JSONObject(responseText)
                    return@withContext jsonObject.optString("id_token", null)  // Retorna id_token o null
                } catch (e: Exception) {
                    Log.e("API", "Error parseando JSON: ${e.message}")
                }
            } else {
                Log.e("API", "Error en la solicitud: Código ${connection.responseCode}")
            }

        } catch (e: Exception) {
            Log.e("API", "Error de conexión: ${e.message}")
        } finally {
            connection.disconnect()
        }

        return@withContext ""
    }
}