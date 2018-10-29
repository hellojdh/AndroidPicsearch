package hellojdh.wintercoding;

import android.os.Bundle;
import android.support.annotation.Nullable;

import hellojdh.wintercoding.tool.BaseActivity;

public class IntroActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
