package gestureunlock.gestureunlock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import gestureunlock.gestureunlock.view.FingerprintApprovePopView;

/**
 * Created by tangqiwei on 2017/6/19.
 */

public class MainActivity extends Activity implements View.OnClickListener,FingerprintApprovePopView.DismissListener,FingerprintApprovePopView.ToPasswordListener{
    Button set;
    Button unlock;
    Button fingerprint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        set= (Button) findViewById(R.id.set);
        unlock= (Button) findViewById(R.id.unlock);
        fingerprint= (Button) findViewById(R.id.fingerprint);
        set.setOnClickListener(this);
        unlock.setOnClickListener(this);
        fingerprint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set:
                SetGestureActivity.actionStart(this,0);
                break;
            case R.id.unlock:
                UnLockGestureActivity.actionStart(this,1,BaseGestureActivity.strToIniAr(getSharedPreferences(BaseGestureActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getString(BaseGestureActivity.PREFERENCES_PASS,"")));
                break;
            case R.id.fingerprint:
                new FingerprintApprovePopView(this,this,this).getPic(getWindow().getDecorView());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this,"设置成功:"+data.getIntArrayExtra("result"),Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(this,"设置失败",Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case 1:
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this,"解锁成功",Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(this,"解锁失败",Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void cancelReceiveValue() {
        Toast.makeText(this,"关闭",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toPassWord() {

    }
}
