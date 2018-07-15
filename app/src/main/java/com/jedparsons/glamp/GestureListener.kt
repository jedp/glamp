package com.jedparsons.glamp

import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import io.reactivex.subjects.PublishSubject

const val MIN_SWIPE_DISTANCE_PX = 150

class GestureListener : OnGestureListener {

  private val observable: PublishSubject<Unit> = PublishSubject.create()

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
  fun onFling() = observable

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
      observable.onNext(Unit)
    }
    return true
  }

  override fun onShowPress(motionEvent: MotionEvent) {}

  override fun onSingleTapUp(motionEvent: MotionEvent) = true

  override fun onDown(motionEvent: MotionEvent) = true

  override fun onScroll(
    downEvent: MotionEvent,
    triggerEvent: MotionEvent,
    distanceX: Float,
    distanceY: Float
  ): Boolean = true

  override fun onLongPress(motionEvent: MotionEvent) {}
}