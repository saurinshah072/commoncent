package com.commoncents.ui.auth.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.commoncents.R
import com.commoncents.core.BaseFragment
import com.commoncents.databinding.FragmentLoginBinding
import com.commoncents.network.Status
import com.commoncents.ui.auth.activity.LoginSignupActivity
import com.commoncents.ui.auth.model.Data
import com.commoncents.ui.auth.viewmodel.LoginViewModel
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.utils.Constants
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils


class LoginFragment : BaseFragment(), View.OnClickListener {
    private lateinit var loginSignupActivity: LoginSignupActivity
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var strUserName = "Saurabh"
    private var strPassword = "1202&&stneC"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginSignupActivity = context as LoginSignupActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.edUsername.setText(strUserName)
        binding.edPassword.setText(strPassword)
        setObserver()
        setListeners()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.btnLogin.setOnClickListener(this)

        binding.edPassword.setOnTouchListener(OnTouchListener { _, event ->
            val rightDrawable = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.edPassword.right - binding.edPassword.compoundDrawables[rightDrawable].bounds.width()
                ) {
                    setPasswordEye()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    /**
     *  Setup Eye icon toggle
     */

    private fun setPasswordEye() {
        if (binding.edPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
            binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(
                0, 0,
                R.drawable.ic_eye_visible, 0
            )
            binding.edPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.edPassword.setSelection(binding.edPassword.text!!.length)
        } else {
            binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(
                0, 0,
                R.drawable.ic_eye_gone, 0
            )
            binding.edPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.edPassword.setSelection(binding.edPassword.text!!.length)
        }
    }


    /**
     *  Check validation on login button click
     */

    private fun checkValidation(): Boolean {
        strUserName = binding.edUsername.text.toString()
        strPassword = binding.edPassword.text.toString()

        if (TextUtils.isEmpty(strUserName.trim())) {
            ViewUtils.snackBar(activity!!,getString(R.string.enter_username), view!!)
            return false
        }
        if (TextUtils.isEmpty(strPassword.trim())) {
            ViewUtils.snackBar(activity!!,getString(R.string.enter_password), view!!)
            return false
        }
        return true
    }


    /**
     * Set observer for login api
     */

    private fun setObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(loginSignupActivity)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    it.data?.let { user ->
                        user.data?.user?.let { it1 ->
                            preferences.setPreference(
                                Constants.PrefKeys.userid, it1.id.toString()
                            )
                            saveUserInfo(user.data)
                        }
                    }
                }
                Status.ERROR -> {
                    hideProgress()
                    it?.let { it1 ->
                        Toast.makeText(activity,it1.message!!,Toast.LENGTH_SHORT).show()
                        //ViewUtils.snackBar(activity!!,it1.message!!, view!!)
                    }
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(loginSignupActivity, view!!)
                    ViewUtils.snackBar(activity!!,getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }


    /**
     * Save user info in preference
     */

    private fun saveUserInfo(userModel: Data) {
        preferences.saveUserDetail(userModel, Constants.SuccessCode)
        userModel.user.let { it1 ->
            preferences.setPreference(
                Constants.PrefKeys.userid, it1!!.id.toString()
            )
        }
        loginSignupActivity.from = ""
        startActivity(Intent(loginSignupActivity, DashboardActivity::class.java))
        loginSignupActivity.finish()
    }

    /**
     * Click listeners
     */

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnLogin.id -> {
                if (checkValidation()) {
                    viewModel.login(strUserName, strPassword)
                }
            }
        }
    }
}
