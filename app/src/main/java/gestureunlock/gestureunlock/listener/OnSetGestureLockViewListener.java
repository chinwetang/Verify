package gestureunlock.gestureunlock.listener;

import gestureunlock.gestureunlock.view.BaseGestureLockViewGroup;

/**
 * Created by tangqiwei on 2017/5/12.
 */

public interface OnSetGestureLockViewListener extends BaseGestureLockViewGroup.OnGestureLockViewListener {

    /**
     * 设置手势
     */
    public void onSetGesture(int[] mAnswer);
}
