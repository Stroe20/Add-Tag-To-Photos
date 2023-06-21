package com.example.tema4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
//functii ce modifica tabela tag
@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: Tag): Long

    @Query("SELECT * FROM tag")
    suspend fun getAllTags(): List<Tag>
    @Query("SELECT image.* FROM image INNER JOIN tag ON image.id = tag.imageId WHERE tag.text = :tagText")
    suspend fun getImagesWithTag(tagText: String): List<Image>
    @Query("SELECT * FROM image WHERE id IN (SELECT imageId FROM tag WHERE text IN (:tags) GROUP BY imageId HAVING COUNT(DISTINCT text) = :tagCount)")
    suspend fun getImagesWithTags(tags: List<String>, tagCount: Int): List<Image>
    @Query("UPDATE tag SET text = :newTag WHERE text = :oldTag")
    suspend fun updateTag(oldTag: String, newTag: String)

    @Query("DELETE FROM tag WHERE text = :tag")
    suspend fun deleteTag(tag: String)

    @Query("SELECT imageId FROM tag WHERE text = :tag")
    suspend fun getImageIdWithTag(tag: String): Long?

}
