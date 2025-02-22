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
                getApiResponse("https://drive.google.com/file/d/1loi5zPz8nO8wYrBHxtlOHSrl8FjI4Z-z/view?usp=drive_link")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}
