package com.github.czy1121.numberpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class NumberPicker extends LinearLayout {

    private int mStep = 1, mValue = 0, mMaxValue = 0, mMinValue = 0;
    ImageView btnLeft, btnRight;
    EditText txtValue;
    OnValueChangedListener mOnValueChanged;

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(int step, int min, int max, int value) {

        if (min == max) {
            txtValue.setFocusable(false);
            txtValue.setFocusableInTouchMode(false);
            mMinValue = max;
            mMaxValue = max;
        } else {
            txtValue.setFocusable(true);
            txtValue.setFocusableInTouchMode(true);
            mMinValue = Math.min(min, max);
            mMaxValue = Math.max(min, max);
        }

        mStep = Math.max(step, 1);
        if (mStep != 1) {
            mMinValue = normalize(mMinValue);
            mMaxValue = normalize(mMaxValue);
        }

        mValue = Integer.MAX_VALUE;
        setValue(value, false);
    }


    private static int dp2px(DisplayMetrics dm, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NumberPicker);

        int buttonSize = dp2px(Resources.getSystem().getDisplayMetrics(), 35);

        try {
            buttonSize = (int) a.getDimension(R.styleable.NumberPicker_npButtonSize, buttonSize);
            mStep = a.getInt(R.styleable.NumberPicker_npStep, 1);
            mValue = a.getInt(R.styleable.NumberPicker_npValue, 0);
            mMinValue = a.getInt(R.styleable.NumberPicker_npMinValue, Integer.MIN_VALUE);
            mMaxValue = a.getInt(R.styleable.NumberPicker_npMaxValue, Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
        setOrientation(HORIZONTAL);
        setFocusableInTouchMode(true);

        btnLeft = new ImageView(context, null, R.attr.npStyleLeft);
        btnRight = new ImageView(context, null, R.attr.npStyleRight);
        txtValue = new EditText(context, null, R.attr.npStyleValue);
        txtValue.setFocusableInTouchMode(true);
        txtValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        txtValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtValue.setSelectAllOnFocus(true);

        init(mStep, mMinValue, mMaxValue, mValue);

        addView(btnLeft, new LayoutParams(buttonSize, buttonSize));
        addView(txtValue, new LayoutParams(buttonSize, buttonSize, 1));
        addView(btnRight, new LayoutParams(buttonSize, buttonSize));


        btnLeft.setOnClickListener(onClick);
        btnRight.setOnClickListener(onClick);
        txtValue.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mKeyboardObserver.register(v);
                } else {
                    syncValue();
                    mKeyboardObserver.unregister();
                }
            }
        });
        txtValue.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mKeyboardObserver.unregister();
            }
        });
        mKeyboardObserver.listen(new OnKeyboardVisibleListener() {
            @Override
            public void onVisibleChanged(boolean visible) {
                if (!visible) {
                    syncValue();
                }
            }
        });

    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChanged) {
        mOnValueChanged = onValueChanged;
    }

    private void syncValue() {
        setValue(editableValue(), true);
    }

    private int editableValue() {
        Editable value = txtValue.getText();
        return value.length() == 0 ? 0 : Integer.valueOf(value.toString());
    }

    public void notifyValueChanged() {
        if (mOnValueChanged != null) {
            mOnValueChanged.onValueChanged(this, mValue);
        }
    }
    public int getValue() {
        if (txtValue.hasFocus()) {
            txtValue.clearFocus();
        }
        return mValue;
    }

    public void setValue(int value) {
        setValue(value, true);
    }

    public void setValue(int value, boolean notifyValueChanged) {
        if (value == mValue) {
            return;
        }
        int valid = Math.min(Math.max(normalize(value), mMinValue), mMaxValue);
        txtValue.setText(String.valueOf(valid));
        if (valid == mValue) { // valid != value
            return;
        }
        mValue = valid;
        btnLeft.setEnabled(mValue != mMinValue);
        btnRight.setEnabled(mValue != mMaxValue);
        if (notifyValueChanged && mOnValueChanged != null) {
            mOnValueChanged.onValueChanged(this, mValue);
        }
    }

    private int normalize(int value) {
        return value - value % mStep;
    }

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int value = editableValue();
            setValue(btnLeft == v ? (value - mStep) : (value + mStep), true);
        }
    };


    KeyboardObserver mKeyboardObserver = new KeyboardObserver();

    private class KeyboardObserver implements ViewTreeObserver.OnGlobalLayoutListener {
        private View root;
        private OnKeyboardVisibleListener listener;

        public KeyboardObserver listen(OnKeyboardVisibleListener l) {
            listener = l;
            return this;
        }
        private boolean isKeyboardVisible(View root) {
            final int softKeyboardHeight = 100;
            Rect rect = new Rect();
            root.getWindowVisibleDisplayFrame(rect);
            DisplayMetrics dm = root.getResources().getDisplayMetrics();
            int diff = root.getBottom() - rect.bottom;
            return diff > softKeyboardHeight * dm.density;
        }
        @Override
        public void onGlobalLayout() {
            if (listener != null) {
                listener.onVisibleChanged(isKeyboardVisible(root));
            }
        }

        public void register(View v) {
            unregister();
            root = v.getRootView();
            root.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
        public void unregister() {
            if (root != null) {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                root = null;
            }
        }
    }

    interface OnKeyboardVisibleListener {
        void onVisibleChanged(boolean visible);
    }

    public interface OnValueChangedListener {
        void onValueChanged(NumberPicker view, int value);
    }
}


