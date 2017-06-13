package title;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Xmpp.XmppTool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;
import title.adapter.SearchRecycleAdapter;
import title.bean.SearchSession;

import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;

/**
 * Created by Administrator on 2017/3/21.
 */

public class SearchFriend extends Activity {


    @BindView(R.id.friend_name)
    EditText friendName;
    @BindView(R.id.searchfirend_list)
    RecyclerView searchfirendList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_friend);
        ButterKnife.bind(this);
        friendName.setFocusable(true);
        List<SearchSession> list = new ArrayList<>();
        searchfirendList.setAdapter(new SearchRecycleAdapter(list, SearchFriend.this));




    }


    @OnClick({R.id.searchfriend_back_btn, R.id.search_friend_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchfriend_back_btn:
                finish();
                break;

            case R.id.search_friend_btn:  //搜索按钮

                try{
                    List<SearchSession> list = new ArrayList<>();
//                    ProviderManager.getInstance().addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
                    String search_name = friendName.getText().toString();
//                    new ServiceDiscoveryManager(con);
                    if (search_name.equals(MyUserName) == false) { //不能搜索自己

                        if (search_name.length() >= 10) {
                            UserSearchManager search = new UserSearchManager(con);
                            //此处一定要加上 search.
                            Form searchForm = search.getSearchForm("search." + con.getServiceName());
                            Form answerForm = searchForm.createAnswerForm();

                            answerForm.setAnswer("Username", true);
                            answerForm.setAnswer("search", search_name);
                            ReportedData data = search.getSearchResults(answerForm, "search." + con.getServiceName());
                            Iterator<ReportedData.Row> it = data.getRows();
                            ReportedData.Row row = null;

                            while (it.hasNext()) {
                                row = it.next();
                                ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
                                VCard card = new VCard();
                                String username = row.getValues("Username").next().toString();

                                card.load(XmppTool.getConnection(), username + "@" + con.getServiceName());
                                String nickName = card.getNickName();
                                byte[] avatar = card.getAvatar();

                                Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                                String description = card.getLastName();
                                if (description == null){
                                    description = "未填写";
                                }

                                list.add(new SearchSession(username, nickName, bitmap, "个性签名：" +description ));

                            }
                            searchfirendList.setLayoutManager(new LinearLayoutManager(SearchFriend.this));
                            searchfirendList.setAdapter(new SearchRecycleAdapter(list, SearchFriend.this));
                        } else {

                            Toast.makeText(SearchFriend.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();

                        }
                    }else {

                        Toast.makeText(SearchFriend.this, "无法搜索用户自己", Toast.LENGTH_SHORT).show();
                    }

                }catch(Exception e){

                }
                break;
        }
    }
}
