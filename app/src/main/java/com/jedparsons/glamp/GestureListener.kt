package com.jedparsons.glamp

import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import com.jedparsons.glamp.GestureEvents.FlingLeft
import com.jedparsons.glamp.GestureEvents.FlingRight
import com.jedparsons.glamp.GestureEvents.Tap
import io.reactivex.subjects.PublishSubject

const val MIN_SWIPE_DISTANCE_PX = 150

class GestureListener : OnGestureListener {

  private val events: PublishSubject<GestureEvents> = PublishSubject.create()

  companion object {

    /**
     * Wire up a gesture listener for the view and return it.
     */
    fun gestureListener(
      view: View
    ): GestureListener {
      val listener = GestureListener()
      val gestureDetector = GestureDetector(view.context, listener)
      view.setOnTouchListener({ _, event ->
        gestureDetector.onTouchEvent(event) })
      return listener
    }
  }

  /**
   * [PublishSubject] for fling events.
   */
  fun events() = events

  override fun onFling(
    startEvent: MotionEvent,
    endEvent: MotionEvent,
    velocityX: Float,
    velocityY: Float
  ): Boolean {
    // It counts if the Euclidean distance between the two points is at least MIN_SWIPE_DISTANCE_PX.
    if ((startEvent.x - endEvent.x) * (startEvent.x - endEvent.x) +
        (startEvent.y - endEvent.y) * (startEvent.y - endEvent.y) >
        MIN_SWIPE_DISTANCE_PX * MIN_SWIPE_DISTANCE_PX
    ) {
      events.onNext(when {
        startEvent.x < endEvent.x -> FlingRight
        else -> FlingLeft
      })
    }
    return true
  }

  override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
    events.onNext(Tap)
    return true
  }

  override fun onShowPress(motionEvent: MotionEvent) {}

  override fun onDown(motionEvent: MotionEvent) = true

  override fun onScroll(
    downEvent: MotionEvent,
    triggerEvent: MotionEvent,
    distanceX: Float,
    distanceY: Float
  ): Boolean = true

  override fun onLongPress(motionEvent: MotionEvent) {}
}