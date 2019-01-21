package sung.dennis.clickablepieview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ClickablePieView extends View implements View.OnTouchListener {
    private int mTextcolor = Color.BLACK;
    private int mTextColorClicked = Color.WHITE;

    private String mDefaultColor = "#f3f3f3";
    private int[] mColors = new int[]{
            Color.RED,
            Color.BLUE,
            Color.YELLOW,
            Color.GREEN,
            Color.BLACK
    };

    private String[] mTexts = new String[]{
            "Chocolaty",
            "Caramelized",
            "Honey",
            "Sweet",
            "Floral",
            "Fruity",
            "Berry",
            "Dried Fruit",
            "Citrus Fruit",
            "Winey",
            "Fermented",
            "Herb-Like",
            "Smokey",
            "Roasted",
            "Spices",
            "Nutty"
    };

    private List<Data> datas = new ArrayList<>();
    private Paint mPiePaint, mTextPaint;
    private float mStartAngle = 0f;
    private int mWidth, mHeight;
    private float r;
    private OnSectorClickListener onSectorClickListener;

    public void setOnSectorClickListener(OnSectorClickListener onSectorClickListener) {
        this.onSectorClickListener = onSectorClickListener;
    }

    public ClickablePieView(Context context) {
        super(context);
        ini(context);
    }

    public ClickablePieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ini(context);
    }

    public ClickablePieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ini(context);
    }

    private void ini(Context context){
        mPiePaint = new Paint();
        mPiePaint.setAntiAlias(true);//抗鋸齒
        mPiePaint.setDither(true);//防抖動
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);//抗鋸齒
        mTextPaint.setDither(true);//防抖動
        mTextPaint.setColor(mTextcolor);
        setOnTouchListener(this);
        iniData();
    }

//    private float blankAngle;
//    private void iniData(){
//        if(datas.size()>0){
//            datas.clear();
//        }
//        float angle = 360 / mTexts.length;
//        blankAngle = (360 - (angle * mTexts.length)) / mTexts.length;//空隙的角度
//        if(blankAngle<=0){
//            blankAngle = 1;
//            angle-=blankAngle;
//        }
//        float currentStartAngle = mStartAngle;
//        for(int i=0;i<mTexts.length;i++){
//            datas.add(new Data(mColors[i%mColors.length], mTexts[i], currentStartAngle, angle));
//            currentStartAngle += angle + blankAngle;
//        }
//    }

    private void iniData(){
        float angle = 360 / mTexts.length;
        float blankAngle = (360 - (angle * mTexts.length)) / mTexts.length;
        angle += blankAngle;
        float currentStartAngle = mStartAngle;
        for(int i=0;i<mTexts.length;i++){
            datas.add(new Data(mColors[i%mColors.length], mTexts[i], currentStartAngle, angle));
            currentStartAngle += angle;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        r = (float) (Math.min(mWidth, mHeight) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth/2, mHeight/2);//原點移動到中心點
        float r2 = (float) (r * 0.05);
        RectF rectF = new RectF();
        RectF rectF2 = new RectF();
        rectF.set(-r, -r, r, r);
        rectF2.set(-r2, -r2, r2, r2);
        for(int i=0;i<datas.size();i++){
            if(datas.get(i).isSelected){
                mPiePaint.setColor(datas.get(i).getColor());
                mTextPaint.setColor(mTextColorClicked);
            }else {
                mPiePaint.setColor(Color.parseColor(mDefaultColor));
                mTextPaint.setColor(mTextcolor);
            }
            //繪製扇形
            canvas.drawArc(rectF, datas.get(i).getStartAngle(), datas.get(i).getAngle(), true, mPiePaint);
            //開始寫字...
        }
        //繪製空隙
        drawLines(canvas);

        mPiePaint.setColor(Color.WHITE);
        canvas.drawArc(rectF2, mStartAngle, 360f, true, mPiePaint);
        canvas.save();
    }

    private void drawLines(Canvas canvas){
        Paint mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);//抗鋸齒
        mLinePaint.setDither(true);//防抖動
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(7);
        float originX = 0;//圓心Ｘ
        float originY = 0;//圓心Ｙ
        Path path = new Path();
        for(int i=0;i<datas.size();i++){
            path.reset();
            double radian = datas.get(i).getStartAngle() * Math.PI / 180;//角度轉弧度
            float x = (float) (originX + r*Math.cos(radian));
            float y = (float) (originY + r*Math.sin(radian));
            path.lineTo(x, y);
            canvas.drawPath(path, mLinePaint);
        }
    }

    private boolean shouldCheck = true;
    private float x, y;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                float newY = event.getY();
                if(Math.abs(x-newX)>100 || Math.abs(y-newY)>100){
                    shouldCheck = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                float x, y;
                x = event.getX();
                y = event.getY();
                if(shouldCheck){
                    for(Data data : datas){
                        if(checkPointInSector(data, x, y) && !checkPointInCenter(x, y)){
                            data.setSelected(!data.isSelected());
                            invalidate();
                            onSectorClickListener.onSectorClicked(data);
                            break;
                        }
                    }
                }
                shouldCheck = true;
                break;
        }
        return true;
    }

    //是否點擊中心點附近(中間白色圓)
    private boolean checkPointInCenter(float x, float y){
        float originX = mWidth/2;//圓心Ｘ
        float originY = mHeight/2;//圓心Ｙ
        float r2 = (float) (r * 0.05);
        return checkPointInCircle(r2, originX, originY, x, y);
    }

    //是否點擊在扇形內
    private boolean checkPointInSector(Data data, float x, float y){
        boolean isInSector = false;
        float originX = mWidth/2;//圓心Ｘ
        float originY = mHeight/2;//圓心Ｙ
        float startAngle = data.getStartAngle();
        float endAngle = startAngle + data.getAngle();
        //以圓心為原點的座標
        float realX = x-originX;
        float realY = y-originY;

        if(checkPointInCircle(r, originX, originY, x, y) && checkPointAngleInSector(startAngle, endAngle, realX, realY)){
            isInSector = true;
        }

        return isInSector;
    }

    private boolean checkPointInCircle(float r, float x1, float y1, float x2, float y2){
        return ((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)) < (r*r);
    }

    private boolean checkPointAngleInSector(float startAngle, float endAngle, float x, float y){
        double pAngle = getAngle(x, y);
        return startAngle<pAngle && endAngle>pAngle;
    }

    private double getAngle(float x, float y){
        double angle = toAngle(Math.atan2(y, x));
        if(angle<0){
            angle += 360;
        }
        return angle;
    }

    private double toAngle(double x){
        return 180*x/Math.PI;
    }

    public void notifyViewUpdate(boolean clearData){
        if(clearData){
            if(datas.size()>0){
                datas.clear();
            }
            iniData();
        }
        invalidate();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle){
        this.mStartAngle = startAngle;
        float angle = 360 / datas.size();
        float blankAngle = (360 - (angle * datas.size())) / datas.size();
        angle += blankAngle;
        float currentStartAngle = mStartAngle;
        for(int i=0;i<datas.size();i++){
            datas.get(i).setStartAngle(currentStartAngle>=360?currentStartAngle-360:currentStartAngle);
            currentStartAngle += angle;
        }
        notifyViewUpdate(false);
    }

    public void setColors(int[] colors){
        this.mColors = colors;
        for(int i=0;i<datas.size();i++){
            datas.get(i).setColor(colors[i%colors.length]);
        }
        notifyViewUpdate(false);
    }

    public void setDatas(String[] texts){
        this.mTexts = texts;
        iniData();
        notifyViewUpdate(true);
    }

    public void setTextcolor(int textColor, int textColorClicked){
        this.mTextcolor = textColor;
        this.mTextColorClicked = textColorClicked;
        notifyViewUpdate(false);
    }

    public class Data {
        private int color;
        private String text;
        private boolean isSelected = false;
        private float startAngle;
        private float angle;

        Data(int color, String text, float startAngle, float angle){
            this.color = color;
            this.text = text;
            this.startAngle = startAngle;
            this.angle = angle;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public float getStartAngle() {
            return startAngle;
        }

        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
        }

        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }
    }

    public interface OnSectorClickListener{
        void onSectorClicked(Object o);
    }
}
