package Cuenta;

import com.fasterxml.jackson.databind.ObjectMapper;
import cuenta.dtos.DtoRegistro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class Controlador {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registroValido() throws Exception {
        DtoRegistro registro = new DtoRegistro("pepe", "pepegomez@gmail.com", "154568", "12345", "ADMIN");
        ResultActions ejecucion = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registro)));
        ejecucion.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(result -> {
            String responseContent = result.getResponse().getContentAsString();
            assertTrue(responseContent.contains("token"), "Response should contain a token");});
    }

    @Test
    void registroInvalidoIncompleto() throws Exception {
        DtoRegistro registro = new DtoRegistro("pepe", "pepegomez@gmail.com", "152626", "789123", "");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andReturn();
        int statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    }

    @Test
    void registroInvalidoExistente() throws Exception {
        // registro con x email
        DtoRegistro registro1 = new DtoRegistro("pepe", "pepegomez@gmail.com", "154568", "123456", "ADMIN");
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registro1)))
            .andExpect(MockMvcResultMatchers.status().isOk());
        // otro con mismo email
        DtoRegistro registro2 = new DtoRegistro("pepe2", "pepegomez@gmail.com", "154826", "456789", "USER");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registro2)))
            .andReturn();
        int statusCode = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
    }
}
