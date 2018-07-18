package com.jedparsons.glamp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.jedparsons.glamp.GestureListener.Companion.gestureListener
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.back_of_card
import kotlinx.android.synthetic.main.content_main.back_of_card_contents
import kotlinx.android.synthetic.main.content_main.columns
import kotlinx.android.synthetic.main.content_main.front_of_card
import kotlinx.android.synthetic.main.content_main.premise_and_conclusion
import kotlinx.android.synthetic.main.content_main.summary
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity() {

  private lateinit var box: Box

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Timber.plant(DebugTree())

    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    box = Box.of(resources, R.raw.icelandic)

    showPremiseAndConclusion(true)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    box.titles()
        .map { menu.add(it) }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.this_is_iceland -> showPremiseAndConclusion(true)
      else -> {
        showPremiseAndConclusion(false)
        chooseDeck(box.getDeck(item.toString()))
      }
    }
    return true
  }

  private fun chooseDeck(deck: Deck) {
    deck.cards()
        .subscribe { word ->
          back_of_card_contents.visibility = GONE
          front_of_card.text = word
        }

    gestureListener(front_of_card)
        .onFling()
        .subscribe { deck.reshuffle() }

     back_of_card
        .setOnClickListener {
          back_of_card_contents.visibility = VISIBLE
          val word = deck.peek()
          summary.text = word.inTheirLanguage
          (0 until 3).forEach { columns.getChildAt(it).visibility = GONE }
          if (word.columns?.isNotEmpty() == true) {
            (0 until word.columns.size).forEach { index ->
              val column = columns.getChildAt(index) as TextView
              column.text = word.columns[index].joinToString("\n")
              column.visibility = VISIBLE
            }
          }
        }
  }

  /**
   * Show Mrs. Premise and Mrs. Conclusion, and hide everything else, or vice versa.
   */
  private fun showPremiseAndConclusion(showThem: Boolean) {
    val opacity = when {
      showThem -> 1.0f
      else -> 0.0f
    }

    // Mrs. Premise and Mrs. Conclusion.
    premise_and_conclusion.animate()
        .alpha(opacity)
        .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        .setListener(null)

    // The flash card word to translate.
    front_of_card.animate()
        .alpha(1.0f - opacity)
        .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        .setListener(null)
  }
}
