package locon.testingtime;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sombose on 30/07/15.
 */
public class OutgoingUIView extends View {
    private static final int PADDING_LEFT = 16;
    private static final int PADDING_RIGHT = 16;
    private static final int PADDING_TOP = 10;
    private static final int PADDING_BOTTOM = 6;
    private static final int PADDING_BETWEEN_TEXT_TIME = 8;
    private static final float FRACTION_BELOW_TEXT = 0.2f;

    private int fontsize;
    private int timeSize;
    private int fontcolor;
    private int timeColor;
    private int maxWidth;
    private boolean isDrawClock;
    private boolean isDrawSingleTick;
    private boolean isDrawDoubleTick;
    private boolean isDrawDoubleTickRead;
    private int textHeight;
    private int textWidth;
    private int timeHeight;
    private int timeWidth;
    private int layoutHeight;
    private int layoutWidth;
    private int lineCount;
    private int timeX;
    private int timeY;
    private TextPaint textPaint;
    private TextPaint timePaint;
    private float scale;
    private StaticLayout textLayout;
    private StaticLayout timeLayout;
    //    private String showString = "hey its a beautiful world hey its a beautiful world hey its a beautiful";
//    private String showString = "hi all";
    private String showString = "hey its a beautiful world a a a a a a a a a a a a";

    public OutgoingUIView(Context context) {
        super(context);
    }

    public OutgoingUIView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(attributeSet, context);
    }

    public OutgoingUIView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, context);
        setBackgroundResource(R.drawable.left_empty);

    }

    private void init(AttributeSet attributeSet, Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.OutgoingUIView, 0, 0);
        try {
            maxWidth = typedArray.getInteger(R.styleable.OutgoingUIView_maxWidth, 240);
            fontsize = typedArray.getInteger(R.styleable.OutgoingUIView_fontsize, 12);
            timeSize = typedArray.getInteger(R.styleable.OutgoingUIView_timeSize, 12);
            fontcolor = typedArray.getInteger(R.styleable.OutgoingUIView_fontcolor, 0);
            timeColor = typedArray.getInteger(R.styleable.OutgoingUIView_timeColor, 0);
            isDrawClock = true;
            isDrawSingleTick = isDrawDoubleTick = isDrawDoubleTickRead = false;
        } finally {
            typedArray.recycle();
        }
        scale = getContext().getResources().getDisplayMetrics().density;
        initPaints();
        initLayouts();
    }


    private void initPaints() {
        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(scale * fontsize + 0.5f);
        textPaint.setAntiAlias(true);

        timePaint = new TextPaint();
        timePaint.setColor(Color.GRAY);
        timePaint.setTextSize(scale * timeSize + 0.5f);
        timePaint.setAntiAlias(true);
    }

    private void initLayouts() {
        textLayout = new StaticLayout(showString, textPaint, dp(maxWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1, false);
        timeLayout = new StaticLayout("12:30", timePaint, dp(40), Layout.Alignment.ALIGN_NORMAL, 0, 1, false);

        float totalWidth = textPaint.measureText(showString); //without any bounds

        textWidth = totalWidth < textLayout.getWidth() ? (int) totalWidth : textLayout.getWidth();
        textHeight = textLayout.getHeight();

        timeWidth = timeLayout.getWidth();
        timeHeight = timeLayout.getHeight();

        layoutHeight = (int) (textHeight + FRACTION_BELOW_TEXT * timeHeight + dp(PADDING_BOTTOM + PADDING_TOP));

        int lastCharacterPosition = (int) Math.ceil(findTextLastX(showString, textPaint));

        if (lastCharacterPosition + dp(PADDING_BETWEEN_TEXT_TIME) + timeWidth > dp(maxWidth)) {
            timeY = (int) (textHeight);
            timeX = textWidth - timeWidth;
            layoutHeight += timeHeight;
            layoutWidth = textWidth + dp(PADDING_LEFT + PADDING_RIGHT);
        } else {
            layoutWidth = textWidth + dp(PADDING_LEFT + PADDING_RIGHT);
            timeY = (int) (textHeight - (1 - FRACTION_BELOW_TEXT) * timeHeight);
            if (textWidth < dp(maxWidth)) {
                timeX = textWidth + dp(PADDING_BETWEEN_TEXT_TIME);
                layoutWidth = textWidth + dp(PADDING_BETWEEN_TEXT_TIME) + timeWidth + dp(PADDING_RIGHT);
            } else
                timeX = textWidth - timeWidth;
        }

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    private float findTextLastX(String showString, TextPaint paint) {
        float position = 0.0f;
        String[] words = showString.split(" ");
        int line = 1;
        float currentSize = 0;
        for (String word : words) {
            currentSize = paint.measureText(word + " ");
            if (position + currentSize > dp(maxWidth))
                line++;
            position = (position + currentSize) >= dp(maxWidth) ? currentSize : (position + currentSize);
        }
        return position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(PADDING_LEFT, PADDING_TOP);
        textLayout.draw(canvas);
        canvas.translate(timeX, timeY);
        timeLayout.draw(canvas);
    }

    @Override
    protected void onMeasure(int width, int height) {
        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    private int dp(float value) {
        return (int) Math.ceil(value * scale);
    }

}
