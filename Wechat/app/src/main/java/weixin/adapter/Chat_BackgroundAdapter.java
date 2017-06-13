package weixin.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.chat_background;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class Chat_BackgroundAdapter extends BaseAdapter {

    int Image[];
    Context context;
    int select_position = 0;


    public Chat_BackgroundAdapter(Context context, int[] image) {
        this.context = context;
        Image = image;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        BackGroundViewHold hold = null;
        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.chat_background_gridlist, null);
            hold = new BackGroundViewHold(view);
            view.setTag(hold);
        } else {

            hold = (BackGroundViewHold) view.getTag();
        }

        hold.backgroundPicture.setBackgroundResource(Image[i]);

        if (select_position == i){

            hold.backgroundPictureSelected.setVisibility(View.VISIBLE);

        }else {

            hold.backgroundPictureSelected.setVisibility(View.GONE);
        }


//        final BackGroundViewHold finalHold = hold;
        final BackGroundViewHold finalHold = hold;
        hold.backgroundPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                select_position = i;

                Drawable background = finalHold.backgroundPicture.getBackground();
                BitmapDrawable bd = (BitmapDrawable) background;
                chat_background  = bd.getBitmap();


//                finalHold.backgroundPictureSelected.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });


        return view;
    }

    class BackGroundViewHold {

        @BindView(R.id.background_picture)
        ImageView backgroundPicture;
        @BindView(R.id.background_picture_selected)
        ImageView backgroundPictureSelected;

        BackGroundViewHold(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
