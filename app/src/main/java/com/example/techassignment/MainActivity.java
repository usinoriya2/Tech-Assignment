package com.example.techassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

public class MainActivity extends AppCompatActivity {
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout descriptionLayout;
    RelativeLayout repo1;
    RelativeLayout repo2;
    Handler handler;
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initialiseUI();
    }

    private void bindViews(){
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        repo1 = findViewById(R.id.repo1);
        repo2 = findViewById(R.id.repo2);
        descriptionLayout = findViewById(R.id.description_layout);
        shimmerFrameLayout.startShimmerAnimation();
    }
    private void initialiseUI(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                repo1.setVisibility(View.VISIBLE);
                repo2.setVisibility(View.VISIBLE);
                repo1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(descriptionLayout.getVisibility() == View.GONE){
                            descriptionLayout.setVisibility(View.VISIBLE);
                        }else{
                            descriptionLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        startHandler();
    }

    public void startHandler() {
        handler.postDelayed(r, 2000); //for 2 seconds
    }
}