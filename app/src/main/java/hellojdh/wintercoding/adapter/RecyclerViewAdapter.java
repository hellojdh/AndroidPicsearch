package hellojdh.wintercoding.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hellojdh.wintercoding.DetailActivity;
import hellojdh.wintercoding.R;
import hellojdh.wintercoding.databinding.ItemLayoutBinding;
import hellojdh.wintercoding.model.ListItem;

/*
 * RecyclerView
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private static List<ListItem> list = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout,viewGroup,false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = viewGroup.getMeasuredWidth()/3;
        params.width =  viewGroup.getMeasuredWidth()/3;
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        ListItem item = list.get(i);
        viewHolder.binding.setItem(item);
        viewHolder.binding.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),DetailActivity.class);
                ListItem item = list.get(i);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("url",item.getUrl());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static void add(List<ListItem> tList){
        list.addAll(tList);
    }

    public static void clear(){
        list.clear();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        ItemLayoutBinding binding;

        public ViewHolder(@NonNull View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }
}
