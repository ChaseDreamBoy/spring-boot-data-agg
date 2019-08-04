package com.xh.service.impl;

import com.xh.entity.User;
import com.xh.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xiaohe
 * @version V1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserService userServiceImpl;

    @Test
    public void testAgg() throws Exception {
        Long userId = 1L;
        User user1 = userServiceImpl.getUserBySerial(userId);
        User user2 = userServiceImpl.getUserByAsyncThread(userId);
        User user3 = userServiceImpl.getUserByParallel(userId);

    }

}
