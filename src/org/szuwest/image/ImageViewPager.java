package org.szuwest.image;

import java.util.HashMap;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.szuwest.lib.BaseActivity;
import org.szuwest.library.R;
import org.szuwest.utils.ScreenUtils;

public class ImageViewPager extends BaseActivity {

	private ViewPager viewPager;
	private ViewGroup main, group;
	private ImageView imageView;
	private ImageView[] imageViews;
	private String urls[];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.hold);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		LayoutInflater inflater = getLayoutInflater();

		urls = getIntent().getStringArrayExtra("imageUrls");
		imageViews = new ImageView[urls.length];
		main = (ViewGroup) inflater.inflate(R.layout.common_image_imageviewpager, null);

		// group是R.layou.main中的负责包裹小圆点的LinearLayout.
		group = (ViewGroup) main.findViewById(R.id.viewGroup);

		viewPager = (ViewPager) main.findViewById(R.id.guidePages);

		for (int i = 0; i < urls.length; i++) {
			imageView = new ImageView(ImageViewPager.this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtils.dip2px(4), ScreenUtils.dip2px(4));
			params.leftMargin = ScreenUtils.dip2px(3);
			params.rightMargin = ScreenUtils.dip2px(3);
			imageView.setLayoutParams(params);
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i].setBackgroundResource(R.drawable.common_image_page_indicator_focus);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.common_image_page_indicator);
			}
			group.addView(imageViews[i]);
		}

		setContentView(main);

		viewPager.setAdapter(new ImagePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
		//滑动切换每个页面时相邻页面的边距
		viewPager.setPageMargin(ScreenUtils.dip2px(60));
		//屏幕之外缓存多少页
		viewPager.setOffscreenPageLimit(3);
	}

	/** 指引页面Adapter */
	@SuppressLint("UseSparseArrays")
	class ImagePageAdapter extends PagerAdapter {
		private HashMap<Integer, View> viewHashMap;

		public ImagePageAdapter() {
			viewHashMap = new HashMap<Integer, View>();
		}

		@Override
		public int getCount() {
			return urls.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
			viewHashMap.remove(position);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View itemView;
			if (viewHashMap.containsKey(position)) {
				itemView = (ImageView) viewHashMap.get(position);
				ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewPagerImage);
//				ImageUtils.showImageWithUrl(imageView, urls[position], true);
			} else {
				itemView = (View) getLayoutInflater().inflate(R.layout.common_image_imageviewpager_item, null);
				ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewPagerImage);
				final ProgressBar progressView = (ProgressBar) itemView.findViewById(R.id.imageViewPagerProgressView);
//				ImageUtils.showImageWithUrl(imageView, urls[position], new ImageLoadingListener() {
//
//					@Override
//					public void onLoadingStarted(String imageUri, View view){
//						progressView.show();
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//							FailReason failReason) {
//						progressView.hide();
//						Toast.makeText(ImageViewPager.this, R.string.network_error, Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//							Bitmap loadedImage){
//						progressView.hide();
//					}
//
//					@Override
//					public void onLoadingCancelled(String imageUri, View view) {
//					}
//				});
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						finish();
						overridePendingTransition(R.anim.hold, R.anim.fade_out);
					}
				});
				viewHashMap.put(position, itemView);
				((ViewPager) container).addView(itemView);
			}

			return itemView;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	/** 指引页面改监听器 */
	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.common_image_page_indicator_focus);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.common_image_page_indicator);
				}
			}

		}

	}

}