/**
 *rohan magare, 001231457, magare.r@husky.neu.edu
 *ritesh gupta, 001280361, gupta.rite@husky.neu.edu
 *pratiksha shetty, 00121643697, shetty.pr@husky.neu.edu
 *yogita jain, 001643815, jain.yo@husky.neu.edu
 **/
package com.csye6225.demo.rest_assured;
import io.restassured.RestAssured;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles()
public class RestApiTest {

    @Test
    public void Demo_test() {
        assertTrue(true);

    }

}
