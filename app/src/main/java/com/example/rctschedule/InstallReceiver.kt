package com.example.rctschedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null && context != null)
        {
            if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                (intent.action == Intent.ACTION_PACKAGE_ADDED &&
                        intent.data?.schemeSpecificPart == context.packageName))
            {
                Log.e("HI", "HIO")

            }
        }


    }
}