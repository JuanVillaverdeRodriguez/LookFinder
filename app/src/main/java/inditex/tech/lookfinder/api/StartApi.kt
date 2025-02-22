package inditex.tech.lookfinder.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

suspend fun getApiResponse(photo: String): String {
    return withContext(Dispatchers.IO) { // Ejecuta en un hilo de fondo
        val encodedPhoto = URLEncoder.encode(photo, "UTF-8")
        val url = URL("https://api.inditex.com/pubvsearch/products?image=$encodedPhoto")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiJZMjZSVjltUFc3dkc0bWF4NU80bDBBd2NpSVE9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiVzBTU0Y0clRtT2lVemsxbDNDaTcyZyIsInN1YiI6Im9hdXRoLW1rcGxhY2Utb2F1dGh2YnJndGZ2Y2h3ZXhlaHFwYnZwcm9wcm8iLCJhdWRpdFRyYWNraW5nSWQiOiJhOTEwMzY2ZC01ZWM5LTQ5ZWMtYTU5Mi1mZDc1Y2M4N2M2NDYtMTIwODE5NDA2IiwiY3VzdG9tIjp7ImNvbnN1bWVyT3JnSWQiOiJqdWFudmlsbGF2ZXJkZXJvZHJpZ3Vlel9nbWFpbC5jb20iLCJtYXJrZXRwbGFjZUNvZGUiOiJvcGVuLWRldmVsb3Blci1wb3J0YWwiLCJtYXJrZXRwbGFjZUFwcElkIjoiZTk4ZDcwMTMtMzU2My00M2M0LWE3MzItM2NmZDNjZTc0N2VkIn0sImlzcyI6Imh0dHBzOi8vYXV0aC5pbmRpdGV4LmNvbTo0NDMvb3BlbmFtL29hdXRoMi9pdHhpZC9pdHhpZG1wIiwidG9rZW5OYW1lIjoiaWRfdG9rZW4iLCJ1c2VySWQiOiJvYXV0aC1ta3BsYWNlLW9hdXRodmJyZ3RmdmNod2V4ZWhxcGJ2cHJvcHJvIiwiYXVkIjoib2F1dGgtbWtwbGFjZS1vYXV0aHZicmd0ZnZjaHdleGVocXBidnByb3BybyIsImlkZW50aXR5VHlwZSI6InNlcnZpY2UiLCJhenAiOiJvYXV0aC1ta3BsYWNlLW9hdXRodmJyZ3RmdmNod2V4ZWhxcGJ2cHJvcHJvIiwiYXV0aF90aW1lIjoxNzQwMTk4MjMzLCJzY29wZSI6Im1hcmtldCB0ZWNobm9sb2d5LmNhdGFsb2cucmVhZCBvcGVuaWQiLCJyZWFsbSI6Ii9pdHhpZC9pdHhpZG1wIiwidXNlclR5cGUiOiJleHRlcm5hbCIsImV4cCI6MTc0MDIwMTgzMywidG9rZW5UeXBlIjoiSldUVG9rZW4iLCJpYXQiOjE3NDAxOTgyMzMsImF1dGhMZXZlbCI6IjEifQ.pQB6hwBAHKt-NF42yu8o5U8qJp__mykqbcnLNL8BL03kzqH0HZt_mVtHZH_RGE5IXV5r88DutxHSbxI-op7lF1qWdzm6CJd-tqald1aQWie3ImXTLvk7KvItsdGI8b95XBFSnVFU24F92GHMgH44mzSZaMD0XF9f7ZzIeeLAN8TGSeHM9bLYId1KwBUznTcIKtIiR2ucg-45F734lInTG_zxmqexBo-ZbxzHq8PQubOBYbNEzJ2Da7Kskkb0_WkVOr9KMNIBqixx7pXB2fhFhdP-hT9QFsIfd6r_FQg81o_C2nTFtzwtxMC6dUOkUlOKoiHNtGaLl97A3Rjeldvppw")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            val response = StringBuilder()

            connection.inputStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    Log.d("API", line!!) // 🔥 Imprime cada línea
                    response.append(line).append("\n") // Guarda la respuesta completa
                }
            }
            response.toString() // Retorna la respuesta completa
        } finally {
            connection.disconnect()
        }

    }
}
