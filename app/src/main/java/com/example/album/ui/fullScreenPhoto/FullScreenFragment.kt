package com.example.album.ui.fullScreenPhoto

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.album.R
import com.example.album.adapters.ImageSliderAdapter
import com.example.album.databinding.FragmentFullScreenBinding
import com.example.album.databinding.MoreBottomsheetBinding
import com.example.album.databinding.SetAsBottomsheetBinding
import com.example.album.model.Hit
import com.example.album.repository.DefaultRepository
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
class FullScreenFragment : Fragment() {

    private val TAG= "FullScreenFragment"
    private val STORAGE_PERMISSION_CODE = 23

    @Inject
    lateinit var repository: DefaultRepository
    lateinit var currModel: Hit

    private lateinit var binding: FragmentFullScreenBinding
    private lateinit var setASBottomSheetBinding: SetAsBottomsheetBinding
    private lateinit var moreBottomSheetBinding: MoreBottomsheetBinding
    private var currentPosition: Int = 0
    private lateinit var viewPager: ViewPager2
    private lateinit var images: ArrayList<Hit>
//    private val args : FullScreenFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullScreenBinding.inflate(inflater)
        setUpViews()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setUpViews(){

        requireActivity().transparentStatusBar(true)
        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressed()
        }
//        images= args.photoResponse.hits!!
//        currentPosition= args.position
        val adapter = ImageSliderAdapter(images)
        viewPager= binding.viewPager
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.setCurrentItem(currentPosition, false)

        // registering for page change callback
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    //update the image number textview
//                    binding.imageNumberTV.text = "${position + 1} / 4"
                    currentPosition= position

                }
            }
        )


        adapter.setOnMoreClickListener(object : ImageSliderAdapter.OnMoreClickListener {
            override fun onMoreClick(position: Int, model: Hit) {

                moreBottomDialog(model)
            }
        })

        adapter.setOnSetAsClickListener(object : ImageSliderAdapter.OnSetAsClickListener {
            override fun onSetAsClick(position: Int, model: Hit) {

                setAsBottomDialog(model)
            }
        })
    }

    private fun setAsBottomDialog(model: Hit){
        setASBottomSheetBinding = SetAsBottomsheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireActivity())



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
        val dialog = BottomSheetDialog(requireActivity())

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
            Toast.makeText(requireActivity(), "Share", Toast.LENGTH_SHORT).show()
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
                            val wallpaperManager = WallpaperManager.getInstance(context)
                            if (screen== 1) {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                            } else if(screen== 0){
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                            } else if(screen== 2){
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                            }

                            Toast.makeText(requireActivity(), "Wallpaper set successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Timber.tag(TAG).e("Failed to set wallpaper: ${e.message}")
                            Toast.makeText(requireActivity(), "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
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

        Toast.makeText(requireActivity(), "download complete", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(): Boolean{

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            //Below android 11
            val write =
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestWriteStoragePermission() {

        //Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val builder: AlertDialog.Builder= AlertDialog.Builder(requireActivity())
            builder.setTitle("Alert").setMessage("Storage permission needed for downloads").setPositiveButton("Grant"){ dialog, _->

                try {
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", requireActivity().getPackageName(), null)
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
                Toast.makeText(requireActivity(), "Storage Permission denied", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireActivity(), "Storage Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun Fragment.shareImage(imageUrl: String) {

        val file = File(requireContext().cacheDir, "share_image.jpg")

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
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Launch the Intent
                        startActivity(Intent.createChooser(shareIntent, "Share Image"))

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Timber.tag(TAG).d("Failed to share image: ${e.message}")
                        Toast.makeText(requireContext(), "Failed to share image", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // Handle any other unexpected exceptions
                        e.printStackTrace()
                        Timber.tag(TAG).d("Unexpected error occurred: ${e.message}")
                        Toast.makeText(requireContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not required
                }
            })
    }



    private fun Activity.transparentStatusBar(it: Boolean) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor =
            if (it) ContextCompat.getColor(
                this,
                android.R.color.transparent
            ) else ContextCompat.getColor(
                this,
                R.color.base2_bg
            )
    }

    override fun onDestroy() {
        super.onDestroy()

        // unregistering the onPageChangedCallback
        viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
    }
}