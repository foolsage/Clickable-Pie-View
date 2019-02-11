package sung.dennis.clickablepieview.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CoffeeWheelV2 extends View implements View.OnTouchListener{
    private final String TAG = "CoffeeWheelV2 Error";
    private Context context;
    private int blankColor = Color.WHITE;
    private int mTextcolor = Color.BLACK;
    private int mTextColorClicked = Color.WHITE;
    private int defaultTextSize = 40;
    private String mElementDefaultColor = "#FFE2E2E2";

    private String[] mColors = new String[]{
            "#623E1A",
            "#BFA14C",
            "#AE5C70",
            "#661D46",
            "#EEB13D",
            "#E69635",
            "#BBBF7B",
            "#487BA1",
            "#F7CD46",
            "#6E7657",
            "#DFBD71",
            "#AD9C3B",
            "#5A6366",
            "#9F4949",
            "#526C88",
            "#AA7A3B"
    };

    private String currentItemName = "";
    private List<String> preLevelItemNames = new ArrayList<>();
    private List<WheelItem> items, currentShowingItems;
    private List<List<WheelItem>> preLevelItemsLists = new ArrayList<>();
    private Paint mPiePaint, mTextPaint, mCenterTextPaint;
    private float mStartAngle = 0f;
    private int mWidth, mHeight;
    private float r, r2;
    private OnElementClickListener onElementClickListener;

    private String json_items = "[" +
            "{\"text\":\"Bitter\",\"children\":[{\"text\":\"Pungent\",\"children\":[{\"text\":\"Creosol\",\"children\":[]},{\"text\":\"Phenolic\",\"children\":[]}]},{\"text\":\"Harsh\",\"children\":[{\"text\":\"Caustic\",\"children\":[]},{\"text\":\"Alkaline\",\"children\":[]}]}]}" +
            ",{\"text\":\"Salt\",\"children\":[{\"text\":\"Sharp\",\"children\":[{\"text\":\"Astringent\",\"children\":[]},{\"text\":\"Rough\",\"children\":[]}]},{\"text\":\"Bland\",\"children\":[{\"text\":\"Neutral\",\"children\":[]},{\"text\":\"Soft\",\"children\":[]}]}]}" +
            ",{\"text\":\"Sweet\",\"children\":[{\"text\":\"Mellow\",\"children\":[{\"text\":\"Delicate\",\"children\":[]},{\"text\":\"Mild\",\"children\":[]}]},{\"text\":\"Adicic\",\"children\":[{\"text\":\"Nippy\",\"children\":[]},{\"text\":\"Piquant\",\"children\":[]}]}]}" +
            ",{\"text\":\"Sour\",\"children\":[{\"text\":\"Winey\",\"children\":[{\"text\":\"Tangy\",\"children\":[]},{\"text\":\"Tart\",\"children\":[]}]},{\"text\":\"Sour\",\"children\":[{\"text\":\"Hard\",\"children\":[]},{\"text\":\"Acrid\",\"children\":[]}]}]}" +
            "]";

    public void setOnElementClickListener(OnElementClickListener onElementClickListener) {
        this.onElementClickListener = onElementClickListener;
    }

    public CoffeeWheelV2(Context context) {
        super(context);
        ini(context);
    }

    public CoffeeWheelV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        ini(context);
    }

    public CoffeeWheelV2(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mCenterTextPaint = new Paint();
        mCenterTextPaint.setAntiAlias(true);//抗鋸齒
        mCenterTextPaint.setDither(true);//防抖動
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterTextPaint.setTextSize(defaultTextSize);
        mCenterTextPaint.setStyle(Paint.Style.FILL);
        mCenterTextPaint.setColor(Color.BLACK);
        setOnTouchListener(this);
        items = new ArrayList<>();
        iniData();
        Log.e(TAG, "");
    }

    private void iniData(){
        if(items.size()<=0){
            try {
                JSONArray jsonArray = new JSONArray(json_items);
                iniDataFromJson(items, jsonArray);
                currentShowingItems = items;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void iniDataFromJson(List<WheelItem> wheelItems, JSONArray jsonArray){
        if(jsonArray.length()>0){
            try {
                float angle = 360 / jsonArray.length();
                float blankAngle = (360 - (angle * jsonArray.length())) / jsonArray.length();
                angle += blankAngle;
                float currentStartAngle = mStartAngle;

                for(int i=0;i<jsonArray.length();i++){
                    List<WheelItem> wheelChildItems = new ArrayList<>();
                    String text = jsonArray.getJSONObject(i).getString("text");
                    JSONArray jsonArrayChildren = jsonArray.getJSONObject(i).getJSONArray("children");

                    WheelItem item = new WheelItem(Color.parseColor(mColors[i%mColors.length]), text, currentStartAngle, angle);

                    if(jsonArrayChildren.length()>0){
                        iniDataFromJson(wheelChildItems, jsonArrayChildren);
                    }

                    item.setChildItems(wheelChildItems);

                    wheelItems.add(item);
                    currentStartAngle += angle;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void resetDatas(){
        currentItemName = "";
        preLevelItemNames.clear();
        items.clear();
        currentShowingItems.clear();
        preLevelItemsLists.clear();
        iniData();
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
        r2 = (float) (r * 0.3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(currentShowingItems !=null && currentShowingItems.size()>0){
            canvas.translate(mWidth/2, mHeight/2);//原點移動到canvas的中心點
            RectF rectF = new RectF();
            RectF rectF2 = new RectF();
            rectF.set(-r, -r, r, r);
            rectF2.set(-r2, -r2, r2, r2);
            for(int i = 0; i< currentShowingItems.size(); i++){
                if(currentShowingItems.get(i).isSelected){
                    mPiePaint.setColor(currentShowingItems.get(i).getColor());
                    mTextPaint.setColor(mTextColorClicked);
                }else {
                    mPiePaint.setColor(Color.parseColor(mElementDefaultColor));
                    mTextPaint.setColor(mTextcolor);
                }
//                mPiePaint.setColor(currentShowingItems.get(i).getColor());
//                mTextPaint.setColor(mTextColorClicked);
                //繪製扇形
                canvas.drawArc(rectF, currentShowingItems.get(i).getStartAngle(), currentShowingItems.get(i).getAngle(), true, mPiePaint);
                //開始寫字
                drawText(canvas, currentShowingItems.get(i));
            }
            //繪製空隙
            drawLines(canvas);

            //繪製中心圓
            mPiePaint.setColor(blankColor);
            canvas.drawArc(rectF2, mStartAngle, 360f, true, mPiePaint);
            //繪製中心圓的字
            if(!TextUtils.isEmpty(currentItemName)){
                drawCenterText(canvas);
            }
            canvas.save();
        }
    }

    private void drawCenterText(Canvas canvas){
        Path path = new Path();
        float pointStart[] = new float[]{-r2, 0};
        float pointEnd[] = new float[]{r2, 0};
        path.moveTo(pointStart[0], pointStart[1]);
        path.lineTo(pointEnd[0], pointEnd[1]);
        autoFitTextSize(currentItemName, mCenterTextPaint);
        canvas.drawTextOnPath(currentItemName, path, 0, getTextDy(mCenterTextPaint), mCenterTextPaint);
    }

    private void drawText(Canvas canvas, WheelItem item){
        Path path = new Path();
        float r3 = r2 + (r-r2)/2;
        RectF rectF = new RectF();
        rectF.set(-r3, -r3, r3, r3);
        if(item.getStartAngle()>=0 && item.getStartAngle()<180){
            //如果是下半圓,讓字上下顛倒
            path.addArc(rectF, item.getStartAngle()+item.getAngle(), -item.getAngle());
        }else {
            path.addArc(rectF, item.getStartAngle(), item.getAngle());
        }
        autoFitTextSize(item.getText(), mTextPaint);
        canvas.drawTextOnPath(item.getText(), path, 0, getTextDy(mTextPaint), mTextPaint);
    }

    private void autoFitTextSize(String text, Paint paint){
        int currentTextSize = defaultTextSize;
        float textWidth = paint.measureText(text);//取得粗略的文字寬度
        while (textWidth > r*0.4){
            currentTextSize--;
            paint.setTextSize(currentTextSize);
            textWidth = paint.measureText(text);
        }
    }

    //讓字根據baseLine偏移到垂直置中
    private float getTextDy(Paint paint){
        Paint.FontMetrics metrics = paint.getFontMetrics();
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
        mLinePaint.setStrokeWidth(r2/30);
        float originX = 0;//圓心Ｘ(以canvas坐標系為基準)
        float originY = 0;//圓心Ｙ(以canvas坐標系為基準)
        Path path = new Path();
        for(int i = 0; i< currentShowingItems.size(); i++){
            path.reset();
            float[] point = getPoint(currentShowingItems.get(i).getStartAngle(), originX, originY, r);
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
                    for(WheelItem item : currentShowingItems){
                        if(isClickInSector(item, x, y) && !isClickInCenter(x, y)){
                            if(item.getChildItems() !=null && item.getChildItems().size()>0){
                                preLevelItemsLists.add(currentShowingItems);
                                preLevelItemNames.add(currentItemName);
                                currentShowingItems = item.getChildItems();
                                currentItemName = item.getText();
                                invalidate();
                            }else {
                                //最內層的item, 可以被選擇了
                                item.setSelected(!item.isSelected());
                                invalidate();
                            }
                            onElementClickListener.onElementClicked(item);
                            break;
                        }
                    }
                    if(isClickInCenter(x, y) && !TextUtils.isEmpty(currentItemName)){
                        //點擊中心圓回到上一層
                        currentShowingItems = preLevelItemsLists.get(preLevelItemsLists.size()-1);
                        currentItemName = preLevelItemNames.get(preLevelItemNames.size()-1);
                        preLevelItemsLists.remove(preLevelItemsLists.size()-1);
                        preLevelItemNames.remove(preLevelItemNames.size()-1);
                        invalidate();
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
    private boolean isClickInSector(WheelItem item, float x, float y){
        boolean isInSector = false;
        float originX = mWidth/2;//圓心Ｘ
        float originY = mHeight/2;//圓心Ｙ
        float startAngle = item.getStartAngle();
        float endAngle = startAngle + item.getAngle();
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

    //-----------------------------------------------------------------------------------------

    public void notifyViewUpdate(){
        resetDatas();
        invalidate();
    }

    public void setElementDefaultColor(String mElementDefaultColor) {
        this.mElementDefaultColor = mElementDefaultColor;
    }

    public void setBlankColor(int blankColor) {
        this.blankColor = blankColor;
    }

    public void setColors(String[] colors){
        this.mColors = colors;
    }

    public void setTextcolor(int textColor){
        this.mTextcolor = textColor;
    }

    public void setClickedTextColor(int textColorClicked){
        this.mTextColorClicked = textColorClicked;
    }

    public List<WheelItem> getSelectedFlavors(){
        List<WheelItem> selectedItems = new ArrayList<>();
        checkAllItems(items, selectedItems);
        return selectedItems;
    }

    private void checkAllItems(List<WheelItem> items, List<WheelItem> selectedItems){
        for(WheelItem item:items){
            if (item.getChildItems()!=null && item.getChildItems().size()>0){
                checkAllItems(item.getChildItems(), selectedItems);
            }else {
                if (item.isSelected())
                    selectedItems.add(item);
            }
        }
    }

    public static class WheelItem {
        private int color;
        private String text;
        private boolean isSelected = false;
        private float startAngle;
        private float angle;
        private String parent;
        private List<WheelItem> childItems = new ArrayList<>();

        public WheelItem(int color, String text, float startAngle, float angle){
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

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public void setChildItems(List<WheelItem> childItems) {
            this.childItems = childItems;
        }

        public List<WheelItem> getChildItems() {
            return childItems;
        }
    }

    public interface OnElementClickListener {
        void onElementClicked(Object o);
    }
}
