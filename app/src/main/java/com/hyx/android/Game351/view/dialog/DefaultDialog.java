package com.hyx.android.Game351.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyx.android.Game351.R;

public class DefaultDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Context mContext;
    private Window window = null;
    private DialogSelectListener mListener;
    private Button btnOk;
    private Button btnCancle;
    private TextView description;
    private TextView dailogTitle;

    private String descriptionStr;
    private String btnOkStr;
    private String btnCancleStr;
    private String dialogTitleStr;

    private View childView;

    private View bottom_sep;

    private LinearLayout container;

    public DefaultDialog(Context context) {
        super(context, R.style.DialogTheme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_default);
        this.window = getWindow();
        Display localDisplay = this.window.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        // localLayoutParams.verticalMargin = -0.1F;
        localLayoutParams.height = LayoutParams.WRAP_CONTENT;
        localLayoutParams.width = LayoutParams.WRAP_CONTENT;
        // localLayoutParams.width = (int) (0.9D * localDisplay.getWidth());
        this.window.setAttributes(localLayoutParams);
        // this.window.setWindowAnimations(R.style.dialogWindowAnim);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initViews();
    }

    private void initViews() {
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancle = (Button) findViewById(R.id.btn_cancle);
        description = (TextView) findViewById(R.id.content);
        dailogTitle = (TextView) findViewById(R.id.dailogTitle);

        container = (LinearLayout) findViewById(R.id.container);

        bottom_sep = (View) findViewById(R.id.bottom_sep);

        if (mListener != null) {
            btnOk.setOnClickListener(this);
            btnCancle.setOnClickListener(this);
        }

        if (descriptionStr != null) {
            description.setText(descriptionStr);
        }

        if (btnOkStr != null) {
            btnOk.setText(btnOkStr);
        } else {
            bottom_sep.setVisibility(View.GONE);
            btnOk.setVisibility(View.GONE);
        }

        if (btnCancleStr != null) {
            btnCancle.setText(btnCancleStr);
        } else {
            bottom_sep.setVisibility(View.GONE);
            btnCancle.setVisibility(View.GONE);
        }

        if (dialogTitleStr != null) {
            dailogTitle.setVisibility(View.VISIBLE);
            dailogTitle.setText(dialogTitleStr);
        }

        if (childView != null) {
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            container.removeAllViews();
            container.addView(childView);
        }
    }

    public void setDialogListener(DialogSelectListener paramListener) {
        mListener = paramListener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onChlidViewClick(v);
        }
        DefaultDialog.this.cancel();
    }

    public void setDescription(String str) {
        descriptionStr = str;
    }

    public void setBtnOk(String str) {
        btnOkStr = str;
    }

    public void setBtnCancle(String str) {
        btnCancleStr = str;
    }

    public void setDialogTitle(String title) {
        dialogTitleStr = title;
    }


    public void AddView(View view) {
        childView = view;
    }
}
