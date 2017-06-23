package gestureunlock.gestureunlock.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import gestureunlock.gestureunlock.R;

public class FingerprintApprovePopView {


    private PopupWindow pop;
    private Activity context;
    private DismissListener listener;
    private ToPasswordListener mPasswordListener;
    private FingerprintIdentify mFingerprintIdentify;
    private VerifyFingerprintListener mFingerprintListener;
    private String mPhone;

    public FingerprintApprovePopView(Activity context) {
        super();
        this.context = context;
        this.mPhone=mPhone;
        popupInit();
    }

    public FingerprintApprovePopView(Activity context,
                                     DismissListener listener,VerifyFingerprintListener fingerprintListener) {
        super();
        this.context = context;
        this.listener = listener;
        this.mFingerprintListener=fingerprintListener;
        popupInit();
    }

    public FingerprintApprovePopView(Activity context,
                                     DismissListener listener, VerifyFingerprintListener fingerprintListener, ToPasswordListener passwordListener) {
        super();
        this.context = context;
        this.listener = listener;
        this.mFingerprintListener=fingerprintListener;
        this.mPasswordListener =passwordListener;
        popupInit();
    }

    public void getPic(View v) {
//        ll_popup.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.activity_translate_up_in));
        mFingerprintIdentify = new FingerprintIdentify(context);
        if(!mFingerprintIdentify.isHardwareEnable()){
            toast("没有搭载硬件");
            return;
        }else if(!mFingerprintIdentify.isRegisteredFingerprint()){
            toast("未录入指纹");
            return;
        }else{
            mFingerprintIdentify.startIdentify(3, new BaseFingerprint.FingerprintIdentifyListener() {
                @Override
                public void onSucceed() {
                    // 验证成功，自动结束指纹识别
                    mFingerprintListener.onSucceed();
                    pop.dismiss();
                }

                @Override
                public void onNotMatch(int availableTimes) {
                    // 指纹不匹配，并返回可用剩余次数并自动继续验证
                    mFingerprintListener.onNotMatch(availableTimes);
                }

                @Override
                public void onFailed() {
                    // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
                    mFingerprintListener.onFailed();
                    pop.dismiss();
                }
            });
        }
        pop.showAtLocation(v, Gravity.BOTTOM, 0, 1);
    }


    @SuppressLint("NewApi")
    public void popupInit() {

        pop = new PopupWindow(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.window_fingerprint, null);


        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        pop.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_transparent));
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        LinearLayout llayoutPass= (LinearLayout) view.findViewById(R.id.llayout_pass);
        TextView txtPass= (TextView) view.findViewById(R.id.txt_pass);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.cancelReceiveValue();
                }
                mFingerprintIdentify.cancelIdentify();
            }
        });
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
            }
        });
        if(mPasswordListener !=null){
            llayoutPass.setVisibility(View.VISIBLE);
            txtPass.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPasswordListener.toPassWord();
                    pop.dismiss();
                }
            });
        }
    }

    public interface DismissListener {
        public void cancelReceiveValue();

    }
    public interface ToPasswordListener {
        public void toPassWord();

    }
    public interface VerifyFingerprintListener {
        public void onSucceed();

        public void onNotMatch(int availableTimes);

        public void onFailed();

    }
    /**
     *  吐司 提示
     */
    protected void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
