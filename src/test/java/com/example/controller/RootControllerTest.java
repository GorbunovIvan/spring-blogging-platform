package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class RootControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testIndex() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/posts"));
    }
}