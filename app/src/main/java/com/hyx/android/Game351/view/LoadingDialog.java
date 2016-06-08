package com.hyx.android.Game351.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.hyx.android.Game351.R;

public class LoadingDialog extends Dialog {
    private ImageView iv_route;

    public LoadingDialog(Context context) {
        super(context, R.style.Dialog_bocop);
        init();
    }

    private void init() {

        View contentView = View.inflate(getContext(), R.layout.activity_custom_loding_dialog_layout, null);
        setContentView(contentView);
        iv_route = (ImageView) contentView.findViewById(R.id.imageLoading);
        getWindow().setWindowAnimations(R.anim.alpha_in);
    }


    @Override
    public void show() {
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_route.getBackground();
        animationDrawable.start();
        super.show();
    }
}
