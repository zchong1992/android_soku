package com.example.zheng.soku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class soku extends View {
    static {
        System.loadLibrary("native-lib");
    }
    public soku(Context context) {
        super(context);

        width=getWidth();
        height=getHeight();
        mMap=new int[9][9];
        mContext=context;
        setPix(3,3,1);
        setPix(4,7,4);
        setPix(1,5,6);
        setPix(2,3,9);
        setPix(7,2,2);
        setPix(6,1,9);

    }
    private Context mContext=null;
    private void drawGrad(Canvas canvas)
    {
        Paint paint = new Paint();
        // 消除锯齿一样的边缘
        paint.setAntiAlias(true);
        // 先画一个空心的矩形 作为外背景。Paint.Style.STROKE 为设置空心
        paint.setStyle(Paint.Style.STROKE);
        // 加载颜色
        paint.setColor(ContextCompat.getColor(mContext, R.color.sokuW));
        canvas.drawRect(0, 0, width*9, height*9, paint);
        for (int i = 0; i < 9; i++) {
            if (i % 3 != 0) {
                paint.setColor(ContextCompat.getColor(mContext, R.color.sockI));
                paint.setStrokeWidth(1.0f);
                paint.setStyle(Paint.Style.STROKE);
                // 横轴
                canvas.drawLine(0, i * height, width * 9, i * height, paint);
                canvas.drawLine(0, i * height +1, width * 9, i * height + 1, paint);
                // 纵轴
                canvas.drawLine(i * width, 0, i * width, height * 9, paint);
                canvas.drawLine(i * width + 1, 0, i * width + 1, height * 9, paint);
            } else {
                paint.setColor(ContextCompat.getColor(mContext, R.color.sockI3));
                paint.setStrokeWidth(3.0f);
                paint.setStyle(Paint.Style.FILL);
                int lineWidth=10;
                canvas.drawRect(0, i * height-lineWidth/2, width*9, i * height+lineWidth/2, paint);
                canvas.drawRect(i * width-lineWidth/2, 0, i * width+lineWidth/2, height * 9, paint);
            }
        }
        if( selectX >=0 && selectX<9 && selectY>=0 && selectY<9)
        {
            int x=selectX+1;
            int y=selectY+1;
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(selectX*width, selectY*height,x*width, y*height, paint);
        }
        paint.setColor(ContextCompat.getColor(mContext, R.color.sukoToSelect));
        paint.setStyle(Paint.Style.FILL);
        for(int i=0;i<10;i++)
        {
            canvas.drawRect(i*width,10*height,(i+1)*width,11*height,paint);
        }
        canvas.drawRect(0,11*height,3*width,12*height,paint);
        canvas.drawRect(4*width,11*height,8*width,12*height,paint);
    }
    private int getPix(int x,int y)
    {
        return mMap[x-1][y-1];
    }

    private void setPix(int x,int y,int value)
    {
        mMap[x-1][y-1]=value;
    }
    private void drawNum(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setTextSize(width);
        // 消除锯齿一样的边缘
        paint.setAntiAlias(true);
        // 先画一个空心的矩形 作为外背景。Paint.Style.STROKE 为设置空心
        paint.setStyle(Paint.Style.STROKE);
        // 加载颜色
        paint.setColor(ContextCompat.getColor(mContext, R.color.sokuW));
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((float) (height * 0.75));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        canvas.drawRect(0, 0, width*9, height*9, paint);
        float x = width / 2;
        float y = height / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2;
        for (int i = 1; i <= 9; i++) {
            for (int j = 1 ;j <= 9;j++)
            {
                int value=getPix(i,j);
                if(value!=0)
                {
                    canvas.drawText(""+value,(i-1)*width+x,(j-1)*height+y,paint);
                }
            }
        }
        for (int i = 1; i <= 9; i++)
        {
            canvas.drawText(""+i,(i-1)*width+x,(10)*height+y,paint);
        }
        canvas.drawText("ClearAll",width+x,11*height+y,paint);
        canvas.drawText("Gen",5*width +x,11*height+y,paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        // 消除锯齿一样的边缘
        paint.setAntiAlias(true);
        // 先画一个空心的矩形 作为外背景。Paint.Style.STROKE 为设置空心
        paint.setStyle(Paint.Style.STROKE);
        // 加载颜色
        paint.setColor(ContextCompat.getColor(mContext, R.color.sokuW));
        // getWidth() getHeight() 得到界面的宽和高
        drawGrad(canvas);
        drawNum(canvas);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 得到 九宫格 的高度和宽度
        width = w / 9f;
        height = h / 9f;

        if(width>height)
        {
            width=height;
        }
        if(height>width)
        {
            height=width;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }
    // 重写 触摸 事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN ||event.getAction() == MotionEvent.ACTION_UP) {
            int X = (int) (event.getX() / width);
            int Y = (int) (event.getY() / height);
            if(Y<10)
            {
                selectY=Y;
                selectX=X;
            }
            else if(Y==10)
            {
                if(selectX>=0 &&selectY>=0 && selectX<9&& selectY<9 && X>=0 && X<9)
                {
                    int value=getPix(selectX+1,selectY+1);
                    if(value!=X+1)  setPix(selectX+1,selectY+1,X+1);
                    else  setPix(selectX+1,selectY+1,0);
                }
                selectX=-1;
                selectY=-1;
            }
            else if(Y==11)
            {
                if(X<3)
                {
                    clearAll();
                }
                if(X>4 && X<9)
                {
                    gen();
                }
            }
            invalidate();
        }

        return super.onTouchEvent(event);
    }

    private void gen() {
        int[] map=new int[81];
        int index=0;
        for(int i=1;i<10;i++)
        {
            for (int j = 1; j <10; j++)
            {
                map[index++]=getPix(i,j);
            }
        }

        int[] res=Gen(map);
        if(res[0]==0)
        {
            Toast.makeText(mContext, "解析失败",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(mContext, "解析成功",Toast.LENGTH_SHORT).show();
            index=1;
            for(int i=1;i<10;i++)
            {
                for (int j = 1; j <10; j++)
                {
                   setPix(i,j, res[index++]);
                }
            }
        }
        invalidate();
    }
    private void clearAll() {
        for(int i=1;i<10;i++) {
            for (int j = 1; j <10; j++)
            {
                setPix(i,j,0);
            }
        }
    }
    public native int[] Gen(int[] map);
    private int[][] mMap;
    private float width;
    private float height;
    private int selectX,selectY;
}
