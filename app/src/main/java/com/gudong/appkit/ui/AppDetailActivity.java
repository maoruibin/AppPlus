package com.gudong.appkit.ui;

import android.databinding.DataBindingUtil;
import android.databinding.layouts.DataBindingInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gudong.appkit.R;
import com.gudong.appkit.databinding.ActivityAppDetailBinding;
import com.gudong.appkit.entity.AppEntity;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAppDetailBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_app_detail);
        AppEntity entity = getIntent().getParcelableExtra("detail");
        binding.setAppInfo(entity);
    }
}

