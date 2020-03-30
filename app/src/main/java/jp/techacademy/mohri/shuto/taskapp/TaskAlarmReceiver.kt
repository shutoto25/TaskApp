package jp.techacademy.mohri.shuto.taskapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import io.realm.Realm

class TaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "TaskAlarmReceiver.onReceive")

        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // SDKバージョンが26以上の場合はチャネルを設定が必要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "channel description"
            notificationManager.createNotificationChannel(channel)
        }

        // TODO 処理をもう少し噛み砕きたい.できたらクラス化したい.
        // 通知設定.
        val builder = NotificationCompat.Builder(context, "default")
        builder.setSmallIcon(R.drawable.small_icon)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.large_icon))
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setAutoCancel(true)

        // Extra情報からTaskのidを取得して、idからTaskのインスタンスを取得する
        val taskId = intent!!.getIntExtra(EXTRA_INTENT_TASK, -1)

        // このスレッドのためのRealmインスタンスを取得
        val realm = Realm.getDefaultInstance()
        val task = realm.where(Task::class.java).equalTo("id", taskId).findFirst()

        // タスクの情報を設定する
        builder.setTicker(task!!.title)   // 5.0以降は表示されない
        builder.setContentTitle(task.title)
        builder.setContentText(task.contents)

        // 通知をタップしたらアプリを起動するようにする
        val startAppIntent = Intent(context, MainActivity::class.java)
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(context, 0, startAppIntent, 0)
        builder.setContentIntent(pendingIntent)

        // 通知を表示する
        notificationManager.notify(task.id, builder.build())
        realm.close()
    }
}