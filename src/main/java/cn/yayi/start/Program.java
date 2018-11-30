package cn.yayi.start;

import cn.yayi.entity.User;
import cn.yayi.util.MapperReader;

/**
 * Created by Administrator on 2018/2/27 0027.
 */
public class Program {
    public static void main(String[] args) throws Exception {

        MapperReader mReader = new MapperReader();
        mReader.test(User.class);
    }




}

