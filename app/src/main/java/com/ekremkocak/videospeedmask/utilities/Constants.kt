package com.ekremkocak.videospeedmask.utilities

import android.car.Car

object Constants {


    const val NIGHT_MODE_KEY = "night_mode"
    const val REQUEST_CODE_PERMISSIONS = 200
    val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA,Car.PERMISSION_SPEED)


}