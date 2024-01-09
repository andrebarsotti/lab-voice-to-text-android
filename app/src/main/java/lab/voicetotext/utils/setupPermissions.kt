package lab.voicetotext.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun setupPermisssions(context: Context) {

    val permissionRecordAudio = Manifest.permission.RECORD_AUDIO
    val checkSelfPermissionRecordAudio =
        ActivityCompat.checkSelfPermission(context, permissionRecordAudio)

    if (checkSelfPermissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(permissionRecordAudio),
            123
        )
    }
}