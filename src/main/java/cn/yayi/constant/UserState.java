package cn.yayi.constant;

import cn.yayi.annotation.Text;

public interface UserState {

    @Text("正常")
    Integer NORMAL=1;
    @Text("锁定")
    Integer LOCKED=2;
}
