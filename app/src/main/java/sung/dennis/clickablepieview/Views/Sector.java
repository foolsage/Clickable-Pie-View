package sung.dennis.clickablepieview.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class Sector extends View {
    private PieView.Flavor flavor;
    private Paint mPiePaint, mTextPaint;
    private float mWidth, mHeight;

    public Sector(Context context, PieView.Flavor flavor) {
        super(context);
        this.flavor = flavor;
        ini();
    }

    private void ini(){
        mPiePaint = new Paint();
        mPiePaint.setAntiAlias(true);//抗鋸齒
        mPiePaint.setDither(true);//防抖動
        mPiePaint.setColor(flavor.getDefaultColor());
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);//抗鋸齒
        mTextPaint.setDither(true);//防抖動
        mTextPaint.setColor(flavor.getDefaultTextColor());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float r = (float) (Math.min(mWidth*0.95, mHeight*0.95));
        float[] originPoint = getOriginPoint(r);
        RectF rectF = new RectF();
        int q = getQuadrant(this);
        switch (q){
            case 1:
                rectF.set(originPoint[0], originPoint[1], r+originPoint[0], r+originPoint[1]);
                break;
            case 2:
                rectF.set(-(r+originPoint[0]), originPoint[1], originPoint[0], r+originPoint[1]);
                break;
            case 3:
                rectF.set(-(r+originPoint[0]), -(r+originPoint[1]), originPoint[0], originPoint[1]);
                break;
            case 4:
                rectF.set(originPoint[0], -(r+originPoint[1]), r+originPoint[0], originPoint[1]);
                break;
        }
        float a = 1;
        //繪製扇形
        if(flavor.isSelected()){
            mPiePaint.setColor(flavor.getColor());
            mTextPaint.setColor(flavor.getTextColor());
        }else {
            mPiePaint.setColor(flavor.getDefaultColor());
            mTextPaint.setColor(flavor.getDefaultTextColor());
        }
        canvas.drawArc(rectF, flavor.getStartAngle() - a, flavor.getAngle() - a, true, mPiePaint);
        canvas.save();
    }

    private int getQuadrant(Sector sector){
        if(sector.getFlavor().getAngle()>0 && sector.getFlavor().getAngle()<=90){
            return 1;
        }else if(sector.getFlavor().getAngle()>90 && sector.getFlavor().getAngle()<=180){
            return 2;
        }else if(sector.getFlavor().getAngle()>180 && sector.getFlavor().getAngle()<=270){
            return 3;
        }else {
            return 4;
        }
    }

    private float[] getOriginPoint(float r){
        float dx = mWidth - r;
        float dy = mHeight - r;
        float[] originPoint = new float[]{dx/2, dy/2};
        return originPoint;
    }

    public PieView.Flavor getFlavor(){
        return flavor;
    }

    public void setSelected(boolean selected) {
        flavor.setSelected(selected);
    }
}
