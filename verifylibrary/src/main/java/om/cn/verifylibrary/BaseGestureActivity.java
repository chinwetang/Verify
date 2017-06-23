package om.cn.verifylibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import om.cn.verifylibrary.view.BmGestureLockViewGroup;


public abstract class BaseGestureActivity extends AppCompatActivity implements BmGestureLockViewGroup.OnGestureLockViewListener {

    public final static String PREFERENCES_NAME = "gesture_name";
    public final static String PREFERENCES_TIME = "time";
    public final static String PREFERENCES_PASS = "pass";
    public final static String PREFERENCES_ERROR_TIME = "error_time";
    public final static String REQUEST_ANSWER = "answer";


    protected SharedPreferences preferences;
    private BmGestureLockViewGroup mGestureLockViewGroup;
    protected LinearLayout unlockLayou;
    protected TextView txtPassUnlock;

    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeSetContentLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gesture);

        unlockLayou = (LinearLayout) findViewById(R.id.llayout_unlock);
        txtPassUnlock = (TextView) findViewById(R.id.txt_pass_unlock);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        mGestureLockViewGroup = (BmGestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        mGestureLockViewGroup.initData(getNoFingerInnerCircleColor(), getNoFingerOuterCircleColor(), getFingerOnColor(), getFingerUpColor(), getCircleCoefficient(), getSpaceCoefficient(), getLineStrokeWidth(), getLineAalpha());
        mGestureLockViewGroup.setOnGestureLockViewListener(this);

        txtTitle.setText(getTxtTitle());
        unlockLayou.setVisibility(isShowPassUnLock() ? View.VISIBLE : View.GONE);
        preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        initView();
        initData();
    }

    protected abstract String getTxtTitle();

    protected void initData() {

    }

    protected void initView() {

    }

    /**
     * 在oncreate后执行，一般为获取intentata
     */
    protected void onBeforeSetContentLayout() {
    }

    protected int getNoFingerInnerCircleColor() {
        return 0xFFFFFFFF;
    }

    protected int getNoFingerOuterCircleColor() {
        return 0xFF1fb8ff;
    }

    protected int getFingerOnColor() {
        return 0xFF1fb8ff;
    }

    protected int getFingerUpColor() {
        return 0xFF1fb8ff;
    }

    protected float getCircleCoefficient() {
        return 0.8f;
    }

    protected float getSpaceCoefficient() {
        return 0.6f;
    }

    protected float getLineStrokeWidth() {
        return 8;
    }

    protected int getLineAalpha() {
        return 255;
    }


    @Override
    public void onBlockSelected(int cId) {
//        Log.d("BaseGestureActivity", String.valueOf(cId));
    }

    @Override
    public void onBlocksSelected(int[] ids) {
        StringBuffer buffer = new StringBuffer();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                buffer.append(ids[i]);
                if (i + 1 != ids.length) {
                    buffer.append(",");
                }
            }
        }
//        Log.d("BaseGestureActivity", buffer.toString());
    }

    /**
     * 整型数组 转 字符串
     *
     * @param ids
     * @return
     */
    public static String intArToStr(int[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < ids.length; i++) {
            buffer.append(ids[i]);
        }
        return buffer.toString();
    }

    /**
     * 字符串 转 整型数组
     *
     * @param str
     * @return
     */
    public static int[] strToIniAr(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int[] n = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            n[i] = Integer.parseInt(str.substring(i, i + 1));
        }
        return n;
    }

    /**
     * 获取密码
     *
     * @return
     */
    public int[] getGestureData() {
        return strToIniAr(preferences.getString(PREFERENCES_PASS, ""));
    }

    /**
     * 设置密码
     */
    public void setGestureData(int[] mResult) {
        preferences.edit().putString(PREFERENCES_PASS, intArToStr(mResult)).commit();
    }

    /**
     * 获取错误的次数
     *
     * @return
     */
    public int getErrorTime() {
        return preferences.getInt(PREFERENCES_TIME, 0);
    }

    /**
     * 是否显示密码解锁
     *
     * @return
     */
    protected boolean isShowPassUnLock() {
        return false;
    }

    /**
     * 密码解锁 点击事件
     *
     * @return
     */
    public void onPassUnlock(View v) {
//        Toast.makeText(this,"密码解锁",Toast.LENGTH_SHORT).show();
    }

    /**
     *  吐司 提示
     */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
