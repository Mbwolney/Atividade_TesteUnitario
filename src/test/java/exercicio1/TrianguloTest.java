package exercicio1;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import exercicio1.Triangulo;

public class TrianguloTest {
			
	private Triangulo triangulo;
	
	public Triangulo getTriangulo() {
		return triangulo;
	}


	public void setTriangulo(Triangulo triangulo) {
		this.triangulo = triangulo;
	}
	
	@Before
	public void setUp() throws Exception{
		setTriangulo(new Triangulo(1,1,1));
	}

	
	@Test
	public void TrianguloEquilatero() throws Exception {
		assertTrue(new Triangulo(3, 3, 3).trianguloEquilatero());
		assertFalse(new Triangulo(2,4,2).trianguloEquilatero());
	}

	@Test
	public void TrianguloEscaleno() throws Exception {
		assertTrue(new Triangulo(9,6,3).trianguloEscaleno());
		assertFalse(new Triangulo(4,4,4).trianguloEscaleno());
	}

	@Test
	public void TrianguloIsosceles() throws Exception {
		assertTrue(new Triangulo(3,6,6).trianguloIsosceles());
		assertFalse(new Triangulo(6,6,-1).trianguloIsosceles());
		assertFalse(new Triangulo(6,6,0).trianguloIsosceles());
	}


}
