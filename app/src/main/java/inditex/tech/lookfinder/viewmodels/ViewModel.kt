package inditex.tech.lookfinder.viewmodels

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import inditex.tech.lookfinder.api.getApiResponse
import inditex.tech.lookfinder.api.getNewToken
import inditex.tech.lookfinder.api.postImage
import inditex.tech.lookfinder.dtos.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class PostViewModel : ViewModel() {
    private val _photoUrl = MutableStateFlow("")
    val photoUrl = _photoUrl.asStateFlow()

    fun fetchPosts(fotoURL: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("API", "FUE GUAY")
                val token = getNewToken()
                val res = getApiResponse(fotoURL, token)

                Log.d("API", "JSON: $res")

                // Comprobar si la respuesta es `{"message":"Unauthorized"}`
                try {
                    val jsonObject = JSONObject(res)
                    if (jsonObject.has("message") && jsonObject.getString("message") == "Unauthorized") {
                        Log.e("API", "Se ha devuelto Unauthorized")
                        return@launch
                    }
                } catch (e: JSONException) {
                    // No es un objeto JSON, entonces es un array y podemos seguir adelante
                }


                val gson = Gson()
                val productListType = object : TypeToken<List<Product>>() {}.type
                val products: List<Product> = gson.fromJson(res, productListType)

                for (product in products) {
                    Log.d("API", "Producto: ${product.name}, Precio: ${product.price.value.current} ${product.price.currency}, Link: ${product.link}")
                }

            } catch (e: JsonSyntaxException) {
                Log.e("API", "Error al parsear JSON: ${e.message}")
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }

    fun uploadPhoto(photoUrl: String) : String{
        var url = ""
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val image = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), photoUrl)
                _photoUrl.value = postImage(image)
            } catch (e: Exception) {
                Log.e("API", "Error al subir la foto: ${e.message}")
            }

        }

        return url
    }

}
