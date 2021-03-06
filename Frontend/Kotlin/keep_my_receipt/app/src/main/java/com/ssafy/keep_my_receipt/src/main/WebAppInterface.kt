package com.ssafy.keep_my_receipt.src.main

import android.R
import android.content.Context
import android.webkit.JavascriptInterface
import com.ssafy.keep_my_receipt.config.ApplicationClass
import android.widget.Toast

import androidx.core.app.NotificationCompat

import android.app.NotificationManager

import android.app.NotificationChannel

import android.app.PendingIntent

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build

import android.webkit.MimeTypeMap

import androidx.core.content.FileProvider

import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.common.internal.service.Common
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class WebAppInterface(private val mContext: Context) {

    @JavascriptInterface
    fun requestToken(): String {
        return ApplicationClass.sSharedPreferences.getToken()
    }

    @JavascriptInterface
    fun getBase64FromBlobData(base64: String) {
        val base64value = base64.substring(base64.indexOf("base64,") + 7)
        val excelAsBytes: ByteArray = Base64.decode(base64value, 0)
        val dwldsPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "example.xlsx"
        )
        val os = FileOutputStream(dwldsPath, false)
        os.write(excelAsBytes)
        os.flush()

        val intent = Intent()
        intent.setAction(android.content.Intent.ACTION_VIEW);
        val apkURI: Uri = FileProvider.getUriForFile(mContext, "com.ssafy.keep_my_receipt.fileprovider", dwldsPath)
        Log.e("apkURI", apkURI.toString())
        Log.e("apkURL Test", mContext.filesDir.absolutePath + "/Download/example.xlsx")
        intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx"))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        val pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(mContext, "MY_channel")
            .setSmallIcon(com.ssafy.keep_my_receipt.R.drawable.keep_my_receipt_launcher_round)
            .setContentTitle("???????????? ?????????")
            .setContentText("example.xlsx ???????????? ??????")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // ????????? ?????? ???????????? ????????? ?????? ??? ????????? ??????
            val channel_id = "MY_channel" // ????????? ?????? ?????? id ??????
            val channel_name = "keep_my_receipt" // ?????? ?????? ??????
            val descriptionText = "keep_my_receipt" // ?????? ????????? ??????
            val importance = NotificationManager.IMPORTANCE_DEFAULT // ?????? ???????????? ??????
            val channel = NotificationChannel(channel_id, channel_name, importance).apply {
                description = descriptionText
            }

            // ?????? ?????? ????????? ???????????? ??????
            val notificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            // ?????? ??????: ????????? ?????? ID(ex: 1002), ?????? ??????
            notificationManager.notify(1002, builder.build())
        }

    }

    @JavascriptInterface
    fun setAutoLogin(status: Boolean) {
        ApplicationClass.sSharedPreferences.setAutoLogin(status)
    }

    @JavascriptInterface
    fun getAutoLogin(): Boolean {
        return ApplicationClass.sSharedPreferences.getAutoLogin()
    }
    // window['Android']['setAutoLogin'](status);

    @JavascriptInterface
    fun setId(id: String) {
        ApplicationClass.sSharedPreferences.setId(id)
    }

    @JavascriptInterface
    fun getId(): String? {
        return ApplicationClass.sSharedPreferences.getId()
    }

    @JavascriptInterface
    fun setPassword(password: String) {
        ApplicationClass.sSharedPreferences.setPassword(password)
    }

    @JavascriptInterface
    fun getPassword(): String? {
        return ApplicationClass.sSharedPreferences.getPassword()
    }

}