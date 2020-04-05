package jp.techacademy.mohri.shuto.taskapp

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

open class Preferences(context: Context) {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "Preferences"
    /**
     * Preference.
     */
    private val mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    /**
     * PreferenceKey(初回起動判定)
     */
    private val PREFERENCE_KEY_FIRST_BOOT =
        "jp.techacademy.mohri.shuto.taskapp.REFERENCE_KEY_FIRST_BOOT"


    /**
     * 初回起動判定.
     * @return true:初回起動 false:起動済み
     */
    fun isFirstBoot(): Boolean {
        Log.d(TAG, "$CLASS_NAME.isFirstBoot")

        return mPreferences.getBoolean(PREFERENCE_KEY_FIRST_BOOT, true)
    }


    /**
     * 初回起動フラグ設定.
     */
    fun setFirstBootFlag() {
        Log.d(TAG, "$CLASS_NAME.isFirstBoot")

        val flag = mPreferences.edit()
        flag.putBoolean(PREFERENCE_KEY_FIRST_BOOT, false).apply()
    }
}