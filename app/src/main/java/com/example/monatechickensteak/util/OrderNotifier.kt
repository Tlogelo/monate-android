package com.example.monatechickensteak.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.monatechickensteak.MainActivity
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.SessionManager
import com.example.monatechickensteak.model.Order

/** Shows a phone notification when an order changes status (if the customer left notifications on). */
object OrderNotifier {

    private const val CHANNEL_ID = "order_status"

    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun notifyStatus(context: Context, order: Order) {
        if (!SessionManager.notificationsEnabled() || !hasPermission(context)) return

        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Order updates", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val open = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_orders)
            .setContentTitle("Order #${order.id}")
            .setContentText("Your order is now: ${order.status.label}")
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(order.id, notification)
    }
}
