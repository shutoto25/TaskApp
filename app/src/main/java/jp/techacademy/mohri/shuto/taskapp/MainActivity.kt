package jp.techacademy.mohri.shuto.taskapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

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
     * レルム.
     */
    private lateinit var mRealm: Realm
    /**
     * レルムリスナー.
     */
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            Log.d(TAG, "$CLASS_NAME.onChange")
            reloadListView()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$CLASS_NAME.onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRealm = Realm.getDefaultInstance()

        // リスナーセット
        setListener()

        // ListViewの設定
        // @はアノテーション的な？
        mTaskAdapter = TaskAdapter(this@MainActivity)

        // テスト用のタスクを作成
        addTaskForTest()
        // 再描画
        reloadListView()

    }


    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }


    /**
     * リストの再描画を行う.
     */
    private fun reloadListView() {
        Log.d(TAG, "$CLASS_NAME.reloadListView")

        // Realmから全てのデータを取得し、新しい日時順(降順)に並べた結果を取得.
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
     * テスト用データ作成
     */
    private fun addTaskForTest() {
        Log.d(TAG, "$CLASS_NAME.addTaskForTest")

        val task = Task()
        task.id = 0
        task.title = "作業"
        task.contents = "プログラムを書いてPUSHする"
        task.date = Date()
        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(task)
        mRealm.commitTransaction()
    }


    /**
     * リスナーセット.
     */
    private fun setListener() {
        Log.d(TAG, "$CLASS_NAME.setListener")

        // Realmデータベース変更リスナー(追加・編集・削除など)
        mRealm.addChangeListener(mRealmListener)

        // フローティングボタンクリックリスナー
        fabAdd.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // リストビュークリックリスナー
        lvTask.setOnItemClickListener { parent, view, position, id ->
            // 編集画面起動
        }

        // リストビュー長タップリスナー
        lvTask.setOnItemLongClickListener { parent, view, position, id ->
            // 削除
            true
        }
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
