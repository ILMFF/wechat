package title.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.Invitation_list;
import static Xmpp.AppTemp.con;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHold> {

    List<String> list;
    Context context;
    VCard card = new VCard();

    public InvitationAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public InvitationViewHold onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.chattogether_invitation_list, null);
        InvitationViewHold hold = new InvitationViewHold(inflate);

        return hold;
    }

    @Override
    public void onBindViewHolder(final InvitationViewHold holder, final int position) {

        try {
            card.load(con,list.get(position)+"@"+con.getServiceName());
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        Bitmap bitmap =  getICon(card);

        holder.invitationIcon.setImageBitmap(bitmap);
        holder.invitationNickname.setText(card.getNickName());

        holder.invitationSelectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.invitationSelectbtn.isChecked()) {
                    Toast.makeText(context, "选中了", Toast.LENGTH_SHORT).show();
                    Invitation_list.add(list.get(position));
                }
                else {
                    Toast.makeText(context, "取消了", Toast.LENGTH_SHORT).show();
                    Invitation_list.remove(list.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Bitmap getICon(VCard card) {


        byte[] avatar = card.getAvatar();

        if (BitmapFactory.decodeByteArray(avatar,0,avatar.length) != null){
            return BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        }else {

            return null;
        }
    }

    class InvitationViewHold extends RecyclerView.ViewHolder {
        @BindView(R.id.invitation_icon)
        ImageView invitationIcon;
        @BindView(R.id.invitation_nickname)
        TextView invitationNickname;
        @BindView(R.id.invitation_selectbtn)
        CheckBox invitationSelectbtn;

        public InvitationViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
