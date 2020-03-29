package jp.techacademy.mohri.shuto.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.support.v7.widget.Toolbar
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*

class InputActivity : AppCompatActivity() {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "InputActivity"
    /**
     * 各種日時情報
     */
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mTask: Task? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$CLASS_NAME.onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // リスナーセット.
        setListener()

        // アクションバー設定.
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar.let {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // Extra情報からTaskのidを取得して、idからTaskのインスタンスを取得する
        val intent = intent
        val taskId = intent.getIntExtra(EXTRA_INTENT_TASK, -1)

        // このスレッドのためのRealmインスタンスを取得
        val realm = Realm.getDefaultInstance()
        // 取得idと一致する検索対象を一件を取得.
        mTask = realm.where(Task::class.java).equalTo("id", taskId).findFirst()
        realm.close()

        if (mTask == null) {
            // 新規作成時.
            // 登録情報がないため日時は現在値を取得.
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)
        } else {
            // タスク内容更新時.
            // 登録済みのタスク内容を取得.
            etTitle.setText(mTask!!.title)
            etContent.setText(mTask!!.contents)

            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            //日付設定.
            val dateString = mYear.toString() + "/" +
                    String.format("%02d", mMonth + 1) + "/" +
                    String.format("%02d", mDay)
            btDate.text = dateString

            // 時間設定.
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)
            btTimes.text = timeString
        }
    }


    /**
     * タスク追加.
     */
    private fun addTask() {
        Log.d(TAG, "$CLASS_NAME.addTask")

        // このスレッドのためのRealmインスタンスを取得
        val realm = Realm.getDefaultInstance()
        // メモ：保存されたオブジェクトのプロパティ変更する場合は必ずトランザクション内で行う必要がある.
        realm.beginTransaction()

        if (mTask == null) {
            // 新規インスタンス作成.
            mTask = Task()
            // タスクを全件取得.
            val taskRealmResult = realm.where(Task::class.java).findAll()
            val identifier: Int = taskRealmResult.max("id")?.let {
                taskRealmResult.max("id")!!.toInt() + 1
            } ?: 0
            // id設定(タスクがnullでなければ現在タスク数+1).
            mTask!!.id = identifier
        }

        // タイトル設定.
        val inputTitle = etTitle.text.toString()
        mTask!!.title = inputTitle

        // 内容設定.
        val inputContent = etContent.text.toString()
        mTask!!.contents = inputContent

        // 日時設定.
        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        mTask!!.date = date

        realm.copyToRealmOrUpdate(mTask!!)
        realm.commitTransaction()

        realm.close()
    }


    /**
     * リスナーセット.
     */
    private fun setListener() {
        Log.d(TAG, "$CLASS_NAME.setListener")

        // Dateボタン.
        btDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    mYear = year
                    mMonth = month
                    mDay = day
                    // メモ：月は0始まりなので+1を行う
                    val dateString = mYear.toString() + "/" +
                            String.format("%02d", mMonth + 1) + "/" +
                            String.format("%02d", mDay)
                    btDate.text = dateString
                }, mYear, mMonth, mDay
            )
            // ダイアログ表示.
            datePickerDialog.show()
        }

        // Timesボタン.
        btTimes.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    mHour = hour
                    mMinute = minute
                    val timeString =
                        String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)
                    btTimes.text = timeString
                }, mHour, mMinute, true
            )
            // ダイアログ表示.
            timePickerDialog.show()
        }

        // Doneボタン
        btDone.setOnClickListener {
            Log.d(TAG, "$CLASS_NAME.btDome is clicked")

            // タスク追加処理後、画面を終了する.
            addTask()
            finish()
        }
    }
}