package inditex.tech.lookfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import inditex.tech.lookfinder.api.getApiResponse
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import inditex.tech.lookfinder.ui.theme.LookFinderTheme
import inditex.tech.lookfinder.viewmodels.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: PostViewModel = PostViewModel()

        setContent {
            LookFinderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )

                        //PUNTO DE CONEXION DE LA API
                        //RECIBE: UNA URL DE UNA FOTO
                        //DEVUELVE: STRING CON RESPUESTA DE LA API
                        LaunchedEffect(Unit) {
                            viewModel.fetchPosts("https://lookfinderserver-production.up.railway.app/uploads/image.jpg")
                        }
                        //----------------------------------------------------------------
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LookFinderTheme {
        Greeting("Android")
    }
}