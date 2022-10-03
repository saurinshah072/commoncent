package com.commoncents.ui.auth.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.commoncents.R
import com.commoncents.core.BaseActivity
import com.commoncents.databinding.ActivitySplashBinding
import com.commoncents.ui.dashboard.DashboardActivity
import java.util.*

@SuppressLint("CustomSplashScreen")

class SplashActivity : BaseActivity(){
    private val splashTime = 1000L
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //requestPermissions()
//        if (!EasyPermissions.hasPermissions(this, *perms)) {
//            val builder = PermissionRequest.Builder(this, 12, *perms)
//                .setRationale("test")
//
//            EasyPermissions.requestPermissions(builder.build())
//        }


        startTimer()
        startAnimation()
    }
//    companion object {
//        var perms = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        } else {
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
    /**
     * Add animation on logo
     */

    private fun startAnimation() {

        val loadLeftRightAnimation = AnimationUtils.loadAnimation(this, R.anim.left_to_right)
        binding.imgTop.startAnimation(loadLeftRightAnimation)

        loadLeftRightAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.imgTop.animate().alpha(0f).duration = 50
                binding.imgTop.visibility = View.GONE
                binding.imgCenter.visibility = View.VISIBLE
                binding.imgCenter.animate().alpha(1f).duration = 250
            }

            override fun onAnimationRepeat(p0: Animation?) {
                TODO("Not yet implemented")
            }

        })

        val loadZoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        binding.imgBottom.startAnimation(loadZoomAnimation)

    }

    /**
     * Move to next screen
     */

    /**
     * Move to next screen
     */

    private fun startTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (preferences.isLogin()) {
                    startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    val myIntent = Intent(this@SplashActivity, LoginSignupActivity::class.java)
                    startActivity(myIntent)
                    finish()
                }
            }
        }, splashTime)
    }

//    private fun requestPermissions() {
//        if (TrackingUtility.hasLocationPermissions(this)) {
//            return
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            EasyPermissions.requestPermissions(
//                this,
//                "You need to accept location permissions to use this app",
//                REQUEST_CODE_LOCATION_PERMISSION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        } else {
//            EasyPermissions.requestPermissions(
//                this,
//                "You need to accept location permissions to use this app",
//                REQUEST_CODE_LOCATION_PERMISSION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        }
//    }
//
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            AppSettingsDialog.Builder(this).build().show()
//        } else {
//            requestPermissions()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//    }

}
