package jp.techacademy.mohri.shuto.taskapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.content_category.*

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

        // アクションバー設定.
        val toolbar = findViewById<View>(R.id.categoryToolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar.let {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onClick(v: View) {
        Log.d(TAG, "$CLASS_NAME.onClick")

        if (etNewCategory.text.isNotEmpty()) {
            // カテゴリーを追加して編集画面に戻る.
            addCategory()
        } else {
            Toast.makeText(this, "入力してください", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * カテゴリー追加.
     */
    private fun addCategory() {
        Log.d(TAG, "$CLASS_NAME.addCategory")

        // 入力された新規カテゴリ取得.
        val inputCategory = etNewCategory.text.toString()

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val categoryRealmResult = realm.where(Category::class.java).findAll()
        // 重複チェック
        for (i in categoryRealmResult) {
            if (i.category == inputCategory) {
                Toast.makeText(this, "すでに登録されています", Toast.LENGTH_SHORT).show()
                realm.close()

                Log.d(TAG, "$CLASS_NAME.addCategory：already registered.")
                return
            }
        }
        val identifier: Int = categoryRealmResult.max("id")?.let {
            categoryRealmResult.max("id")!!.toInt() + 1
        } ?: 0
        // 新規カテゴリ登録する.
        val category = Category()
        category.id = identifier
        category.category = inputCategory

        realm.copyToRealmOrUpdate(category)
        realm.commitTransaction()
        realm.close()

        // 編集画面に戻る.
        Toast.makeText(this, "「$inputCategory」を追加しました", Toast.LENGTH_SHORT).show()
        finish()
    }
}