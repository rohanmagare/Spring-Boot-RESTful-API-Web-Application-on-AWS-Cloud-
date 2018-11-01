/**
 * rohan magare, 001231457, magare.r@husky.neu.edu
 * ritesh gupta, 001280361, gupta.rite@husky.neu.edu
 * pratiksha shetty, 00121643697, shetty.pr@husky.neu.edu
 * yogita jain, 001643815, jain.yo@husky.neu.edu
 **/
package com.csye6225.demo.rest_assured;

import com.csye6225.demo.dao.UserDao;
import com.csye6225.demo.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRegisterTest {

    @Mock
    UserDao userDao;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        User u = new User();
        u.setEmailId("Unittest");
        u.setPassword("UnitTest111");
        Mockito.when(userDao.findUserByEmailId("Unittest")).thenReturn(u);
    }

    @Test
    public void testUserregister() throws Exception {

        User u = userDao.findUserByEmailId("Unittest");
        assertEquals(u.getEmailId(), "Unittest");
    }
}