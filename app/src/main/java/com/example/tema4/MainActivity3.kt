package com.example.tema4
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide

import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget

import com.bumptech.glide.request.transition.Transition
import com.example.tema4.databinding.ActivityMain3Binding
import kotlinx.coroutines.launch

class MainActivity3 : AppCompatActivity() {
    // Inflate  layout pentru View Binding
    private lateinit var binding: ActivityMain3Binding
    private lateinit var database: AppDatabase
    private val searchedTags: MutableList<String> = mutableListOf()

    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 42
    //functie de cerere a permisiunii pentru accesarea bazei de date

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                checkPermissions()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Cannot access media files.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    //functie ce verifica permisiunea

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // explica de ce este nevoie de permisiune
                Toast.makeText(
                    this,
                    "Permission required to access media files",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // cere permisiune folosind SAF
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            requestPermissionLauncher.launch(intent)
        } else {
            performMediaDocumentAccess()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initializare baza de date

        database = AppDatabase.getInstance(applicationContext)

        // trimitere catre prima pagina
        binding.lista.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // acceseaza baza de date si cauta imagine in functie de tag
        binding.cauta.setOnClickListener {
            val tag = binding.baraCautare.text.toString()
            if (tag.isNotEmpty()) {
                searchedTags.add(tag)
                performMediaDocumentAccess()
            } else {
                Toast.makeText(this, "Please enter a tag", Toast.LENGTH_SHORT).show()
            }
        }
    }
//functie de retragere imagini
    private fun performMediaDocumentAccess() {
        lifecycleScope.launch {
            val images = database.tagDao().getImagesWithTags(searchedTags, searchedTags.size)
            // apeleaza functie ce se ocupa de imaginile gasite
            handleSearchedImages(images)
        }
    }
//functie ce se ocupa de imaginile gasite
    private fun handleSearchedImages(images: List<Image>) {
        val imageSwitcher = binding.rezultat

        if (images.isNotEmpty()) {
            val imageUris = images.map { Uri.parse(it.imageUri) }
            var imagePosition = 0

            imageSwitcher.setFactory {
                ImageView(applicationContext)
            }
//buton ce afiseaza imaginea urmatoare
            val nextButton = binding.next
            nextButton.setOnClickListener {
                if (imagePosition < imageUris.size - 1) {
                    imagePosition++
                    loadImageFromUri(imageUris[imagePosition], imageSwitcher)
                }
            }
//buton ce afiseaza imaginea anterioara
            val previousButton = binding.previous
            previousButton.setOnClickListener {
                if (imagePosition > 0) {
                    imagePosition--
                    loadImageFromUri(imageUris[imagePosition], imageSwitcher)
                }
            }

            // Seteaza imaginea initiala
            loadImageFromUri(imageUris[imagePosition], imageSwitcher)
        } else {
            Toast.makeText(this, "No images found with the given tags", Toast.LENGTH_SHORT).show()
        }
    }
//incarcare imagine din Uri
    private fun loadImageFromUri(uri: Uri, imageSwitcher: ImageSwitcher) {
        try {
            val imageView = ImageView(this)
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

            val requestOptions = RequestOptions()
                .error(android.R.drawable.ic_dialog_alert) // Replace with your error drawable resource

            Glide.with(this)
                .load(uri)
                .apply(requestOptions)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        imageView.setImageDrawable(resource)

                        // afiseaza imaginea in imageSwitcher
                        imageSwitcher.setImageDrawable(imageView.drawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        imageView.setImageDrawable(placeholder)

                        // afiseaza imaginea in imageSwitcher
                        imageSwitcher.setImageDrawable(imageView.drawable)
                    }
                })
        } catch (e: Exception) {//in caz de eroare afiseaza mesajul
            e.printStackTrace()
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }
}



