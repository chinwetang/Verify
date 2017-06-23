package gestureunlock.gestureunlock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gestureunlock.gestureunlock.R;

/**
 * 整体包含n*n个GestureLockView,每个GestureLockView间间隔mMarginBetweenLockView，
 * 最外层的GestureLockView与容器存在mMarginBetweenLockView的外边距
 *
 * 关于GestureLockView的边长（n*n）： n * mGestureLockViewWidth + ( n + 1 ) *
 * mMarginBetweenLockView = mWidth ; 得：mGestureLockViewWidth = 4 * mWidth / ( 5
 * * mCount + 1 ) 注：mMarginBetweenLockView = mGestureLockViewWidth * 0.25 ;
 * Created by tangqiwei on 2017/5/11.
 */

public class BmGestureLockViewGroup extends RelativeLayout {

    private static final String TAG = "BackupGestureLockViewGroup";
    /**
     * 保存所有的GestureLockView
     */
    private BmGestureLockView[] mBmGestureLockViews;
    /**
     * 每个边上的GestureLockView的个数
     */
    private int mCount = 4;
    /**
     * 保存用户选中的GestureLockView的id
     */
    private List<Integer> mChoose = new ArrayList<Integer>();

    private Paint mPaint;

    /**
     * 引线宽度
     */
    private float lineStrokeWidth =4;
    /**
     * 引线透明度
     */
    private int lineAalpha =50;
    /**
     * 内外圆半径比
     */
    private double circleCoefficient =0.7;
    /**
     * 间距与mGestureLockViewWidth比例
     * mMarginBetweenLockView/mGestureLockViewWidth
     */
    private double spaceCoefficient =0.25;
    /**
     * 每个GestureLockView中间的间距 设置为：mGestureLockViewWidth * spaceCoefficient
     */
    private int mMarginBetweenLockView = 30;
    /**
     * GestureLockView的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mGestureLockViewWidth;

    /**
     * GestureLockView无手指触摸的状态下内圆的颜色
     */
    private int mNoFingerInnerCircleColor = 0xFF939090;
    /**
     * GestureLockView无手指触摸的状态下外圆的颜色
     */
    private int mNoFingerOuterCircleColor = 0xFFE0DBDB;
    /**
     * GestureLockView手指触摸的状态下内圆和外圆的颜色
     */
    private int mFingerOnColor = 0xFF378FC9;
    /**
     * GestureLockView手指抬起的状态下内圆和外圆的颜色
     */
    private int mFingerUpColor = 0xFFFF0000;

    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;

    private Path mPath;
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引下的结束位置
     */
    private Point mTmpTarget = new Point();
    /**
     * 回调接口
     */
    private OnGestureLockViewListener mOnGestureLockViewListener;

    public BmGestureLockViewGroup(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BmGestureLockViewGroup(Context context, AttributeSet attrs,
                                  int defStyle)
    {
        super(context, attrs, defStyle);
        /**
         * 获得所有自定义的参数的值
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BmGestureLockViewGroup, defStyle, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.BmGestureLockViewGroup_color_no_finger_inner_circle:
                    mNoFingerInnerCircleColor = a.getColor(attr,
                            mNoFingerInnerCircleColor);
                    break;
                case R.styleable.BmGestureLockViewGroup_color_no_finger_outer_circle:
                    mNoFingerOuterCircleColor = a.getColor(attr,
                            mNoFingerOuterCircleColor);
                    break;
                case R.styleable.BmGestureLockViewGroup_color_finger_on:
                    mFingerOnColor = a.getColor(attr, mFingerOnColor);
                    break;
                case R.styleable.BmGestureLockViewGroup_color_finger_up:
                    mFingerUpColor = a.getColor(attr, mFingerUpColor);
                    break;
                case R.styleable.BmGestureLockViewGroup_count:
                    mCount = a.getInt(attr, 3);
                    break;
                case R.styleable.BmGestureLockViewGroup_spaceCoefficient:
                    spaceCoefficient = a.getFloat(attr, 0.25f);
                    break;
                case R.styleable.BmGestureLockViewGroup_circleCoefficient:
                    circleCoefficient = a.getFloat(attr, 0.7f);
                    break;
                case R.styleable.BmGestureLockViewGroup_lineAalpha:
                    lineAalpha = a.getInt(attr,50);
                    break;
                case R.styleable.BmGestureLockViewGroup_lineStrokeWidth:
                    lineStrokeWidth = a.getDimension(attr,4f);
                    break;
                default:
                    break;
            }
        }

        a.recycle();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        // mPaint.setStrokeWidth(20);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setColor(Color.parseColor("#aaffffff"));
        mPath = new Path();
    }

    /**
     *
     * @param mNoFingerInnerCircleColor GestureLockView无手指触摸的状态下内圆的颜色
     * @param mNoFingerOuterCircleColor GestureLockView无手指触摸的状态下外圆的颜色
     * @param mFingerOnColor GestureLockView手指触摸的状态下内圆和外圆的颜色
     * @param mFingerUpColor GestureLockView手指抬起的状态下内圆和外圆的颜色
     * @param circleCoefficient 内外圆半径比
     * @param spaceCoefficient 间距与mGestureLockViewWidth比例 mMarginBetweenLockView/mGestureLockViewWidth
     * @param lineStrokeWidth 引线宽度
     * @param lineAalpha 引线透明度
     */
    public void initData(int mNoFingerInnerCircleColor,int mNoFingerOuterCircleColor,int mFingerOnColor,int mFingerUpColor,float circleCoefficient,float spaceCoefficient,float lineStrokeWidth,int lineAalpha){
        this.mNoFingerInnerCircleColor=mNoFingerInnerCircleColor;
        this.mNoFingerOuterCircleColor=mNoFingerOuterCircleColor;
        this.mFingerOnColor=mFingerOnColor;
        this.mFingerUpColor=mFingerUpColor;
        this.circleCoefficient=circleCoefficient;
        this.spaceCoefficient=spaceCoefficient;
        this.lineStrokeWidth=lineStrokeWidth;
        this.lineAalpha=lineAalpha;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Log.e(TAG, mWidth + "");
        // Log.e(TAG, mHeight + "");

        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;

        // setMeasuredDimension(mWidth, mHeight);

        // 初始化mGestureLockViews
        if (mBmGestureLockViews == null)
        {
            mBmGestureLockViews = new BmGestureLockView[mCount * mCount];
            // 计算每个GestureLockView的宽度
            mGestureLockViewWidth = (int) (mWidth/((mCount+1)* spaceCoefficient +mCount));
            //计算每个GestureLockView的间距
            mMarginBetweenLockView = (int) (mGestureLockViewWidth * spaceCoefficient);
            // 设置画笔的宽度为GestureLockView的内圆直径稍微小点（不喜欢的话，随便设）
            mPaint.setStrokeWidth(lineStrokeWidth);

            for (int i = 0; i < mBmGestureLockViews.length; i++)
            {
                //初始化每个GestureLockView
                mBmGestureLockViews[i] = new BmGestureLockView(getContext(),
                        mNoFingerInnerCircleColor, mNoFingerOuterCircleColor,
                        mFingerOnColor, mFingerUpColor,circleCoefficient);
                mBmGestureLockViews[i].setId(i + 1);
                //设置参数，主要是定位GestureLockView间的位置
                RelativeLayout.LayoutParams lockerParams = new RelativeLayout.LayoutParams(
                        mGestureLockViewWidth, mGestureLockViewWidth);

                // 不是每行的第一个，则设置位置为前一个的右边
                if (i % mCount != 0)
                {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF,
                            mBmGestureLockViews[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mCount - 1)
                {
                    lockerParams.addRule(RelativeLayout.BELOW,
                            mBmGestureLockViews[i - mCount].getId());
                }
                //设置右下左上的边距
                int rightMargin = mMarginBetweenLockView;
                int bottomMargin = mMarginBetweenLockView;
                int leftMagin = 0;
                int topMargin = 0;
                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i >= 0 && i < mCount)// 第一行
                {
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mCount == 0)// 第一列
                {
                    leftMagin = mMarginBetweenLockView;
                }

                lockerParams.setMargins(leftMagin, topMargin, rightMargin,
                        bottomMargin);
                mBmGestureLockViews[i].setMode(BmGestureLockView.Mode.STATUS_NO_FINGER);
                addView(mBmGestureLockViews[i], lockerParams);
            }

//            Log.e(TAG, "mWidth = " + mWidth + " ,  mGestureViewWidth = "
//                    + mGestureLockViewWidth + " , mMarginBetweenLockView = "
//                    + mMarginBetweenLockView);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                // 重置
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mFingerOnColor);
                mPaint.setAlpha(lineAalpha);
                BmGestureLockView child = getChildIdByPos(x, y);
                if (child != null)
                {
                    int cId = child.getId();
                    if (!mChoose.contains(cId))
                    {
                        mChoose.add(cId);
                        child.setMode(BmGestureLockView.Mode.STATUS_FINGER_ON);
                        if (mOnGestureLockViewListener != null)
                            mOnGestureLockViewListener.onBlockSelected(cId);
                        // 设置指引线的起点
                        mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
                        mLastPathY = child.getTop() / 2 + child.getBottom() / 2;

                        if (mChoose.size() == 1)// 当前添加为第一个
                        {
                            mPath.moveTo(mLastPathX, mLastPathY);
                        } else
                        // 非第一个，将两者使用线连上
                        {
                            mPath.lineTo(mLastPathX, mLastPathY);
                        }

                    }
                }
                // 指引线的终点
                mTmpTarget.x = x;
                mTmpTarget.y = y;
                break;
            case MotionEvent.ACTION_UP:

                mPaint.setColor(mFingerUpColor);
                mPaint.setAlpha(lineAalpha);

//                switch (mode) {
//                    case MODE_SET:
//                        if(mChoose.size()<4){
//                            Toast.makeText(getContext(),"至少连接四个点",Toast.LENGTH_SHORT).show();
//                        }else if(mResult==null){
//                                mResult=new int[mChoose.size()];
//                            for (int i = 0; i < mResult.length; i++) {
//                                mResult[i]=mChoose.get(i);
//                            }
//                            Toast.makeText(getContext(),"再次确认",Toast.LENGTH_SHORT).show();
//                        }else{
//                            if(mChoose.size()!=mResult.length){
//                                Toast.makeText(getContext(),"两次输入不一样",Toast.LENGTH_SHORT).show();
//                                mResult=null;
//                            }else{
//                                boolean isSame=true;
//                                for (int i = 0; i < mResult.length; i++) {
//                                    if(mResult[i]!=mChoose.get(i)){
//                                        isSame=false;
//                                        break;
//                                    }
//                                }
//                                if(isSame){
//                                    mOnGestureLockViewListener.onSetSucceed(mResult);
//                                }else{
//                                    Toast.makeText(getContext(),"两次输入不一样",Toast.LENGTH_SHORT).show();
//                                }
//                                mResult=null;
//                            }
//                        }
//                        break;
//                    case MODE_UNLOCK:
//                        this.mTryTimes--;
//
//                        // 回调是否成功
//                        if (mOnGestureLockViewListener != null && mChoose.size() > 0)
//                        {
//                            mOnGestureLockViewListener.onGestureEvent(checkAnswer());
//                            if (this.mTryTimes == 0)
//                            {
//                                mOnGestureLockViewListener.onUnmatchedExceedBoundary();
//                            }
//                        }
//                        break;
//                }
                if(mChoose!=null&&mChoose.size()>0){
                    int[] iDs=new int[mChoose.size()];
                    for (int i = 0; i < iDs.length; i++) {
                        iDs[i]=mChoose.get(i);
                    }
                    mOnGestureLockViewListener.onBlocksSelected(iDs);
                }

//                Log.e(TAG, "mUnMatchExceedBoundary = " + mTryTimes);
                Log.e(TAG, "mChoose = " + mChoose);
                // 将终点设置位置为起点，即取消指引线
                mTmpTarget.x = mLastPathX;
                mTmpTarget.y = mLastPathY;

                // 改变子元素的状态为UP
                changeItemMode();

                // 计算每个元素中箭头需要旋转的角度
                for (int i = 0; i + 1 < mChoose.size(); i++)
                {
                    int childId = mChoose.get(i);
                    int nextChildId = mChoose.get(i + 1);

                    BmGestureLockView startChild = (BmGestureLockView) findViewById(childId);
                    BmGestureLockView nextChild = (BmGestureLockView) findViewById(nextChildId);

                    int dx = nextChild.getLeft() - startChild.getLeft();
                    int dy = nextChild.getTop() - startChild.getTop();
                    // 计算角度
                    int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
                    startChild.setArrowDegree(angle);
                }
                isAutoReset=true;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isAutoReset){
                            reset();
                            invalidate();
                        }
                    }
                },100);
                break;

        }
        invalidate();
        return true;
    }
    private boolean isAutoReset;
    private void changeItemMode()
    {
        for (BmGestureLockView bmGestureLockView : mBmGestureLockViews)
        {
            if (mChoose.contains(bmGestureLockView.getId()))
            {
                bmGestureLockView.setMode(BmGestureLockView.Mode.STATUS_FINGER_UP);
            }
        }
    }

    /**
     *
     * 做一些必要的重置
     */
    private void reset()
    {
        isAutoReset=false;
        mChoose.clear();
        mPath.reset();
        for (BmGestureLockView bmGestureLockView : mBmGestureLockViews)
        {
            bmGestureLockView.setMode(BmGestureLockView.Mode.STATUS_NO_FINGER);
            bmGestureLockView.setArrowDegree(-1);
        }
    }
    /**
     * 检查用户绘制的手势是否正确
     * @return
     */
//    private boolean checkAnswer()
//    {
//        if (mAnswer.length != mChoose.size())
//            return false;
//
//        for (int i = 0; i < mAnswer.length; i++)
//        {
//            if (mAnswer[i] != mChoose.get(i))
//                return false;
//        }
//
//        return true;
//    }

    /**
     * 检查当前左边是否在child中
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y)
    {

        //设置了内边距，即x,y必须落入下GestureLockView的内部中间的小区域中，可以通过调整padding使得x,y落入范围不变大，或者不设置padding
        int padding = (int) (mGestureLockViewWidth * 0.15);

        if (x >= child.getLeft() + padding && x <= child.getRight() - padding
                && y >= child.getTop() + padding
                && y <= child.getBottom() - padding)
        {
            return true;
        }
        return false;
    }

    /**
     * 通过x,y获得落入的GestureLockView
     * @param x
     * @param y
     * @return
     */
    private BmGestureLockView getChildIdByPos(int x, int y)
    {
        for (BmGestureLockView bmGestureLockView : mBmGestureLockViews)
        {
            if (checkPositionInChild(bmGestureLockView, x, y))
            {
                return bmGestureLockView;
            }
        }

        return null;

    }

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnGestureLockViewListener(OnGestureLockViewListener listener)
    {
        this.mOnGestureLockViewListener = listener;
    }

//    /**
//     * 对外公布设置答案的方法
//     *
//     * @param answer
//     */
//    public void setAnswer(int[] answer)
//    {
//        this.mAnswer = answer;
//    }

//    /**
//     * 设置最大实验次数
//     *
//     * @param boundary
//     */
//    public void setUnMatchExceedBoundary(int boundary)
//    {
//        this.mTryTimes = boundary;
//    }

    @Override
    public void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        //绘制GestureLockView间的连线
        if (mPath != null)
        {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoose.size() > 0)
        {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x,
                        mTmpTarget.y, mPaint);
        }

    }

    public interface OnGestureLockViewListener
    {
        /**
         * 单独选中元素的Id
         *
         * @param cId
         */
        public void onBlockSelected(int cId);
        /**
         * 选中元素的Ids
         *
         * @param iDs
         */
        public void onBlocksSelected(int[] iDs);

        /**
         * 是否匹配
         *
         * @param matched
         */
//        public void onGestureEvent(boolean matched);

        /**
         * 超过尝试次数
         */
//        public void onUnmatchedExceedBoundary();
        /**
         *设置成功返回答案
         */
//        public void onSetSucceed(int[] result);
    }
}
