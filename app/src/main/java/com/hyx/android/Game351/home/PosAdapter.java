package com.hyx.android.Game351.home;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.modle.PositionBean;
import com.hyx.android.Game351.view.AutoWrapAdapter;

/**
 * Created by soli on 6/14/16.
 */
public class PosAdapter extends AutoWrapAdapter<PositionBean> {

    private boolean isWrold = false;

    public PosAdapter(Context context) {
        super(context);
    }

    public void setIsWorld(boolean misWorld) {
        isWrold = misWorld;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(ctx, R.layout.position_item, parent);
        ViewHolder holder = new ViewHolder(convertView);

        PositionBean bean = getItem(position);
        holder.title.setText(bean.getStr());
        holder.title.setVisibility(bean.isShow() ? View.VISIBLE : View.INVISIBLE);

        if (isWrold) {
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getFontSize() + 20);
            int _10dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, ctx.getResources().getDisplayMetrics());
            holder.title.setPadding(holder.title.getPaddingLeft() + _10dp, holder.title.getPaddingTop(), holder.title.getPaddingRight() + _10dp, holder.title.getPaddingBottom());
        } else {
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getFontSize());
        }

        return convertView;
    }


    private class ViewHolder {
        private TextView title;

        public ViewHolder(View view) {

            title = (TextView) view.findViewById(R.id.title);
        }
    }
}
