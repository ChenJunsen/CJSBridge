package com.cjs.widgets.cjsheadbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CJSHeadBar extends FrameLayout {

    private FrameLayout head_back;
    private TextView head_title;

    private String title;

    private OnBackClickListener onBackClickListener;

    private Context context;

    public void setOnBackClickListener(final OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
        if (head_back != null) {
            head_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBackClickListener != null) {
                        onBackClickListener.onBackClick(v);
                    } else {
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                }
            });
        }
    }

    public CJSHeadBar(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CJSHeadBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CJSHeadBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CJSHeadBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        this.context = context;
        View headBarView = LayoutInflater.from(context).inflate(R.layout.layout_head_bar, this, true);
        head_back = findViewById(R.id.back);
        head_title = findViewById(R.id.tv_title);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CJSHeadBar);
            title = array.getString(R.styleable.CJSHeadBar_title);
            array.recycle();
        }
        setTitle(title);
        setOnBackClickListener(onBackClickListener);
        if (getBackground() == null) {
            setBackgroundColor(Color.WHITE);
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (head_title != null) {
            head_title.setText(title);
        }
    }

    public interface OnBackClickListener {
        void onBackClick(View v);
    }
}
