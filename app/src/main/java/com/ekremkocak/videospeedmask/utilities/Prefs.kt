package com.ekremkocak.videospeedmask.utilities

import android.content.Context
import androidx.core.content.edit



class Prefs {

    companion object Prefs {
        fun setStringListSharedPreferences(
            context: Context?,
            value: MutableSet<String>,
            key: String
        ) {

            context!!.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit {
                putStringSet(key, value)
            }
        }



        fun getStringListSharedPreferences(context: Context?, key: String): MutableSet<String>? {
            return context!!.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getStringSet(key, null)
        }

        fun setKeySharedPreferences(context: Context, key: String, value: String) {
            context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit {
                putString(key, value).apply()
            }
        }

        fun getKeySharedPreferences(context: Context, key: String): String {
            return context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString(key, null) ?: ""
        }

        fun setKeySharedPreferencesBoolean(context: Context, key: String, value: Boolean) {
            context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit {
                putBoolean(key, value).apply()
            }
        }

        fun getKeySharedPreferencesBoolean(context: Context, key: String): Boolean {
            return context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getBoolean(key, false)
        }


        fun setKeySharedPreferencesInt(context: Context, key: String, value: Int) {
            context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit {
                putInt(key, value).apply()
            }
        }

        fun getKeySharedPreferencesInt(context: Context, key: String): Int {
            return context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getInt(key, 0)
        }

        fun removeSharedPreferences(context: Context) {
            context.applicationContext.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).edit().clear().apply()
        }
    }
}