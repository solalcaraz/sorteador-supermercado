package logica;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SorteoTest {

    @Test
    void elCodigoTieneDiaMesYTicketConCeros() {
        Sorteo sorteo = new Sorteo(YearMonth.of(2025, 1), 1, new Random(42));

        assertEquals("07014504", sorteo.sortearGanador());
    }

    @Test
    void elDiaRespetaLaCantidadDeDiasDelMes() {
        assertEquals(28, diaMaximoSorteado(YearMonth.of(2025, 2)));
        assertEquals(29, diaMaximoSorteado(YearMonth.of(2024, 2)));
        assertEquals(30, diaMaximoSorteado(YearMonth.of(2025, 9)));
        assertEquals(31, diaMaximoSorteado(YearMonth.of(2025, 10)));
    }

    @Test
    void siSaleUnCodigoRepetidoVuelveASortear() {
        // Devuelve día 1 y ticket 1 dos veces seguidas, y después día 2 y ticket 2.
        Random repetido = new Random() {
            private final int[] valores = {0, 0, 0, 0, 1, 1};
            private int i;

            @Override
            public int nextInt(int bound) {
                return valores[i++];
            }
        };
        Sorteo sorteo = new Sorteo(YearMonth.of(2025, 1), 2, repetido);

        assertEquals("01010001", sorteo.sortearGanador());
        assertEquals("02010002", sorteo.sortearGanador());
    }

    @Test
    void noRepiteGanadores() {
        Sorteo sorteo = new Sorteo(YearMonth.of(2025, 1), 5000, new Random(7));
        Set<String> ganadores = new HashSet<>();

        while (!sorteo.estaCompleto()) {
            assertTrue(ganadores.add(sorteo.sortearGanador()));
        }
        assertEquals(5000, sorteo.getCantidadSorteados());
    }

    @Test
    void noSorteaMasGanadoresQueLosPedidos() {
        Sorteo sorteo = new Sorteo(YearMonth.of(2025, 3), 2);
        sorteo.sortearGanador();
        sorteo.sortearGanador();

        assertTrue(sorteo.estaCompleto());
        assertThrows(IllegalStateException.class, sorteo::sortearGanador);
    }

    @Test
    void rechazaCantidadesFueraDeRango() {
        YearMonth febrero = YearMonth.of(2025, 2);

        assertThrows(IllegalArgumentException.class, () -> new Sorteo(febrero, 0));
        assertThrows(IllegalArgumentException.class, () -> new Sorteo(febrero, 28 * 9999 + 1));
    }

    private int diaMaximoSorteado(YearMonth mes) {
        Sorteo sorteo = new Sorteo(mes, 2000, new Random(1));
        int maximo = 0;
        while (!sorteo.estaCompleto()) {
            String codigo = sorteo.sortearGanador();
            assertEquals(String.format("%02d", mes.getMonthValue()), codigo.substring(2, 4));
            maximo = Math.max(maximo, Integer.parseInt(codigo.substring(0, 2)));
        }
        return maximo;
    }
}
