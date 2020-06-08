package com.example.myapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.myapplication.R;


/**
 * ComposTextView
 */
public class ComposTextView extends AppCompatTextView {

    private boolean showOneLine;
    private float textSize;
    private TextPaint paint;
    private int textColor;
    private Context mContext;

    public ComposTextView(Context context) {
        this(context, null);
    }

    public ComposTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComposTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ComposTextView);
        showOneLine = typedArray.getBoolean(R.styleable.ComposTextView_composOneLine, false);
        textSize = typedArray.getDimension(R.styleable.ComposTextView_composTextSize, sp2px(20));
        textColor = typedArray.getColor(R.styleable.ComposTextView_composTextColor, Color.parseColor("#424D5C"));
        initPain();
        typedArray.recycle();
    }

    private void initPain() {
        paint = getPaint();
        paint.setColor(textColor);
        paint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CharSequence content = getText();
        if (!(content instanceof String)) {
            super.onDraw(canvas);
            return;
        }
        String text = (String) content;
        Layout layout = getLayout();

        for (int i = 0; i < layout.getLineCount(); ++i) {
            int lineBaseline = layout.getLineBaseline(i) + getPaddingTop();
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            if (showOneLine && layout.getLineCount() == 1) {
                //只有一行并且左右对齐
                String line = text.substring(lineStart, lineEnd);
                float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
                this.drawScaledText(canvas, line, lineBaseline, width);
            } else if (i == layout.getLineCount() - 1) {
                //最后一行或者第一行，默认画法不处理
                canvas.drawText(text.substring(lineStart), getPaddingLeft(), lineBaseline, paint);
                break;
            } else {
                //中间行
                String line = text.substring(lineStart, lineEnd);
                float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
                this.drawScaledText(canvas, line, lineBaseline, width);
            }
        }

    }

    /**
     * 计算从新绘制每行文本内容
     * StaticLayout处理英文断开换行，有几个好用的方法在代码也体现出来了
     * 1. etLineBaseline可以直接获取到各行的baseline，baseline就是每行的基准线，该行文字就是依据该baseline进行绘制
     * 3. getLineStart，getLineEnd获取每行起始结束的角标
     * 4. getDesiredWidth获取每行的宽度
     */

    private void drawScaledText(Canvas canvas, String line, float baseLineY, float lineWidth) {
        if (line.length() < 1) {
            return;
        }
        float x = getPaddingLeft();
        boolean forceNextLine = line.charAt(line.length() - 1) == 10;
        int length = line.length();
        if (forceNextLine || length == 0) {
            canvas.drawText(line, x, baseLineY, paint);
            return;
        }

        //计算字符之间的宽度
        float d = (getMeasuredWidth() - lineWidth - getPaddingLeft() - getPaddingRight()) / length;

        //重绘所有字符
        for (int i = 0; i < line.length(); ++i) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, this.paint);
            canvas.drawText(c, x, baseLineY, this.paint);
            x += cw + d;
        }
    }

    protected int sp2px(float sp) {
        final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}