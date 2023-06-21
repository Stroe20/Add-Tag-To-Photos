
package com.example.tema4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tema4.databinding.ActivityMain2Binding
import com.example.tema4.databinding.ActivityMainBinding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.widget.Toast
import androidx.lifecycle.lifecycleScope


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: ActivityMain2Binding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate  layout pentru View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding2 = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initializare baza de date
        database = AppDatabase.getInstance(applicationContext)

        // click listener pentru butoane
        binding.Add.setOnClickListener { startActivity(Intent(this, MainActivity2::class.java)) }//trimitere catre mainactivity2
        binding.Find.setOnClickListener { startActivity(Intent(this, MainActivity3::class.java)) }//trimitere catre mainactivity3

        // apelare functie ce afiseaza toate tagurile create
        setupListaTaguriTextView()

        // butonul de modificare tag
        binding.Modify.setOnClickListener {
            val oldTag = binding.oldTag.text.toString()//valoarea introdusa in oldTag este savlata
            val newTag = binding.newTag.text.toString()//valoarea introdusa in newTag este savlata
            if (oldTag.isNotEmpty() && newTag.isNotEmpty()) {//daca EditTexturile nu sunt goale
                // Lanseaza o corutina pentru a updata tag-ul in baza de date
                lifecycleScope.launch {
                    updateTag(oldTag, newTag)
                }
                // Updateaza lista de taguri afisata
                setupListaTaguriTextView()
            } else {//in caz ca nu a fost introdus tagul vechi/nou afiseaza mesajul urmator
                Toast.makeText(this, "Please enter both old and new tags", Toast.LENGTH_SHORT).show()
            }
        }

        // butonul de stergere
        binding.Delete.setOnClickListener {
            val oldTag = binding.oldTag.text.toString()//salveaza tagul vechi in variabila
            if (oldTag.isNotEmpty()) {//daca a fost introdusa o valoare
                // Lanseaza o corutina ce sterge tagul si imaginea asociata din baza de date
                lifecycleScope.launch {
                    deleteTagAndImage(oldTag)
                }
                // updateaza lista de taguri
                setupListaTaguriTextView()
            } else {// in caz ca nu a fost introdus un tag afiseaza mesajul
                Toast.makeText(this, "Please enter the old tag", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // functie ce afiseaza tagurile in textView
    private fun setupListaTaguriTextView() {
        GlobalScope.launch(Dispatchers.Main) {
            val tags = withContext(Dispatchers.IO) {
                // obtine toate tag-urile din baza de date
                database.tagDao().getAllTags()
            }

            // se afiseaza fiecare tag o singura data si sunt separate prin ','
            val distinctTagTexts = tags.map { it.text }.distinct()
            val joinedTagTexts = distinctTagTexts.joinToString(", ")

            val textView = binding.ListaTaguri
            textView.text = joinedTagTexts
        }
    }

    // functie ce updateaza tag-ul in baza de date
    private suspend fun updateTag(oldTag: String, newTag: String) {
        withContext(Dispatchers.IO) {
            // updateaza tag-ul
            database.tagDao().updateTag(oldTag, newTag)
        }// tag-ul a fost updatat cu succes
        Toast.makeText(this, "Tag updated successfully", Toast.LENGTH_SHORT).show()
    }

    // functia de stergere tag si imagine asociata
    private suspend fun deleteTagAndImage(tag: String) {
        val tagDao = database.tagDao()
        val imageDao = database.imageDao()

        val imageId = tagDao.getImageIdWithTag(tag)
        if (imageId != null) {
            // sterge tag-ul si imaginea asociata
            tagDao.deleteTag(tag)
            imageDao.deleteImageById(imageId)
            //stergerea a fost efectuata cu succes
            Toast.makeText(this, "Tag and associated image deleted successfully", Toast.LENGTH_SHORT).show()
        } else {
            //tag-ul nu a fost gasit
            Toast.makeText(this, "Tag not found", Toast.LENGTH_SHORT).show()
        }
    }
}
