package inditex.tech.lookfinder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inditex.tech.lookfinder.api.getApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostViewModel : ViewModel() {
    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("API", "FUE GUAY")
                val res = getApiResponse("https://lookfinderserver-production.up.railway.app/uploads/image.jpg")
                Log.d("API", res)
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}
