package com.example.techassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShimmerFrameLayout shimmerFrameLayout =
                (ShimmerFrameLayout) findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmerAnimation();
    }
}