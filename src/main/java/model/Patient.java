package model;

import java.time.LocalTime;

public class Patient implements Comparable<Patient> {
	
	
	@Override
	public String toString() {
		return "Patient [arrival_time=" + arrival_time + ", color=" + color + ", num=" + num + "]";
	}

	public enum ColorCode{
		NEW, //in triage
		WHITE,YELLOW,RED,BLACK, //in sala di attesa
		TREATING, //dentro studio medico
		OUT //a casa (abbandonato o curato)
	};
	
	
	private LocalTime arrival_time;
	private ColorCode color;
	private int num;
	
	
	public Patient(int num,LocalTime arrival_time, ColorCode color) {
		super();
		this.arrival_time = arrival_time;
		this.color = color;
		this.num=num;
	}
	
	public LocalTime getArrival_time() {
		return arrival_time;
	}
	public void setArrival_time(LocalTime arrival_time) {
		this.arrival_time = arrival_time;
	}
	public ColorCode getColor() {
		return color;
	}
	public void setColor(ColorCode color) {
		this.color = color;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public int compareTo(Patient other) {
		//Criterio di confronto: priorità di colore
		if(this.getColor().equals(other.getColor())) { //Stesso colore: vince chi è arrivato prima
			return this.arrival_time.compareTo(other.arrival_time);
		}else if(this.color.equals(Patient.ColorCode.RED)) {
			return -1;//passa prima this
		}else if(other.color.equals(Patient.ColorCode.RED)) {
			return +1;//passa prima other
		}else if(this.color.equals(Patient.ColorCode.YELLOW)) { //Yellow e White
			return -1;
		}else // White - Yellow
			return +1;
	
	}

	//Equals e HashCode : potere fare .remore() in una  Collection
	//CompareTo: potere inserire nella coda prioritaria
	
	
	
}
