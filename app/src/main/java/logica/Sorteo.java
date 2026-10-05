package logica;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sorteo mensual entre códigos de participante con formato DDMMTTTT:
 * día de entrega del sobre, mes y número de ticket de 4 dígitos.
 */
public class Sorteo {

    private static final int TICKET_MAXIMO = 9999;

    private final YearMonth mes;
    private final int cantidadGanadores;
    private final Random random;
    private final List<String> ganadores = new ArrayList<>();

    public Sorteo(YearMonth mes, int cantidadGanadores) {
        this(mes, cantidadGanadores, new Random());
    }

    // Recibe el Random para poder fijar la semilla en los tests.
    Sorteo(YearMonth mes, int cantidadGanadores, Random random) {
        int codigosPosibles = mes.lengthOfMonth() * TICKET_MAXIMO;
        if (cantidadGanadores < 1 || cantidadGanadores > codigosPosibles) {
            throw new IllegalArgumentException(
                    "La cantidad de ganadores tiene que estar entre 1 y " + codigosPosibles + ".");
        }
        this.mes = mes;
        this.cantidadGanadores = cantidadGanadores;
        this.random = random;
    }

    public String sortearGanador() {
        if (estaCompleto()) {
            throw new IllegalStateException("Ya se alcanzó la cantidad de ganadores.");
        }
        String codigo;
        do {
            codigo = generarCodigo();
        } while (ganadores.contains(codigo));
        ganadores.add(codigo);
        return codigo;
    }

    public boolean estaCompleto() {
        return ganadores.size() == cantidadGanadores;
    }

    public int getCantidadSorteados() {
        return ganadores.size();
    }

    public int getCantidadGanadores() {
        return cantidadGanadores;
    }

    private String generarCodigo() {
        int dia = random.nextInt(mes.lengthOfMonth()) + 1;
        int ticket = random.nextInt(TICKET_MAXIMO) + 1;
        return String.format("%02d%02d%04d", dia, mes.getMonthValue(), ticket);
    }
}
