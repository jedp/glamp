package com.jedparsons.glamp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.jedparsons.glamp.GestureListener.Companion.gestureListener
import kotlinx.android.synthetic.main.activity_main.everything
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.flash_card.front_of_card
import kotlinx.android.synthetic.main.flash_card.premise_and_conclusion
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity() {

  private lateinit var practicum: Practicum

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Timber.plant(DebugTree())

    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    practicum = Practicum(this, gestureListener(everything).events())

    showPremiseAndConclusion(true)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    practicum.titles
        .map { menu.add(it) }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_visibility -> practicum.toggleLearningMode()
      R.id.shuffle -> practicum.shuffleDecks()
      R.id.this_is_iceland -> showPremiseAndConclusion(true)
      else -> {
        showPremiseAndConclusion(false)
        practicum.studyDeck(item.toString())
      }
    }
    return true
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
