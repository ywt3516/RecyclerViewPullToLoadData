package com.example.ywt.recyclerview_pulltoloaddata;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywt on 2018/09/07
 */
public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<String> list;

    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 6;
    private GridLayoutManager mLayoutManager;
    private MyAdapter adapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        findView();
        initRefreshLayout();
        initRecyclerView();
    }

    private void initData() {
        list = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            list.add("条目" + i);
        }
    }


    private void findView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    private void initRefreshLayout() {
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 设置可见
                // refreshLayout.setRefreshing(true);
                // 重置adapter的数据源为空
                adapter.resetDatas();
                // 获取第第0条到第PAGE_COUNT（值为10）条的数据
                updateRecyclerView(0, PAGE_COUNT);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 模拟网络加载时间，设置不可见
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void initRecyclerView() {
        // 初始化RecyclerView的Adapter
        // 第一个参数为数据，上拉加载的原理就是分页，所以我设置常量PAGE_COUNT=10，即每次加载10个数据
        // 第二个参数为Context
        // 第三个参数为hasMore，是否有新数据
        adapter = new MyAdapter(getDatas(0, PAGE_COUNT), this, getDatas(0, PAGE_COUNT).size() > 0 ? true : false);
        mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // 实现上拉加载重要步骤，设置滑动监听器，RecyclerView自带的ScrollListener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 在newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1，自己可以算一下
                    if (adapter.isFadeTips() == false && lastVisibleItem + 1 == adapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }

                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                    if (adapter.isFadeTips() == true && lastVisibleItem + 2 == adapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 在滑动完成后，拿到最后一个可见的item的位置
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private List<String> getDatas(final int firstIndex, final int lastIndex) {
        List<String> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < list.size()) {
                resList.add(list.get(i));
            }
        }
        return resList;
    }

    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView(int fromIndex, int toIndex) {
        // 获取从fromIndex到toIndex的数据
        List<String> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            // 然后传给Adapter，并设置hasMore为true
            adapter.updateList(newDatas, true);
        } else {
            adapter.updateList(null, false);
        }
    }

//    @Override
//    public void onRefresh() {
//        // 设置可见
//      // refreshLayout.setRefreshing(true);
//        // 重置adapter的数据源为空
//        adapter.resetDatas();
//        // 获取第第0条到第PAGE_COUNT（值为10）条的数据
//        updateRecyclerView(0, PAGE_COUNT);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 模拟网络加载时间，设置不可见
//                refreshLayout.setRefreshing(false);
//            }
//        }, 1000);
//    }


}
























