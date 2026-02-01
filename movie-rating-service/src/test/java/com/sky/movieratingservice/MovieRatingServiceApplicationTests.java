package com.sky.movieratingservice;

import com.sky.movieratingservice.support.PostgresContainerBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieRatingServiceApplicationTests extends PostgresContainerBase {

    @Test
    void contextLoads() {
    }

}
