package cn.fuyoushuo.fqbb.view.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import rx.functions.Action1;


public class UserguideActivity extends BaseActivity {

    @Bind(R.id.userguidePage)
    ViewPager myViewPage;

    @Bind(R.id.user_guide_iconfont_area)
    View userGuideArea;

    @Bind(R.id.user_guide_iconfont)
    TextView userGuideIconfont;

    private MyPageAdapter pagerAdapter;

    //用于保存新手引导页面
    private SparseArray<ImageView> views;

    //当前图片的Index
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);
        //初始化引导页内容
        views = new SparseArray<ImageView>();
        int[] imageIds = new int[]{R.mipmap.guide_1,R.mipmap.guide_2,R.mipmap.guide_3};
        initGuideViews(imageIds);
        pagerAdapter = new MyPageAdapter(views);
        myViewPage.setAdapter(pagerAdapter);
        myViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                return;
            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                if(position == views.size()-1){
                    userGuideArea.setVisibility(View.VISIBLE);
                }else{
                    userGuideArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               return;
            }
        });

        RxView.clicks(userGuideArea).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(UserguideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //初始化图标
        initIconFont();
    }

    private void initIconFont(){
        Typeface iconfont = Typeface.createFromAsset(getAssets(),"iconfront/iconfont_userguide.ttf");
        userGuideIconfont.setTypeface(iconfont);
    }

    //自定义适配器
    private class MyPageAdapter extends PagerAdapter{

       SparseArray<ImageView> views;

        public MyPageAdapter(SparseArray<ImageView> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           //super.destroyItem(container, position, object);
           container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }


    }

    //初始化引导页
    private void initGuideViews(int[] imageIds){
        for(int i=0;i<imageIds.length;i++){
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            views.append(i,imageView);
        }
    }
}
