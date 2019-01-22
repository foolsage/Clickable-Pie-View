package sung.dennis.clickablepieview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ClickablePieView.OnSectorClickListener {
    ClickablePieView clickablePieView;
    PieView pieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        clickablePieView = new ClickablePieView(this);
        clickablePieView = findViewById(R.id.clickablePieView);
        clickablePieView.setOnSectorClickListener(this);
        LinearLayout container = findViewById(R.id.container);
//        container.addView(clickablePieView);

//        pieView = new PieView(this);
//        container.addView(pieView);
    }

    public void setAngle(View view){
        clickablePieView.setStartAngle(clickablePieView.getStartAngle()>=360?
                clickablePieView.getStartAngle()%360 + 90:
                clickablePieView.getStartAngle() + 90);
        clickablePieView.notifyViewUpdate();
    }

    private int index = 0;
    private int[] mColors = new int[]{
            Color.RED,
            Color.BLUE,
            Color.BLACK
    };
    public void setColors(View view){
        if(index>=2){
            clickablePieView.setColors(mColors);
            index = 0;
        }else {
            clickablePieView.setColors(new int[]{mColors[index]});
            index++;
        }
        clickablePieView.notifyViewUpdate();
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
            clickablePieView.setDatas(mTexts);
            reset = false;
        }else {
            clickablePieView.setDatas(new String[]{"Hokaido", "Tokyo", "Fukuoka", "Kyoto", "Chiba", "Osaka", "Okinawa"});
            reset = true;
        }
        clickablePieView.notifyViewUpdate();
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
        clickablePieView.setDatas(mTexts2, percentages);
        clickablePieView.notifyViewUpdate();
        reset = true;
    }

    private boolean isStop = true;
    private float from = 0, end = 0;
    public void rotate(View view){
        if (!isStop)
            return;
        isStop = false;
        end += Math.random()*360;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(clickablePieView,"rotation",from,end+360*10);
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
    public void onSectorClicked(Object o) {
        ClickablePieView.Data data = (ClickablePieView.Data) o;
        if(data.isSelected()){
            Toast.makeText(this, "你選了：" + data.getText(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "你取消選了：" + data.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
