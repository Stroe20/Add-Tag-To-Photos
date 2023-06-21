package com.example.tema4

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
//tabela unde sunt salvate tagurile
@Entity(tableName = "tag", foreignKeys = [ForeignKey(entity = Image::class, parentColumns = ["id"], childColumns = ["imageId"], onDelete = ForeignKey.CASCADE)])
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val imageId: Long
)
