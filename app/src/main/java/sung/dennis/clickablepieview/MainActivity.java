package sung.dennis.clickablepieview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ClickablePieView.OnSectorClickListener {
    ClickablePieView clickablePieView;
    PieView pieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickablePieView = new ClickablePieView(this);
        clickablePieView.setOnSectorClickListener(this);
        pieView = new PieView(this);
        LinearLayout container = findViewById(R.id.container);
        container.addView(clickablePieView);
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
            Color.YELLOW,
            Color.GREEN,
            Color.BLACK
    };
    public void setColors(View view){
        if(index>=4){
            clickablePieView.setColors(mColors);
            index = 0;
        }else {
            clickablePieView.setColors(new int[]{mColors[index]});
            index++;
        }
        clickablePieView.notifyViewUpdate();
    }

    boolean reset = false;
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
