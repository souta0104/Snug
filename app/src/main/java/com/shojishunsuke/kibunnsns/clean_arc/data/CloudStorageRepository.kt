package com.shojishunsuke.kibunnsns.clean_arc.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.shojishunsuke.kibunnsns.clean_arc.data.repository.StorageRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class CloudStorageRepository(private val uploadListener: ImageUploadListener) : StorageRepository {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference


    private val TAG = "CloudStorageRepository"
    lateinit var bitmap: Bitmap


    override suspend fun downloadImage(url: Uri) {
        val islandRef = storage.getReferenceFromUrl("$url")
        val TEN_MEGABYTE: Long = 1024 * 1024 * 10

//        var bitmap: Bitmap = Resources.getSystem().getDrawable(R.drawable.hasikan).toBitmap()


        islandRef.getBytes(TEN_MEGABYTE).addOnSuccessListener {
            bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            Log.d(TAG, "Success downloading image from cloud storage")
            uploadListener.onDownloadTaskComplete(bitmap)

        }.addOnFailureListener {
            it.printStackTrace()
            Log.d(TAG, "Failure downloading image from clout storage")
        }

    }

    override suspend fun uploadImage(bitmap: Bitmap) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val path = "icons/" + UUID.randomUUID() + ".png"
        val iconsRef = storage.getReference(path)


        val uploadTask = iconsRef.putBytes(data)


        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.d("uploading icon", "failure")
                }
            } else {
                Log.d("Whynot", "Fuck off")
            }
            return@Continuation iconsRef.downloadUrl
        }).addOnCompleteListener { task ->
            GlobalScope.launch {
                if (task.isSuccessful) {
                    val downloadUri = task.result ?: Uri.parse("")
                    uploadListener.onUploadTaskComplete(downloadUri)
                }

            }
        }
    }

    fun getStorageRefByUri(uri: Uri) = storage.getReferenceFromUrl("$uri")

    interface ImageUploadListener {
        suspend fun onUploadTaskComplete(result: Uri)
        fun onDownloadTaskComplete(result: Bitmap)
    }
}