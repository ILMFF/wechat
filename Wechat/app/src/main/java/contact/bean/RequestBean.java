package contact.bean;

import title.bean.SearchSession;

/**
 * Created by Administrator on 2017/3/24.
 */

public class RequestBean {

    public String type;
    public SearchSession searchSession;

    public RequestBean(String type, SearchSession searchSession) {
        this.type = type;
        this.searchSession = searchSession;
    }
}
