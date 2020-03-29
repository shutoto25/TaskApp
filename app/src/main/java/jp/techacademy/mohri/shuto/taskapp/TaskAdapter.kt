package jp.techacademy.mohri.shuto.taskapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(context: Context) : BaseAdapter() {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "TaskAdapter"
    /**
     * 他のxmlリソースのViewを取り扱うための仕組み?
     */
    private val mLayoutInflater = LayoutInflater.from(context)
    /**
     * タスクリスト.→アイテムを保持する
     */
    var taskList = mutableListOf<Task>()


    /**
     * @return size アイテム数
     */
    override fun getCount(): Int {
        Log.d(TAG, "$CLASS_NAME.getCount")

        return taskList.size
    }


    /**
     * @return position アイテムデータ
     */
    override fun getItem(position: Int): Any {
        Log.d(TAG, "$CLASS_NAME.getItem")

        return taskList[position]
    }


    /**
     * @return id アイテムID
     */
    override fun getItemId(position: Int): Long {
        Log.d(TAG, "$CLASS_NAME.getItemId")

        return taskList[position].id.toLong()
    }


    /**
     * Viewを返す.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        Log.d(TAG, "$CLASS_NAME.getView")

        // Viewを再利用して描画する仕組みがあるのでnull判定する.
        // TODO 表示しよとしている行がnullの場合inflateする->枠が足りない場合は増やすってこと？
        val view: View =
            convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null)

        val titleText = view.findViewById<TextView>(android.R.id.text1)
        val dateText = view.findViewById<TextView>(android.R.id.text2)

        titleText.text = taskList[position].title

        // 日本時間の日付ファーマットを作成.
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        // taskから日付を取得.
        val date = taskList[position].date
        // viewのdateTextに日付をセット.
        dateText.text = simpleDateFormat.format(date)

        return view
    }
}