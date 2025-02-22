package inditex.tech.lookfinder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class PostViewModel : ViewModel() {
    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val posts = RetrofitClient.apiService.getPosts()
                posts.forEach { post ->
                    println("Title: ${post.title}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}
