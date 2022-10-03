package com.commoncents.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadImageBG (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val imageDataList =  inputData.getStringArray("imageData")
        uploadImages(imageDataList!!)
        return Result.success()
    }

    private fun uploadImages(imageString:Array<String>) {
        Log.e("myTag"," == "+imageString.toString()+" == "+imageString.size+" "+imageString.get(0))
    }
}
