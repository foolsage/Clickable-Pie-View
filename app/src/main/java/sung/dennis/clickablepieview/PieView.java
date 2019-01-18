package sung.dennis.clickablepieview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PieView extends ViewGroup {
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

    private List<Sector> datas = new ArrayList<>();

    public PieView(Context context) {
        super(context);
        iniData(context);
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniData(context);
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniData(context);
    }

    private void iniData(Context context){
        if(datas.size()>0){
            datas.clear();
        }
        float angle = 360 / mTexts.length;
        float a = (360 - (angle * mTexts.length)) / mTexts.length;
        float currentAngle = 0;
        for(int i=0;i<mTexts.length;i++){
            Flavor flavor = new Flavor(
                    mColors[i%mColors.length],
                    mTexts[i],
                    currentAngle,
                    angle,
                    i);
            final Sector sector = new Sector(context, flavor);
            datas.add(sector);
            addView(datas.get(datas.size()-1));
            currentAngle = currentAngle + angle + a;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int mWidth, mHeight;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i=0;i<getChildCount();i++){
            datas.get(i).layout(l, t, r, b);
        }
    }

    class Flavor{
        private String defaultColor = "#FFF3F3F3";
        private int color;
        private String text;
        private int defaultTextColor = Color.BLACK;
        private int textColor = Color.WHITE;
        private boolean isSelected = false;
        private float startAngle;
        private float eangle;
        private int index;

        Flavor(int color, String text, float startAngle, float eangle, int index){
            this.color = color;
            this.text = text;
            this.startAngle = startAngle;
            this.eangle = eangle;
            this.index = index;
        }

        public int getDefaultColor() {
            return Color.parseColor(defaultColor);
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

        public int getDefaultTextColor() {
            return defaultTextColor;
        }

        public int getTextColor() {
            return textColor;
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
            return eangle;
        }

        public void setAngle(float eangle) {
            this.eangle = eangle;
        }

        public int getIndex() {
            return index;
        }
    }
}
