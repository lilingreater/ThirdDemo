package com.xhw.android.mygride;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lilin on 2017/6/12.
 * func :
 */
public class GridAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Data> datas;

    public GridAdapter(Context context, ArrayList<Data> datas) {
        this.context = context;
        this.datas = datas;
    }

    public int getCount() {
        return (datas.size() + 1);
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        ImageView item_image, item_delete;
        TextView item_name, item_position;
        convertView = View.inflate(context, R.layout.item_gride, null);
        item_image = (ImageView) convertView.findViewById(R.id.item_image);
        item_delete = (ImageView) convertView.findViewById(R.id.item_delete);
        item_name = (TextView) convertView.findViewById(R.id.item_name);
        item_position = (TextView) convertView.findViewById(R.id.item_position);

        item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDeleteClick != null) {
                    onItemDeleteClick.onItemDelete(position);
                }
            }
        });
        item_delete.setVisibility(View.VISIBLE);
        if (position == 0) {
            item_delete.setVisibility(View.GONE);
            item_image.setImageResource(R.mipmap.ic_launcher);
            item_image.setAdjustViewBounds(true);
            item_image.setMaxHeight(200);//这个值你自己写吧
            item_image.setMaxWidth(200);
        }else {

            //设置图片的位置
            item_image.setImageResource(R.mipmap.ic_launcher);
            item_image.setAdjustViewBounds(true);
            item_image.setMaxHeight(100);
            item_image.setMaxWidth(100);
            RelativeLayout.LayoutParams layoutParams9 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams9.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            item_image.setLayoutParams(layoutParams9);


        }
        if (position == datas.size()) {//最后一个
            item_image.setImageBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.mipmap.icon_add));
            item_delete.setVisibility(View.GONE);
            item_name.setVisibility(View.GONE);
            item_position.setVisibility(View.GONE);
            if (position == MainActivity.MAXITEM) {//最大数目
                item_image.setVisibility(View.GONE);
            }
        } else {//glide加载图片
//            item_image.setImageBitmap(data.get(position).getImageUrl());
            item_name.setText(datas.get(position).getName());
            item_position.setText(datas.get(position).getPosition());
        }

        return convertView;
    }

    /**
     * 添加一条数据
     * @param data
     */
    public void addData(Data data) {
        this.datas.add(data);
        notifyDataSetChanged();
    }

    /**
     * 删除一条数据
     * @param position
     */
    public void deleteData(int position) {
        this.datas.remove(position);
        notifyDataSetChanged();
    }

    public interface onItemDeleteClick {
        void onItemDelete(int position);
    }

    private onItemDeleteClick onItemDeleteClick;

    public void setOnItemDeleteClick(GridAdapter.onItemDeleteClick onItemDeleteClick) {
        this.onItemDeleteClick = onItemDeleteClick;
    }
}
