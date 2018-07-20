package com.jedparsons.glamp

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import com.jedparsons.glamp.GestureEvents.FlingLeft
import com.jedparsons.glamp.GestureEvents.FlingRight
import com.jedparsons.glamp.GestureEvents.Tap
import com.jedparsons.glamp.LearningMode.Drill
import com.jedparsons.glamp.LearningMode.Review
import io.reactivex.Observable
import kotlinx.android.synthetic.main.flash_card.view.back_of_card
import kotlinx.android.synthetic.main.flash_card.view.columns
import kotlinx.android.synthetic.main.flash_card.view.front_of_card
import kotlinx.android.synthetic.main.flash_card.view.part_of_speech
import kotlinx.android.synthetic.main.flash_card.view.summary

sealed class LearningMode(val backOfCardVisibility: Int) {
  object Review : LearningMode(VISIBLE)
  object Drill : LearningMode(GONE)
}

sealed class GestureEvents {
  object FlingRight : GestureEvents()
  object FlingLeft : GestureEvents()
  object Tap : GestureEvents()
}

class Practicum(
  private val context: Activity,
  private val gestures: Observable<GestureEvents>,
  var learningMode: LearningMode = Review
) {

  private val box = Box.of(context.resources, R.raw.icelandic)

  val titles: List<String>
    get() = box.titles()

  fun shuffleDecks() = box.shuffleDecks()

  fun studyDeck(name: String) {
    val deck = box.getDeck(name)

    deck
        .cards()
        .subscribe(this::displayNewWord)

    gestures
        .subscribe {
          when (it) {
            is FlingRight -> when (learningMode) {
              Review -> deck.cycleForward()
              Drill -> deck.reshuffleCard()
            }
            is FlingLeft -> when (learningMode) {
              Review -> deck.cycleBackward()
              Drill -> deck.reshuffleCard()
            }
            is Tap -> {
              deck.peek()
              revealWord()
            }
          }
        }
  }

  fun toggleLearningMode() {
    learningMode = when (learningMode) {
      Review -> Drill
      Drill -> Review
    }
    context.findViewById<LinearLayout>(R.id.back_of_card)
        .visibility = learningMode.backOfCardVisibility
  }

  fun displayNewWord(word: Word) {
    with(context.findViewById(R.id.flash_card_content) as ConstraintLayout) {
      front_of_card.text = word.inOurLanguage
      back_of_card.visibility = learningMode.backOfCardVisibility
      summary.text = word.inTheirLanguage
      part_of_speech.text = word.type

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
  }

  private fun revealWord() {
    context.findViewById<LinearLayout>(R.id.back_of_card)
        .visibility = VISIBLE
  }
}