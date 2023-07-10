package xy.xy.base.widget.bar.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import xy.xy.base.R;
import xy.xy.base.utils.exp.ExpContextKt;

public class ProgressBuild {
    private final Context context;
    private final View view;

    private int progress = 0;
    private int mBackgroundColor = -0x1a1a16;
    private int progressColor = -0xc9ac01;
    private int progressTvColor = -0x1;
    private int progressTvSize = 48;
    private float stokeWidth = 0;
    private boolean progressReal = false;
    private boolean showTv = true;

    public ProgressBuild(View view, AttributeSet attributeSet) {
        context = view.getContext();
        this.view = view;
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ProgressView);
            mBackgroundColor = typedArray.getColor(R.styleable.ProgressView_progress_background_color, mBackgroundColor);
            progressColor = typedArray.getColor(R.styleable.ProgressView_progress_progress_color, progressColor);
            progressTvColor = typedArray.getColor(R.styleable.ProgressView_progress_progress_tv_color, progressTvColor);
            progressTvSize = typedArray.getDimensionPixelSize(R.styleable.ProgressView_progress_progress_tv_size, progressTvSize);
            progress = typedArray.getInt(R.styleable.ProgressView_progress_progress_number, 0);
            progressReal = typedArray.getBoolean(R.styleable.ProgressView_progress_real, false);
            showTv = typedArray.getBoolean(R.styleable.ProgressView_progress_show_tv, true);
            stokeWidth = typedArray.getDimension(R.styleable.ProgressView_progress_progress_circle_stoke_width,  ExpContextKt.getResDimension(context,R.dimen.dp_5));
            typedArray.recycle();
        }
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        view.invalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        view.invalidate();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        view.invalidate();
    }

    public int getProgressTvColor() {
        return progressTvColor;
    }

    public void setProgressTvColor(int progressTvColor) {
        this.progressTvColor = progressTvColor;
        view.invalidate();
    }

    public int getProgressTvSize() {
        return progressTvSize;
    }

    public void setProgressTvSize(int progressTvSize) {
        this.progressTvSize = progressTvSize;
        view.invalidate();
    }

    public boolean isProgressReal() {
        return progressReal;
    }

    public void setProgressReal(boolean progressReal) {
        this.progressReal = progressReal;
        view.invalidate();
    }

    public boolean isShowTv() {
        return showTv;
    }

    public void setShowTv(boolean showTv) {
        this.showTv = showTv;
        view.invalidate();
    }

    public float getStokeWidth() {
        return stokeWidth;
    }

    public void setStokeWidth(float stokeWidth) {
        this.stokeWidth = stokeWidth;
        view.invalidate();
    }
}
