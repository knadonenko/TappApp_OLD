package com.taptester.animation;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.taptester.tappapp.R;

/**
 * Created by Const on 11.08.2014.
 */
public class Animate extends Activity {

    private Handler sizeHandler = new Handler();

    ImageView imageView;

    public void ChangeSize(final ImageView imageView) {

        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();


        new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long l) {
                int width;
                width = imageView.getMaxWidth();
                width++;
                imageView.setMaxWidth(width);
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

}
