package me.ajax.cardswipe1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.ajax.cardswipe1.layoutmanager.CardLayoutManger;

public class MainActivity extends AppCompatActivity {

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new CardLayoutManger());
        recyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
            @Override
            public int onGetChildDrawingOrder(int childCount, int i) {
                return childCount - i - 1;
            }
        });
        myAdapter = new MyAdapter();
        myAdapter.setDrawableResList(getDatas());
        recyclerView.setAdapter(myAdapter);
    }

    List<Integer> getDatas() {

        List<Integer> images = new ArrayList<>();
        images.add(R.mipmap.image12);
        images.add(R.mipmap.image9);
        images.add(R.mipmap.image10);
        images.add(R.mipmap.image11);
        images.add(R.mipmap.image1);
        images.add(R.mipmap.image2);
        images.add(R.mipmap.image3);
        images.add(R.mipmap.image4);
        images.add(R.mipmap.image5);
        images.add(R.mipmap.image6);
        images.add(R.mipmap.image7);
        images.add(R.mipmap.image8);
        return images;
    }
}
