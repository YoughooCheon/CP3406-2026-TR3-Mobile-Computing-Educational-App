package com.example.cp3406_assessment3_younghoocheon

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cp3406_assessment3_younghoocheon.ui.navigation.AppNavHost
import com.example.cp3406_assessment3_younghoocheon.ui.theme.MemoryDeskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            MemoryDeskTheme {
                AppNavHost()
            }
        }
    }
}