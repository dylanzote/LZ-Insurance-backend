package com.zote.user.service.infrastructure;

import com.zote.common.utils.utils.JsonUtil;
import com.zote.user.service.api.request.AuthRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class KeyCloakUserServiceInfrastructureApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void loginUser() {
        var authRequest = new AuthRequest("Cedric2024", "Cedric@556677");
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(authRequest)))
                .andExpect(status().isOk());
    }

}
