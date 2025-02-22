package inditex.tech.lookfinder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import inditex.tech.lookfinder.api.getApiResponse
import inditex.tech.lookfinder.api.getNewToken
import inditex.tech.lookfinder.dtos.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class PostViewModel : ViewModel() {
    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("API", "FUE GUAY")
                val token = getNewToken()
                val res = getApiResponse("https://lookfinderserver-production.up.railway.app/uploads/image.jpg", token)

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
}
