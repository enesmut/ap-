package com.seninadiniz.apiogren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.seninadiniz.apiogren.ui.theme.ApiogrenTheme
import com.seninadiniz.apiogren.ui.theme.NewsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiogrenTheme {
                NewsScreen()
            }
        }
    }
}