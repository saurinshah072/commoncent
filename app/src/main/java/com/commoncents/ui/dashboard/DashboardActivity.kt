package com.commoncents.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.commoncents.R
import com.commoncents.core.BaseActivity
import com.commoncents.databinding.ActivityDashboardBinding
import com.commoncents.ui.auth.activity.LoginSignupActivity
import com.commoncents.ui.dashboard.packidoperation.fragment.PackIDOperationFragment
import com.commoncents.ui.dashboard.quickscan.fragment.QuickscanFragment
import com.commoncents.utils.Constants
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.hdodenhof.circleimageview.CircleImageView


class DashboardActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var navController: NavController? = null
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mCrash: FirebaseCrashlytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getHeaderView()
        setupNavigation()
        getUnKnowTextView()
        mCrash = FirebaseCrashlytics.getInstance()
    }

    fun getUnKnowTextView(): TextView {
        return binding.txUnKnowIPO
    }

    fun hideUnHideUnKnowPO(flag: Boolean) {
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        when (navHostFragment!!.childFragmentManager.fragments[0]) {
            is QuickscanFragment -> {
                getUnKnowTextView().text = getString(R.string.unknown)
            }
            is PackIDOperationFragment -> {
                if (flag) {
                    getUnKnowTextView().text = getString(R.string.assign_location)
                } else {
                    getUnKnowTextView().text = ""
                }
            }
            else -> {
                getUnKnowTextView().text = ""
            }
        }
    }

    private fun getHeaderView() {
        val headerLayout = binding.navigationView.getHeaderView(0)
        val imgProfile = headerLayout.findViewById<CircleImageView>(R.id.imgProfile)
        val txtName = headerLayout.findViewById<AppCompatTextView>(R.id.txtName)
        val txtEmail = headerLayout.findViewById<AppCompatTextView>(R.id.txtEmail)

        if (preferences.getUserData().user!!.image!!.isNotEmpty()) {
            ViewUtils.loadImage(this, preferences.getUserData().user!!.image!!, imgProfile)
        }

        txtName.text = preferences.getUserData().user!!.name
        txtEmail.text = preferences.getUserData().user!!.email

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.myaccountFragment,
                R.id.quickscanFragment,
                R.id.packIDOperationFragment,
                R.id.assignPackagingFragment,
                R.id.breakDownFragment,
                R.id.retrievingFragment,
                R.id.pickingFragment,
            ), binding.drawerLayout
        )
    }

    // Setting Up One Time Navigation
    private fun setupNavigation() {
        setSupportActionBar(binding.includeToolbar.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment
        ) as NavHostFragment

        navController = navHostFragment.navController


        setupActionBarWithNavController(this, navController!!, appBarConfiguration)
        setupWithNavController(binding.navigationView, navController!!)
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        try {
            Utility.hideKeyboard(this, this.currentFocus!!)
        } catch (e: Exception) {
        }
        return navigateUp(findNavController(this, R.id.fragment), appBarConfiguration)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

        try {
            Utility.hideKeyboard(this, this.currentFocus!!)
        } catch (e: Exception) {
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        binding.drawerLayout.closeDrawers()
        when (menuItem.itemId) {
            R.id.quickscanFragment -> navController!!.navigate(R.id.quickscanFragment)
            R.id.myaccountFragment -> navController!!.navigate(R.id.myaccountFragment)
            R.id.assignLocationFragment -> navController!!.navigate(R.id.assignLocationFragment)
            R.id.assignPalletFragment -> {
                val bundle = Bundle()
                bundle.putBoolean(Constants.FROM_DRAWER, true)
                navController!!.navigate(R.id.assignPalletFragment)
            }
            R.id.packidoprationFragment -> {
                val bundle = Bundle()
                bundle.putString(Constants.PO_NUMBER, "")
                bundle.putString(Constants.apiKeys.PACK_ID, "")
                bundle.putString(Constants.apiKeys.PACK_ID, "")
                bundle.putBoolean(Constants.IS_UN_KNOW, false)
                bundle.putBoolean(Constants.IS_SINGLE, true)
                bundle.putBoolean(Constants.FROM_DRAWER, true)
                navController!!.navigate(R.id.packIDOperationFragment, bundle)
            }
            R.id.assignRetrievingFragment -> navController!!.navigate(R.id.retrievingFragment)
            R.id.pickingFragment -> navController!!.navigate(R.id.pickingFragment)
            R.id.assignPackagingFragment -> navController!!.navigate(R.id.assignPackagingFragment)
            R.id.breakDownFragment -> navController!!.navigate(R.id.breakDownFragment)
            R.id.logoutFragment -> {
                val intent = Intent(this, LoginSignupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
}