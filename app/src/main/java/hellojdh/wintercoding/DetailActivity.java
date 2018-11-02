package hellojdh.wintercoding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import hellojdh.wintercoding.databinding.ActivityDetailBinding;
import hellojdh.wintercoding.net.FileDownload;
import hellojdh.wintercoding.tool.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * 상세 화면
 */
public class DetailActivity extends BaseActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            +File.separator+"winterCoding"+File.separator;
    ActivityDetailBinding binding;
    private static String title,url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);
        binding.setActivity(this);

        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        Log.d(TAG,"Title ="+title);
        Log.d(TAG,"Url ="+url);
        binding.detailTitle.setText(title);
        Glide.with(this).load(url).into(binding.detailImageView);
    }

    public void clickToDownload(View view){
        int permission = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission==PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        File dir = new File(savePath);
        Log.d(TAG,"File path ="+dir.getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();

        final File target = new File(savePath,"/"+System.currentTimeMillis()+".jpg");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileDownload.downFile(target,response.body(),DetailActivity.this);
                makeToast("갤러리에 저장되었습니다");
            }
        });
    }

    void makeToast(final String t){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailActivity.this,t,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            makeToast("권한이 승인되었습니다.");
        }else{
            makeToast("권한을 승인시 다운로드가 가능합니다.");
        }
    }
}
