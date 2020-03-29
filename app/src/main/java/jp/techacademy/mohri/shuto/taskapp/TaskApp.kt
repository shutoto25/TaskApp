package jp.techacademy.mohri.shuto.taskapp

import android.app.Application
import android.util.Log
import io.realm.Realm

/**
 * ログ用タグ.
 */
const val TAG = "TaskApp"

/**
 * Applicationクラスを継承しているため
 * アプリ起動時一番初めに呼び出されるクラス
 */
class TaskApp : Application() {

    override fun onCreate() {
        Log.d(TAG, "extends Application TasApp.onCreate")
        super.onCreate()

        // Realm初期化.
        Realm.init(this)
    }
}