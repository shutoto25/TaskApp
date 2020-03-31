package jp.techacademy.mohri.shuto.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import io.realm.Realm
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_main.*

/**
 * Intent Extra情報(Task Id)
 */
const val EXTRA_INTENT_TASK = "jp.techacademy.mohri.shuto.taskapp.EXTRA_INTENT_TASK"


class MainActivity : AppCompatActivity() {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "MainActivity"
    /**
     * アダプタ.
     */
    private lateinit var mTaskAdapter: TaskAdapter
    /**
     * Realm.
     */
    private lateinit var mRealm: Realm
    /**
     * 検索カテゴリー.
     */
    private var mSearchCategory = ""

    companion object {
        /**
         * スピナーアイテム.
         * TODO 自分で追加編集できるようにしたい.
         */
        val spinnerItems = arrayOf(
            "タスク",
            "仕事",
            "プライベート",
            "買い物",
            "その他"
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$CLASS_NAME.onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初期化設定.
        mRealm = Realm.getDefaultInstance()
        mTaskAdapter = TaskAdapter(this@MainActivity)


        // スピナー設定.
        val spinnerAdapter = ArrayAdapter(
            applicationContext, android.R.layout.simple_spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMainCategory.adapter = spinnerAdapter

        // リスナーセット
        setListener()

        // リスト再描画
        reloadListView()
    }


    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }


    /**
     * リスト再描画(全件取得)
     */
    private fun reloadListView() {
        Log.d(TAG, "$CLASS_NAME.reloadListView")

        // Realmから全データを取得し、新しい日時順(降順)に並べた結果を取得.
        val taskRealmResults =
            mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        // 取得結果をtaskListとしてセット.
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)
        // Taskのリストビュー用のアダプタを渡す.
        lvTask.adapter = mTaskAdapter
        // アダプタに変更を知らせて更新を行う.
        mTaskAdapter.notifyDataSetChanged()
    }


    /**
     * 検索カテゴリーに一致するリストを取得し再描画.
     */
    private fun reloadTargetListView() {
        // カテゴリーが一致するタスクを取得.
        val hitTasks = mRealm.where(Task::class.java)
            .equalTo("category", mSearchCategory).findAll()

        // 取得結果をtaskListとしてセット.
        mTaskAdapter.taskList = mRealm.copyFromRealm(hitTasks)
        // Taskのリストビュー用のアダプタを渡す.
        lvTask.adapter = mTaskAdapter
        // アダプタに変更を知らせて更新を行う.
        mTaskAdapter.notifyDataSetChanged()

        if(hitTasks.size == 0){
            Toast.makeText(this,"一致するタスクはありません",Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * リスナーセット.
     */
    private fun setListener() {
        Log.d(TAG, "$CLASS_NAME.setListener")

        // Realmデータベース変更リスナー(追加・編集・削除など)
        mRealm.addChangeListener { element ->
            // リスト再描画.
            reloadListView()
        }


        // フローティングボタンクリックリスナー
        fabAdd.setOnClickListener { view ->
            // 新規タスク画面起動.
            val newTaskIntent = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(newTaskIntent)
        }


        // リストビュークリックリスナー
        lvTask.setOnItemClickListener { parent, view, position, id ->
            // クリックしたリスト(parent)のアイテムデータを取得.
            val task = parent.adapter.getItem(position) as Task
            // 取得したデータからIdを取得し,Extra情報に付与して編集画面起動.
            val editIntent = Intent(this@MainActivity, InputActivity::class.java)
            editIntent.putExtra(EXTRA_INTENT_TASK, task.id)
            startActivity(editIntent)
        }


        // リストビュー長タップリスナー
        lvTask.setOnItemLongClickListener { parent, view, position, id ->
            // クリックしたリストのアイテムデータを取得.
            val task = parent.adapter.getItem(position) as Task

            val dialogBuilder = AlertDialog.Builder(this@MainActivity)
            dialogBuilder.setTitle("削除")
            dialogBuilder.setMessage("${task.title}を削除しますか？")

            dialogBuilder.setPositiveButton("OK") { _, _ ->
                val result = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                result.deleteAllFromRealm()
                mRealm.commitTransaction()

                // アラーム解除.
                cancelAlarm(task)

                // リスト再描画
                reloadListView()
                Toast.makeText(this@MainActivity, "削除しました", Toast.LENGTH_SHORT).show()
            }
            dialogBuilder.setNegativeButton("CANCEL", null)

            // ダイアログ生成/表示
            val dialog = dialogBuilder.create()
            dialog.show()

            true
        }


        // スピナー選択.
        spMainCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // アイテム選択
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as String
                mSearchCategory = item
            }
            // アイテム未選択
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ここtでは特に何もしない
            }
        }


        // 検索ボタンクリックリスナー.
        btSearch.setOnClickListener { view ->
            if (btSearch.text == "Search") {
                // 検索対象を取得して再描画.
                reloadTargetListView()
                btSearch.text = "Revert"
            } else {
                // 全件取得して再描画.
                reloadListView()
                btSearch.text = "Search"
            }
        }

    }

    /**
     * Realmからタスクを削除するタイミングでアラームを解除.
     * @param task 長タップされたタスク.
     *
     */
    private fun cancelAlarm(task: Task) {
        //セットした時と同じIntent、PendingIntentを作成.
        val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
        val resultPendingIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            task.id,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // アラームをキャンセル.
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(resultPendingIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
