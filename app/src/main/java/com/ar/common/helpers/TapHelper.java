/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ar.common.helpers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


import com.ar.navigation.data.Tap;

import java.util.ArrayDeque;

/**
 * Helper to detect taps using Android GestureDetector, and pass the taps between UI thread and
 * render thread.
 */
public final class TapHelper implements OnTouchListener {
  private final GestureDetector gestureDetector;
    public static ArrayDeque<Tap> S_QUEUES_SingleTaps = new ArrayDeque();

  /**
   * Creates the tap helper.
   *
   * @param context the application's context.
   */
  public TapHelper(Context context, InstantPlacementSettings instantPlacementSettings) {
      S_QUEUES_SingleTaps = instantPlacementSettings.getTapMotion();
    gestureDetector =
        new GestureDetector(
            context,
            new GestureDetector.SimpleOnGestureListener() {
              @Override
              public boolean onSingleTapUp(MotionEvent e) {
                // Queue tap if there is space. Tap is lost if queue is full.
                  Tap tap = new Tap(e.getX(), e.getY(), 1);
                  S_QUEUES_SingleTaps.offer(tap);
                  instantPlacementSettings.setTapMotion(e.getX(), e.getY(), 1);
//                queuedSingleTaps.offer(e);
                return true;
              }

              @Override
              public boolean onDown(MotionEvent e) {
                return true;
              }
            });
  }

  /**
   * Polls for a tap.
   *
   * @return if a tap was queued, a MotionEvent for the tap. Otherwise null if no taps are queued.
   */
  public Tap poll() {
    return S_QUEUES_SingleTaps.poll();
  }

  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    return gestureDetector.onTouchEvent(motionEvent);
  }
}