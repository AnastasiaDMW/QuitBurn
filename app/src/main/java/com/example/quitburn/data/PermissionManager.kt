package com.example.quitburn.data

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class PermissionManager(private val activity: ComponentActivity) {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    fun initialize() {
        requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){}
    }

    fun requestPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }

}