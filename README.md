# Emergency

We simulate the behavior of an Emergency department in an hospital.

The department in organized in two sections
 - A single Triage, where patients are received, quickly examined, and assigned a severity code
 - A number `NS` of doctor studios, that operate in parallel. Each doctor will receive the next patient, act on him, and then release him

The severity code gives priority in accessing the doctors. Patients with the same severity will be called in arrival order.

### Severity Codes

 - `WHITE`: not urgent, may wait without problems.
    - After `WHITE_TIMEOUT`, if not served, goes home
 - `YELLOW`: serious but not urgent
    - After `YELLOW_TIMEOUT`, if not served, becomes `RED`
 - `RED`: serious and urgent, risking life, must be served as soon as possible
    - After `RED_TIMEOUT`, if not served, becomes `BLACK`
 - `BLACK`: dead. No need to be served.
 
### Timing

Phase | Required Time | Example 

------|---------------|--------

Triage | DURATION_TRIAGE | 5 minutes

Handling a White patient  | DURATION_TRIAGE | 5 minutes

Handling a Yellow patient  | DURATION_TRIAGE | 10 minutes

Handling a Red patient | DURATION_TRIAGE | 15 minutes

Handling a Black patient | N/A |  not needed

## Simulation Parameters

### Inputs

- Parameter: NS
- Initial events: 
    - NP patients
    - arriving every T_ARRIVAL minutes
    - with a round-robin severity (white/yellow/red/white/…)
- Simulate from 8:00 to 20:00

### Outputs

- Number of patients dismissed
- Number of patients that abandoned
- Number of patients dead
