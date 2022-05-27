package com.xy.baselib.widget.multiline.label;

import androidx.annotation.Nullable;

public class StringLabelMode  implements LabelEntry{
    private String str = "";

    public StringLabelMode(@Nullable String str) {
        this.str = str;
    }

    @Nullable
    @Override
    public String onLabel() {
        return str;
    }
}
