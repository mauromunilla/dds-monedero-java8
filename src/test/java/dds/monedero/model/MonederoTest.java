package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void IngresarDineroEnCuenta() {
    cuenta.ingresarDinero(1500);
    assertEquals(cuenta.getSaldo(),1500,0);
    assertEquals(cuenta.getMovimientos().size(),1,0);
  }

  @Test
  void IngresarMontoNegativoEnCuentaLanzaException() {
    assertThrows(MontoNegativoException.class, () -> cuenta.ingresarDinero(-1500));
  }

  @Test
  void IngresarTresDepositosEsPermitido() {
    cuenta.ingresarDinero(1500);
    cuenta.ingresarDinero(456);
    cuenta.ingresarDinero(1900);
    assertEquals(cuenta.getSaldo(),3856,0);
    assertEquals(cuenta.getMovimientos().size(),3,0);
  }

  @Test
  void IngresarMasDeTresDepositosLanzaException() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.ingresarDinero(1500);
          cuenta.ingresarDinero(456);
          cuenta.ingresarDinero(1900);
          cuenta.ingresarDinero(245);
    });
    assertEquals(cuenta.getSaldo(),3856,0);
    assertEquals(cuenta.getMovimientos().size(),3,0);
  }

  @Test
  void ExtraerMasQueElSaldoLanzaException() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.extraerDinero(1001);
    });
    assertEquals(cuenta.getSaldo(),90,0);
  }

  @Test
  public void ExtraerMasDe1000LanzaException() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.extraerDinero(1001);
    });
    assertEquals(cuenta.getSaldo(),5000,0);
  }

  @Test
  public void ExtraerMontoNegativoLanzaException() {
    assertThrows(MontoNegativoException.class, () -> cuenta.extraerDinero(-500));
  }

  @Test
  public void ExtraerDineroEnLaCuentaRestaSaldo() {
    cuenta.setSaldo(200);
    cuenta.extraerDinero(150);
    assertEquals(cuenta.getSaldo(),50,0);
  }

  @Test
  public void CalculoDeMontoExtraídoFiltraSoloLoDeUnDíaEspecífico() {
    LocalDate otraFecha = LocalDate.of(2022,3,13);
    cuenta.agregarExtraccion(otraFecha,500);
    LocalDate hoy = LocalDate.now();
    cuenta.agregarExtraccion(hoy,300);
    assertEquals(cuenta.getMontoExtraidoA(hoy),300,0);
  }

}