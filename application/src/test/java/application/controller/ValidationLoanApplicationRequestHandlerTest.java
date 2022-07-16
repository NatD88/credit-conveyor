package application.controller;

import application.dto.LoanApplicationRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@SpringBootTest
class ValidationLoanApplicationRequestHandlerTest {

    @MockBean
    private HttpHeaders headers;
    @MockBean
    private HttpStatus status;
    @MockBean
    private WebRequest request;
    @MockBean
    private MethodArgumentNotValidException ex;

    @Autowired
    ValidationLoanApplicationRequestHandler validationLoanApplicationRequestHandler;

    @MockBean
    LoanApplicationRequestDTO loanApplicationRequestDTO;

    @Test
    void handleMethodArgumentNotValid() {
        BeanPropertyBindingResult beanPropertyBindingResult = Mockito.spy(new  BeanPropertyBindingResult(loanApplicationRequestDTO,"loanApplicationRequestDTO"));
        beanPropertyBindingResult.rejectValue("lastName", "error.lastName.symbolTypes", "Фамилия должна содержать только латинские буквы");

        Mockito.when(ex.getBindingResult()).thenReturn(beanPropertyBindingResult);

        ResponseEntity<Object> response = validationLoanApplicationRequestHandler.handleMethodArgumentNotValid(ex,headers,status,request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody() instanceof Map);
        Map<String, String> errors = (Map<String, String>) response.getBody();
        Assertions.assertEquals("Фамилия должна содержать только латинские буквы", errors.get("lastName"));
    }
}