package model;

import java.time.LocalTime;

public class Event implements Comparable<Event>{

	//Eventi: arrivo, timeout,triage
	
	enum EventType{
		ARRIVAL, //arrivo un nuovo paziente in Triage
		TRIAGE, //è finito il triage, entro in sala d'attesa
		TIMEOUT, //passa un certo tempo di attesa
		FREE_STUDIO, //si è libero un studio
		TREATED, //paziente curato
		TICK, //timer scatta ogni 5 min: vedere il num di studi liberi
		
	};
	
	@Override
	public String toString() {
		return "Event [time=" + time + ", type=" + type + ", patient=" + patient + "]";
	}


	public Event(LocalTime time, EventType type, Patient patient) {
		super();
		this.time = time;
		this.type = type;
		this.patient = patient;
	}


	private LocalTime time;
	private EventType type;
	private Patient patient;
	
	
	public LocalTime getTime() {
		return time;
	}


	public void setTime(LocalTime time) {
		this.time = time;
	}


	public EventType getType() {
		return type;
	}


	public void setType(EventType type) {
		this.type = type;
	}


	public Patient getPatient() {
		return patient;
	}


	public void setPatient(Patient patient) {
		this.patient = patient;
	}


	@Override
	public int compareTo(Event e) {
		// TODO Auto-generated method stub
		return this.time.compareTo(e.getTime());
	}
	
	
}
