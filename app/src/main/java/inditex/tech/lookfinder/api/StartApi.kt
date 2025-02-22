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
            connection.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiJZMjZSVjltUFc3dkc0bWF4NU80bDBBd2NpSVE9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiUkhJTzJydFRwNTdfbXAyQjZhb1BLQSIsInN1YiI6Im9hdXRoLW1rcGxhY2Utb2F1dGh2YnJndGZ2Y2h3ZXhlaHFwYnZwcm9wcm8iLCJhdWRpdFRyYWNraW5nSWQiOiJiYzE4MjA4Mi01OWZjLTQ5NjItYjllNC0xMjIwODg4NjEwZTEtMTE5NzA4MTc0IiwiY3VzdG9tIjp7ImNvbnN1bWVyT3JnSWQiOiJqdWFudmlsbGF2ZXJkZXJvZHJpZ3Vlel9nbWFpbC5jb20iLCJtYXJrZXRwbGFjZUNvZGUiOiJvcGVuLWRldmVsb3Blci1wb3J0YWwiLCJtYXJrZXRwbGFjZUFwcElkIjoiZTk4ZDcwMTMtMzU2My00M2M0LWE3MzItM2NmZDNjZTc0N2VkIn0sImlzcyI6Imh0dHBzOi8vYXV0aC5pbmRpdGV4LmNvbTo0NDMvb3BlbmFtL29hdXRoMi9pdHhpZC9pdHhpZG1wIiwidG9rZW5OYW1lIjoiaWRfdG9rZW4iLCJ1c2VySWQiOiJvYXV0aC1ta3BsYWNlLW9hdXRodmJyZ3RmdmNod2V4ZWhxcGJ2cHJvcHJvIiwiYXVkIjoib2F1dGgtbWtwbGFjZS1vYXV0aHZicmd0ZnZjaHdleGVocXBidnByb3BybyIsImlkZW50aXR5VHlwZSI6InNlcnZpY2UiLCJhenAiOiJvYXV0aC1ta3BsYWNlLW9hdXRodmJyZ3RmdmNod2V4ZWhxcGJ2cHJvcHJvIiwiYXV0aF90aW1lIjoxNzQwMTgxMDY4LCJzY29wZSI6Im1hcmtldCB0ZWNobm9sb2d5LmNhdGFsb2cucmVhZCBvcGVuaWQiLCJyZWFsbSI6Ii9pdHhpZC9pdHhpZG1wIiwidXNlclR5cGUiOiJleHRlcm5hbCIsImV4cCI6MTc0MDE4NDY2OCwidG9rZW5UeXBlIjoiSldUVG9rZW4iLCJpYXQiOjE3NDAxODEwNjgsImF1dGhMZXZlbCI6IjEifQ.e_n6WsJQQGGE7FJ9YWY3fMHD-ya5WmDHDSQ8LRfzaXJnF8ldfF9X6lzoQg_ADxIGV2XY5E4FJ8MwBmfKtM4kTri-XLX7q1kk5SLlVITd1_fsiOvCYP6-EWUPL5hbbErTo1oBbE4cx_6RZD3XZ9CT36cxmQ3yghlrcPzEIfAx41B1ckmiNeg5wwiDI6_mw3Fr-D__NaNx7YcNk52VmQeMmEPy8tUVwnRkIUNH8_E0HWQpnFBFVyqFxs-ZrkOYjxgly0OH8jBKw49FDPhnVK7wS1YQMDI5IMBqJkCfBuamhk1nzSCWTMnv_MNG51fp-A5bwk_29b43PMYEqlvs6vFwZg")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
