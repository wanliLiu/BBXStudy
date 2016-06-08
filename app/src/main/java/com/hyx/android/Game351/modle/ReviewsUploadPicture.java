package com.hyx.android.Game351.modle;

import java.io.Serializable;

public class ReviewsUploadPicture implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6186911390546235242L;

    // 图片的地址
    public String picureFilePath = "";
    // 是否可以添加图片
    public boolean isAddPicure = false;

    public ReviewsUploadPicture(boolean isAdd, String picture) {
        picureFilePath = picture;
        isAddPicure = isAdd;
    }
}
