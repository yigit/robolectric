package org.robolectric.shadows;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.OverScroller;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(OverScroller.class)
public class ShadowOverScroller {
  private int startX;
  private int startY;
  private int finalX;
  private int finalY;
  private long startTime;
  private long duration;
  private boolean started;

  @Implementation
  protected int getStartX() {
    return startX;
  }

  @Implementation
  protected int getStartY() {
    return startY;
  }

  @Implementation
  protected int getCurrX() {
    long dt = deltaTime();
    return dt >= duration ? finalX : startX + (int) ((deltaX() * dt) / duration);
  }

  @Implementation
  protected int getCurrY() {
    long dt = deltaTime();
    return dt >= duration ? finalY : startY + (int) ((deltaY() * dt) / duration);
  }

  @Implementation
  protected int getFinalX() {
    return finalX;
  }

  @Implementation
  protected int getFinalY() {
    return finalY;
  }

  @Implementation
  protected int getDuration() {
    return (int) duration;
  }

  @Implementation
  protected void startScroll(int startX, int startY, int dx, int dy, int duration) {
    this.startX = startX;
    this.startY = startY;
    finalX = startX + dx;
    finalY = startY + dy;
    startTime = SystemClock.currentThreadTimeMillis();
    this.duration = duration;
    started = true;
    // post a empty task so that the scheduler will actually run
    Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(() -> {}, duration);
  }

  @Implementation
  protected void abortAnimation() {
    duration = deltaTime() - 1;
  }

  @Implementation
  protected void forceFinished(boolean finished) {
    if (!finished) {
      throw new RuntimeException("Not implemented.");
    }

    finalX = getCurrX();
    finalY = getCurrY();
    duration = deltaTime() - 1;
  }

  @Implementation
  protected boolean computeScrollOffset() {
    if (!started) {
      return false;
    }
    started &= deltaTime() < duration;
    return true;
  }

  @Implementation
  protected boolean isFinished() {
    return deltaTime() > duration;
  }

  @Implementation
  protected int timePassed() {
    return (int) deltaTime();
  }

  @Implementation
  protected boolean isScrollingInDirection(float xvel, float yvel) {
    final int dx = finalX - startX;
    final int dy = finalY - startY;
    return !isFinished()
        && Math.signum(xvel) == Math.signum(dx)
        && Math.signum(yvel) == Math.signum(dy);
  }

  private long deltaTime() {
    return SystemClock.currentThreadTimeMillis() - startTime;
  }

  private int deltaX() {
    return (finalX - startX);
  }

  private int deltaY() {
    return (finalY - startY);
  }
}

