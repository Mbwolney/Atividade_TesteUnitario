package exercicio1;

import exercicio1.TrianguloITF;

public class Triangulo implements TrianguloITF {
	
	private int ladoA;
	private int ladoB;
	private int ladoC;
	
	public Triangulo(int a, int b, int c) {
		// TODO Auto-generated method stub
		this.ladoA = a;
		this.ladoB = b;
		this.ladoC = c;
		
	}
		
	public boolean trianguloT() {
			// TODO Auto-generated method stub
			
	   if ((ladoA < ladoB + ladoC) && (ladoB < ladoA + ladoC) && (ladoC < ladoA + ladoB)) {
		   return true;
		}
	   return false;
	}
	
	public boolean trianguloEquilatero() {
		// TODO Auto-generated method stub
		if ((ladoA == ladoB && ladoB == ladoC) && this.trianguloT()) {
			return true;
		}
		return false;
		}
	
	public boolean trianguloEscaleno() {
		// TODO Auto-generated method stub
		if ((ladoA != ladoB && ladoA != ladoC && ladoB != ladoC) && this.trianguloT()) {
			return true;
		}
		return false;
	}
	
	public boolean trianguloIsosceles() {
		// TODO Auto-generated method stub
		if (((ladoA == ladoB) || (ladoA == ladoC)) && this.trianguloT()) {
			return true;
		}
			return false;
	}

	
}
