package Cuenta;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import cuenta.dtos.DtoCuenta;
import cuenta.modelo.Cuenta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ServicioCuenta {

    @InjectMocks
    private ServicioCuenta cuentaService;

    @Test
    void convertToEntity_shouldReturnAccount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        
        LocalDate today = LocalDate.now();
        DtoCuenta accountDto = new DtoCuenta(today, 100.0, "123456");
        
        Method convertToEntityMethod = ServicioCuenta.class.getDeclaredMethod("convertToEntity", DtoCuenta.class);
        convertToEntityMethod.setAccessible(true);  // Make the private method accessible
        Cuenta result = (Cuenta) convertToEntityMethod.invoke(cuentaService, accountDto);

        assertEquals(today, result.getFechaAlta());
        assertEquals(100.0, result.getSaldo());
        assertEquals("123456", result.getMercadoPagoId());
    }
}
