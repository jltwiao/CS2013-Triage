import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AlienTriageSystem {

    // ---------------- COMPLAINT RULE CLASS ----------------
    static class ComplaintRule {
        private final String complaintName;
        private final int severityScore;
        private final int injuryPriority;
        private final List<String> validLocations;

        public ComplaintRule(String complaintName, int severityScore, int injuryPriority, List<String> validLocations) {
            this.complaintName = complaintName.toLowerCase();
            this.severityScore = severityScore;
            this.injuryPriority = injuryPriority;
            this.validLocations = new ArrayList<>();
            for (String loc : validLocations) {
                this.validLocations.add(loc.toLowerCase());
            }
        }

        public String getComplaintName() {
            return complaintName;
        }

        public int getSeverityScore() {
            return severityScore;
        }

        public int getInjuryPriority() {
            return injuryPriority;
        }

        public List<String> getValidLocations() {
            return validLocations;
        }

        public boolean matchesComplaint(String complaint) {
            return complaint.toLowerCase().contains(complaintName);
        }

        public boolean isValidLocation(String location) {
            String lower = location.toLowerCase();
            for (String valid : validLocations) {
                if (lower.contains(valid)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ---------------- RULE LIST ----------------
    static final List<ComplaintRule> COMPLAINT_RULES = Arrays.asList(
            new ComplaintRule("headache", 5, 5, Arrays.asList("head")),
            new ComplaintRule("concussion", 20, 20, Arrays.asList("head", "brain")),
            new ComplaintRule("eye infection", 15, 15, Arrays.asList("eye")),
            new ComplaintRule("heart attack", 50, 50, Arrays.asList("heart", "chest", "ribcage")),
            new ComplaintRule("stroke", 50, 50, Arrays.asList("head", "brain")),
            new ComplaintRule("collapsed lung", 50, 50, Arrays.asList("chest", "ribcage")),
            new ComplaintRule("stomach ache", 8, 8, Arrays.asList("abdomen", "torso", "side")),
            new ComplaintRule("fracture", 25, 20, Arrays.asList("arm", "leg", "hand", "foot", "shoulder", "hip", "knee")),
            new ComplaintRule("broken bone", 25, 20, Arrays.asList("arm", "leg", "hand", "foot", "shoulder", "hip", "knee")),
            new ComplaintRule("tentacle sprain", 10, 10, Arrays.asList("arm", "leg", "shoulder", "hand", "hip", "knee")),
            new ComplaintRule("missing limbs", 60, 50, Arrays.asList("arm", "leg", "hand", "foot")),
            new ComplaintRule("disintegration", 60, 50, Arrays.asList("arm", "leg", "torso", "chest")),
            new ComplaintRule("ray gun wound", 55, 45, Arrays.asList("chest", "arm", "leg", "abdomen", "shoulder", "torso", "hand", "foot")),
            new ComplaintRule("gunshot", 50, 45, Arrays.asList("chest", "arm", "leg", "abdomen", "shoulder", "torso")),
            new ComplaintRule("3rd degree burn", 45, 40, Arrays.asList("arm", "leg", "torso", "chest", "back", "hand", "foot")),
            new ComplaintRule("third degree burn", 45, 40, Arrays.asList("arm", "leg", "torso", "chest", "back", "hand", "foot")),
            new ComplaintRule("severe burn", 40, 35, Arrays.asList("arm", "leg", "torso", "chest", "back", "hand", "foot")),
            new ComplaintRule("chemical burn", 35, 25, Arrays.asList("arm", "leg", "hand", "foot", "torso", "chest", "back")),
            new ComplaintRule("acid splash", 35, 25, Arrays.asList("arm", "hand", "torso", "eye", "chest")),
            new ComplaintRule("radiation poisoning", 45, 30, Arrays.asList("torso", "abdomen", "chest")),
            new ComplaintRule("toxic spore exposure", 30, 30, Arrays.asList("chest", "torso", "abdomen")),
            new ComplaintRule("alien parasite bite", 30, 25, Arrays.asList("arm", "leg", "hand", "foot", "neck")),
            new ComplaintRule("bleeding", 30, 30, Arrays.asList("arm", "leg", "hand", "foot", "chest", "abdomen", "head")),
            new ComplaintRule("orbital debris laceration", 30, 30, Arrays.asList("arm", "leg", "face", "chest", "back", "hand")),
            new ComplaintRule("space suit puncture injury", 35, 15, Arrays.asList("chest", "torso", "arm", "leg")),
            new ComplaintRule("frozen tissue damage", 25, 15, Arrays.asList("hand", "foot", "arm", "leg")),
            new ComplaintRule("electrical shock", 40, 35, Arrays.asList("hand", "arm", "chest")),
            new ComplaintRule("severe dehydration", 15, 15, Arrays.asList("torso", "abdomen")),
            new ComplaintRule("fever", 10, 10, Arrays.asList("head", "torso")),
            new ComplaintRule("panic attack", 5, 5, Arrays.asList("head", "chest")),
            new ComplaintRule("teleportation sickness", 10, 15, Arrays.asList("head", "torso"))
    );

    // ---------------- PATIENT CLASS ----------------
    static class Patient {
        private static final int L_HEAD = 30;
        private static final int L_CHEST = 25;
        private static final int L_ABDOMEN = 20;
        private static final int L_BACK = 15;
        private static final int L_LIMB = 10;
        private static final int L_HAND = 8;
        private static final int L_FOOT = 5;
        private static final int L_DEFAULT = 0;
        private static final int WAITING_INTERVAL_MINUTES = 10;

        private final int id;
        private String name;
        private String complaint;
        private String bodyLocation;
        private final LocalDateTime intakeTime;
        private int severityScore;
        private int injuryPriority;
        private int locationPriority;

        public Patient(int id, String name, String complaint, String bodyLocation) {
            this(id, name, complaint, bodyLocation, LocalDateTime.now());
        }

        public Patient(int id, String name, String complaint, String bodyLocation, LocalDateTime intakeTime) {
            if (!isComplaintLocationValid(complaint, bodyLocation)) {
                throw new IllegalArgumentException(
                        "Complaint \"" + complaint + "\" does not make sense for body location \"" + bodyLocation + "\"."
                );
            }

            this.id = id;
            this.name = name;
            this.complaint = complaint;
            this.bodyLocation = bodyLocation;
            this.intakeTime = intakeTime;
            this.severityScore = deriveSeverity(complaint);
            this.injuryPriority = deriveInjuryPriority(complaint);
            this.locationPriority = deriveLocationPriority(bodyLocation);
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getComplaint() { return complaint; }
        public String getBodyLocation() { return bodyLocation; }
        public LocalDateTime getIntakeTime() { return intakeTime; }
        public int getSeverityScore() { return severityScore; }
        public int getInjuryPriority() { return injuryPriority; }
        public int getLocationPriority() { return locationPriority; }

        public void setComplaint(String complaint) {
            if (!isComplaintLocationValid(complaint, this.bodyLocation)) {
                throw new IllegalArgumentException(
                        "Complaint \"" + complaint + "\" does not make sense for body location \"" + bodyLocation + "\"."
                );
            }

            this.complaint = complaint;
            this.severityScore = deriveSeverity(complaint);
            this.injuryPriority = deriveInjuryPriority(complaint);
        }

        public void setBodyLocation(String bodyLocation) {
            if (!isComplaintLocationValid(this.complaint, bodyLocation)) {
                throw new IllegalArgumentException(
                        "Complaint \"" + complaint + "\" does not make sense for body location \"" + bodyLocation + "\"."
                );
            }

            this.bodyLocation = bodyLocation;
            this.locationPriority = deriveLocationPriority(bodyLocation);
        }

        public long getMinutesWaiting() {
            return Duration.between(intakeTime, LocalDateTime.now()).toMinutes();
        }

        public int getWaitingScore() {
            return (int) (getMinutesWaiting() / WAITING_INTERVAL_MINUTES);
        }

        public int getTotalPriority() {
            return severityScore + injuryPriority + locationPriority + getWaitingScore();
        }

        public String getTriageCategory() {
            int total = getTotalPriority();
            if (total >= 100) return "CRITICAL";
            if (total >= 75) return "URGENT";
            if (total >= 45) return "MODERATE";
            return "MINOR";
        }

        public static int deriveSeverity(String complaint) {
            ComplaintRule rule = findComplaintRule(complaint);
            if (rule != null) return rule.getSeverityScore();

            System.out.println("[WARN] Unrecognised complaint for severity: \"" + complaint + "\" - defaulting to 10");
            return 10;
        }

        private static int deriveInjuryPriority(String complaint) {
            ComplaintRule rule = findComplaintRule(complaint);
            if (rule != null) return rule.getInjuryPriority();

            System.out.println("[WARN] Unrecognised complaint for injury priority: \"" + complaint + "\" - defaulting to 10");
            return 10;
        }

        private static int deriveLocationPriority(String location) {
            String l = location.toLowerCase();
            if (l.contains("head") || l.contains("heart") || l.contains("brain") || l.contains("eye")) return L_HEAD;
            if (l.contains("chest") || l.contains("neck") || l.contains("ribcage")) return L_CHEST;
            if (l.contains("abdomen") || l.contains("torso") || l.contains("side")) return L_ABDOMEN;
            if (l.contains("back") || l.contains("spine")) return L_BACK;
            if (l.contains("arm") || l.contains("leg") || l.contains("shoulder") || l.contains("hip") || l.contains("knee")) return L_LIMB;
            if (l.contains("hand")) return L_HAND;
            if (l.contains("foot")) return L_FOOT;
            return L_DEFAULT;
        }

        public static boolean isComplaintLocationValid(String complaint, String location) {
            ComplaintRule rule = findComplaintRule(complaint);
            if (rule == null) return true;
            return rule.isValidLocation(location);
        }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return String.format(
                    "ID: %-4d | Name: %-10s | Complaint: %-28s | Location: %-12s | Sev: %2d | Inj: %2d | Loc: %2d | Wait: %2d | TOTAL: %3d | Category: %-8s | Intake: %s (%d min ago)",
                    id, name, complaint, bodyLocation,
                    severityScore, injuryPriority, locationPriority, getWaitingScore(), getTotalPriority(),
                    getTriageCategory(), intakeTime.format(fmt), getMinutesWaiting()
            );
        }
    }

    // ---------------- COMPARATOR ----------------
    static class PatientComparator implements Comparator<Patient> {
        @Override
        public int compare(Patient p1, Patient p2) {
            int cmp = Integer.compare(p2.getTotalPriority(), p1.getTotalPriority());
            if (cmp != 0) return cmp;

            cmp = p1.getIntakeTime().compareTo(p2.getIntakeTime());
            if (cmp != 0) return cmp;

            return Integer.compare(p1.getId(), p2.getId());
        }
    }

    // ---------------- EMERGENCY ROOM CLASS ----------------
    static class EmergencyRoom {
        private PriorityQueue<Patient> criticalQueue;
        private PriorityQueue<Patient> urgentQueue;
        private PriorityQueue<Patient> moderateQueue;
        private PriorityQueue<Patient> minorQueue;

        private final HashMap<Integer, Patient> patientMap;
        private final List<Patient> seenPatients;
        private final Map<Integer, Patient> treatmentRooms;

        private int patientsSeen;
        private int patientsLeftWithoutBeingSeen;
        private long totalWaitMinutesSeen;
        private long longestWaitMinutes;

        public EmergencyRoom() {
            Comparator<Patient> comparator = new PatientComparator();
            criticalQueue = new PriorityQueue<>(comparator);
            urgentQueue = new PriorityQueue<>(comparator);
            moderateQueue = new PriorityQueue<>(comparator);
            minorQueue = new PriorityQueue<>(comparator);

            patientMap = new HashMap<>();
            seenPatients = new ArrayList<>();
            treatmentRooms = new HashMap<>();
        }

        private PriorityQueue<Patient> getQueueForCategory(String category) {
            switch (category) {
                case "CRITICAL": return criticalQueue;
                case "URGENT": return urgentQueue;
                case "MODERATE": return moderateQueue;
                default: return minorQueue;
            }
        }

        private void addToCorrectQueue(Patient patient) {
            getQueueForCategory(patient.getTriageCategory()).offer(patient);
        }

        private void removeFromQueues(Patient patient) {
            criticalQueue.remove(patient);
            urgentQueue.remove(patient);
            moderateQueue.remove(patient);
            minorQueue.remove(patient);
        }

        private void rebuildQueue(PriorityQueue<Patient> queue) {
            List<Patient> all = new ArrayList<>(queue);
            queue.clear();
            queue.addAll(all);
        }

        private void rebuildAllQueues() {
            rebuildQueue(criticalQueue);
            rebuildQueue(urgentQueue);
            rebuildQueue(moderateQueue);
            rebuildQueue(minorQueue);
        }

        private Patient pollNextFromQueues() {
            rebuildAllQueues();
            if (!criticalQueue.isEmpty()) return criticalQueue.poll();
            if (!urgentQueue.isEmpty()) return urgentQueue.poll();
            if (!moderateQueue.isEmpty()) return moderateQueue.poll();
            if (!minorQueue.isEmpty()) return minorQueue.poll();
            return null;
        }

        private List<Patient> getAllWaitingPatientsInOrder() {
            rebuildAllQueues();
            List<Patient> all = new ArrayList<>();
            all.addAll(criticalQueue);
            all.addAll(urgentQueue);
            all.addAll(moderateQueue);
            all.addAll(minorQueue);
            all.sort(new PatientComparator());
            return all;
        }

        public boolean intakePatient(Patient patient) {
            if (patientMap.containsKey(patient.getId())) {
                System.out.println("[ERROR] Duplicate patient ID: " + patient.getId());
                return false;
            }
            patientMap.put(patient.getId(), patient);
            addToCorrectQueue(patient);
            return true;
        }

        public Patient seeNextPatient() {
            Patient next = pollNextFromQueues();
            if (next != null) {
                patientMap.remove(next.getId());
                seenPatients.add(next);

                long wait = next.getMinutesWaiting();
                patientsSeen++;
                totalWaitMinutesSeen += wait;
                if (wait > longestWaitMinutes) {
                    longestWaitMinutes = wait;
                }
            }
            return next;
        }

        public boolean removePatient(int id) {
            Patient patient = patientMap.get(id);
            if (patient == null) return false;

            removeFromQueues(patient);
            patientMap.remove(id);
            patientsLeftWithoutBeingSeen++;
            return true;
        }

        public boolean updatePatient(int id, String newComplaint, String newBodyLocation) {
            Patient patient = patientMap.get(id);
            if (patient == null) return false;

            if (!Patient.isComplaintLocationValid(newComplaint, newBodyLocation)) {
                System.out.println("[ERROR] Complaint and body location do not match.");
                return false;
            }

            removeFromQueues(patient);
            patient.setComplaint(newComplaint);
            patient.setBodyLocation(newBodyLocation);
            addToCorrectQueue(patient);
            return true;
        }

        public List<Patient> searchByName(String name) {
            String query = name.toLowerCase();
            List<Patient> results = new ArrayList<>();
            for (Patient p : patientMap.values()) {
                if (p.getName().toLowerCase().contains(query)) {
                    results.add(p);
                }
            }
            results.sort(new PatientComparator());
            return results;
        }

        public void displayPatients() {
            if (patientMap.isEmpty()) {
                System.out.println("\nNo patients are currently waiting.");
                return;
            }

            List<Patient> ordered = getAllWaitingPatientsInOrder();
            System.out.println("\n--- Patients in Priority Order ---");
            int rank = 1;
            for (Patient p : ordered) {
                System.out.println(rank++ + ". " + p);
            }
        }

        public void displaySeenPatients() {
            if (seenPatients.isEmpty()) {
                System.out.println("\nNo patients have been seen yet.");
                return;
            }

            System.out.println("\n--- Seen / Discharged Patients ---");
            for (int i = 0; i < seenPatients.size(); i++) {
                System.out.println((i + 1) + ". " + seenPatients.get(i));
            }
        }

        public void displayQueuesSummary() {
            System.out.println("\n--- Queue Summary ---");
            System.out.println("Critical: " + criticalQueue.size());
            System.out.println("Urgent:   " + urgentQueue.size());
            System.out.println("Moderate: " + moderateQueue.size());
            System.out.println("Minor:    " + minorQueue.size());
        }

        public int estimateWaitTime(int patientId) {
            Patient target = patientMap.get(patientId);
            if (target == null) return -1;

            List<Patient> ordered = getAllWaitingPatientsInOrder();
            int position = -1;
            for (int i = 0; i < ordered.size(); i++) {
                if (ordered.get(i).getId() == patientId) {
                    position = i;
                    break;
                }
            }
            if (position == -1) return -1;
            return position * 15;
        }

        public boolean assignNextPatientToRoom(int roomNumber) {
            if (treatmentRooms.containsKey(roomNumber)) return false;
            Patient next = seeNextPatient();
            if (next == null) return false;
            treatmentRooms.put(roomNumber, next);
            return true;
        }

        public boolean dischargePatientFromRoom(int roomNumber) {
            if (!treatmentRooms.containsKey(roomNumber)) return false;
            treatmentRooms.remove(roomNumber);
            return true;
        }

        public void displayRoomAssignments() {
            if (treatmentRooms.isEmpty()) {
                System.out.println("\nNo treatment rooms are currently occupied.");
                return;
            }

            System.out.println("\n--- Treatment Room Assignments ---");
            List<Integer> roomNumbers = new ArrayList<>(treatmentRooms.keySet());
            Collections.sort(roomNumbers);
            for (int roomNumber : roomNumbers) {
                System.out.println("Room " + roomNumber + ": " + treatmentRooms.get(roomNumber));
            }
        }

        public void displayComplaintStats() {
            if (patientMap.isEmpty()) {
                System.out.println("\nNo waiting patients for complaint statistics.");
                return;
            }

            Map<String, Integer> counts = new HashMap<>();
            for (Patient p : patientMap.values()) {
                counts.put(p.getComplaint(), counts.getOrDefault(p.getComplaint(), 0) + 1);
            }

            System.out.println("\n--- Complaint Statistics ---");
            List<String> complaints = new ArrayList<>(counts.keySet());
            Collections.sort(complaints);
            for (String complaint : complaints) {
                System.out.println(complaint + ": " + counts.get(complaint));
            }
        }

        public void displayAnalytics() {
            System.out.println("\n--- Emergency Room Analytics ---");
            System.out.println("Patients currently waiting: " + size());
            System.out.println("Patients seen today: " + patientsSeen);
            System.out.println("Patients left without being seen: " + patientsLeftWithoutBeingSeen);
            System.out.println("Longest recorded wait time: " + longestWaitMinutes + " minutes");

            double avgWait = patientsSeen == 0 ? 0.0 : (double) totalWaitMinutesSeen / patientsSeen;
            System.out.printf("Average wait time for seen patients: %.2f minutes%n", avgWait);
        }

        public boolean isEmpty() {
            return patientMap.isEmpty();
        }

        public int size() {
            return patientMap.size();
        }

        public int seenCount() {
            return seenPatients.size();
        }
    }

    // ---------------- HELPERS ----------------
    private static final Random RANDOM = new Random();

    public static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a whole number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        int value;
        do {
            System.out.print(prompt);
            value = readInt(scanner);
            if (value <= 0) System.out.println("Please enter a positive number.");
        } while (value <= 0);
        return value;
    }

    public static ComplaintRule findComplaintRule(String complaint) {
        String c = complaint.toLowerCase();
        for (ComplaintRule rule : COMPLAINT_RULES) {
            if (rule.matchesComplaint(c)) {
                return rule;
            }
        }
        return null;
    }

    public static String getValidLocationsForComplaint(String complaint) {
        ComplaintRule rule = findComplaintRule(complaint);
        if (rule == null) return "Any location";
        return String.join(", ", rule.getValidLocations());
    }

    public static Patient generateRandomPatient(int id) {
        String[] names = {
                "James", "Emma", "Liam", "Olivia", "Noah", "Ava", "William",
                "Sophia", "Benjamin", "Isabella", "Lucas", "Mia", "Henry",
                "Charlotte", "Alexander", "Amelia", "Michael", "Harper",
                "Daniel", "Evelyn", "Matthew", "Abigail", "Joseph", "Emily",
                "David", "Ella", "Samuel", "Scarlett"
        };

        String name = names[RANDOM.nextInt(names.length)];
        ComplaintRule rule = COMPLAINT_RULES.get(RANDOM.nextInt(COMPLAINT_RULES.size()));
        List<String> validLocations = rule.getValidLocations();
        String bodyLocation = validLocations.get(RANDOM.nextInt(validLocations.size()));

        int minutesAgo = RANDOM.nextInt(61); // 0 to 60 minutes ago
        LocalDateTime randomIntakeTime = LocalDateTime.now().minusMinutes(minutesAgo);

        return new Patient(id, name, rule.getComplaintName(), bodyLocation, randomIntakeTime);
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EmergencyRoom er = new EmergencyRoom();

        for (int i = 1; i <= 20; i++) {
            er.intakePatient(generateRandomPatient(i));
        }

        int nextId = 21;
        boolean running = true;

        while (running) {
            System.out.println("\n=== Chemical Alien Invasion Triage System ===");
            System.out.println("Waiting: " + er.size() + "  |  Seen today: " + er.seenCount());
            System.out.println("1. Intake new patient");
            System.out.println("2. Update patient condition");
            System.out.println("3. See next patient (highest priority)");
            System.out.println("4. Display waiting list");
            System.out.println("5. Remove patient (left without being seen)");
            System.out.println("6. Search waiting patients by name");
            System.out.println("7. View seen / discharged patients");
            System.out.println("8. Show queue summary");
            System.out.println("9. Estimate wait time for patient");
            System.out.println("10. Assign next patient to treatment room");
            System.out.println("11. Discharge patient from room");
            System.out.println("12. View room assignments");
            System.out.println("13. View complaint statistics");
            System.out.println("14. View analytics");
            System.out.println("15. Exit");
            System.out.print("Choose an option: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1: {
                    System.out.println("(Auto-assigned ID: " + nextId + ")");
                    System.out.print("Enter patient name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter complaint: ");
                    String complaint = scanner.nextLine().trim();

                    ComplaintRule intakeRule = findComplaintRule(complaint);
                    if (intakeRule != null) {
                        System.out.println("Valid body locations for this complaint: " + getValidLocationsForComplaint(complaint));
                    } else {
                        System.out.println("Complaint not found in rule list. Any body location will be accepted.");
                    }

                    System.out.print("Enter body location of wound: ");
                    String bodyLocation = scanner.nextLine().trim();

                    while (!Patient.isComplaintLocationValid(complaint, bodyLocation)) {
                        System.out.println("[ERROR] That complaint does not match that body location.");
                        System.out.println("Valid locations: " + getValidLocationsForComplaint(complaint));
                        System.out.print("Enter body location again: ");
                        bodyLocation = scanner.nextLine().trim();
                    }

                    Patient newPatient = new Patient(nextId, name, complaint, bodyLocation);
                    System.out.println("Auto-calculated severity score: " + newPatient.getSeverityScore());

                    er.intakePatient(newPatient);
                    nextId++;
                    System.out.println("Patient added. Total priority: " + newPatient.getTotalPriority());
                    break;
                }

                case 2: {
                    System.out.print("Enter patient ID to update: ");
                    int updateId = readInt(scanner);
                    System.out.print("Enter new complaint: ");
                    String newComplaint = scanner.nextLine().trim();

                    ComplaintRule updateRule = findComplaintRule(newComplaint);
                    if (updateRule != null) {
                        System.out.println("Valid body locations for this complaint: " + getValidLocationsForComplaint(newComplaint));
                    } else {
                        System.out.println("Complaint not found in rule list. Any body location will be accepted.");
                    }

                    System.out.print("Enter new body location: ");
                    String newBodyLocation = scanner.nextLine().trim();

                    while (!Patient.isComplaintLocationValid(newComplaint, newBodyLocation)) {
                        System.out.println("[ERROR] That complaint does not match that body location.");
                        System.out.println("Valid locations: " + getValidLocationsForComplaint(newComplaint));
                        System.out.print("Enter new body location again: ");
                        newBodyLocation = scanner.nextLine().trim();
                    }

                    boolean updated = er.updatePatient(updateId, newComplaint, newBodyLocation);
                    System.out.println(updated ? "Patient updated successfully." : "Patient ID not found.");
                    break;
                }

                case 3: {
                    Patient next = er.seeNextPatient();
                    if (next == null) {
                        System.out.println("No patients waiting.");
                    } else {
                        System.out.println("\nDoctor is now seeing:");
                        System.out.println(next);
                    }
                    break;
                }

                case 4:
                    er.displayPatients();
                    break;

                case 5: {
                    System.out.print("Enter patient ID to remove: ");
                    int removeId = readInt(scanner);
                    boolean removed = er.removePatient(removeId);
                    System.out.println(removed ? "Patient " + removeId + " removed from waiting list." : "Patient ID not found.");
                    break;
                }

                case 6: {
                    System.out.print("Enter name to search: ");
                    String query = scanner.nextLine().trim();
                    List<Patient> results = er.searchByName(query);

                    if (results.isEmpty()) {
                        System.out.println("No waiting patients match \"" + query + "\".");
                    } else {
                        System.out.println("\n--- Search Results ---");
                        for (Patient p : results) {
                            System.out.println("  " + p);
                        }
                    }
                    break;
                }

                case 7:
                    er.displaySeenPatients();
                    break;

                case 8:
                    er.displayQueuesSummary();
                    break;

                case 9: {
                    System.out.print("Enter patient ID to estimate wait time: ");
                    int patientId = readInt(scanner);
                    int estimate = er.estimateWaitTime(patientId);

                    if (estimate == -1) {
                        System.out.println("Patient ID not found in waiting list.");
                    } else {
                        System.out.println("Estimated wait time for patient " + patientId + ": " + estimate + " minutes.");
                    }
                    break;
                }

                case 10: {
                    int roomNumber = readPositiveInt(scanner, "Enter room number to assign: ");
                    boolean assigned = er.assignNextPatientToRoom(roomNumber);
                    System.out.println(assigned
                            ? "Next patient assigned to room " + roomNumber + "."
                            : "Could not assign patient. Room may already be occupied or no patients are waiting.");
                    break;
                }

                case 11: {
                    int roomNumber = readPositiveInt(scanner, "Enter room number to discharge patient from: ");
                    boolean discharged = er.dischargePatientFromRoom(roomNumber);
                    System.out.println(discharged
                            ? "Patient discharged from room " + roomNumber + "."
                            : "Room not found or already empty.");
                    break;
                }

                case 12:
                    er.displayRoomAssignments();
                    break;

                case 13:
                    er.displayComplaintStats();
                    break;

                case 14:
                    er.displayAnalytics();
                    break;

                case 15:
                    running = false;
                    System.out.println("Exiting triage system. Stay safe out there.");
                    break;

                default:
                    System.out.println("Invalid option. Please enter 1-15.");
            }
        }

        scanner.close();
    }
}
