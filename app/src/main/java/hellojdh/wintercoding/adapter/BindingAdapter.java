package hellojdh.wintercoding.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

/*
 * Binding 어뎁터
 */
public class BindingAdapter {
    @android.databinding.BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView,String url){
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }
}
