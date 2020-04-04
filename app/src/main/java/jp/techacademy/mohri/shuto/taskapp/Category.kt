package jp.techacademy.mohri.shuto.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Category : RealmObject(), Serializable {
    /**
     * id
     */
    @PrimaryKey
    var id: Int = 0
    /**
     * カテゴリー
     */
    var category: String = ""
}