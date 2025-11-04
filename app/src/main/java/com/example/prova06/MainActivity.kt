package com.example.prova06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prova06.navigation.ImobiNavGraph
import com.example.prova06.ui.auth.AuthViewModel
import com.example.prova06.ui.theme.ImobiControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImobiControlTheme {
                val authViewModel: AuthViewModel = viewModel()
                ImobiNavGraph(authViewModel = authViewModel)
            }
        }
    }
}
