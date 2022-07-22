package gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gateway.dto.ClientApplication;
import gateway.service.FeignServiceDealMs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
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
    void getAppById() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        Mockito.when(feignServiceDealMs.getClientAppById(Mockito.any())).thenReturn(clientApplication);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        get("/admin/application/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(15L));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(clientApplication)));
    }

    @Test
    void getAllApps() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        List<ClientApplication> list = new ArrayList<>();
        list.add(clientApplication);
        Mockito.when(feignServiceDealMs.getAllApps()).thenReturn(list);

        MockHttpServletRequestBuilder mockRequest =
                MockMvcRequestBuilders.
                        get("/admin/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }
}