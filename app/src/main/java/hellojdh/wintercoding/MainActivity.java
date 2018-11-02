package hellojdh.wintercoding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hellojdh.wintercoding.adapter.RecyclerViewAdapter;
import hellojdh.wintercoding.databinding.ActivityMainBinding;
import hellojdh.wintercoding.model.ListItem;
import hellojdh.wintercoding.tool.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * 메인 화면
 */
public class MainActivity extends BaseActivity{
    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    private InputMethodManager imm;
    private boolean searchFlag = true;
    static RecyclerViewAdapter adapter = new RecyclerViewAdapter();

    private static int page=1;
    private String URL = "https://secure.flickr.com/services/rest/?method=flickr.photos.search" +
            "&nojsoncallback=1"+"&content_type=1"+"&safe_search=1" +
            "&per_page=21"+"&format=json"+"&sort=interestingness-desc";
    private String API_KEY = "&api_key=faf87d4e3d76896a3dba4ad4b79dae1c";
    private String TEXT = "&text=";
    private String REQUEST = URL+API_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setActivity(this);

        binding.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                clickFlag = false;
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    onButtonClick(binding.button);
                    return true;
                }
                return false;
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0) return;
                // 맨 아래라면 갱신
                if(!recyclerView.canScrollVertically(1)){
                    if(binding.editText.getText().toString().equals("")) return;
                    else if(!clickFlag) return;
                    if(searchFlag){
                        searchFlag = false;
                        Log.d(TAG,"라스트 포지션");
                        getData(true);
                    }
                }
            }
        });

        // for Keyboard
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        changeText(false);
        changeProgress(false);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }

    private JSONArray photo;
    private JSONObject totalObject,photos,object;
    void getJson(String response,boolean flag) throws JSONException{
        totalObject = new JSONObject(response);
        String stat = totalObject.getString("stat");
        // 검색 실패한 경우
        if(stat.equals("fail")){
            makeToast("다시 검색해 주세요");
            changeProgress(flag);
            return;
        }
        photos = totalObject.getJSONObject("photos");
        photo = photos.getJSONArray("photo");

        // 중간에 검색 결과가 없을 경우
        if(photo.length()==0&&page!=1){
            makeToast("마지막 페이지 입니다");
            searchFlag = true; // 연속 검색 허용
            changeProgress(flag);
            return;
        }
        // 처음부터 검색 결과가 없을 경우
        if(photo.length()==0){
            searchFlag = true; // 연속 검색 허용
            changeText(true);
            dataChange(0,0,false);
            changeProgress(flag);
            return;
        }
        List<ListItem> list = new ArrayList<>();
        for(int i=0;i<photo.length();i++){
            object = photo.getJSONObject(i);
            // https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
            int farm = object.getInt("farm");
            String id = object.getString("id");
            String secret = object.getString("secret");
            String server = object.getString("server");
            String title = object.getString("title");
            String tUrl = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
            list.add(new ListItem(title,tUrl));
        }
        int t1 = adapter.getItemCount();
        int t2 = list.size();
        RecyclerViewAdapter.add(list);
        dataChange(t1,t1+t2-1,flag);
        changeProgress(flag);
        searchFlag = true; // 연속 검색 허용
        page++; // page 올리기
    }

    // progressBar 상태 바꾸기
    void changeProgress(final boolean flag){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // true -> 다음 페이지 로드
                // false -> 첫 검색
                if(flag){
                    Log.d(TAG,binding.loadLayout.getVisibility()+"");
                    if(binding.loadLayout.getVisibility() == View.VISIBLE) binding.loadLayout.setVisibility(View.GONE);
                    else binding.loadLayout.setVisibility(View.VISIBLE);
                }else{
                    if(binding.progressBar.getVisibility()==View.VISIBLE) binding.progressBar.setVisibility(View.INVISIBLE);
                    else binding.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void changeText(final boolean flag){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    if(!flag) binding.nosearchText.setVisibility(View.INVISIBLE);
                    else binding.nosearchText.setVisibility(View.VISIBLE);
            }
        });
    }
    void dataChange(final int t1, final int t2,final boolean flag){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(flag) adapter.notifyItemRangeChanged(t1,t2);
                else adapter.notifyDataSetChanged();
            }
        });
    }

    void makeToast(final String t){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,t,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onButtonClick(View v){
        // 키보드 내리기
        imm.hideSoftInputFromWindow(binding.editText.getWindowToken(),0);
        if(binding.editText.getText().toString().equals("")){
            makeToast("검색어를 입력해 주세요");
        }else{
            changeText(false);
            // 새로운 검색시 1 page와 연속 검색 초기화 & 기존 검색 삭제
            clickFlag = true;
            page = 1;
            RecyclerViewAdapter.clear();
            getData(false);
        }
    }

    static boolean clickFlag = false;
    void getData(final boolean flag){
        changeProgress(flag);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(REQUEST+"&page="+page+TEXT+binding.editText.getText().toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {getJson(response.body().string(),flag);}
                catch (JSONException e) {e.printStackTrace();}
            }
        });
    }
}
