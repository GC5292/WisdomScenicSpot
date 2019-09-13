package com.chen.fy.wisdomscenicspot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.fy.wisdomscenicspot.R;
import com.chen.fy.wisdomscenicspot.adapter.ItemClickListener;
import com.chen.fy.wisdomscenicspot.adapter.ViewPointAdapter;
import com.chen.fy.wisdomscenicspot.beans.SceneryInfo;
import com.chen.fy.wisdomscenicspot.beans.ViewPointInfo;
import com.chen.fy.wisdomscenicspot.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 景点推荐活动
 */
public class ViewPointActivity extends AppCompatActivity implements ItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private List<ViewPointInfo> list;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this, true);
        setContentView(R.layout.view_point_layout);

        initView();
        initData();

        //2 RecyclerView设置
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        ViewPointAdapter viewPointAdapter = new ViewPointAdapter(list);
        viewPointAdapter.setItemClickLister(this);
        recyclerView.setAdapter(viewPointAdapter);

    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_viewpoint);
        toolbar = findViewById(R.id.toolbar_view_point);
        toolbar.setNavigationOnClickListener(this);

    }

    private void initData() {
        if (getIntent() != null) {
            switch (getIntent().getStringExtra("目的地")) {
                case "重庆":
                    toolbar.setTitle("重庆");
                    break;
                case "北京":
                    toolbar.setTitle("北京");
                    break;
                case "上海":
                    toolbar.setTitle("上海");
                    break;
            }
        }
        //2 初始化RecyclerView显示的数据
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.isEmpty()) {
            list.clear();
        }
        ViewPointInfo viewPointInfo1 = new ViewPointInfo();
        viewPointInfo1.setName("磁器口古镇");
        viewPointInfo1.setAddress("重庆市沙坪坝区磁器口古镇");
        viewPointInfo1.setNumber(10719);
        viewPointInfo1.setDistance(30.15);

        ViewPointInfo viewPointInfo2 = new ViewPointInfo();
        viewPointInfo2.setName("解放碑步行街");
        viewPointInfo2.setAddress("重庆市渝中区解放碑周边区域");
        viewPointInfo2.setNumber(7825);
        viewPointInfo2.setDistance(30.15);

        ViewPointInfo viewPointInfo3 = new ViewPointInfo();
        viewPointInfo3.setName("武隆天生三桥");
        viewPointInfo3.setAddress("重庆市武隆区仙女山镇游客接待中心");
        viewPointInfo3.setNumber(5950);
        viewPointInfo3.setDistance(30.15);

        ViewPointInfo viewPointInfo4 = new ViewPointInfo();
        viewPointInfo4.setName("大足石刻");
        viewPointInfo4.setAddress("重庆市大足区宝顶镇大足石刻风景区");
        viewPointInfo4.setNumber(3550);
        viewPointInfo4.setDistance(30.15);

        ViewPointInfo viewPointInfo5 = new ViewPointInfo();
        viewPointInfo5.setName("白公馆");
        viewPointInfo5.setAddress("沙坪坝区壮志路治法三村63号");
        viewPointInfo5.setNumber(2550);
        viewPointInfo5.setDistance(30.15);

        ViewPointInfo viewPointInfo6 = new ViewPointInfo();
        viewPointInfo6.setName("长江索道");
        viewPointInfo6.setAddress("重庆市渝中区新华路151号");
        viewPointInfo6.setNumber(4550);
        viewPointInfo6.setDistance(30.15);

        ViewPointInfo viewPointInfo7 = new ViewPointInfo();
        viewPointInfo7.setName("南山风景区");
        viewPointInfo7.setAddress("重庆市南岸区南山镇南山公园附近");
        viewPointInfo7.setNumber(1550);
        viewPointInfo7.setDistance(30.15);

        list.add(viewPointInfo1);
        list.add(viewPointInfo2);
        list.add(viewPointInfo3);
        list.add(viewPointInfo4);
        list.add(viewPointInfo5);
        list.add(viewPointInfo6);
        list.add(viewPointInfo7);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_view_point:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onItemClick(int i) {
        Intent intent;
        switch (list.get(i).getName()) {
            case "磁器口古镇":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.578936);
                intent.putExtra("Longitude", 106.452215);
                startActivity(intent);
                break;
            case "解放碑步行街":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.557564);
                intent.putExtra("Longitude", 106.577233);
                startActivity(intent);
                break;
            case "武隆天生三桥":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.429943);
                intent.putExtra("Longitude", 107.803549);
                startActivity(intent);
                break;
            case "大足石刻":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.74814);
                intent.putExtra("Longitude", 105.795545);
                startActivity(intent);
                break;
            case "白公馆":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.576473);
                intent.putExtra("Longitude", 106.432065);
                startActivity(intent);
                break;
            case "长江索道":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.556249);
                intent.putExtra("Longitude", 106.586634);
                startActivity(intent);
                break;
            case "南山风景区":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Latitude", 29.556989);
                intent.putExtra("Longitude", 106.623053);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLongClick(int i) {

    }
}
