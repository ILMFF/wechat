package title;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

/**
 * Created by Administrator on 2017/3/21.
 */

public class AddNewFriend extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_newfriend);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.addnewfriend_back_btn, R.id.search_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addnewfriend_back_btn:
                finish();

                break;
            case R.id.search_layout:

                Intent intent = new Intent(AddNewFriend.this,SearchFriend.class);
                startActivity(intent);

                break;
        }
    }
}
