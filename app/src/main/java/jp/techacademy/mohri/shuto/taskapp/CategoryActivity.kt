package jp.techacademy.mohri.shuto.taskapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity()
    , View.OnClickListener {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "CategoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$CLASS_NAME.onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        btAdd.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        Log.d(TAG, "$CLASS_NAME.onClick")

        if (etAddCategory.text.isNotEmpty()) {
            // カテゴリー追加
            addCategory()
        } else {
            Toast.makeText(this, "入力してください", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * カテゴリー追加.
     * TODO 重複チェックを入れる
     */
    private fun addCategory() {
        Log.d(TAG, "$CLASS_NAME.addCategory")

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val category = Category()
        val categoryRealmResult = realm.where(Category::class.java).findAll()
        val identifier: Int = categoryRealmResult.max("id")?.let {
            categoryRealmResult.max("id")!!.toInt() + 1
        } ?: 0
        // id設定(タスクがnullでなければ現在タスク数+1).
        category.id = identifier

        val inputCategory = etAddCategory.text.toString()
        category.category = inputCategory

        realm.copyToRealmOrUpdate(category)
        realm.commitTransaction()
        realm.close()
    }
}