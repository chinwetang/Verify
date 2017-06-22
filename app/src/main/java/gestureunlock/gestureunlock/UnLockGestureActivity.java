package gestureunlock.gestureunlock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 解锁
 * Created by tangqiwei on 2017/6/19.
 */

public class UnLockGestureActivity extends BaseGestureActivity {


    private int[] mAnswer;
    private int mTryTimes;


    public static void actionStart(Activity activity, int requestCode, int[] answer) {
        Intent intent = new Intent(activity, UnLockGestureActivity.class);
        intent.putExtra(REQUEST_ANSWER, answer);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        mAnswer = getIntent().getIntArrayExtra(REQUEST_ANSWER);
    }

    @Override
    protected String getTxtTitle() {
        return "请描绘开锁图案";
    }

    @Override
    protected void initView() {
        unlockLayou.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        if (mAnswer == null || mAnswer.length == 0) {
            toast(getSetPass());
            SetGestureActivity.actionStart(this, 0);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    mAnswer = getGestureData();
                    break;
                case RESULT_CANCELED:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBlocksSelected(int[] ids) {
        super.onBlocksSelected(ids);
        if (mAnswer == null || mAnswer.length == 0) {
            return;
        }
        mTryTimes++;
        if (checkAnswer(ids)) {
            setResult(RESULT_OK);
            finish();
        } else {
            if (mTryTimes >= getMaxTime()) {
                toast(getErroNum());
            }
        }


    }

    /**
     * 检查用户绘制的手势是否正确
     *
     * @return
     */
    private boolean checkAnswer(int[] result) {
        if (mAnswer.length != result.length)
            return false;

        for (int i = 0; i < mAnswer.length; i++) {
            if (mAnswer[i] != result[i])
                return false;
        }

        return true;
    }

    /**
     * 最大次数
     *
     * @return
     */
    protected int getMaxTime() {
        return 5;
    }

    /**
     * 预留 用于计算等待时间
     *
     * @param time
     * @return
     */
    protected long getWaitTime(int time) {
        long oneMinu = 1000 * 60;
        switch (time) {
            case 0:
                return 0;
            case 1:
            case 2:
            case 3:
                return 3 * time * oneMinu;
            case 4:
            case 5:
            case 6:
                return 5 * time * oneMinu;
            default:
                return 60 * oneMinu;
        }
    }

    /**
     * 预留 校验需要等待的时间
     *
     * @return ture:无需等待;false:需要等待.
     */
    protected boolean checkWaitTime() {
        if (getErrorTime() > 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * 错误5次 提示
     *
     * @return
     */
    protected String getErroNum() {
        return new StringBuffer().append("错误").append(getMaxTime()).append("次").toString();
    }
    /**
     * 请先设置密码 提示
     *
     * @return
     */
    protected String getSetPass() {
        return "请先设置密码";
    }
    @Override
    protected boolean isShowPassUnLock() {
        return true;
    }
}
