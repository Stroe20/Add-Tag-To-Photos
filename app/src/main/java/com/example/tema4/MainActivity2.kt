package com.example.tema4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity

import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope


import com.example.tema4.databinding.ActivityMain2Binding
import com.example.tema4.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var binding2: ActivityMainBinding
    private lateinit var database: AppDatabase

    // Stocheaza Uri-ul imaginii selectate
    private var images: ArrayList<Uri?>? = null

    // pozitia curenta a imaginii selectate
    private var position = 0

    // codul de cerere pentru imagine
    private val PICK_IMAGES_CODE = 0

    // insereaza imagine in baza de date
    private suspend fun insertImage(image: Image): Long {
        return database.imageDao().insert(image)
    }

    // insereaza un tag in baza de date
    private suspend fun insertTag(tag: Tag): Long {
        return database.tagDao().insert(tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate  layout pentru View Binding
        binding = ActivityMain2Binding.inflate(layoutInflater)
        binding2 = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // Initializare baza de date

        database = AppDatabase.getInstance(applicationContext)

        // trimitere catre prima pagina cand butonul este apasat
        binding.listaTag.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // adauga tag si imaginea asociata
        binding.AddTag.setOnClickListener {
            val tagText = binding.EnterTag.text.toString()
            val selectedImages = images ?: emptyList()

            if (tagText.isNotEmpty() && selectedImages.isNotEmpty()) {//daca au fost introduse un tag si cel putin o imagine
                lifecycleScope.launch {
                    val imageIds = ArrayList<Long>()
                    for (imageUri in selectedImages) {
                        val image = Image(imageUri = imageUri.toString())
                        val imageId = insertImage(image)
                        imageIds.add(imageId)
                    }

                    for (imageId in imageIds) {
                        val tag = Tag(text = tagText, imageId = imageId)
                        insertTag(tag)
                    }

                    Toast.makeText(this@MainActivity2, "Tag added", Toast.LENGTH_SHORT).show()
                }
            } else {//daca nu a fost introdus un tag/imagine
                Toast.makeText(this@MainActivity2, "No tag or image selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Initializare lista pentru imaginile alese
        images = ArrayList()

        // imageSwitcher
        binding.imageSwitcher.setFactory { ImageView(applicationContext) }

        // Alege imagini apasand pe buton
        binding.pickImagesBtn.setOnClickListener {
            pickImagesIntent()
        }

        // Alege imaginea urmatoare apasand pe buton
        binding.nextBtn.setOnClickListener {
            if (position < images!!.size - 1) {
                position++
                binding.imageSwitcher.setImageURI(images!![position])
            } else {//in caz ca nu urmeaza nicio imagine
                Toast.makeText(this, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        // Alegea imaginea anterioara
        binding.previousBtn.setOnClickListener {
            if (position > 0) {
                position--
                binding.imageSwitcher.setImageURI(images!![position])
            } else {//in caz ca nu mai sunt imagini
                Toast.makeText(this, "No more images...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // functia pickImagesIntent
    private fun pickImagesIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE)
    }

    // Rezultatul functiei pickImagesIntent este lucrat aici(in functie de cate imagini au fost alese)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                // au fost alese mai multe imagini
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    images!!.add(imageUri)
                }
                binding.imageSwitcher.setImageURI(images!![0])
                position = 0
            } else if (data?.data != null) {
                // a fost aleasa o singura imagine
                val imageUri = data.data
                images!!.add(imageUri)
                binding.imageSwitcher.setImageURI(imageUri)
                position = 0
            }
        }
    }
}
