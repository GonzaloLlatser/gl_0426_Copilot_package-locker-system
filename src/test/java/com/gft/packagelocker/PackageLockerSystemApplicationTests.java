package com.gft.packagelocker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PackageLockerSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Basic smoke test: verifies Spring context loads
    }

    @Test
    void shouldReturnLockers() throws Exception {
        // Lightweight integration test: verifies API endpoint responds
        mockMvc.perform(get("/api/lockers"))
                .andExpect(status().isOk());
    }
}