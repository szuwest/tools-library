package org.szuwest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.szuwest.library.R;
import org.szuwest.utils.ScreenUtils;
import org.szuwest.utils.UiUtils;

/**
 * @description 适合大部分情况用的标题栏
 */
public class TopBarView extends RelativeLayout {

	/**
	 * 标题居中
	 */
	public static final int TITLE_ALIGN_CENTER = 0;
	/**
	 * 标题靠左
	 */
	public static final int TITLE_ALIGN_LEFT = 1;
	
	private int titleAlign = TITLE_ALIGN_CENTER;
	
	private Button leftButton, rightButton;
	private TextView titleView;
	private RelativeLayout.LayoutParams leftBtnLayoutParams, rightBtnLayoutParams, titleViewLayoutParams;

	private static int LEFT_BTN_ID = 1;
	private static int RIGHT_BTN_ID = 2;
	private static int TITLE_VIEW_ID = 3;

	public TopBarView(Context context) {
		super(context);
	}

	public TopBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
		try {
			titleAlign = ta.getInt(R.styleable.TopBar_topbarTitleAlign, TITLE_ALIGN_CENTER);

			//left btn
			Drawable leftDrawable = null;
			String leftString = null;
			if(ta.hasValue(R.styleable.TopBar_leftButtonDrawable)){
				leftDrawable = ta.getDrawable(R.styleable.TopBar_leftButtonDrawable);
			}
			if(ta.hasValue(R.styleable.TopBar_leftButtonText)){
				leftString = ta.getString(R.styleable.TopBar_leftButtonText);
			}
			if(titleAlign == TITLE_ALIGN_LEFT){
				leftString = "";
				if(leftDrawable == null){
					leftDrawable = context.getResources().getDrawable(R.drawable.topbar_left_back);
				}
			}
			if(leftString != null && !leftString.equalsIgnoreCase("") && leftDrawable == null)
				leftDrawable = context.getResources().getDrawable(R.drawable.topbar_left_btn_bg);
			setLeftButton(leftDrawable, leftString);
			
			//right btn
			Drawable rightDrawable = null;
			String rightString = null;
			if(ta.hasValue(R.styleable.TopBar_rightButtonDrawable)){
				rightDrawable = ta.getDrawable(R.styleable.TopBar_rightButtonDrawable);
			}
			if(ta.hasValue(R.styleable.TopBar_rightButtonText)){
				rightString = ta.getString(R.styleable.TopBar_rightButtonText);
			}
			if(rightString != null && !rightString.equalsIgnoreCase("") && rightDrawable == null)
				rightDrawable = context.getResources().getDrawable(R.drawable.topbar_right_btn_bg);
			setRightButton(rightDrawable, rightString);

            //title
            Drawable titleBgDrawable = null;
            String title = ta.getString(R.styleable.TopBar_topbarTitle);
            if(ta.hasValue(R.styleable.TopBar_titleBackground)){
                titleBgDrawable = ta.getDrawable(R.styleable.TopBar_titleBackground);
            }
            setTitle(titleBgDrawable, title);
		} finally {
			ta.recycle();
		}

		refreshTopBar();
	}

	/**
	 * 设置TopBar的背景
	 * 
	 * @param bgDrawable
	 */
	@SuppressWarnings("deprecation")
	private void setTopBarViewBackgroundDrawable(Drawable bgDrawable) {
		setBackgroundDrawable(bgDrawable);
	}

	/**
	 * 设置TopBar的背景
	 * 
	 * @param resId
	 */
	private void setTopBarViewBackground(int resId) {
		setTopBarViewBackgroundDrawable(getResources().getDrawable(resId));
	}

    /**
     * 标题的对齐方式
     * 该必须在 setTitle 方法之前调用。如果之前已经实例化过title, 需要调用setTitle（null, null）把title去掉
     * @param align
     */
    public void setTitleAlign(int align) {
        this.titleAlign = align;
    }

	/**
	 * 设置标题
	 * 
	 * @param title
	 * @param bgDrawable
	 * 如果title和bgDrawable同时为null，则不显示
	 */
	@SuppressWarnings("deprecation")
	public void setTitle(Drawable bgDrawable, String title) {
		if (titleView == null) {
			if (title != null || bgDrawable != null) {
				titleView = new TextView(getContext());
				titleView.setId(TITLE_VIEW_ID);
				titleView.setShadowLayer(1, 0, 1, 0x66000000);
				titleView.setText(""); // test
				titleView.setTextColor(Color.WHITE);
				titleView.setTextSize(20.0f);
                titleView.setSingleLine();
                titleView.setEllipsize(TextUtils.TruncateAt.END);
				titleViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				if (titleAlign == TITLE_ALIGN_CENTER) {
					titleViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                    titleViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, LEFT_BTN_ID);
//                    titleViewLayoutParams.addRule(RelativeLayout.LEFT_OF, RIGHT_BTN_ID);
                    if (!isInEditMode()) {
                        titleView.setMaxWidth(ScreenUtils.getScreenWidth() - ScreenUtils.dip2px(130));
                    }
				} else {
					titleViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
					titleViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, LEFT_BTN_ID);
					titleView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(leftButton != null)
								leftButton.performClick();
						}
					});
				}
                if (isInEditMode()) {
                    titleViewLayoutParams.leftMargin = 5;
                    titleViewLayoutParams.rightMargin = 5;
                } else {
                    titleViewLayoutParams.leftMargin = ScreenUtils.dip2px(4);
                    titleViewLayoutParams.rightMargin = ScreenUtils.dip2px(4);
                }
				addView(titleView, titleViewLayoutParams);
			} else {
				return;
			}
		} else {
			if (title == null && bgDrawable == null) {
				removeView(titleView);
				titleView = null;
				return;
			}
		}
		titleView.setBackgroundDrawable(bgDrawable);
		titleView.setText(title);
	}

	/**
	 * 设置标题
	 * 
	 * @param resId
	 *            如果resId为0，则不显示
	 */
	public void setTitle(int resId) {
		if (resId == 0) { 
			setTitle(null, null);
		} else {
			setTitle(null, getResources().getString(resId));
		}
	}

	/**
	 * 设置标题
	 * 
	 * @param name
	 *            如果name为null，则不显示
	 */
	public void setTitle(String name) {
		setTitle(null, name);
	}
	
	/**
	 * 设置标题
	 * 
	 * @param bgResId 
	 * @param titleId
	 * 
	 * 如果bgResId，titleId为0，则不显示
	 */
	public void setTitle(int bgResId, int titleId){
		if (titleId == 0) {
			setTitle(getResources().getDrawable(bgResId), null);
		} else {
			setTitle(getResources().getDrawable(bgResId), getResources().getString(titleId));
		}
		
	}
	
	/**
	 * 设置左边Button
	 * 
	 * @param bgDrawable
	 * @param name
	 * 如果bgDrawable和name 为null，则不显示
	 */
	@SuppressWarnings("deprecation")
	public void setLeftButton(Drawable bgDrawable, String name) {
		if (leftButton == null) {
			if (bgDrawable != null) {
				leftButton = new Button(getContext());
				leftButton.setId(LEFT_BTN_ID);
				leftButton.setTextColor(Color.WHITE);
				leftButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				leftButton.setShadowLayer(1, 0, 1, 0x66000000);
				if (name != null && !name.equals("")) {
                    if (isInEditMode()) {
					    leftButton.setMinWidth(81);
                    } else {
                        leftButton.setMinWidth(ScreenUtils.dip2px(54));
                    }
				}
				leftBtnLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				leftBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				leftBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				addView(leftButton, leftBtnLayoutParams);
			} else {
				return;
			}

		} else {
			if (bgDrawable == null && name == null) {
				removeView(leftButton);
				leftButton = null;
				return;
			}
		}
		leftButton.setBackgroundDrawable(bgDrawable);
		if (name != null && !name.equalsIgnoreCase("")) {
            if (isInEditMode()) {
			    leftButton.setMinWidth(81);
            } else {
                leftButton.setMinWidth(ScreenUtils.dip2px(54));
            }
		} else {
            leftButton.setMinWidth(0);
        }
		leftButton.setText(name);

	}

	/**
	 * 设置左边Button
	 * 
	 * @param bgResId <=0 不显示
	 * @param nameResId <=0 不显示
	 */
	public void setLeftButton(int bgResId, int nameResId) {
		if (bgResId <= 0) {
			if (nameResId <= 0) {
				setLeftButton(null, null);
            }
			else {
				setLeftButton(null, getResources().getString(nameResId));
            }
		} else {
			if (nameResId <= 0) {
				setLeftButton(getResources().getDrawable(bgResId), null);
			} else {
				setLeftButton(getResources().getDrawable(bgResId), getResources().getString(nameResId));
			}
		}
	}

	/**
	 * 设置右边Button
	 * 
	 * @param bgDrawable
	 * @param name
	 * 如果bgDrawable和name 为null，则不显示
	 */
	@SuppressWarnings("deprecation")
	public void setRightButton(Drawable bgDrawable, String name) {
		if (rightButton == null) {
			if (bgDrawable != null) {
				rightButton = new Button(getContext());
				rightButton.setId(RIGHT_BTN_ID);
				rightButton.setTextColor(Color.WHITE);
				rightButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				rightButton.setShadowLayer(1, 0, 1, 0x66000000);
				if (name != null && !name.equals("")) {
                    if (isInEditMode()) {
					    rightButton.setMinWidth(81);
                    } else {
                        rightButton.setMinWidth(ScreenUtils.dip2px(54));
                    }
				}
				rightButton.setGravity(Gravity.CENTER);
				rightBtnLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				rightBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				rightBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				addView(rightButton, rightBtnLayoutParams);
			} else {
				return;
			}
		} else {
			if (bgDrawable == null && name == null) {
				removeView(rightButton);
				rightButton = null;
				return;
			}
		}
		rightButton.setBackgroundDrawable(bgDrawable);
		if (name != null&& !name.equalsIgnoreCase("")) {
            if (isInEditMode()) {
                rightButton.setMinWidth(81);
            } else {
                rightButton.setMinWidth(ScreenUtils.dip2px(54));
            }
		} else {
            rightButton.setMinWidth(0);
        }
		rightButton.setText(name);

	}

	/**
	 * 设置右边Button
	 * 
	 * @param bgResId <=0 不显示
	 * @param nameResId <=0 不显示
	 */
	public void setRightButton(int bgResId, int nameResId) {
		if (bgResId <= 0) {
			if (nameResId <= 0) {
				setRightButton(null, null);
            }
			else {
				setRightButton(null, getResources().getString(nameResId));
            }
		} else {
			if (nameResId <= 0) {
				setRightButton(getResources().getDrawable(bgResId), "");
			} else {
				setRightButton(getResources().getDrawable(bgResId), getResources().getString(nameResId));
			}
		}
	}

	/**
	 * 设置左边Button的监听器
	 * 
	 * @param listener
	 */
	public void setLeftButtonClickListener(View.OnClickListener listener) {
		if (leftButton != null) {
			leftButton.setOnClickListener(listener);
			leftButton.setOnTouchListener(UiUtils.addPressedEffect);
		}
	}

	/**
	 * 设置右边Button的监听器
	 * 
	 * @param listener
	 */
	public void setRightButtonClickListener(View.OnClickListener listener) {
		if (rightButton != null) {
			rightButton.setOnClickListener(listener);
			rightButton.setOnTouchListener(UiUtils.addPressedEffect);
		}
	}
	
	public void setTitleOnClickListener(View.OnClickListener listener){
		if (titleView != null) {
			titleView.setOnClickListener(listener);
			titleView.setOnTouchListener(UiUtils.addPressedEffect);
		}
	}
	
	public void setLeftButtonEnable(boolean enable){
		if (leftButton != null)
			leftButton.setEnabled(enable);
	}
	
	public void setRightButtonEnable(boolean enable){
		if (rightButton != null)
			rightButton.setEnabled(enable);
	}
	
	public void refreshTopBar() {
//        int resId = R.drawable.topbar_bg_default;
//        if (!isInEditMode()) {
//            String currentTheme = UserConfig.getString(UserConfig.CURRENT_THEME_STRING, "topbar_bg_default");
//		    resId = getResources().getIdentifier(currentTheme, "drawable", getContext().getPackageName());
//        }
//		if (resId != 0) {
//			setTopBarViewBackground(resId);
//		}
	}
	
	public Button getLeftButton(){
		return leftButton;
	}

    public Button getRightButton() {
        return rightButton;
    }
	
	private TextView messageCountText = null;
	/**
	 * 动态通知中心提醒标记
	 * @param msgCount
	 * msgCount 为0时标记不显示
	 */
	public void showMessageCount(int msgCount){
		if (msgCount > 0) {
			if (messageCountText == null) {
				messageCountText = new TextView(getContext());
//				messageCountText.setBackgroundResource(R.drawable.home_notify_new);
				messageCountText.setTextColor(0xff975500);
				messageCountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                if (isInEditMode()) {
				    params.rightMargin = 12;
				    params.topMargin = 8;
				    messageCountText.setPadding(6, 0, 6, 2);
                } else {
                    params.rightMargin = ScreenUtils.dip2px(8);
                    params.topMargin = ScreenUtils.dip2px(5);
                    messageCountText.setPadding(ScreenUtils.dip2px(4), 0, ScreenUtils.dip2px(4), ScreenUtils.dip2px(2));
                }
				addView(messageCountText, params);
			}
//			messageCountText.setText(String.valueOf(msgCount));
//			messageCountText.setText("N");
		} else {
			if (messageCountText != null) {
				removeView(messageCountText);
				messageCountText = null;
			}
		}
	}
	
	public void setLeftButtonVisible(int visible){
		if (leftButton != null) {
			leftButton.setVisibility(visible);
		}
	}
	
	public void setRightButtonVisible(int visible){
		if (rightButton != null){
			rightButton.setVisibility(visible);
		}
	}
}
