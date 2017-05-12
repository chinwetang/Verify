package gestureunlock.gestureunlock.listener;

import gestureunlock.gestureunlock.view.BackupGestureLockViewGroup;

/**
 * Created by tangqiwei on 2017/5/12.
 */

public interface OnSetGestureLockViewListener extends BackupGestureLockViewGroup.OnGestureLockViewListener {

    /**
     * 设置手势
     */
    public void onSetGesture(int[] mAnswer);
}
