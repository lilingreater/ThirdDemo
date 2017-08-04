package com.example.dsw.cardviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        List<ImageInfor> list = new ArrayList<>();
        list.add(new ImageInfor(R.mipmap.caiyilin, "蔡依林"));
        list.add(new ImageInfor(R.mipmap.ulinxinru, "林心如"));
        list.add(new ImageInfor(R.mipmap.caiyilin,"蔡依林"));
        list.add(new ImageInfor(R.mipmap.ulinxinru, "林心如"));
        list.add(new ImageInfor(R.mipmap.caiyilin,"蔡依林"));
        list.add(new ImageInfor(R.mipmap.ulinxinru, "林心如"));


        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        MyAdapter myAdapter = new MyAdapter(list);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClick(new OnItemClick(){
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplication(),"点击了：" + position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        private List<ImageInfor> list;
        public MyAdapter(List<ImageInfor> list){
            this.list = list;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carditem,viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
//            myViewHolder.iv_backgroud.setBackgroundResource(list.get(i).getImageId());
            myViewHolder.item_cv.setBackgroundResource(list.get(i).getImageId());
            myViewHolder.tv_title.setText(list.get(i).getName());
            final int position = i;
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClick != null){
                        onItemClick.onItemClick(view,position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView iv_backgroud;
            private TextView tv_title;
            private CardView item_cv;
            public MyViewHolder(View itemView) {
                super(itemView);
                iv_backgroud = (ImageView) itemView.findViewById(R.id.picture);
                item_cv = (CardView) itemView.findViewById(R.id.item_cv);
                tv_title = (TextView) itemView.findViewById(R.id.name);
            }
        }

        private OnItemClick onItemClick;

        public void setOnItemClick(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }
    }
}
