package com.cjs.cjsbridge_ui.head;

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

import com.cjs.cjsbridge_ui.R;


/**
 * 自定义标题栏
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/17 0017 14:33
 */
public class CJSHeadBar extends FrameLayout {

    private FrameLayout head_back, head_right;
    private TextView head_title;
    private View divider;

    private String title;
    private boolean showBottomDivider;
    private boolean showRightIcon;

    private OnBackClickListener onBackClickListener;
    private OnRightClickListener onRightClickListener;

    private Context context;

    /**
     * 设置返回点击监听器
     *
     * @param onBackClickListener
     */
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

    /**
     * 设置标题栏右侧按钮点击监听器
     *
     * @author JasonChen
     * @email chenjunsen@outlook.com
     * @createTime 2020/8/17 0017 14:50
     */
    public void setOnRightClickListener(final OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
        if (head_right != null) {
            head_right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRightClickListener != null) {
                        onRightClickListener.onRightClick(v);
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
        LayoutInflater.from(context).inflate(R.layout.layout_head_bar, this, true);
        head_back = findViewById(R.id.back);
        head_title = findViewById(R.id.tv_title);
        head_right = findViewById(R.id.right);
        divider = findViewById(R.id.divider);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CJSHeadBar);
            title = array.getString(R.styleable.CJSHeadBar_title);
            showBottomDivider = array.getBoolean(R.styleable.CJSHeadBar_showBottomDivider, true);
            showRightIcon = array.getBoolean(R.styleable.CJSHeadBar_showRightIcon, true);
            array.recycle();
        }
        setTitle(title);
        setOnBackClickListener(onBackClickListener);
        setOnRightClickListener(onRightClickListener);
        setShowRightIcon(showRightIcon);
        if (getBackground() == null) {
            setBackgroundColor(Color.WHITE);
        }
    }


    public String getTitle() {
        return title;
    }

    /**
     * 设置标题文字
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        if (head_title != null) {
            head_title.setText(title);
        }
    }

    public boolean isShowBottomDivider() {
        return showBottomDivider;
    }

    /**
     * 设置是否显示底部分割线
     *
     * @param showBottomDivider
     */
    public void setShowBottomDivider(boolean showBottomDivider) {
        this.showBottomDivider = showBottomDivider;
        if (divider != null) {
            divider.setVisibility(showBottomDivider ? View.VISIBLE : View.GONE);
        }
    }

    public void setShowRightIcon(boolean showRightIcon) {
        this.showRightIcon = showRightIcon;
        if (head_right != null) {
            head_right.setVisibility(showRightIcon ? View.VISIBLE : View.GONE);
        }
    }


    /**
     * 返回监听器
     *
     * @author JasonChen
     * @email chenjunsen@outlook.com
     * @createTime 2020/8/17 0017 14:32
     */
    public interface OnBackClickListener {
        /**
         * 返回键点击
         *
         * @param v
         */
        void onBackClick(View v);
    }

    /**
     * 右键点击监听
     *
     * @author JasonChen
     * @email chenjunsen@outlook.com
     * @createTime 2020/8/17 0017 14:48
     */
    public interface OnRightClickListener {
        /**
         * 右键点击
         *
         * @param v
         */
        void onRightClick(View v);
    }
}
