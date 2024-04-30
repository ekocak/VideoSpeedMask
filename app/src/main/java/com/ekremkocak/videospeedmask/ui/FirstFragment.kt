package com.ekremkocak.videospeedmask.ui

import android.car.Car
import android.car.hardware.CarSensorManager
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekremkocak.videospeedmask.databinding.FragmentFirstBinding
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.appcompat.app.AppCompatDelegate
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.hardware.AutomotiveCarHardwareManager
import androidx.fragment.app.viewModels
import com.ekremkocak.videospeedmask.utilities.Constants.NIGHT_MODE_KEY
import com.ekremkocak.videospeedmask.utilities.Constants.REQUEST_CODE_PERMISSIONS
import com.ekremkocak.videospeedmask.utilities.Constants.REQUIRED_PERMISSIONS
import com.ekremkocak.videospeedmask.utilities.Prefs
import com.ekremkocak.videospeedmask.viewmodel.FirstFragmentViewModel
import java.util.concurrent.Executors


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private val viewModel: FirstFragmentViewModel by viewModels()
    private lateinit var car : Car
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }



    @OptIn(ExperimentalCarApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initCar()

        if (allPermissionsGranted()) {
            startCamera()
            readSpeedSecondMethod()
          //  car.connect()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }



        try {
            AutomotiveCarHardwareManager(requireContext()).carInfo.addSpeedListener(Executors.newSingleThreadExecutor()) {
                try {
                    binding.transparent.text = String.format("%.0f",it.rawSpeedMetersPerSecond.value)
                }catch (ex: Exception){

                }
            }
        }catch (exf : Exception){

        }



        binding.toogleNightMode.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setKeySharedPreferencesBoolean(requireContext(),NIGHT_MODE_KEY,isChecked)
            changeNightMode()
        }
        binding.textColor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.viewTextColor.visibility = View.GONE
            }else{
                binding.viewTextColor.visibility = View.VISIBLE
            }
        }


        changeNightMode()



    }
    private fun changeNightMode(){
        AppCompatDelegate.setDefaultNightMode(
            when(Prefs.getKeySharedPreferencesBoolean(requireContext(), NIGHT_MODE_KEY)){
                true ->  AppCompatDelegate.MODE_NIGHT_YES
                else ->  AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())



        cameraProviderFuture.addListener(Runnable {

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }



            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        if(false&&allPermissionsGranted()) {
            if (!car.isConnected && !car.isConnecting) {
                car.connect()
            }
        } else {
           // requestPermissions(permissions, 0)
        }

    }

    override fun onPause() {
        if(false&&car.isConnected) {
            car.disconnect()
        }

        super.onPause()
    }

    private fun initCar() {
        /*
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            return
        }*/

        if(::car.isInitialized) {
            return
        }

        car = Car.createCar(requireContext(), object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                onCarServiceReady()
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                // on failure callback
            }
        })
    }
    @OptIn(ExperimentalCarApi::class)
    private fun readSpeedSecondMethod(){
        try {
            AutomotiveCarHardwareManager(requireContext()).carInfo.addSpeedListener(Executors.newSingleThreadExecutor()) {
                try {
                    binding.transparent.text = String.format("%.0f",it.rawSpeedMetersPerSecond.value)
                }catch (ex: Exception){
                    Error("Error : $ex")
                }
            }
        }catch (exf : Exception){

        }
    }

    private fun onCarServiceReady() {
        watchSpeedSensor()
    }

    private fun watchSpeedSensor() {
        val sensorManager = car.getCarManager(Car.SENSOR_SERVICE) as CarSensorManager
        sensorManager.registerListener(
            { carSensorEvent ->
                binding.transparent.text = String.format("%.0f",carSensorEvent.floatValues[0])
            },
            CarSensorManager.SENSOR_TYPE_CAR_SPEED,
            CarSensorManager.SENSOR_RATE_NORMAL
        )
    }
}