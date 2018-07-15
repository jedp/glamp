package com.jedparsons.glamp

import android.content.res.Resources
import android.support.annotation.RawRes
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.lang.Math.min
import java.util.Random

/** A card in a [Deck] of flash cards. */
data class Card(
  private val word: Word,
  private var draws: Int = 0,
  private var peeks: Int = 0
) {
  /** Show the word in the language you know. */
  fun show(): String {
    draws++
    return word.inOurLanguage
  }

  /** Peek at the translation. */
  fun peek(): String {
    peeks++
    return word.inTheirLanguage
  }

  /** Simple measure of how familiar someone is with the word. */
  fun familiarity() = draws / (peeks + 1)
}

private fun ArrayList<Card>.top() = this[0]

private fun ArrayList<Card>.pop() = this.removeAt(0)

/**
 * A deck of flash [Cards].
 *
 * Subscribe to `cards()` to observe the cards as they are drawn.
 *
 * Call `reshuffle()` when you're done looking at a card.
 *
 * Call `peek()` to peek at the translation of the word.
 */
class Deck(
  private val cards: ArrayList<Card>
) {

  private val random = Random()
  private val observable: BehaviorSubject<String> = BehaviorSubject.create()

  companion object {

    fun of(
      resources: Resources,
      @RawRes languageResourceId: Int
    ): Deck = ArrayList<Card>()
        .let {
          it.addAll(loadWords(resources, languageResourceId).words.map { Card(it) })
          return Deck(it)
        }
  }

  init {
    nextCard()
  }

  fun cards(): Observable<String> = observable

  /**
   * Put the card back in the deck. The better you know the card, the farther back it goes.
   */
  fun reshuffle() {
    val card = cards.pop()
    val newIndex = min(5 + card.familiarity() * (random.nextInt(12) + 1), cards.size - 1)
    cards.add(newIndex, card)
    nextCard()
  }

  fun peek() = cards.top().peek()

  private fun nextCard() {
    observable.onNext(cards.top().show())
  }
}