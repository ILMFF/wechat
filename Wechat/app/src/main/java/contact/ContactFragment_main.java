package contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import Xmpp.GetALLFriend;
import Xmpp.GetRealName;
import Xmpp.RecycleviewDistance;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import contact.adpter.UserFriendRecycleAdapter;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.ContactPersonList;
import static Xmpp.AppTemp.Invitation_list;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.newFriendRecycleAdapter;
import static Xmpp.XmppTool.configure;
import static Xmpp.XmppTool.openConnection;
import static org.jivesoftware.smackx.filetransfer.FileTransfer.Error.connection;
import static teacher.davisstore.com.wechat.Login_main.password;

/**
 * Created by Administrator on 2017/2/11.
 */

public class ContactFragment_main extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.contact_friendlist)
    RecyclerView contactFriendlist;
    int spacingInPixels = 8;
    @BindView(R.id.contact_refresh)
    SwipeRefreshLayout contactRefresh;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.contactfragment_main, null);
        ButterKnife.bind(this, inflate);

     /*   ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
        ContactPersonList = new ArrayList<>();
        newFriendRecycleAdapter = new UserFriendRecycleAdapter(getActivity());
        //获取联系人
        List<RosterEntry> allFriends = GetALLFriend.getAllFriends();
        Iterator<RosterEntry> iterator = allFriends.iterator();
        while (iterator.hasNext()) {

            ContactPersonList.add(GetRealName.getRealName(iterator.next().getUser()));

        }*/

        contactFriendlist.addItemDecoration(new RecycleviewDistance(spacingInPixels));
        contactFriendlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactFriendlist.setAdapter(newFriendRecycleAdapter);

        contactRefresh.setOnRefreshListener(this);

//        Log.e("你好","你好"); //每次切换回来都会重新运行调用

        return inflate;
    }


    @OnClick({R.id.contact_newfriend, R.id.contact_chattogether})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_newfriend:
                Intent newfriend = new Intent(getActivity(), ContactFragment_newfriends.class);
                startActivity(newfriend);
                break;
            case R.id.contact_chattogether:
                Intent chattogether = new Intent(getActivity(), ContactFragment_chattogether.class);
                startActivity(chattogether);
                break;
        }
    }




    @Override
    public void onRefresh() {

        newFriendRecycleAdapter.notifyDataSetChanged();
        contactRefresh.setRefreshing(false);
    }


    public static class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            newFriendRecycleAdapter.notifyDataSetChanged();
        }
    }


}
