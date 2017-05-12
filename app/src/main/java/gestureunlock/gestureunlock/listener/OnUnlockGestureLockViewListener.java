package gestureunlock.gestureunlock.listener;

import android.view.View;

/**
 * Created by tangqiwei on 2017/5/12.
 */

public interface OnUnlockGestureLockViewListener extends View.OnGenericMotionListener {
    /**
     * 是否匹配
     *
     * @param matched
     */
    public void onGestureEvent(boolean matched);

    /**
     * 超过尝试次数
     */
    public void onUnmatchedExceedBoundary();
}
