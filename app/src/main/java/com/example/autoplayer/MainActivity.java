package com.example.autoplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoplayer.adapter.FeedAdapter;
import com.example.autoplayer.databinding.ActivityMainBinding;
import com.example.autoplayer.enums.FeedPostType;
import com.example.autoplayer.model.FeedBean;
import com.example.autoplayer.utils.Utils;
import com.player.autoplayer.AutoPlayerManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<FeedBean> feedBeanList = new ArrayList<>();
    private RecyclerView rvFeeds;
    private FeedAdapter feedAdapter;
    private boolean isMute = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        rvFeeds = binding.rvFeeds;
        AutoPlayerManager autoPlayerManager = new AutoPlayerManager(this);
        autoPlayerManager.setAutoPlayerId(R.id.autoPlayer);
        autoPlayerManager.setUseController(true);
        autoPlayerManager.attachRecyclerView(rvFeeds);
        autoPlayerManager.setup();

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        feedBeanList.clear();
        feedBeanList.addAll(Utils.getDummyData());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvFeeds.setLayoutManager(layoutManager);
        feedAdapter = new FeedAdapter(this, feedBeanList, (position) -> {
            isMute = !isMute;
            for (int i = 0; i < feedBeanList.size(); i++) {
                if (feedBeanList.get(i).getType() == FeedPostType.VIDEO) {
                    feedBeanList.get(i).setMute(isMute);
                    if (i != position) {
                        // Refresh all items of adapter instead of current item,
                        // bcz we're already changing the icon of current item in adapter.
                        feedAdapter.notifyItemChanged(i);
                    }
                }
            }
        }
        );
        rvFeeds.setAdapter(feedAdapter);
    }

}