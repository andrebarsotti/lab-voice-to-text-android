package lab.voicetotext.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun setupRecordPermisssions(context: Context): Boolean {
    var isGranted = false

    isGranted = checkRecordPermission(context)

    if (!isGranted) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            123
        )

        isGranted = checkRecordPermission(context)
    }

    return  isGranted
}

fun checkRecordPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}