package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;

public abstract class Movimiento {
  public LocalDate fecha;
  // Nota: En ningún lenguaje de programación usen jamás doubles (es decir, números con punto flotante) para modelar dinero en el mundo real.
  // En su lugar siempre usen numeros de precision arbitraria o punto fijo, como BigDecimal en Java y similares
  // De todas formas, NO es necesario modificar ésto como parte de este ejercicio. 
  public double monto;

  public Movimiento(LocalDate fecha, double monto) {
    this.fecha = fecha;
    this.monto = monto;
  }

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public abstract boolean isDeposito();

  public boolean fueDepositado(LocalDate fecha) {
    return isDeposito() && esDeLaFecha(fecha);
  }

  public boolean fueExtraido(LocalDate fecha) {
    return (!isDeposito()) && esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  public abstract double calcularValor(Cuenta cuenta);

  public abstract void operacion(Cuenta cuenta);
}

class Deposito extends Movimiento {
  public Deposito(LocalDate fecha, double monto) {
    super(fecha,monto);
  }
  public boolean isDeposito() { return true; }

  public double calcularValor(Cuenta cuenta) {
    return cuenta.getSaldo() + getMonto();
  }

  public void operacion(Cuenta cuenta) {
      if (monto <= 0) {
        throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
      }

      if (cuenta.getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
        throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
      }

      cuenta.agregarDeposito(LocalDate.now(), monto);
  }
}

class Extraccion extends Movimiento {
  public Extraccion(LocalDate fecha, double monto) {
    super(fecha,monto);
  }
  public boolean isDeposito() { return false; }

  public double calcularValor(Cuenta cuenta) {
    return cuenta.getSaldo() - getMonto();
  }

  public void operacion(Cuenta cuenta) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (cuenta.getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + cuenta.getSaldo() + " $");
    }
    double montoExtraidoHoy = cuenta.getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
    cuenta.agregarExtraccion(LocalDate.now(), monto);
  }
}