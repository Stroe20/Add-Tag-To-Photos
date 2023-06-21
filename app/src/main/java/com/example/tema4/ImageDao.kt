package com.example.tema4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
//fuctii ce modifica tabela image
@Dao
interface ImageDao {
    @Insert
    suspend fun insert(image: Image):Long

    @Query("SELECT * FROM image")
    suspend fun getAllImages(): List<Image>
    @Query("SELECT * FROM image WHERE id IN (SELECT imageId FROM tag WHERE text IN (:tags) GROUP BY imageId HAVING COUNT(DISTINCT text) = :tagCount)")
    suspend fun getImagesWithTags(tags: List<String>, tagCount: Int): List<Image>
    @Query("DELETE FROM image WHERE id = :imageId")
    suspend fun deleteImageById(imageId: Long)
}
