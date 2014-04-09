package org.szuwest.image;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import org.szuwest.lib.BaseActivity;
import org.szuwest.library.R;
import org.szuwest.view.TouchImageView;

public class ImageViewer extends BaseActivity implements OnClickListener {

	private TouchImageView imageView;
	private String url;
    private String thumbUrl;
	private ProgressBar progressBar;
    private int from;
    private int width;
    private int height;
    private String urlInServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.hold);
		url = getIntent().getStringExtra("url");
        thumbUrl = getIntent().getStringExtra("thumbUrl");
        from = getIntent().getIntExtra("from", 0);
        if (from == 1) {
            width = getIntent().getIntExtra("width", 0);
            height = getIntent().getIntExtra("height", 0);
            urlInServer = getIntent().getStringExtra("urlInServer");
        }
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		RelativeLayout container = new RelativeLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setContentView(container);

		RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		imageView = new TouchImageView(this);
		imageView.setLayoutParams(mParams);
		imageView.setEnabled(true);
		imageView.setOnClickListener(this);
		container.addView(imageView);
		
		progressBar = new ProgressBar(this);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.wait_bg_clockwise_indicator));
		mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		progressBar.setLayoutParams(mParams);
		container.addView(progressBar);
//		ImageUtils.showImageWithUrl(imageView, url, loaderListener);

        if (from == 1 && urlInServer != null && !urlInServer.equals("")) {
            ImageButton add2AlbumBtn = new ImageButton(this);
            mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            add2AlbumBtn.setLayoutParams(mParams);
            add2AlbumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
//            add2AlbumBtn.setImageResource(R.drawable.album_add_icon);
            add2AlbumBtn.setBackgroundDrawable(null);
            container.addView(add2AlbumBtn);
        }
	}

	@Override
	public void onClick(View v) {
//        if ( !(dialog != null && dialog.isShowing()) ) {
		    finish();
		    overridePendingTransition(R.anim.hold, R.anim.fade_out);
//        }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.hold, R.anim.fade_out);
		}
		return super.onKeyDown(keyCode, event);
	}
	
//	ImageLoadingListener loaderListener = new ImageLoadingListener(){
//
//		@Override
//		public void onLoadingStarted(String imageUri, View view) {
//            if (thumbUrl != null && !thumbUrl.equals("")) {//如果缩略图，先显示缩略图，一般来说，缩略图是已经存在缓存里面，可以立即显示出来在界面上
//                List<Bitmap> values = MemoryCacheUtil.findCachedBitmapsForImageUri(thumbUrl, ImageLoader.getInstance().getMemoryCache());
//                if (values.size() > 0) {
//                    Bitmap b = values.get(0);
//                    if (b != null) {
//                        imageView.setImageBitmap(b);
////                        LogUtil.d("thumb not null");
//                    } else {
////                        LogUtil.d("thumb null");
//                    }
//                }
//            }
//		}
//
//		@Override
//		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//			progressBar.setVisibility(View.GONE);
//			Toast.makeText(ImageViewer.this, R.string.load_failed, Toast.LENGTH_SHORT).show();
//			finish();
//			overridePendingTransition(R.anim.hold, R.anim.fade_out);
//		}
//
//		@Override
//		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//			progressBar.setVisibility(View.GONE);
//            if (loadedImage != null) imageView.setImageBitmap(loadedImage);
//		}
//
//		@Override
//		public void onLoadingCancelled(String imageUri, View view) {
//		}
//
//	};
////
//    private void uploadToAlbum(final String url, int width, int height) {
//        AppsHttpHelper httpHelper = new AppsHttpHelper(new HttpResponse(this) {
//            @Override
//            public void onStart() {
//                showWaiting(null, false);
//            }
//
//            @Override
//            public void onSuccess(JSONObject data) throws JSONException {
//                hideWaiting();
////                HintDialog.showOk(ImageViewer.this, R.string.album_upload_success2, 1500);
//                XiaoenaiUtils.showToast(R.string.album_upload_success2);
//            }
//
//            @Override
//            public void onError(int errCode) {
//                hideWaiting();
//                super.onError(errCode);
//            }
//        });
//        httpHelper.addPhotos(url, width, height);
//    }

//    private void showCommonDialog() {
//        dialog = new CommonDialog(this);
//        dialog.addButton(R.string.chat_add_2_album, CommonDialog.COLOR_WHITE, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                uploadToAlbum(urlInServer, width, height);
//            }
//        });
//        dialog.addButton(R.string.chat_save_2_sdcard, CommonDialog.COLOR_WHITE, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (url.startsWith("file://")) {
//                    String path = url.substring(7);
//                    if (path.startsWith(FileUtils.PHOTOALBUM_PATH)) {
//                        XiaoenaiUtils.showToast(R.string.photo_already_in_sdcard);
//                    } else {
//                        File file = new File(path);
//                        String toPath = FileUtils.PHOTOALBUM_PATH + file.getName();
//                        int ret = FileUtils.copySdcardFile(path, toPath);
//                        if (ret == 0) {
//                            ImageUtils.addPhotoToGallery(new File(toPath));
//                            XiaoenaiUtils.showToast(R.string.download_done);
//                        } else {
//                            XiaoenaiUtils.showToast(R.string.download_failed);
//                        }
//                    }
//                    return;
//                }
//                if (!XiaoenaiUtils.getExternalStorageState()) {
//                    HintDialog.showError(ImageViewer.this, R.string.sdcard_unmounted_tip3, 1500);
//                    return;
//                }
//                XiaoenaiUtils.startDownLoadPhoto(url);
//            }
//        });
//        dialog.show();
//    }
}