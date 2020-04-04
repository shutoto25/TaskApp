package jp.techacademy.mohri.shuto.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class Task : RealmObject(), Serializable {
    /**
     * id
     */
    @PrimaryKey
    var id: Int = 0
    /**
     * カテゴリー
     */
    var category: Category? = null
    /**
     * タイトル
     */
    var title: String = ""
    /**
     * 内容
     */
    var contents: String = ""
    /**
     * 日時
     */
    var date: Date = Date()

}