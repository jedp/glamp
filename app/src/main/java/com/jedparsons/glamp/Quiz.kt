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
  fun show(): Word {
    draws++
    return word
  }

  /** Peek at the translation. */
  fun hadToPeek() {
    peeks++
  }

  /** Simple measure of how familiar someone is with the word. */
  fun familiarity() = draws / (peeks + 1)
}

private fun ArrayList<Card>.popFirst() = this.removeAt(0)

private fun ArrayList<Card>.popLast() = this.removeAt(size - 1)

private fun ArrayList<Card>.reshuffleFirst(random: Random) {
  val card = this.popFirst()
  val newIndex = min(5 + card.familiarity() * (random.nextInt(17) + 1), size)
  this.add(newIndex, card)
}

private fun ArrayList<Card>.cycleRight() = this.add(this.popFirst())

private fun ArrayList<Card>.cycleLeft() = this.add(0, this.popLast())

/**
 * A deck of flash [Card]s.
 *
 * Subscribe to `cards()` to observe the cards as they are drawn.
 *
 * Call `reshuffle()` when you're done looking at a card.
 *
 * Call `peek()` to peek at the translation of the word.
 */
class Deck(
  val title: String,
  private val cards: ArrayList<Card>
) {

  private val random = Random()
  private val observable: BehaviorSubject<Word> = BehaviorSubject.create()

  init {
    nextCard()
  }

  fun cards(): Observable<Word> = observable

  /**
   * Put the card back in the deck. The better you know the card, the farther back it goes.
   */
  fun reshuffle() {
    cards.reshuffleFirst(random)
    nextCard()
  }

  fun cycleForward() {
    cards.cycleRight()
    nextCard()
  }

  fun cycleBackward() {
    cards.cycleLeft()
    nextCard()
  }

  fun peek() = cards.first().hadToPeek()

  private fun nextCard() {
    observable.onNext(cards.first().show())
  }
}

/**
 * A box of flash card [Deck]s.
 *
 * You can get the `titles()` of the decks.
 *
 * You cah also `getDeck(title)`.
 */
class Box(
  private val decks: ArrayList<Deck>
) {

  companion object {

    fun of(
      resources: Resources,
      @RawRes languageResourceId: Int
    ): Box = ArrayList<Deck>()
        .let {
          it.addAll(
              // Make a Deck for each section in the vocabulary
              loadWords(resources, languageResourceId).sections.map {
                val section = it
                ArrayList<Card>().let deck@{
                  it.addAll(section.words.map { Card(it) })
                  it.shuffle()
                  return@deck Deck(section.title, it)
                }
              })
          return Box(it)
        }
  }

  fun titles() = decks.map { it.title }

  fun getDeck(title: String) = decks.first { it.title == title }
}
