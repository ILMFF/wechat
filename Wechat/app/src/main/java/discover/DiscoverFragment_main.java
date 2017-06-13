package discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

/**
 * Created by Administrator on 2017/2/11.
 */

public class DiscoverFragment_main extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.discoverfragment_main, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @OnClick(R.id.discover_myfriendCircle)
    public void onClick() {

        Intent intent = new Intent(getActivity(),DiscoverFragment_FriendCirCle.class);
        startActivity(intent);
    }
}
