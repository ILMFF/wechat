package title.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;

/**
 * Created by Administrator on 2017/2/11.
 */
public class PopmenuAdapter extends BaseAdapter {

    String[] text;
    int[] img;
    Context context;

    public PopmenuAdapter(String[] text, int[] img, Context context) {
        this.text = text;
        this.img = img;
        this.context = context;
    }


    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hold = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.title_popmenu_list, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);


        } else {
            hold = (ViewHolder) convertView.getTag();
        }

        hold.PopmenuImg.setImageResource(img[position]);
        hold.PopmenuText.setText(text[position]);


        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.Popmenu_img)
        ImageView PopmenuImg;
        @BindView(R.id.Popmenu_text)
        TextView PopmenuText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}