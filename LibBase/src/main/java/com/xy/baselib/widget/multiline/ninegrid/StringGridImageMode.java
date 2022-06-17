package com.xy.baselib.widget.multiline.ninegrid;

import androidx.annotation.Nullable;

public class StringGridImageMode implements OnNineGridImageListener {

    private String fileString;

    public StringGridImageMode(String fileString) {
        this.fileString = fileString;
    }

    @Nullable
    @Override
    public String onMediaUrl() {
        return fileString;
    }
}
