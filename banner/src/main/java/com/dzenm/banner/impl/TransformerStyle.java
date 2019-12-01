package com.dzenm.banner.impl;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-09-06 15:16
 */
@IntDef({TransformerStyle.STYLE_NONE, TransformerStyle.STYLE_FILM, TransformerStyle.STYLE_COVER,
        TransformerStyle.STYLE_FOLD, TransformerStyle.STYLE_DIY})
@Retention(RetentionPolicy.SOURCE)
public @interface TransformerStyle {
    int STYLE_NONE = 1;
    int STYLE_FILM = 2;
    int STYLE_COVER = 3;
    int STYLE_FOLD = 4;
    int STYLE_DIY = 10;
}
