package com.example.tema4

import androidx.room.Entity
import androidx.room.PrimaryKey
//tabela unde sunt salvate imaginile
@Entity(tableName = "image")
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String
)
