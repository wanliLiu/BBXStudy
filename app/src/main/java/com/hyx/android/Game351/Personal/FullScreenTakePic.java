package com.hyx.android.Game351.Personal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.view.HackyViewPager;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class FullScreenTakePic extends BaseActivity {

    private HackyViewPager fullscreenPicture;

    private HeadView headView;

    private List<String> imagesList = new ArrayList<String>();
    private int currentPosition;

    @Override
    protected void initView() {
        setContentView(R.layout.picture_fullscreen);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle("照片墙");
        headView.getBtnAction().setEnabled(false);

        fullscreenPicture = (HackyViewPager) findViewById(R.id.pictureFullscreen);
    }

    @Override
    protected void initListener() {
        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onBackPressed();
            }
        });
        headView.setOnActionBtnListener(new OnActionBtnListener() {

            @Override
            public void onClick() {

            }
        });
        headView.setEnabled(false);
    }

    @Override
    protected void initData() {

        imagesList = JSON.parseArray(getIntent().getStringExtra("pictureArr"), String.class);
        currentPosition = getIntent().getIntExtra("position", 0);
        if (imagesList != null && imagesList.size() > 0) {

            ProductPictureFullscreenAdapter adapter = new ProductPictureFullscreenAdapter(ctx);

            fullscreenPicture.setAdapter(adapter);
            fullscreenPicture.setCurrentItem(currentPosition, false);
            fullscreenPicture.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    headView.getBtnAction().setText(arg0 + 1 + "/" + imagesList.size());
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
            headView.getBtnAction().setText(currentPosition + 1 + "/" + imagesList.size());
        }
    }

    public class ProductPictureFullscreenAdapter extends PagerAdapter {

        private Context ctx;

        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public ProductPictureFullscreenAdapter(Context ctx) {

            this.ctx = ctx;

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.default_image_320320)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(250, true, true, false))//是否图片加载好后渐入的动画时间
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            imageLoader = ImageLoader.getInstance();

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return imagesList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {

            View imageLayout = LayoutInflater.from(ctx).inflate(R.layout.productdetail_activity_fullscreen_picture_item, view, false);

            PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.imageView);

            imageLoader.displayImage(Constants.ServerTopicAdd + imagesList.get(position), imageView, options);

            ((ViewPager) view).addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }
}
