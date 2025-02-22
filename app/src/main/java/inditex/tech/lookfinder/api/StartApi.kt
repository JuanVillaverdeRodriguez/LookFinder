package inditex.tech.lookfinder.api

import android.util.Log
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
            connection.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiJZMjZSVjltUFc3dkc0bWF4NU80bDBBd2NpSVE9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiSE16SlRPNW5iTkVkcUlpTkpxVGxmQSIsInN1YiI6Im9hdXRoLW1rcGxhY2Utb2F1dGh2YnJndGZ2Y2h3ZXhlaHFwYnZwcm9wcm8iLCJhdWRpdFRyYWNraW5nSWQiOiJiYzE4MjA4Mi01OWZjLTQ5NjItYjllNC0xMjIwODg4NjEwZTEtMTIwMTE1NzkzIiwiY3VzdG9tIjp7ImNvbnN1bWVyT3JnSWQiOiJqdWFudmlsbGF2ZXJkZXJvZHJpZ3Vlel9nbWFpbC5jb20iLCJtYXJrZXRwbGFjZUNvZGUiOiJvcGVuLWRldmVsb3Blci1wb3J0YWwiLCJtYXJrZXRwbGFjZUFwcElkIjoiZTk4ZDcwMTMtMzU2My00M2M0LWE3MzItM2NmZDNjZTc0N2VkIn0sImlzcyI6Imh0dHBzOi8vYXV0aC5pbmRpdGV4LmNvbTo0NDMvb3BlbmFtL29hdXRoMi9pdHhpZC9pdHhpZG1wIiwidG9rZW5OYW1lIjoiaWRfdG9rZW4iLCJ1c2VySWQiOiJvYXV0aC1ta3BsYWNlLW9hdXRodmJyZ3RmdmNod2V4ZWhxcGJ2cHJvcHJvIiwiYXVkIjoib2F1dGgtbWtwbGFjZS1vYXV0aHZicmd0ZnZjaHdleGVocXBidnByb3BybyIsImlkZW50aXR5VHlwZSI6InNlcnZpY2UiLCJhenAiOiJvYXV0aC1ta3BsYWNlLW9hdXRodmJyZ3RmdmNod2V4ZWhxcGJ2cHJvcHJvIiwiYXV0aF90aW1lIjoxNzQwMTg5MzY1LCJzY29wZSI6Im1hcmtldCB0ZWNobm9sb2d5LmNhdGFsb2cucmVhZCBvcGVuaWQiLCJyZWFsbSI6Ii9pdHhpZC9pdHhpZG1wIiwidXNlclR5cGUiOiJleHRlcm5hbCIsImV4cCI6MTc0MDE5Mjk2NSwidG9rZW5UeXBlIjoiSldUVG9rZW4iLCJpYXQiOjE3NDAxODkzNjUsImF1dGhMZXZlbCI6IjEifQ.RWiccUD_sdFOyLy31w5VfPFU0-ZTgAEGQQgQ6m2zRdNbLVWf9igb3bF0BIMkHBOiQRZWgtHas8aDd1H288EsdASk-n7jjYmYB9V9Oi7WDElplDPJS4wWiIGNtBjfaufbltDzbRemMjCAbJ9-4dwD7zjr2tFcDdnztq2AU8qkV2QVDdtUINfwV0tvhDUZX2NqqYdHAvY2OmeRHsaa8_umCItfXM0P4DBHV1r0Ce3l3HadY3QB20pHyJ3TBfoRtfzDwVbQqkx0ReGUhLG2JkJBFkJFE8y2fxmLE9PfCLeeongLudBZuJX2xGyk4tN1632xrsb0b-U1LD25F3LVWZvXfA")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
