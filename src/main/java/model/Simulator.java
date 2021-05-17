package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;

public class Simulator {

	//Coda degli eventi
	private PriorityQueue<Event> queue;
	
	//Modello del Mondo
	private List<Patient> patients;
	private PriorityQueue<Patient> waitingRoom; //Contiene SOLO i pazienti in attesa (W/Y//R)
	private int freeStudios;
	
	private Patient.ColorCode ultimoColore;
	
	//Parametri di input
	private int totStudios = 3; //NS
	
	private int numPatients = 120;//NP
	
	private Duration T_ARRIVAL = Duration.ofMinutes(5);
	
	private Duration DURATION_TRIAGE = Duration.ofMinutes(5);
	private Duration DURATION_WHITE = Duration.ofMinutes(10);
	private Duration DURATION_YELLOW = Duration.ofMinutes(15);
	private Duration DURATION_RED = Duration.ofMinutes(30);
	
	private Duration TIME_OUT_WHITE = Duration.ofMinutes(60);
	private Duration TIME_OUT_YELLOW = Duration.ofMinutes(30);
	private Duration TIME_OUT_RED = Duration.ofMinutes(30);
	
	private LocalTime startTime = LocalTime.of(8, 00);
	private LocalTime end_time = LocalTime.of(20, 00);
	
	
	//Parametri di output
	private int patientsTreated;
	private int patientsAbbandoned;
	private int patientsDead;
	
	//Inizializa il simulatore
	public void init() {
		
		//inizializa coda degli eventi e modelo del mondo
		this.queue=new PriorityQueue<>();
		this.patients=new ArrayList<>();
		this.waitingRoom=new PriorityQueue<Patient>();
		this.freeStudios=this.totStudios;
		this.ultimoColore=  ColorCode.RED;
		
		//Inizializza i parametri di output
		this.patientsAbbandoned=0;
		this.patientsDead=0;
		this.patientsTreated=0;
		
		//inietta gli eventi di input(ARRIVAL)
		LocalTime ora = this.startTime;
		int inseriti=0;
		Patient.ColorCode colore = ColorCode.WHITE;
		this.queue.add(new Event(ora.plus(Duration.ofMinutes(5)), EventType.FREE_STUDIO,null));
		
		
		while(ora.isBefore(end_time) && inseriti<this.numPatients) {
			Patient p = new Patient(inseriti,ora,ColorCode.NEW);
			
			Event e = new Event(ora, EventType.ARRIVAL, p);
			
			this.queue.add(e);
			this.patients.add(p);
			
			inseriti++;
			ora=ora.plus(T_ARRIVAL);
		
		}
		
		
		
	}
	
	private Patient.ColorCode prossimoColore(){
		if(ultimoColore.equals(ColorCode.WHITE)) {
			ultimoColore = ColorCode.YELLOW;
		} else if(ultimoColore.equals(ColorCode.YELLOW)) {
			ultimoColore = ColorCode.RED;
		}else {
			ultimoColore = ColorCode.WHITE;
		}
		return ultimoColore;
	
	}
	
	
	//INIZIALIZZA IL SIMULATORE E CREA GLI EVENTI INIZIALI
	public void run() {
		
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
		
	}
	
	public void processEvent(Event e) {
		
		Patient p = e.getPatient();
		LocalTime ora = e.getTime();
		
		
		switch(e.getType()) {
		case ARRIVAL: //arriva un paziente
			this.queue.add(new Event(ora.plus(DURATION_TRIAGE),EventType.TRIAGE,p));
			break;
		
		case  TRIAGE:
			p.setColor(prossimoColore());
			if(p.getColor().equals(Patient.ColorCode.WHITE)) {
				this.queue.add(new Event(ora.plus(TIME_OUT_WHITE),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
			} else if (p.getColor().equals(Patient.ColorCode.YELLOW)) {
				this.queue.add(new Event(ora.plus(TIME_OUT_YELLOW),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
			}else if (p.getColor().equals(Patient.ColorCode.RED)) {
				this.queue.add(new Event(ora.plus(TIME_OUT_RED),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
			}
			break;
		
		
		case  TIMEOUT:
			Patient.ColorCode colore = p.getColor();
			switch(colore) {
			case WHITE:
				p.setColor(ColorCode.OUT);
				this.waitingRoom.remove(p);
				this.patientsAbbandoned++;
				break;
			case YELLOW:
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.RED);
				this.queue.add(new Event(ora.plus(TIME_OUT_RED),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
				break;
			case RED:
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.BLACK);
				this.patientsDead++;
				break;
			default:
				//Non è nella waitingRoom --> non vale 
				//System.out.println("ERRORE:TIME OUT CON COLORE:  "+colore);
			} //chiude l'intero switch --> non ha bisogno del break
			
		break;
		
		
		case  FREE_STUDIO:
			if(this.freeStudios==0) {
				return;
			}
			//Quale paziente faccio entrare? Chi ha il diritto?
			Patient primo = this.waitingRoom.poll(); //ritorna null se la coda è vuota
			if(primo!=null) {
				//ammetti il paziente nello studio
				if(primo.getColor().equals(ColorCode.WHITE)) {
					this.queue.add(new Event(ora.plus(DURATION_WHITE), EventType.TREATED, primo));
				}
				else if(primo.getColor().equals(ColorCode.YELLOW)) {
					this.queue.add(new Event(ora.plus(DURATION_YELLOW), EventType.TREATED, primo));
				}
				else if(primo.getColor().equals(ColorCode.RED)) {
					this.queue.add(new Event(ora.plus(DURATION_RED), EventType.TREATED, primo));
				}
				
				primo.setColor(ColorCode.TREATING);
				this.freeStudios--;
			}
			
			break;
		
		
		case TREATED:
			this.patientsTreated++;
			p.setColor(ColorCode.OUT);
			this.freeStudios++;
			this.queue.add(new Event(ora,EventType.FREE_STUDIO,null));
			break;
			
		case TICK:
			if(this.freeStudios>0 && !this.waitingRoom.isEmpty()) {
				this.queue.add(new Event(ora, EventType.FREE_STUDIO,null));	
			}
			if(ora.isBefore(end_time)) {
				this.queue.add(new Event(ora.plus(Duration.ofMinutes(5)), EventType.TICK,null));
			}
			
			break;
		}
		
		
			
		
		
	}

	public void setFreeStudios(int freeStudios) {
		this.freeStudios = freeStudios;
	}

	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}

	public void setDURATION_TRIAGE(Duration dURATION_TRIAGE) {
		DURATION_TRIAGE = dURATION_TRIAGE;
	}

	public void setDURATION_WHITE(Duration dURATION_WHITE) {
		DURATION_WHITE = dURATION_WHITE;
	}

	public void setDURATION_YELLOW(Duration dURATION_YELLOW) {
		DURATION_YELLOW = dURATION_YELLOW;
	}

	public void setDURATION_RED(Duration dURATION_RED) {
		DURATION_RED = dURATION_RED;
	}

	public void setTIME_OUT_WHITE(Duration tIME_OUT_WHITE) {
		TIME_OUT_WHITE = tIME_OUT_WHITE;
	}

	public void setTIME_OUT_YELLOW(Duration tIME_OUT_YELLOW) {
		TIME_OUT_YELLOW = tIME_OUT_YELLOW;
	}

	public void setTIME_OUT_RED(Duration tIME_OUT_RED) {
		TIME_OUT_RED = tIME_OUT_RED;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setEnd_time(LocalTime end_time) {
		this.end_time = end_time;
	}

	public int getPatientsTreated() {
		return patientsTreated;
	}

	public int getPatientsAbbandoned() {
		return patientsAbbandoned;
	}

	public int getPatientsDead() {
		return patientsDead;
	}
	
	
}
