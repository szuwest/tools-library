package org.szuwest.lib;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import org.szuwest.lib.BaseActivity;
import org.szuwest.library.R;
import org.szuwest.utils.ScreenUtils;
import org.szuwest.view.TopBarView;

public abstract class TopbarActivity extends BaseActivity {

	public static final int BACK_ANIM_NONE = 0;
	public static final int BACK_ANIM_TOP2DOWN = 1;
	public static final int BACK_ANIM_LET2RIGHT = 2;

	/**
	 * 返回动画类型
	 */
	protected int mBackAnimType = BACK_ANIM_LET2RIGHT;

	protected TopBarView mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentLayout());
		initTitleBar();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTitleBar != null)
			mTitleBar.refreshTopBar();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void back(int animType) {
		ScreenUtils.hideIme(this);
		finish();
		if (animType == BACK_ANIM_TOP2DOWN) {
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
		} else if (animType == BACK_ANIM_LET2RIGHT) {
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		} else {
		}
	}

	public void back(){
		back(mBackAnimType);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back(mBackAnimType);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void initTitleBar() {
		mTitleBar = (TopBarView) findViewById(R.id.topbar);
		if (mTitleBar != null) {
			mTitleBar.setLeftButtonClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					back(mBackAnimType);
				}
			});
		}
	}

    abstract int getContentLayout();
}
