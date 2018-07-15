package com.jedparsons.glamp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.jedparsons.glamp.GestureListener.Companion.gestureListener
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.word_in_their_language
import kotlinx.android.synthetic.main.content_main.flash_card_content
import kotlinx.android.synthetic.main.content_main.premise_and_conclusion
import kotlinx.android.synthetic.main.content_main.word_in_our_language
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
        .subscribe { word -> word_in_our_language.text = word }

    gestureListener(flash_card_content)
        .onFling()
        .subscribe { deck.reshuffle() }

    word_in_their_language
        .setOnClickListener { view ->
          val snackbar = Snackbar.make(view, deck.peek(), Snackbar.LENGTH_LONG)
          snackbar.setText(deck.peek())
          val snackbarTextView =
            snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
          snackbarTextView.setTextSize(
              TypedValue.COMPLEX_UNIT_PX,
              resources.getDimension(R.dimen.snackbar_textsize)
          )
          snackbar.show()
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
    word_in_our_language.animate()
        .alpha(1.0f - opacity)
        .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        .setListener(null)

    // The translation.
    word_in_their_language.animate()
        .alpha(1.0f - opacity)
        .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
        .setListener(null)

  }
}
