package jp.techacademy.mohri.shuto.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

//シリアライズがよく分からない
// またopen修飾子を付けるのは、Realmが内部的にTaskを継承したクラスを作成して利用するためです？？？？
open class Task : RealmObject(), Serializable {
    /**
     * id
     */
    @PrimaryKey
    var id: Int = 0
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