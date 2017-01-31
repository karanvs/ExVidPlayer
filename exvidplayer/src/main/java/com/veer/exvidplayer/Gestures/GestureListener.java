package com.veer.exvidplayer.Gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Brajendr on 1/26/2017.
 */

abstract public class GestureListener implements View.OnTouchListener,IGestureListener {

  private final GestureDetector gestureDetector;
  public static final int ONE_FINGER = 1;

  public GestureListener(Context ctx) {
    gestureDetector = new GestureDetector(ctx, new MyGestureListener());
  }

  public boolean onTouch(final View view, final MotionEvent motionEvent) {
   return gestureDetector.onTouchEvent(motionEvent);
  }

  private final class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      float deltaY = e2.getY() - e1.getY();
      float deltaX = e2.getX() - e1.getX();

      if (Math.abs(deltaX) > Math.abs(deltaY)) {
        if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
          onHorizontalScroll(e2,deltaX);
        }
      } else {
        if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
          onVerticalScroll(e2,deltaY);
        }
      }
      return false;
    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
      onTap();
      return false;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      // Fling event occurred.  Notification of this one happens after an "up" event.
      boolean result = false;
      try {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
          if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
              onSwipeRight();
            } else {
              onSwipeLeft();
            }
          }
          result = true;
        } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
          if (diffY > 0) {
            onSwipeBottom();
          } else {
            onSwipeTop();
          }
        }
        result = true;

      } catch (Exception exception) {
        exception.printStackTrace();
      }
      return result;
    }
  }

}