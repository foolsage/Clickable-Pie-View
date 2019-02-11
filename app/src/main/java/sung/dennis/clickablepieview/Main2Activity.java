package sung.dennis.clickablepieview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sung.dennis.clickablepieview.Views.CoffeeWheelV2;

public class Main2Activity extends AppCompatActivity implements CoffeeWheelV2.OnElementClickListener {
    private CoffeeWheelV2 coffeeWheel;
    private TextView textView_flavors;
    private final String DEFAULT = "choose flavors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        clickablePieView = new ClickablePieView(this);
        textView_flavors = findViewById(R.id.textView_flavors);
        textView_flavors.setText(DEFAULT);
        coffeeWheel = findViewById(R.id.coffeeWheelV2);
        coffeeWheel.setOnElementClickListener(this);
        LinearLayout container = findViewById(R.id.container);
//        container.addView(coffeeWheel);
    }

    private int index = 0;
    private String[] mColors = new String[]{
            "#ff0019",
            "#000dff",
            "#000000"
    };
    public void setColors(View view){
        if(index>2){
            coffeeWheel.setColors(defaultColors);
            index = 0;
        }else {
            coffeeWheel.setColors(new String[]{mColors[index]});
            index++;
        }
        coffeeWheel.notifyViewUpdate();
    }
    private String[] defaultColors = new String[]{
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
        CoffeeWheelV2.WheelItem item = (CoffeeWheelV2.WheelItem) o;
        Toast.makeText(this, "你點擊了：" + item.getText(), Toast.LENGTH_SHORT).show();

        if(item.getChildItems()==null || item.getChildItems().size()<=0){
            if(item.isSelected()){
                Toast.makeText(this, "你選了：" + item.getText(), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "你取消選了：" + item.getText(), Toast.LENGTH_SHORT).show();
            }
            showSelectedFlavors();
        }
    }

    private void showSelectedFlavors(){
        List<CoffeeWheelV2.WheelItem> selectedFlavors = coffeeWheel.getSelectedFlavors();
        if(selectedFlavors.size()>0){
            textView_flavors.setText("");
            for (int i=0;i<selectedFlavors.size();i++){
                CoffeeWheelV2.WheelItem flavor = selectedFlavors.get(i);
                if(i == selectedFlavors.size()-1){
                    textView_flavors.setText(textView_flavors.getText() + flavor.getText());
                }else {
                    textView_flavors.setText(textView_flavors.getText() + flavor.getText() + " , ");
                }
            }
        }else {
            textView_flavors.setText(DEFAULT);
        }
    }
}
