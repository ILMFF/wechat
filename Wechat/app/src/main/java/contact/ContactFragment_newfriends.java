package contact;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import Xmpp.GetALLFriend;
import Xmpp.XmppTool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import contact.adpter.NewFriendRecycleAdapter;
import contact.bean.RequestBean;
import teacher.davisstore.com.wechat.R;
import title.SearchFriend;
import title.bean.SearchSession;

import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.requestlist;

/**
 * Created by Administrator on 2017/3/24.
 */

public class ContactFragment_newfriends extends Activity {


    @BindView(R.id.contact_newfriend_requestlist)
    RecyclerView contactNewfriendRequestlist;
    public static NewFriendRecycleAdapter newFriendRecycleRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contactfragment_newfriends);
        ButterKnife.bind(this);

        contactNewfriendRequestlist.setLayoutManager(new LinearLayoutManager(ContactFragment_newfriends.this));
        newFriendRecycleRequestAdapter = new NewFriendRecycleAdapter(requestlist);
        contactNewfriendRequestlist.setAdapter(newFriendRecycleRequestAdapter);

    }

    @OnClick({R.id.contact_newfriend_back_btn, R.id.contact_newfriend_add_btn, R.id.contact_newfriend_searchlayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_newfriend_back_btn:
                finish();
                break;
            case R.id.contact_newfriend_add_btn:
                Intent intent = new Intent(ContactFragment_newfriends.this, SearchFriend.class);
                startActivity(intent);
                break;
            case R.id.contact_newfriend_searchlayout:
                break;
        }
    }



}
