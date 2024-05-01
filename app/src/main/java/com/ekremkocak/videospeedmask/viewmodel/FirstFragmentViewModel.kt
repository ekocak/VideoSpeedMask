package com.ekremkocak.videospeedmask.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekremkocak.videospeedmask.R
import com.ekremkocak.videospeedmask.utilities.Constants
import com.ekremkocak.videospeedmask.utilities.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstFragmentViewModel: ViewModel() {
    fun holdNightMode(
        context: Context,
        isChecked: Boolean
    ){
        viewModelScope.launch(Dispatchers.IO) {
            Prefs.setKeySharedPreferencesBoolean(context, Constants.NIGHT_MODE_KEY,isChecked)
        }
    }

    fun allPermissionsGranted(context: Context) = Constants.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}