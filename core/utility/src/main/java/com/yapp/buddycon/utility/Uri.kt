package com.yapp.buddycon.utility

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun Uri.getAbsolutePath(context: Context): String {
    val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    val c: Cursor? = context.contentResolver.query(
        this,
        proj,
        null,
        null,
        null
    )
    c?.moveToFirst()
    val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

    val resultPath = c?.getString(index!!)
    c?.close()

    return resultPath!!
}
