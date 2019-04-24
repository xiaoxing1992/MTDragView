package com.sonnyjack.drawview;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sonnyjack.widget.dragview.SonnyJackDragView;

public class MainActivity extends AppCompatActivity {

    private SonnyJackDragView mSonnyJackDragView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.btn_move);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        imageView.setOnClickListener(v -> Toast.makeText(MainActivity.this, "点击了...", Toast.LENGTH_SHORT).show());

        mSonnyJackDragView = new SonnyJackDragView.Builder()
                .setActivity(this)
                .setToolBarSize(dp2px(56))
                .setBottomMaxSize(30)
                .setDefaultLeft(30)
                .setDefaultButtom(30)
                .setSize(100)
                .setView(imageView)
                .build();
    }

    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
