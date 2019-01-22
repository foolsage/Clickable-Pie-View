package sung.dennis.clickablepieview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClickablePieView extends View implements View.OnTouchListener {
    private final String TAG = "ClickablePieView";
    private Context context;
    private int blankColor = Color.WHITE;
    private int mTextcolor = Color.BLACK;
    private int mTextColorClicked = Color.WHITE;
    private int defaultTextSize = 40;
    private String mDefaultColor = "#FFE2E2E2";

    private int[] mColors = new int[]{
            Color.RED,
            Color.BLUE,
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
    private List<Float> percentages;
    private List<Data> datas;
    private Paint mPiePaint, mTextPaint;
    private float mStartAngle = 0f;
    private int mWidth, mHeight;
    private float r, r2;
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
        this.context = context;
        mPiePaint = new Paint();
        mPiePaint.setAntiAlias(true);//抗鋸齒
        mPiePaint.setDither(true);//防抖動
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);//抗鋸齒
        mTextPaint.setDither(true);//防抖動
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(defaultTextSize);
        mTextPaint.setStyle(Paint.Style.FILL);
        setOnTouchListener(this);
        datas = new ArrayList<>();
//        iniData();
    }

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

    private void iniDataWithPercentage(){
        float sumPercentage = 0;
        for(int k=0;k<percentages.size();k++){
            sumPercentage += percentages.get(k);
        }
        if(sumPercentage>100){
            for(int k=0;k<percentages.size();k++){
                percentages.add(k, percentages.get(k)-(sumPercentage-100)/percentages.size());
            }
        }

        float blankAngle = 0;
        if(sumPercentage<100){
            blankAngle = (360*(100-sumPercentage)/100) / percentages.size();
        }
        float angle;
        float currentStartAngle = mStartAngle;
        for(int i=0;i<percentages.size();i++){
            angle = (360 * percentages.get(i)/100) + blankAngle;
            datas.add(new Data(mColors[i%mColors.length], mTexts[i], currentStartAngle, angle));
            currentStartAngle += angle;
        }
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
        r = (float) (Math.min(mWidth, mHeight) / 2);
        r2 = (float) (r * 0.05);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(datas!=null && datas.size()>0){
            canvas.translate(mWidth/2, mHeight/2);//原點移動到canvas的中心點
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
                //開始寫字
                drawText(canvas, datas.get(i));
            }
            //繪製空隙
            drawLines(canvas);

            mPiePaint.setColor(blankColor);
            canvas.drawArc(rectF2, mStartAngle, 360f, true, mPiePaint);
            canvas.save();
        }
    }

    private void drawText(Canvas canvas, Data data){
        float originX = 0;//圓心Ｘ(以canvas坐標系為基準)
        float originY = 0;//圓心Ｙ(以canvas坐標系為基準)
        Path path = new Path();
        float angle = data.getStartAngle()+data.getAngle()/2;
        float[] point = getPoint(angle, originX, originY, r);
        if(angle>90 && angle<270){
            //如果是左半圓,讓字上下顛倒
            path.moveTo(point[0], point[1]);
            path.lineTo(originX, originY);
        }else {
            path.lineTo(point[0], point[1]);
        }
        autoFitTextSize(data.getText());
        canvas.drawTextOnPath(data.getText(), path, 0, getTextDy(), mTextPaint);
    }

    private void autoFitTextSize(String text){
        int currentTextSize = defaultTextSize;
        float textWidth = mTextPaint.measureText(text);//取得粗略的文字寬度
        while (textWidth > r*0.4){
            currentTextSize--;
            mTextPaint.setTextSize(currentTextSize);
            textWidth = mTextPaint.measureText(text);
        }
    }

    //讓字根據baseLine偏移到垂直置中
    private float getTextDy(){
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHalfH = (metrics.descent-metrics.ascent)/2;
        float centerY = metrics.descent - textHalfH;
        return centerY<0?-centerY:centerY;
    }

    private void drawLines(Canvas canvas){
        Paint mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);//抗鋸齒
        mLinePaint.setDither(true);//防抖動
        mLinePaint.setColor(blankColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(r2/7);
        float originX = 0;//圓心Ｘ(以canvas坐標系為基準)
        float originY = 0;//圓心Ｙ(以canvas坐標系為基準)
        Path path = new Path();
        for(int i=0;i<datas.size();i++){
            path.reset();
            float[] point = getPoint(datas.get(i).getStartAngle(), originX, originY, r);
            path.lineTo(point[0], point[1]);
            canvas.drawPath(path, mLinePaint);
        }
    }

    //已知角度 Ａ點 距離 ,求Ｂ點
    private float[] getPoint(float angle, float originX, float originY, float bevel){
        double radian = angle * Math.PI / 180;//角度轉弧度
        float[] point = new float[2];
        point[0] = (float) (originX + bevel*Math.cos(radian));
        point[1] = (float) (originY + bevel*Math.sin(radian));
        return point;
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
                        if(isClickInSector(data, x, y) && !isClickInCenter(x, y)){
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

    //是否點擊中心點附近(中間小圓)
    private boolean isClickInCenter(float x, float y){
        float originX = mWidth/2;//圓心Ｘ
        float originY = mHeight/2;//圓心Ｙ
        return checkPointInCircle(r2, originX, originY, x, y);
    }

    //是否點擊在扇形內
    private boolean isClickInSector(Data data, float x, float y){
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
        return ((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)) < (r*r);//求兩點直線距離的公式
    }

    private boolean checkPointAngleInSector(float startAngle, float endAngle, float x, float y){
        double pAngle = getAngle(x, y);
        if(endAngle>=360){
            return (startAngle<pAngle && 360>pAngle) || (0<pAngle && endAngle-360>pAngle);
        }else {
            return startAngle<pAngle && endAngle>pAngle;
        }
    }

    private double getAngle(float x, float y){
        double angle = toAngle(Math.atan2(y, x));
        if(angle<0){
            angle += 360;
        }
        return angle;
    }

    private double toAngle(double x){
        return 180 * x / Math.PI;
    }

    public void notifyViewUpdate(){
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
    }

    public void setBlankColor(int blankColor) {
        this.blankColor = blankColor;
    }

    public void setColors(int[] colors){
        this.mColors = colors;
        for(int i=0;i<datas.size();i++){
            datas.get(i).setColor(colors[i%colors.length]);
        }
    }

    public void setDatas(String[] texts){
        this.mTexts = texts;
        datas.clear();
        iniData();
    }

    public void setDatas(List<Data> datas){
        this.datas = datas;
    }

    public void setDatas(String[] texts, List<Float> percentages){
        if(texts.length != percentages.size()){
            Log.e(TAG, "texts.length and percentages.size are different");
            return;
        }
        this.mTexts = texts;
        this.percentages = percentages;
        datas.clear();
        iniDataWithPercentage();
    }

    public void setTextcolor(int textColor, int textColorClicked){
        this.mTextcolor = textColor;
        this.mTextColorClicked = textColorClicked;
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
