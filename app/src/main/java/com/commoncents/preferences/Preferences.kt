package com.commoncents.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.commoncents.ui.auth.model.Data
import com.commoncents.ui.auth.model.User
import com.commoncents.utils.Constants
import com.commoncents.utils.Utility
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Preferences @Inject constructor(@ApplicationContext private val context: Context) {

    private lateinit var pref: SharedPreferences
    var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    init {
        pref = EncryptedSharedPreferences.create(
            Utility.commonCentsPref,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    fun getPreference(key: String): String? {
        return pref.getString(key, "") // getting String
    }

    // token
    fun setPreference(key: String, value: String) {
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(key, value) // Storing long
        editor.apply() // commit changes
    }

    fun removePreference(ctx: Context, key: String) {
        val editor: SharedPreferences.Editor = pref.edit()
        editor.remove(key); // will delete key name
        editor.apply(); // commit changes
    }

    fun getPreference(ctx: Context, key: String): String? {
        return pref.getString(key, ""); // getting String
    }

    fun saveUserDetail(data: Data, code: Int) {
        val gson = Gson()

        if (code == Constants.SuccessCode) {
            val data = gson.toJson(data)
            setPreference(Constants.PrefKeys.user, data)
        } else {
            val userData = User()
            userData.id = getUserData().user!!.id
            userData.email = getUserData().user!!.email
            userData.name = getUserData().user!!.name
            userData.image = getUserData().user!!.image

            data.user = userData

            val user = gson.toJson(data)
            setPreference(Constants.PrefKeys.user, user)
        }
    }

    fun getUserData(): Data {
        val data = getPreference(Constants.PrefKeys.user)
        if (data != null) {
            if (data.isNotEmpty()) {
                val gson = Gson()
                return gson.fromJson(data, Data::class.java)
            }
        }
        return Data()
    }

    fun isLogin(): Boolean {
        return !Utility.isEmpty(getPreference(Constants.PrefKeys.userid))
    }
}
