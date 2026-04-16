# Triage App
Members: Joshua and Andrew
This lab we create a chemical alien invasion scenario for triaging patients. We create rules and the structure of the app and allow AI (ChatGPT) to fill in the code.
## Triage Rules
Our rules are as follows:
1. Priority based on injury (disintegration, ray gun wound, headaches...)
2. Secondary priority based on area of injury (head, torso, leg, foot...)
3. Lastly based on the time they have been in the ER 
## Specifics
We use mainly a priority queue with a rule list to determine the priority of each patient to add to the queue. 
For ease we also implemented a patient generator with random set of injuries.
## Example output
```
=== Chemical Alien Invasion Triage System ===
Waiting: 20  |  Seen today: 0
1. Intake new patient
2. Update patient condition
3. See next patient (highest priority)
4. Display waiting list
5. Remove patient (left without being seen)
6. Search waiting patients by name
7. View seen / discharged patients
8. Show queue summary
9. Estimate wait time for patient
10. Assign next patient to treatment room
11. Discharge patient from room
12. View room assignments
13. View complaint statistics
14. View analytics
15. Exit
Choose an option: 4

--- Patients in Priority Order ---
1. ID: 11   | Name: James      | Complaint: disintegration               | Location: torso        | Sev: 60 | Inj: 50 | Loc: 20 | Wait:  4 | TOTAL: 134 | Category: CRITICAL | Intake: 2026-04-16 14:15:09 (42 min ago)
2. ID: 2    | Name: Henry      | Complaint: stroke                       | Location: head         | Sev: 50 | Inj: 50 | Loc: 30 | Wait:  1 | TOTAL: 131 | Category: CRITICAL | Intake: 2026-04-16 14:43:09 (14 min ago)
3. ID: 17   | Name: Scarlett   | Complaint: stroke                       | Location: brain        | Sev: 50 | Inj: 50 | Loc: 30 | Wait:  1 | TOTAL: 131 | Category: CRITICAL | Intake: 2026-04-16 14:45:09 (12 min ago)
4. ID: 13   | Name: Michael    | Complaint: collapsed lung               | Location: chest        | Sev: 50 | Inj: 50 | Loc: 25 | Wait:  5 | TOTAL: 130 | Category: CRITICAL | Intake: 2026-04-16 13:59:09 (58 min ago)
5. ID: 7    | Name: Ava        | Complaint: collapsed lung               | Location: chest        | Sev: 50 | Inj: 50 | Loc: 25 | Wait:  4 | TOTAL: 129 | Category: CRITICAL | Intake: 2026-04-16 14:12:09 (45 min ago)
6. ID: 4    | Name: Emma       | Complaint: collapsed lung               | Location: chest        | Sev: 50 | Inj: 50 | Loc: 25 | Wait:  0 | TOTAL: 125 | Category: CRITICAL | Intake: 2026-04-16 14:56:09 (1 min ago)
7. ID: 9    | Name: Harper     | Complaint: ray gun wound                | Location: leg          | Sev: 55 | Inj: 45 | Loc: 10 | Wait:  5 | TOTAL: 115 | Category: CRITICAL | Intake: 2026-04-16 14:01:09 (56 min ago)

```
## Requirements
All code are tested and ran on Java 21.