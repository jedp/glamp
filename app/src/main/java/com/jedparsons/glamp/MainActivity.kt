package com.jedparsons.glamp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.jedparsons.glamp.GestureEvents.Fling
import com.jedparsons.glamp.GestureEvents.Tap
import com.jedparsons.glamp.GestureListener.Companion.gestureListener
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.back_of_card
import kotlinx.android.synthetic.main.content_main.columns
import kotlinx.android.synthetic.main.content_main.flash_card_content
import kotlinx.android.synthetic.main.content_main.front_of_card
import kotlinx.android.synthetic.main.content_main.premise_and_conclusion
import kotlinx.android.synthetic.main.content_main.summary
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity() {

  private lateinit var box: Box

  private var defaultVisibility = GONE

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Timber.plant(DebugTree())

    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    box = Box.of(resources, R.raw.icelandic)
    back_of_card.visibility = defaultVisibility

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
      R.id.action_visibility -> {
        defaultVisibility = when (defaultVisibility) {
          GONE -> VISIBLE
          else -> GONE
        }
        back_of_card.visibility = defaultVisibility
      }
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
        .subscribe(::displayNewWord)

    gestureListener(flash_card_content)
        .events()
        .subscribe {
          when (it) {
            is Fling -> deck.reshuffle()
            is Tap -> {
              deck.peek()
              back_of_card.visibility = VISIBLE
            }
          }
        }
  }

  private fun displayNewWord(word: Word) {
    front_of_card.text = word.inOurLanguage

    back_of_card.visibility = defaultVisibility

    summary.text = word.inTheirLanguage

    // Hide the three text columns.
    (0 until 3).forEach {
      columns.getChildAt(it)
          .visibility = GONE
    }

    // Fill the columns that should have text and show them.
    (0 until word.columns.size).forEach { index ->
      val column = columns.getChildAt(index) as TextView
      column.text = word.columns[index].joinToString("\n")
      column.visibility = VISIBLE
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
