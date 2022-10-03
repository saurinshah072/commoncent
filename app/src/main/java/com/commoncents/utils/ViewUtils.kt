package com.commoncents.utils


import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.WorkerThread
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.commoncents.R
import com.google.android.material.snackbar.Snackbar
import java.net.URISyntaxException


object ViewUtils {

    fun View.show() {
        visibility = View.VISIBLE
    }

    /**
     * Show snackBar
     */
    fun snackBar(activity: Activity, message: String, view: View) {
        val mInflater: LayoutInflater = activity.layoutInflater
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        val layout = snackBar.view as Snackbar.SnackbarLayout
        val textView =
            layout.findViewById<View>(R.id.snackbar_text) as TextView
        textView.visibility = View.INVISIBLE
        val snackView: View = mInflater.inflate(R.layout.my_snackbar, null)
        val textViewTop = snackView.findViewById(R.id.txtMessage) as TextView
        textViewTop.text = message
        layout.setPadding(0, 0, 0, 0)
        layout.addView(snackView, 0)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        //snackBar.anchorView = view
        snackBar.show()
    }


    /**
     * Custom Alert View For showing error Message Also it will be display on
     */
    fun AlertView(activity: Activity, message: String, view: View) {
        val mInflater: LayoutInflater = activity.layoutInflater
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        val layout = snackBar.view as Snackbar.SnackbarLayout
        val textView =
            layout.findViewById<View>(R.id.snackbar_text) as TextView
        textView.visibility = View.INVISIBLE
        val snackView: View = mInflater.inflate(R.layout.my_snackbar, null)
        val textViewTop = snackView.findViewById(R.id.txtMessage) as TextView
        textViewTop.text = message
        layout.setPadding(0, 0, 0, 0)
        layout.addView(snackView, 0)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        //snackBar.anchorView = view
        snackBar.show()
    }


   /* fun snackBar(activity: Activity , message: String?, view_: View) {
        val toast = Toast(activity)
        val view: View = LayoutInflater.from(activity)
            .inflate(R.layout.my_snackbar, null)
        val tvMessage = view.findViewById<TextView>(R.id.txtMessage)
        tvMessage.text = message
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.view = view
        toast.show()
    }*/


    /*fun snackBar(activity: Activity, message: String,view: View) {
        var xOffset = 0
        var yOffset = 0
        val gvr = Rect()
        val parent = view.parent as View
        val parentHeight = parent.height
        if (view.getGlobalVisibleRect(gvr)) {
            val root = view.rootView
            val halfWidth = root.right / 2
            val halfHeight = root.bottom / 2
            val parentCenterX: Int = (gvr.right - gvr.left) / 2 + gvr.left
            val parentCenterY: Int = (gvr.bottom - gvr.top) / 2 + gvr.top
            yOffset = if (parentCenterY <= halfHeight) {
                -(halfHeight - parentCenterY) - parentHeight
            } else {
                parentCenterY - halfHeight - parentHeight
            }
            if (parentCenterX < halfWidth) {
                xOffset = -(halfWidth - parentCenterX)
            }
            if (parentCenterX >= halfWidth) {
                xOffset = parentCenterX - halfWidth
            }
        }


        val toast = Toast(activity)
        val toastView: View = LayoutInflater.from(activity)
            .inflate(R.layout.my_snackbar, null)
        val tvMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        tvMessage.text = message
        toast.view = toastView
        toast.setGravity(Gravity.CENTER, xOffset, yOffset)
        toast.show()
    }
*/

    /**
     * Show loadImage
     */

    fun loadImage(context: Context?, url: String, imageView: ImageView?) {
        try {
            Glide.with(context!!)
                .load(url.replace("\\s", "").trim { it <= ' ' })
                .apply(
                    RequestOptions.placeholderOf(R.drawable.user)
                        .error(R.drawable.user)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(imageView!!)
        } catch (e: Exception) {
            Logger.e("Error :", e.message!!)
        }
    }

    /**
     * make sure to use this getFilePath method from worker thread
     */
    @WorkerThread
    @Throws(URISyntaxException::class)
    fun getFilePath(context: Context, fileUri: Uri): String? {
        var uri = fileUri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                context.applicationContext,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                when (split[0]) {
                    "image" -> {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )
            try {
                val cursor =
                    context.contentResolver?.query(uri, projection, selection, selectionArgs, null)
                var path: String? = null
                if (cursor != null) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(column_index)
                        cursor.close()
                    }
                }
                return path
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "" + e.message);
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}

