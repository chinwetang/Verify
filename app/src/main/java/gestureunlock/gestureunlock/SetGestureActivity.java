package gestureunlock.gestureunlock;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 设置手势
 * Created by tangqiwei on 2017/6/16.
 */

public class SetGestureActivity extends BaseGestureActivity {

    private int[] mResult;


    public static void actionStart(Activity activity,int requestCode){
        Intent intent=new Intent(activity,SetGestureActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected String getTxtTitle() {
        return "设置开锁图案";
    }

    @Override
    protected void initData() {
        int[] pass=getGestureData();
        if(pass!=null&&pass.length>0){
            toast(getCheckOldPass());
            UnLockGestureActivity.actionStart(this,0,pass);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            switch (resultCode) {
                case RESULT_OK:

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
        if (ids.length < getMinMarkNum()) {
            toast(getMinMarkNumString());
        } else if (mResult == null) {
            mResult = ids;
            toast(getReconfirmString());
        } else {
            if (ids.length != mResult.length) {
                toast(getInconformityString());
                mResult = null;
            } else {
                boolean isSame = true;
                for (int i = 0; i < mResult.length; i++) {
                    if (mResult[i] != ids[i]) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    setGestureData(mResult);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    toast(getInconformityString());
                }
                mResult = null;
            }
        }
    }
    /**
     * 最少连接几个点
     * @return
     */
    protected int getMinMarkNum(){
        return 4;
    }
    /**
     * 最少连接几个点 提示
     * @return
     */
    protected String getMinMarkNumString(){
        return new StringBuffer().append("至少连接").append(getMinMarkNum()).append("点").toString();
    }
    /**
     * 再次确认 提示
     * @return
     */
    protected String getReconfirmString(){
        return new StringBuffer().append("再次确认").toString();
    }
    /**
     * 两次不一致 提示
     * @return
     */
    protected String getInconformityString(){
        return new StringBuffer().append("两次不一致").toString();
    }
    /**
     * 先校验旧密码 提示
     * @return
     */
    protected String getCheckOldPass(){
        return "先校验旧密码";
    }

}
