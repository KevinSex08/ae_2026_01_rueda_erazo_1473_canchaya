package com.example.canchaYa.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `when no token provided then return 401 Unauthorized`() {
        mockMvc.perform(get("/api/v1/courts"))
            .andExpect(status().isUnauthorized)
    }
}
