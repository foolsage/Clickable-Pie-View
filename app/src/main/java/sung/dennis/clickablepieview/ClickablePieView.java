package sung.dennis.clickablepieview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private List<Flavor> datas = new ArrayList<>();
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

    private void iniData(){
        if(datas.size()>0){
            datas.clear();
        }
        float angle = 360 / mTexts.length;
        float blankAngle = (360 - (angle * mTexts.length)) / mTexts.length;//空隙的角度
        if(blankAngle<=0){
            blankAngle = 1;
            angle-=blankAngle;
        }
        float currentStartAngle = mStartAngle;
        for(int i=0;i<mTexts.length;i++){
            datas.add(new Flavor(mColors[i%mColors.length], mTexts[i], currentStartAngle, angle));
            currentStartAngle += angle + blankAngle;
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

        canvas.translate(mWidth / 2, mHeight / 2);//移動到中心點
        float r2 = (float) (r * 0.05);
        RectF rectF = new RectF();
        RectF rectF2 = new RectF();
        rectF.set(-r, -r, r, r);
        rectF2.set(-r2, -r2, r2, r2);
        for(int i=0;i<mTexts.length;i++){
            //繪製扇形
            if(datas.get(i).isSelected){
                mPiePaint.setColor(datas.get(i).getColor());
                mTextPaint.setColor(mTextColorClicked);
            }else {
                mPiePaint.setColor(Color.parseColor(mDefaultColor));
                mTextPaint.setColor(mTextcolor);
            }
            canvas.drawArc(rectF, datas.get(i).getStartAngle(), datas.get(i).getAngle(), true, mPiePaint);
        }
        mPiePaint.setColor(Color.WHITE);
        canvas.drawArc(rectF2, mStartAngle, 360f, true, mPiePaint);
        canvas.save();
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
                    for(Flavor flavor : datas){
                        if(checkPointInSector(flavor, x, y)){
                            flavor.setSelected(!flavor.isSelected());
                            invalidate();
                            onSectorClickListener.onSectorClicked(flavor);
                            break;
                        }
                    }
                }
                shouldCheck = true;
                break;
        }
        return true;
    }

    private boolean checkPointInSector(Flavor flavor, float x, float y){
        boolean isInSector = false;
        float originX = mWidth/2;//圓心Ｘ
        float originY = mHeight/2;//圓心Ｙ
        float startAngle = flavor.getStartAngle();
        float endAngle = startAngle + flavor.getAngle();
        //以圓心為原點的座標
        float realX = x-originX;
        float realY = y-originY;

        if(checkPointInCircle(originX, originY, x, y) && checkPointAngleInSector(startAngle, endAngle, realX, realY)){
            isInSector = true;
        }

        return isInSector;
    }

    private boolean checkPointInCircle(float x1, float y1, float x2, float y2){
        return ((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)) < (r*r);
    }

    private boolean checkPointAngleInSector(float startAngle, float endAngle, float x, float y){
        double anglePoint = getPointAngle(x, y);
        return startAngle<anglePoint && endAngle>anglePoint;
    }

    private double getPointAngle(float x, float y){
        double angle = toAngle(Math.atan2(y, x));
        if(angle<0){
            angle += 360;
        }
        return angle;
    }

    private double toAngle(double x){
        return 180*x/Math.PI;
    }

    private void updateView(){
        invalidate();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle){
        this.mStartAngle = startAngle;
        iniData();
        updateView();
    }

    public void setColors(int[] colors){
        this.mColors = colors;
        iniData();
        updateView();
    }

    public void setTexts(String[] texts){
        this.mTexts = texts;
        iniData();
        updateView();
    }

    public void setTextcolor(int textColor, int textColorClicked){
        this.mTextcolor = textColor;
        this.mTextColorClicked = textColorClicked;
        updateView();
    }

    public class Flavor{
        private int color;
        private String text;
        private boolean isSelected = false;
        private float startAngle;
        private float angle;

        Flavor(int color, String text, float startAngle, float angle){
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

        public float getAngle() {
            return angle;
        }
    }

    public interface OnSectorClickListener{
        void onSectorClicked(Object o);
    }
}
