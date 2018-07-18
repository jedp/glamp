package com.jedparsons.glamp

import android.content.res.Resources
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class Word(
  val inOurLanguage: String,
  val inTheirLanguage: String,
  val columns: List<List<String>>?,
  val type: String
)

@JsonClass(generateAdapter = true)
data class Section(
  val title: String,
  val words: List<Word>
)

@JsonClass(generateAdapter = true)
data class Vocabulary(
  val sections: List<Section>
)

/**
 * Load the vocabulary from the JSON file at `vocabularyResId`.
 */
fun loadWords(
  resources: Resources,
  vocabularyResId: Int
): Vocabulary = resources
    .openRawResource(vocabularyResId)
    .bufferedReader()
    .use { it.readText() }
    .let {
      Moshi.Builder()
          .add(KotlinJsonAdapterFactory())
          .build()
          .adapter(Vocabulary::class.java)
          .fromJson(it)
    } ?: throw RuntimeException("En leiðinlegt!")
