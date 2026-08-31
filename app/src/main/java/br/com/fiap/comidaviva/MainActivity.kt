package br.com.fiap.comidaviva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.fiap.comidaviva.navigation.NavigationRoutes
import br.com.fiap.comidaviva.ui.theme.ComidaVivaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComidaVivaTheme {
                NavigationRoutes()
            }
        }
    }
}