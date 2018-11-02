package hellojdh.wintercoding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import hellojdh.wintercoding.tool.BaseActivity;

/*
 * 인트로 화면
 */
public class IntroActivity extends BaseActivity {
    private static final String TAG = IntroActivity.class.getSimpleName();
    private static int INTRO_TIME = 1300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        goMain();
    }

    void goMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },INTRO_TIME);
    }
}
