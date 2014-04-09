package org.szuwest.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.NumberFormat;

import org.szuwest.library.R;

/**
 * <p>A dialog showing a progress indicator and an optional text message or view.
 * Only a text message or a view can be used at the same time.</p>
 * <p>The dialog can be made cancelable on back key press.</p>
 * <p>The progress range is 0..10000.</p>
 */
//public class MyProgressDialog extends AlertDialog {
public class MyProgressDialog extends Dialog {
    
    /** Creates a ProgressDialog with a ciruclar, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;
    
    /** Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;
    
    private ProgressBar mProgress;
    private TextView mMessageView;
    
    private int mProgressStyle = STYLE_SPINNER;
    private TextView mProgressNumber;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;
    
    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;
    
    private boolean mHasStarted;
    private Handler mViewUpdateHandler;
    
    
    public MyProgressDialog(Context context) {
//        this(context, com.android.internal.R.style.Theme_Dialog_Alert);
        this(context, R.style.NewDialog, STYLE_SPINNER);
    }

    public MyProgressDialog(Context context, int theme, int pStyle) {
        super(context, theme);
        mProgressStyle = pStyle;
        
    }

    public static MyProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        return show(context, title, message, false);
    }

    public static MyProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static MyProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static MyProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener) {
    	MyProgressDialog dialog = new MyProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (mProgressStyle == STYLE_HORIZONTAL) {
            
            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    
                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();
                    double percent = (double) progress / (double) max;
                    String format = mProgressNumberFormat;
                    mProgressNumber.setText(String.format(format, progress, max));
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mProgressPercent.setText(tmp);
                }
            };
            View view = inflater.inflate(R.layout.dialog_progress_horizontal, null);
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
            mProgressNumberFormat = "%d/%d";
            mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
            mProgressPercentFormat = NumberFormat.getPercentInstance();
            mProgressPercentFormat.setMaximumFractionDigits(0);
            mMessageView = (TextView) view.findViewById(R.id.message);
            setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        } else {
            View view = inflater.inflate(R.layout.dialog_progress_stylelarge, null);
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mMessageView = (TextView) view.findViewById(R.id.message);
            setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
        onProgressChanged();
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    public void setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
    }

    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }
    
//    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
//                super.setMessage(message);
            	mMessageView.setText(message);
            } else {
            	mMessageView.setText(message);
            }
        } else {
            mMessage = message;
        }
    }
    
    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    /**
     * Change the format of Progress Number. The default is "current/max".
     * Should not be called during the number is progressing.
     * @param format Should contain two "%d". The first is used for current number
     * and the second is used for the maximum.
     * @hide
     */
    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
    }
    
    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            mViewUpdateHandler.sendEmptyMessage(0);
        }
    }
}
