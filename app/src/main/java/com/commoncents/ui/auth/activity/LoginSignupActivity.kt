package com.commoncents.ui.auth.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Transition
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.commoncents.R
import com.commoncents.core.BaseActivity
import com.commoncents.databinding.ActivityLoginSignupBinding


class LoginSignupActivity : BaseActivity(), View.OnClickListener {
    var from: String? = ""
    lateinit var binding: ActivityLoginSignupBinding
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignupBinding.inflate(layoutInflater)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        setContentView(binding.root)
    }

    /**
     *  add fragment/move to next fragment
     */

    private fun goToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.add(R.id.container, fragment).commit()
    }

    /**
     *  add start transition animation
     */

    fun enterTransition(): Transition {
        val bounds = ChangeBounds()
        bounds.duration = 2000
        return bounds
    }

    /**
     *  add end transition animation
     */

    private fun returnTransition(): Transition {
        val bounds = ChangeBounds()
        bounds.interpolator = DecelerateInterpolator()
        bounds.duration = 2000
        return bounds
    }


    /**
     * Click listeners
     */

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }


    /**
     * On back-pressed listener
     */


    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val backStackCount = navHostFragment?.childFragmentManager?.backStackEntryCount

        if (!from.isNullOrEmpty()) {
            finish()
        } else {
            if (backStackCount!! > 0)
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .popBackStack()
            else
                finishAffinity()
        }
    }
}
