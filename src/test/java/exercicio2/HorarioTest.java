package exercicio2;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.junit.Test;

public class HorarioTest {
	SimpleDateFormat d = new SimpleDateFormat("HH:mm:ss.SSS");

	@Test
	public void test() {
		assertNotEquals(d.format(new Date(0)), new Horario().getHoraio(), 0.1);
	}

}
