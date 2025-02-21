package com.example.album.ui.imageViewer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.album.R
import com.example.album.databinding.ActivityFullscreenBinding
import com.example.album.databinding.ActivityImagerViewerBinding
import com.example.album.databinding.MoreBottomsheetBinding
import com.example.album.databinding.SetAsBottomsheetBinding
import com.example.album.model.Hit
import com.example.album.paging.PagingImageSliderAdapter
import com.example.album.repository.DefaultRepository
import com.example.album.ui.home.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class ImagerViewerActivity : AppCompatActivity() {

    private val TAG= "ImagerViewerActivity"
    private val STORAGE_PERMISSION_CODE = 23

    @Inject
    lateinit var repository: DefaultRepository
    lateinit var currModel: Hit

    private lateinit var binding: ActivityImagerViewerBinding
    private lateinit var setASBottomSheetBinding: SetAsBottomsheetBinding
    private lateinit var moreBottomSheetBinding: MoreBottomsheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagerViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViews()
    }

    private fun setUpViews(){

        val selectedHit: Hit? = intent.getParcelableExtra("selectedImage")
        bindItem(selectedHit!!)
        currModel= selectedHit

        binding.backBtn.setOnClickListener{
            this.onBackPressed()
        }
    }

    fun bindItem(model: Hit){

        var dummyURL: String?= null

        model.previewURL?.let {
            dummyURL= it
        }

        model.largeImageURL?.let {
            try {

                Glide
                    .with(this)
                    .load(it)
                    .thumbnail(Glide.with(this).load(dummyURL))
                    .fitCenter()
//                        .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.pvFullscreen)

            }catch (e: Exception){

                Timber.tag(TAG).e("An Error occurred: %s", e)
            }
        }

        model.likes?.let {
            binding.iLike.tvLike.text= it.toString()
        }

        model.downloads?.let {
            binding.iDownloads.tvDownloads.text= it.toString()
        }

        binding.iMore.ivMore.setOnClickListener {
            moreBottomDialog(model)
        }

        binding.iSetAs.ivSetAs.setOnClickListener {
            setAsBottomDialog(model)
        }
    }

    private fun setAsBottomDialog(model: Hit){
        setASBottomSheetBinding = SetAsBottomsheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)



        setASBottomSheetBinding.rlHome.setOnClickListener {
            setWallpaperFromUrl(model.largeImageURL.toString(), 1)
            dialog.dismiss()
        }

        setASBottomSheetBinding.rlLock.setOnClickListener {
            setWallpaperFromUrl(model.largeImageURL.toString(), 0)
            dialog.dismiss()
        }

        setASBottomSheetBinding.rlBoth.setOnClickListener {
            setWallpaperFromUrl(model.largeImageURL.toString(), 2)
            dialog.dismiss()
        }

        setASBottomSheetBinding.rlClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(setASBottomSheetBinding.root)
        dialog.show()
    }

    private fun moreBottomDialog(model: Hit){
        moreBottomSheetBinding = MoreBottomsheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)

        moreBottomSheetBinding.rlShare.setOnClickListener {
            dialog.dismiss()
            shareImage(model.largeImageURL!!)
        }

        moreBottomSheetBinding.rlDownload.setOnClickListener {
            dialog.dismiss()
            currModel= model
            if(checkPermission())
                downloader(model.user!!, model.largeImageURL!!)
            else
                requestWriteStoragePermission()
        }

        moreBottomSheetBinding.rlAdd.setOnClickListener {
            Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        moreBottomSheetBinding.rlVisit.setOnClickListener{

            val uri = Uri.parse(model.pageURL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        moreBottomSheetBinding.rlClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(moreBottomSheetBinding.root)
        dialog.show()
    }

    private fun setWallpaperFromUrl(imageUrl: String, screen: Int) {

        GlobalScope.launch(Dispatchers.IO){

            Glide.with(binding.root)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        try {
                            val wallpaperManager = WallpaperManager.getInstance(this@ImagerViewerActivity)
                            if (screen== 1) {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                            } else if(screen== 0){
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                            } else if(screen== 2){
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                            }

                            Toast.makeText(this@ImagerViewerActivity, "Wallpaper set successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Timber.tag(TAG).e("Failed to set wallpaper: ${e.message}")
                            Toast.makeText(this@ImagerViewerActivity, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Do nothing
                    }
                })
        }
    }

    private fun downloader(username: String, url: String){

        GlobalScope.launch(Dispatchers.IO){

            repository.downloadFile(username, url)
        }

        Toast.makeText(this@ImagerViewerActivity, "download complete", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(): Boolean{

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            //Below android 11
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestWriteStoragePermission() {

        //Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val builder: AlertDialog.Builder= AlertDialog.Builder(this)
            builder.setTitle("Alert").setMessage("Storage permission needed for downloads").setPositiveButton("Grant"){ dialog, _->

                try {
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", this@ImagerViewerActivity.getPackageName(), null)
                    intent.setData(uri)
                    storageActivityResultLauncher.launch(intent)
                } catch (e: java.lang.Exception) {
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    storageActivityResultLauncher.launch(intent)
                }

                dialog.dismiss()
            }
                .setNegativeButton("Cancel"){ dialog, _->
                    dialog.dismiss()
                }
                .setCancelable(false)
            builder.create().show()

        } else {
            //Below android 11
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }

    }

    // Handle permission callback for OS versions for Android 11 or more
    private val storageActivityResultLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android is 11(R) or above
            if (Environment.isExternalStorageManager()) {
                // Manage External Storage Permission is granted
                downloader(currModel.user!!, currModel.largeImageURL!!)
                Timber.tag(TAG)
                    .d("storageActivityResultLauncher: Manage External Storage Permission is granted")
            } else {
                // Manage External Storage Permission is denied
                Toast.makeText(this@ImagerViewerActivity, "Storage Permission denied", Toast.LENGTH_SHORT).show()
                Timber.tag(TAG)
                    .d("storageActivityResultLauncher: Manage External Storage Permission is denied")
            }
        } else {
            // Android is below 11(R)
        }
    }

    // Handle permission callback for OS versions below Android 11
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                //check each permission if granted or not
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read){
                    //External Storage Permission granted
                    downloader(currModel.user!!, currModel.largeImageURL!!)
                    Timber.tag(TAG)
                        .d("onRequestPermissionsResult: External Storage Permission granted")
                }
                else{
                    //External Storage Permission denied...
                    Timber.tag(TAG)
                        .d("onRequestPermissionsResult: External Storage Permission denied...")
                    Toast.makeText(this@ImagerViewerActivity, "Storage Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun Activity.shareImage(imageUrl: String) {

        val file = File(this@ImagerViewerActivity.cacheDir, "share_image.jpg")

        // Load the image and save it to a file
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        FileOutputStream(file).use { outputStream ->
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }

                        // Create an Intent to share the image
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@ImagerViewerActivity, "${this@ImagerViewerActivity.packageName}.provider", file))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Launch the Intent
                        startActivity(Intent.createChooser(shareIntent, "Share Image"))

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Timber.tag(TAG).d("Failed to share image: ${e.message}")
                        Toast.makeText(this@ImagerViewerActivity, "Failed to share image", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // Handle any other unexpected exceptions
                        e.printStackTrace()
                        Timber.tag(TAG).d("Unexpected error occurred: ${e.message}")
                        Toast.makeText(this@ImagerViewerActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not required
                }
            })
    }
}