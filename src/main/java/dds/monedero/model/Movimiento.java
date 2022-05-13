package dds.monedero.model;

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
}

class Deposito extends Movimiento {
  public Deposito(LocalDate fecha, double monto) {
    super(fecha,monto);
  }
  public boolean isDeposito() { return true; }

  public double calcularValor(Cuenta cuenta) {
    return cuenta.getSaldo() + getMonto();
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
}