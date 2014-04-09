package org.szuwest.image;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.szuwest.image.croper.CropImage;
import org.szuwest.lib.BaseActivity;
import org.szuwest.library.R;
import org.szuwest.utils.FileUtils;
import org.szuwest.utils.ScreenUtils;
import org.szuwest.view.CommonDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import my.base.util.LogUtils;

public class ImagePicker {

	public static interface onPickedListener {

		public void onCancel();

		public void onPicked(String path);
	}

	protected static onPickedListener listener;
	protected Activity activity;
	protected static Boolean needCrop = false;
	protected static int width = 0;
	protected static int height = 0;
	protected static String cropOutputPath = null;
	protected static int pickType = 0;

	public static int PICK_TYPE_CAMERA = 0;
	public static int PICK_TYPE_GALLERY = 1;

    public static final int GET_IMG_CODE = 21;

	public ImagePicker(Activity activity) {
		cropOutputPath = null;
		this.activity = activity;
		ImagePicker.needCrop = false;
		listener = new onPickedListener() {

			@Override
			public void onPicked(String path) {
			}

			@Override
			public void onCancel() {
			}
		};
	}

    /**
     * 设置一个回调方法接收图片路径path
     *
     * 该方法已经废弃。因为在跳转到拍照或者取图片的页面时，我们的应用很有可能会被系统干掉（内存不足的时候）
     * 如果被系统干掉，再回来的时候所有页面是重新生成的，之前的回调listener也会释放掉。
     * 为了避免上述情况，应该在onActivityResult接收data
     * @param listener 接收path的回调
     */
    @Deprecated
    public void setonPickedLinstener(onPickedListener listener) {
        ImagePicker.listener = listener;
    }

    public void setCropAble(int width, int height) {
        ImagePicker.needCrop = true;
        ImagePicker.width = width;
        ImagePicker.height = height;
    }

    /**
     * 先对图片按比例（width, height）缩放，再显示出来，在start方法之前调用
     * @param width
     * @param height
     */
    public void setScale(int width, int height) {
//        ImagePicker.scale = true;
        ImagePicker.width = width;
        ImagePicker.height = height;
    }

    /**
     * 调用者Activity必须重载onActivityResult方法。针对requestCode等于GET_IMG_CODE的时候接收数据，
     * 从Intent里面获取data。可以参照ChatActivity的onActivityResult方法的实现
     * @param pickType 选取图片还是拍照
     */
    public void start(int pickType) {
        cropOutputPath = null;
        ImagePicker.pickType = pickType;
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, GET_IMG_CODE);
    }

    /**
     * 调用者Activity必须重载onActivityResult方法。针对requestCode等于GET_IMG_CODE的时候接收数据，
     * 从Intent里面获取data。可以参照ChatActivity的onActivityResult方法的实现
     * @param title 弹窗显示的标题
     */
    public void startWithChooser(String title) {
		final CommonDialog dialog = new CommonDialog(activity);
		dialog.addButton(R.string.common_image_take_photo, CommonDialog.COLOR_WHITE, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				start(PICK_TYPE_CAMERA);// 拍照
			}
		});
		dialog.addButton(R.string.common_image_pick_photo, CommonDialog.COLOR_WHITE, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				start(PICK_TYPE_GALLERY);// 从相册选择
			}
		});
		dialog.show();
	}
    /**
     * 调用者Activity必须重载onActivityResult方法。针对requestCode等于GET_IMG_CODE的时候接收数据，
     * 从Intent里面获取data。可以参照ChatActivity的onActivityResult方法的实现
     * @param title 弹窗显示的标题
     */
    public void startWithAlbum(String title) {
        final CommonDialog dialog = new CommonDialog(activity);
        dialog.setTitle(title);
        dialog.addButton(R.string.common_image_pick_photo, CommonDialog.COLOR_WHITE, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                start(PICK_TYPE_GALLERY);// 从相册选择
            }
        });
        dialog.show();
    }

    /**
     * 调用者Activity必须重载onActivityResult方法。针对requestCode等于GET_IMG_CODE的时候接收数据，
     * 从Intent里面获取data。可以参照ChatActivity的onActivityResult方法的实现
     * @param title 弹窗显示的标题
     */
    public void startWithCamera(String title) {
        final CommonDialog dialog = new CommonDialog(activity);
        dialog.setTitle(title);
        dialog.addButton(R.string.common_image_take_photo, CommonDialog.COLOR_WHITE, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                start(PICK_TYPE_CAMERA);// 拍照
            }
        });
        dialog.show();
    }

	private static String outputFilePath() {
		if (cropOutputPath == null) {
			cropOutputPath = FileUtils.creatFileForTempImage(String.valueOf(System.currentTimeMillis()) + ".jpg");
		}
		return cropOutputPath;
	}

	private static String path = null;

	public static class ImagePickerActivity extends BaseActivity {

		private static final int GALLERY_REQUEST_CODE = 1;
		private static final int GALLERY_AND_CROP_REQUEST_CODE = 2;
		private static final int CAMERA_REQUEST_CODE = 3;
		private static final int CAMERA_AND_CROP_REQUEST_CODE = 4;
		private static final int CROPED_REQUEST_CODE = 5;

		private ProgressBar progressBar;

		@SuppressWarnings("deprecation")
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			RelativeLayout layout = new RelativeLayout(this);
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			progressBar = new ProgressBar(this);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.wait_bg_clockwise_indicator));
			RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(ScreenUtils.dip2px(40), ScreenUtils.dip2px(40));
			progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			progressBar.setLayoutParams(progressParams);
			layout.addView(progressBar);
			setContentView(layout);
            if (savedInstanceState != null) {
                path = savedInstanceState.getString("path");
                cropOutputPath = path;
                Log.e("ImagePickerActivity", "restore from savedInstanceState!!! path=" + path);
                return;
            }
			path = null;
			if (pickType == PICK_TYPE_GALLERY) {
//				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				if (needCrop) {
					startActivityForResult(intent, GALLERY_AND_CROP_REQUEST_CODE);
				} else {
					startActivityForResult(intent, GALLERY_REQUEST_CODE);
				}
			} else if (pickType == PICK_TYPE_CAMERA) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outputFilePath())));
				if (needCrop) {
					startActivityForResult(intent, CAMERA_AND_CROP_REQUEST_CODE);
				} else {
					startActivityForResult(intent, CAMERA_REQUEST_CODE);
				}
			}
		}

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (cropOutputPath != null)
                outState.putString("path", cropOutputPath);
        }

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				if (requestCode == CAMERA_REQUEST_CODE) {
                    if (false) {//要预览
                        reSizeImageByPath(outputFilePath(), false);
                    } else {//不需要预览
					    path = outputFilePath();
                        callback();
					    finish();
                    }
				} else if (requestCode == GALLERY_REQUEST_CODE) {
					String imgPath = getImgPath(data);
                    if (imgPath != null ) {
                        if (true) {//要预览
                            reSizeImageByPath(imgPath, false);
                        } else {//不需要预览
                            path = imgPath;
                            callback();
                            finish();
                        }
                    } else {
//	                    XiaoenaiUtils.showToast(R.string.common_image_pick_picture_failed);
                        callback();
                        finish();
                    }

				} else if (requestCode == GALLERY_AND_CROP_REQUEST_CODE) {
					String imgPath = getImgPath(data);
					if (imgPath != null) {
						reSizeImageByPath(imgPath, true);
					} else {
//						XiaoenaiUtils.showToast(R.string.common_image_pick_picture_failed);
                        callback();
                        finish();
					}

				} else if (requestCode == CAMERA_AND_CROP_REQUEST_CODE) {
					reSizeImageByPath(outputFilePath(), true);

				} else if (requestCode == CROPED_REQUEST_CODE) {
					File file = new File(outputFilePath());
					if (file.exists() && file.length() > 0) {
						path = cropOutputPath;
                        callback();
						finish();
					} else {
                        callback();
						finish();
					}

				} else {
                    callback();
					finish();

				}
			} else {
                callback();
				finish();
			}
		}

		public void reSizeImageByPath(final String imgPath, boolean needCrop) {
			Intent intent = new Intent(ImagePickerActivity.this, CropImage.class);
			intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
			intent.putExtra("scale", true);
			intent.putExtra("crop", true);
			intent.putExtra("aspectX", width);
			intent.putExtra("aspectY", height);
			intent.putExtra("outputX", width);
			intent.putExtra("outputY", height);
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("return-data", false);
			intent.putExtra("setWallpaper", false);
			intent.putExtra("image-path", imgPath);
			cropOutputPath = null;
			intent.putExtra("output", outputFilePath());
            intent.putExtra("needCrop", needCrop);
			startActivityForResult(intent, CROPED_REQUEST_CODE);
		}

        private void callback() {
            if (listener == null) {
                if (path != null) {
                    Intent intent = new Intent();
                    intent.setData(Uri.fromFile(new File(path)));
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED);
                }
            } else {
                if (path == null) {
                    listener.onCancel();
                } else {
                    listener.onPicked(path);
                }
            }
//            scale = false;
            needCrop = false;
            width = 0;
            height = 0;
            pickType = 0;
            listener = null;
        }

		@Override
		protected void onDestroy() {
//            callback();
			super.onDestroy();
		}

		/**
		 * 返回图片的路径，有可能获取不到而返回空值
		 * @param data
		 * @return
		 */
		private String getImgPath(Intent data) {
            String imgPath = null;
            if (data == null) return null;
            if (data.getData() != null) {
                Uri uri = data.getData();
                //这里的uri一般有两种情况：以content:///开头的和以file:///开头的
                LogUtils.d(getClass().getSimpleName(), "uri=" + uri.toString());
                imgPath = uri.getPath();
                if (imgPath == null) {
                    imgPath = uriToPath(uri);
                } else {
                    File file = new File(imgPath);
                    if (!file.exists()) {
                        imgPath = uriToPath(uri);
                    }
                }

			} else {
				Bundle extras = data.getExtras();
				if (extras != null) {
					// 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
					Bitmap image = extras.getParcelable("data");
					if (image != null) {
						imgPath = saveBitmap(image);
					}
				}
			}

            File file = new File(imgPath);
            if (file.exists()) {
			    return imgPath;
            } else {
                return null;
            }
		}

        private String uriToPath(Uri uri) {
            String imgPath = null;
            imgPath = getPathFromMediaStore(uri);
            if(imgPath == null){
                Bitmap image = getBitmapFromMediaStore(uri);
                if (image != null) {
                    imgPath = saveBitmap(image);
                }
            }
            return imgPath;
        }

        private String getPathFromMediaStore(Uri uri) {
            String imgPath = null;
            if (uri != null) {
                String[] images = { MediaStore.Images.Media.DATA };// 将图片URI转换成存储路径
                try{
                    Cursor cursor = getContentResolver().query(uri, images, null, null, null);
                    if(cursor!= null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        imgPath = cursor.getString(column_index);
                    }
                    if(cursor != null)
                        cursor.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return imgPath;
        }

        private Bitmap getBitmapFromMediaStore(Uri uri) {
            Bitmap image = null;
            try {
                // 这个方法是根据Uri获取Bitmap图片的静态方法
                image = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

		private String saveBitmap(Bitmap bitmap){
			BufferedOutputStream bos = null;
			String imgPath = outputFilePath();
			File picture = new File(imgPath);
			try {
				bos = new BufferedOutputStream(new FileOutputStream(picture));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				imgPath = null;
			}
			return imgPath;
		}
	}

}