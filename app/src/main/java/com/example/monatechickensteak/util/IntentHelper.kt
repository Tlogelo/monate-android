package com.example.monatechickensteak.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/** Opens phone, email, map and web links. Returns false when the phone has no app for it. */
object IntentHelper {

    fun open(context: Context, intent: Intent): Boolean =
        try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }

    fun dial(context: Context, phone: String) =
        open(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))

    fun email(context: Context, address: String, subject: String) =
        open(
            context,
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$address")
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
        )

    fun map(context: Context, address: String) =
        open(context, Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(address))))

    fun web(context: Context, url: String) =
        open(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
