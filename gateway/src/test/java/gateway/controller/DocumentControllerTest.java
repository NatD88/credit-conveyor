package gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gateway.service.FeignServiceDealMs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private MockMvc mockMvc;

    @MockBean
    FeignServiceDealMs feignServiceDealMs;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void createDocuments() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/document/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(10L));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

    }

    @Test
    void signRequestDocuments() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/document/15/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(10L));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void approveDocuments() throws Exception {
        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        post("/document/15/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(10L));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }
}