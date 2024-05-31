package com.example.infobuscopy.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.infobuscopy.R
import com.example.infobuscopy.presentation.navigation.NavHostController
import com.example.infobuscopy.presentation.ui.theme.InfobusCopyTheme
import com.example.infobuscopy.util.LruCacheImpl
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfobusCopyTheme {
                Scaffold { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        NavHostController()
                    }
                }
            }
        }
    }
}

