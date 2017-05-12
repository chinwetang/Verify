package gestureunlock.gestureunlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import gestureunlock.gestureunlock.view.BaseGestureLockViewGroup;

public class MainActivity extends AppCompatActivity {

    private BaseGestureLockViewGroup mGestureLockViewGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGestureLockViewGroup = (BaseGestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        mGestureLockViewGroup.setAnswer(new int[] { 1, 2, 3, 4,5 });
        mGestureLockViewGroup
                .setOnGestureLockViewListener(new BaseGestureLockViewGroup.OnGestureLockViewListener()
                {

                    @Override
                    public void onUnmatchedExceedBoundary()
                    {
                        Toast.makeText(MainActivity.this, "错误5次...",
                                Toast.LENGTH_SHORT).show();
                        mGestureLockViewGroup.setUnMatchExceedBoundary(5);
                    }

                    @Override
                    public void onGestureEvent(boolean matched)
                    {
                        Toast.makeText(MainActivity.this, matched+"",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBlockSelected(int cId)
                    {
                    }
                });
    }
}
