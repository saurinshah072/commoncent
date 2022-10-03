package com.commoncents.ui.breakdown.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.commoncents.R
import com.commoncents.databinding.FragmentBreakdownBinding
import com.commoncents.ui.dashboard.DashboardActivity
import com.commoncents.ui.dashboard.packaging.fragment.AssignPackagingFragment
import com.commoncents.core.BaseFragment
import com.commoncents.network.Status
import com.commoncents.ui.breakdown.viewModel.BreakDownVM
import com.commoncents.ui.dashboard.packaging.viewmodel.AssignPackagingVM
import com.commoncents.utils.PrimaryTimerHelper
import com.commoncents.utils.SecondaryTimerHelper
import com.commoncents.utils.Utility
import com.commoncents.utils.ViewUtils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class BreakDownFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentBreakdownBinding
    private lateinit var activity: DashboardActivity
    //
    private val viewModel: BreakDownVM by viewModels()
    //
    lateinit var primaryTimer: PrimaryTimerHelper
    lateinit var secondaryTimer: SecondaryTimerHelper
    private val p_timer = Timer()
    private val s_timer = Timer()
    private var screenType = 0
    //
    private val navigation: NavController by lazy {
        findNavController()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBreakdownBinding.inflate(inflater, container, false)
        activity.hideUnHideUnKnowPO(false)
        //
        primaryTimer = PrimaryTimerHelper(activity)
        secondaryTimer = SecondaryTimerHelper(activity)
        //
        p_timer.scheduleAtFixedRate(PrimaryTimeTask(), 0, 500)
        //s_timer.scheduleAtFixedRate(SecondaryTimeTask(), 0, 500)
        //
        primaryTimer.setStartTime(Date())
        primaryTimer.setTimerCounting(true)
        //
        initView()
        if(screenType == 0){
            initBreakDown()
        }else{
            initReprintScreen()
        }
        setBreakDownObserver()
        //
        return binding.root
    }

    private fun initView(){
        //
        Log.e("myTag"," -- "+Utility.getCurrentDate());
        //
        binding.breakDownStartAcb.setOnClickListener(this)
        binding.rlPackIDQrCode.setOnClickListener(this)
        binding.reprintScanPackIdRl.setOnClickListener(this)
        binding.reprintAddLocationRl.setOnClickListener(this)
    }

    private fun initBreakDown(){
        binding.breakDownContainerViewRl.visibility = View.VISIBLE
        binding.breakDownButtonViewRl.visibility = View.VISIBLE
        binding.reprintContainerViewRl.visibility = View.GONE
        binding.reprintButtonViewRl.visibility = View.GONE
    }

    private fun initReprintScreen(){
        binding.breakDownContainerViewRl.visibility = View.GONE
        binding.breakDownButtonViewRl.visibility = View.GONE
        binding.reprintContainerViewRl.visibility = View.VISIBLE
        binding.reprintButtonViewRl.visibility = View.VISIBLE
    }

    private inner class PrimaryTimeTask: TimerTask()
    {
        override fun run()
        {
            if(primaryTimer.timerCounting())
            {
                val time = Date().time - primaryTimer.startTime()!!.time
                Log.e("myTag","Primary Timer "+timeStringFromLong(time))
            }
        }
    }

    private inner class SecondaryTimeTask: TimerTask()
    {
        override fun run()
        {
            if(secondaryTimer.timerCounting())
            {
                val time = Date().time - secondaryTimer.startTime()!!.time
                Log.e("myTag","Secondary Timer "+timeStringFromLong(time))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as DashboardActivity
    }

    override fun onPause() {
        super.onPause()
        Log.e("myTag","onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("myTag","onDestroyView")
//        resetPrimaryTimer()
//        resetSecondaryTimer()
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("myTag","onDestroyView")
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MyaccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AssignPackagingFragment().apply {}
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.break_down_start_acb ->{
                //Toast.makeText(activity, "test", Toast.LENGTH_SHORT).show()
                screenType = 1
                initReprintScreen()
                s_timer.scheduleAtFixedRate(SecondaryTimeTask(), 0, 500)
                secondaryTimer.setStartTime(Date())
                secondaryTimer.setTimerCounting(true)
            }
            R.id.reprint_move_next_acb ->{
                Log.e("myTag"," reprint_move_next_acb ")
              //  resetPrimaryTimer()
            }
            R.id.reprint_reprint_acb ->{
                secondaryRestartTimer()
                Log.e("myTag"," reprint_reprint_acb ")
            }
            R.id.reprint_scan_pack_id_rl ->{
                checkCameraPermission(view)
            }
            R.id.reprint_add_location_rl ->{
                checkCameraPermission(view)
            }
            R.id.rlPackIDQrCode ->{
                checkCameraPermission(view)
            }
        }
    }

    private fun resetPrimaryTimer(){
        primaryTimer.setStopTime(null)
        primaryTimer.setStartTime(null)
        primaryTimer.setTimerCounting(false)
    }

    private fun resetSecondaryTimer(){
        secondaryTimer.setStopTime(null)
        secondaryTimer.setStartTime(null)
        secondaryTimer.setTimerCounting(false)
    }

    private fun primaryRestartTimer(): Date
    {
        val diff = primaryTimer.startTime()!!.time - primaryTimer.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun secondaryRestartTimer(): Date
    {
        val diff = secondaryTimer.startTime()!!.time - secondaryTimer.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun timeStringFromLong(ms: Long): String
    {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String
    {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    private fun loadApis() {
        //
        val jsonObject = JSONObject()
        jsonObject.put("task_type", "19-B-11")
        jsonObject.put("src_pack_id", "19-B-11")
        jsonObject.put("src_loc_id", "19-B-11")
        jsonObject.put("start_time", "19-B-11")
        jsonObject.put("end_time", "19-B-11")
        jsonObject.put("time_record_type", "19-B-11")
        jsonObject.put("secondary_timers", "19-B-11")
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()
        //
        viewModel.setBreakDownData(jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull()))
    }

    /**
     * Set Break Down Observer
     */
    private fun setBreakDownObserver() {
        viewModel.breakDownVMResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress(activity!!)
                }
                Status.SUCCESS -> {
                    hideProgress()
                    Utility.hideKeyboard(activity!!, view!!)
                    it.data?.let { breakDownData ->
                        Log.e("myTag","test ==>> "+it.data?.toString())
                    }
                }
                Status.ERROR -> {
                    //Log.e("myTag","test2 ==>> "+it.code+" "+it.errorItem.toString())
                    Toast.makeText(activity,it.errorItem!!.get(0).detail,Toast.LENGTH_SHORT).show()
                    hideProgress()
                }
                Status.NO_INTERNET -> {
                    hideProgress()
                    Utility.hideKeyboard(activity, view!!)
                    ViewUtils.snackBar(activity, getString(R.string.internetconnection), view!!)
                }
                else -> {}
            }
        }
    }

    private fun checkCameraPermission(myView: View?) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Utility.hideKeyboard(context!!, myView!!)
                /**
                 * clear the search text
                 */
                //clearSearch()

                navigation.navigate(R.id.breakDown_scan)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                // This is autogenerated method
            }
        }
        TedPermission.with(context).setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied))
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.CAMERA).check()
    }



}
