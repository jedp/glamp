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
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    box.titles()
        .map { menu.add(it) }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    chooseDeck(box.getDeck(item.toString()))
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
}
