package sung.dennis.clickablepieview;

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

    public void updatePieView(View view){
        clickablePieView.setStartAngle(clickablePieView.getStartAngle()>=360?
                clickablePieView.getStartAngle()%360 + 90:
                clickablePieView.getStartAngle() + 90);
    }

    @Override
    public void onSectorClicked(Object o) {
        ClickablePieView.Flavor flavor = (ClickablePieView.Flavor) o;
        if(flavor.isSelected()){
            Toast.makeText(this, "你選了：" + flavor.getText(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "你取消選了：" + flavor.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
