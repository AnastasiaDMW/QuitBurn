package com.example.quitburn

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import com.example.quitburn.data.PermissionManager
import com.example.quitburn.repository.MoodRepository
import com.example.quitburn.repository.ProgressRepository
import com.example.quitburn.ui.home.HomeViewModel
import com.example.quitburn.ui.home.HomeViewModelProvider
import com.example.quitburn.ui.theme.QuitBurnTheme
import com.example.quitburn.util.AppStateManager

class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundColor = ContextCompat.getColor(this, R.color.background)
        permissionManager = PermissionManager(this)
        permissionManager.initialize()

        setContent {
            QuitBurnTheme {
                val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelProvider(
                    (LocalContext.current.applicationContext as QuitBurnApplication).moodRepository,
                    (LocalContext.current.applicationContext as QuitBurnApplication).progressRepository)
                )
                viewModel.setWorkManager(WorkManager.getInstance(applicationContext))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(backgroundColor)
                ) {
                    QuitBurnApp(permissionManager, viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppStateManager.setAppInForeground(true)
    }

    override fun onPause() {
        super.onPause()
        AppStateManager.setAppInForeground(false)
    }
}