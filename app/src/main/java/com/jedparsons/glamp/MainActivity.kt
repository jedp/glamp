package com.jedparsons.glamp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.jedparsons.glamp.GestureListener.Companion.gestureListener
import kotlinx.android.synthetic.main.activity_main.word_in_their_language
import kotlinx.android.synthetic.main.content_main.flash_card_content
import kotlinx.android.synthetic.main.content_main.word_in_our_language
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Timber.plant(DebugTree())

    setContentView(R.layout.activity_main)
    //setSupportActionBar(toolbar)

    val quiz = Deck.of(resources, R.raw.icelandic)

    quiz.cards()
        .subscribe { word -> word_in_our_language.text = word }

    gestureListener(flash_card_content)
        .onFling()
        .subscribe { quiz.reshuffle() }


    word_in_their_language
        .setOnClickListener { view ->
          val snackbar = Snackbar.make(view, quiz.peek(), Snackbar.LENGTH_LONG)
          snackbar.setText(quiz.peek())
          val snackbarTextView =
            snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
          snackbarTextView.setTextSize(
              TypedValue.COMPLEX_UNIT_PX,
              resources.getDimension(R.dimen.snackbar_textsize)
          )
          snackbar.show()
        }

  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }
}
