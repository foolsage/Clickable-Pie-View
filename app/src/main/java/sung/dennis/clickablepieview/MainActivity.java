package sung.dennis.clickablepieview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sung.dennis.clickablepieview.Views.CoffeeWheel;

public class MainActivity extends AppCompatActivity implements CoffeeWheel.OnElementClickListener {
    CoffeeWheel coffeeWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        clickablePieView = new ClickablePieView(this);
        coffeeWheel = findViewById(R.id.coffeeWheel);
        coffeeWheel.setOnElementClickListener(this);
        LinearLayout container = findViewById(R.id.container);
//        container.addView(coffeeWheel);
    }

    public void setAngle(View view){
        coffeeWheel.setStartAngle(coffeeWheel.getStartAngle()>=360?
                coffeeWheel.getStartAngle()%360 + 90:
                coffeeWheel.getStartAngle() + 90);
        coffeeWheel.notifyViewUpdate();
    }

    private int index = 0;
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
    public void setColors(View view){
        if(index>=2){
            coffeeWheel.setColors(mColors);
            index = 0;
        }else {
            coffeeWheel.setColors(new String[]{mColors[index]});
            index++;
        }
        coffeeWheel.notifyViewUpdate();
    }

    private boolean reset = false;
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
    public void setDatas(View view){
        if (reset){
            coffeeWheel.setElements(mTexts);
            reset = false;
        }else {
            coffeeWheel.setElements(new String[]{"Hokaido", "Tokyo", "Fukuoka", "Kyoto", "Chiba", "Osaka", "Okinawa"});
            reset = true;
        }
        coffeeWheel.notifyViewUpdate();
    }

    private String[] mTexts2 = new String[]{
            "10%",
            "22.3%",
            "3%",
            "7.7%",
            "57%"
    };
    private List<Float> percentages;
    private void iniPercentages(){
        if(percentages==null){
            percentages = new ArrayList<>();
        }
        for(int i=0;i<mTexts2.length;i++){
            percentages.add(Float.parseFloat(mTexts2[i].replace("%", "")));
        }
    }
    public void setPercent(View view){
        if(percentages==null || percentages.size()<0){
            iniPercentages();
        }
        coffeeWheel.setElements(mTexts2, percentages);
        coffeeWheel.notifyViewUpdate();
        reset = true;
    }

    private boolean isStop = true;
    private float from = 0, end = 0;
    public void rotate(View view){
        if (!isStop)
            return;
        isStop = false;
        end += Math.random()*360;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(coffeeWheel,"rotation",from,end+360*10);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isStop = true;
                from = end;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(5000);
        objectAnimator.start();
    }

    @Override
    public void onElementClicked(Object o) {
        CoffeeWheel.WheelItem item = (CoffeeWheel.WheelItem) o;
        if(item.isSelected()){
            Toast.makeText(this, "你選了：" + item.getText(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "你取消選了：" + item.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    public void goV2(View view){
        startActivity(new Intent(this, Main2Activity.class));
    }
}
