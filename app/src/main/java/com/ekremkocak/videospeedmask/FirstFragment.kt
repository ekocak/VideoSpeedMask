package com.ekremkocak.videospeedmask

import android.car.Car
import android.car.hardware.CarSensorManager
import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.Typeface
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
import com.ekremkocak.videospeedmask.utilities.Prefs


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var car : Car
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCar()

        if (allPermissionsGranted()) {
            startCamera()
            car.connect()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS_CAR, 200)
        /*try {
            AutomotiveCarHardwareManager(requireContext()).carInfo.addSpeedListener(Executors.newSingleThreadExecutor()) {
                try {
                    binding.transparent.text = String.format("%.0f",it.rawSpeedMetersPerSecond.value)
                }catch (ex: Exception){

                }
            }
        }catch (exf : Exception){

        }

         */

        binding.toogleNightMode.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setKeySharedPreferencesBoolean(requireContext(),"night_mode",isChecked)
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
    fun changeNightMode(){
        AppCompatDelegate.setDefaultNightMode(
            when(Prefs.getKeySharedPreferencesBoolean(requireContext(),"night_mode")){
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

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    // creates a folder inside internal storage


    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                //finish()
            }
        }
    }
    companion object {
        private const val TAG = "CameraXGFG"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA,Car.PERMISSION_SPEED,)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        if(allPermissionsGranted()) {
            if (!car.isConnected && !car.isConnecting) {
                car.connect()
            }
        } else {
           // requestPermissions(permissions, 0)
        }

    }

    override fun onPause() {
        if(car.isConnected) {
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