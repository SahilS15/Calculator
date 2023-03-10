package com.james.calculator;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.TypedValue;

import net.objecthunter.exp4j.ExpressionBuilder;

public class CustomEditText extends AppCompatEditText {
    private Snackbar snackbar;

    protected boolean invalid;
    private float minTextSize;
    private float maxTextSize;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        maxTextSize = this.getTextSize();
        if (maxTextSize < 25) {
            maxTextSize = 30;
        }
        minTextSize = 20;
    }

    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            int availableWidth = textWidth - getPaddingLeft() - getPaddingRight();
            float trySize = maxTextSize;

            setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            while (trySize > minTextSize && getPaint().measureText(text) > availableWidth) {
                trySize -= 1;
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());

        if (snackbar == null) {
            try {
                snackbar = Snackbar.make(this, "Invalid Equation", Snackbar.LENGTH_INDEFINITE);
            } catch (Exception ignored) {
            }
        }

        invalid = false;

        if (snackbar == null) return;

        if (getText().length() < 1) {
            if (!snackbar.isShown()) {
                snackbar.show();
            }
            return;
        }

        try {
            new ExpressionBuilder(getText().toString()).build().evaluate();
        } catch (Exception e) {
            e.printStackTrace();
            if (!snackbar.isShown()) {
                snackbar.show();
            }
            return;
        }

        if (snackbar.isShown()) {
            snackbar.dismiss();

            try {
                snackbar = Snackbar.make(CustomEditText.this, "Invalid Equation", Snackbar.LENGTH_INDEFINITE);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        refitText(this.getText().toString(), parentWidth);
    }
}
