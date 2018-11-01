/**
 *rohan magare, 001231457, magare.r@husky.neu.edu
 *ritesh gupta, 001280361, gupta.rite@husky.neu.edu
 *pratiksha shetty, 00121643697, shetty.pr@husky.neu.edu
 *yogita jain, 001643815, jain.yo@husky.neu.edu
 **/
package com.csye6225.demo.rest_assured;

import com.csye6225.demo.dao.UserDao;
import com.csye6225.demo.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class Demo {


    @Test
    public void Demo_test() {
        assertTrue(true);

    }

}
