package weixin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;

/**
 * Created by Administrator on 2017/4/15 0015.
 */

public class EmojiGridAdapter extends BaseAdapter {

    int Image[];
    Context context;
    EditText MessageText;

    public EmojiGridAdapter(int[] image, Context context, EditText messageText) {
        Image = image;
        this.context = context;
        MessageText = messageText;
    }

    @Override
    public int getCount() {
        return Image.length;
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
    public View getView(final int position, View converview, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (converview == null) {

            converview = LayoutInflater.from(context).inflate(R.layout.chat_emoji_gridlist, null);
            holder = new ViewHolder(converview);
            converview.setTag(holder);
        }else {
            holder = (ViewHolder) converview.getTag();
        }

        holder.emojiImage.setImageResource(Image[position]);

        holder.emojiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int index = MessageText.getSelectionStart();
                Editable text = MessageText.getText();
                SpannableString spannableString = TranslationEmoji(position, "[emoji" + position + "]");
                text.insert(index,spannableString);
                MessageText.setText(text);
                MessageText.setSelection(index+spannableString.length());


//                MessageText.setText(""+position);
               /* Drawable drawable = MessageText.getResources().getDrawable(Image[position]);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                //需要处理的文本，[smile]是需要被替代的文本
                SpannableString spannable = new SpannableString("[emoji]");
                //要让图片替代指定的文字就要用ImageSpan
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
                //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
                String str = "[emoji]";
                spannable.setSpan(span,MessageText.getText().length(),MessageText.getText().length()+str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                MessageText.setText(spannable);*/

            }
        });


        return converview;
    }

    public SpannableString TranslationEmoji(int position, String str){

        Drawable drawable = MessageText.getResources().getDrawable(Image[position]);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //需要处理的文本，[smile]是需要被替代的文本
        SpannableString spannable = new SpannableString(str);
        //要让图片替代指定的文字就要用ImageSpan
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
        spannable.setSpan(span,0,str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    class ViewHolder {
        @BindView(R.id.emoji_image)
        ImageView emojiImage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
