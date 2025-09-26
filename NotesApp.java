import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NotesApp {
    private static final String NOTES_FILE = "notes.txt";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ensureFileExists();
        System.out.println("=== Simple Notes App (File I/O) ===");
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addNote(); break;
                case "2": listNotes(); break;
                case "3": deleteNote(); break;
                case "4": editNote(); break;
                case "5": searchNotes(); break;
                case "6": clearAllNotes(); break;
                case "0": System.out.println("Exiting. Bye!"); return;
                default: System.out.println("Invalid choice — try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nChoose:");
        System.out.println("1) Add note (multi-line — finish with an empty line)");
        System.out.println("2) List all notes");
        System.out.println("3) Delete note (by number)");
        System.out.println("4) Edit note (by number)");
        System.out.println("5) Search notes (keyword)");
        System.out.println("6) Clear all notes");
        System.out.println("0) Exit");
        System.out.print("Enter choice: ");
    }

    private static void ensureFileExists() {
        try {
            File f = new File(NOTES_FILE);
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            System.err.println("Cannot create notes file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void addNote() {
        try {
            System.out.println("Type your note. Press an empty line to finish:");
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = sc.nextLine();
                if (line.isEmpty()) break;
                sb.append(line).append("\n");
            }
            if (sb.length() == 0) {
                System.out.println("No note entered. Aborting.");
                return;
            }
            // Convert internal newlines to a single-line representation so each saved note is one file line.
            String singleLine = sb.toString().replace("\r", "").replace("\n", " \\n ");
            String record = LocalDateTime.now().format(DTF) + " :: " + singleLine;

            try (FileWriter fw = new FileWriter(NOTES_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(record);
                bw.newLine();
            }
            System.out.println("Note saved.");
        } catch (IOException e) {
            System.err.println("Error saving note: " + e.getMessage());
        }
    }

    private static List<String> readAllLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(NOTES_FILE))) {
            String l;
            while ((l = br.readLine()) != null) lines.add(l);
        }
        return lines;
    }

    private static void listNotes() {
        try {
            List<String> lines = readAllLines();
            if (lines.isEmpty()) {
                System.out.println("(No notes found)");
                return;
            }
            System.out.println("\nSaved notes:");
            int idx = 1;
            for (String rec : lines) {
                String[] parts = rec.split(" :: ", 2);
                String ts = parts.length > 0 ? parts[0] : "";
                String note = parts.length > 1 ? parts[1] : "";
                // convert stored " \n " back to real newlines for display
                String pretty = note.replace(" \\n ", "\n\t");
                System.out.println(idx + ") " + ts);
                System.out.println("\t" + pretty.replace("\n", "\n\t"));
                idx++;
            }
        } catch (IOException e) {
            System.err.println("Error reading notes: " + e.getMessage());
        }
    }

    private static void deleteNote() {
        try {
            List<String> lines = readAllLines();
            if (lines.isEmpty()) { System.out.println("(No notes to delete)"); return; }
            listNotes();
            System.out.print("Enter note number to delete: ");
            String s = sc.nextLine().trim();
            int num = Integer.parseInt(s);
            if (num < 1 || num > lines.size()) {
                System.out.println("Invalid number.");
                return;
            }
            System.out.print("Confirm delete note " + num + " ? (y/n): ");
            String conf = sc.nextLine().trim();
            if (!conf.equalsIgnoreCase("y")) { System.out.println("Aborted."); return; }
            lines.remove(num - 1);
            writeBack(lines);
            System.out.println("Deleted.");
        } catch (NumberFormatException nfe) {
            System.out.println("Please enter a valid number.");
        } catch (IOException e) {
            System.err.println("Error deleting note: " + e.getMessage());
        }
    }

    private static void editNote() {
        try {
            List<String> lines = readAllLines();
            if (lines.isEmpty()) { System.out.println("(No notes to edit)"); return; }
            listNotes();
            System.out.print("Enter note number to edit: ");
            int num = Integer.parseInt(sc.nextLine().trim());
            if (num < 1 || num > lines.size()) { System.out.println("Invalid number."); return; }

            // Show old note
            String old = lines.get(num - 1);
            System.out.println("Old: " + old.replace(" \\n ", "\n\t"));

            System.out.println("Type new note content (finish with empty line):");
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = sc.nextLine();
                if (line.isEmpty()) break;
                sb.append(line).append("\n");
            }
            if (sb.length() == 0) { System.out.println("No change made."); return; }
            String singleLine = sb.toString().replace("\r", "").replace("\n", " \\n ");
            String newRec = LocalDateTime.now().format(DTF) + " :: " + singleLine;
            lines.set(num - 1, newRec);
            writeBack(lines);
            System.out.println("Note updated.");
        } catch (NumberFormatException nfe) {
            System.out.println("Enter a valid number.");
        } catch (IOException e) {
            System.err.println("Error editing note: " + e.getMessage());
        }
    }

    private static void searchNotes() {
        try {
            List<String> lines = readAllLines();
            if (lines.isEmpty()) { System.out.println("(No notes to search)"); return; }
            System.out.print("Enter search keyword: ");
            String kw = sc.nextLine().trim().toLowerCase();
            int idx = 1;
            boolean found = false;
            for (String rec : lines) {
                if (rec.toLowerCase().contains(kw)) {
                    String[] parts = rec.split(" :: ", 2);
                    String ts = parts.length > 0 ? parts[0] : "";
                    String note = parts.length > 1 ? parts[1] : "";
                    String pretty = note.replace(" \\n ", "\n\t");
                    System.out.println(idx + ") " + ts);
                    System.out.println("\t" + pretty.replace("\n", "\n\t"));
                    found = true;
                }
                idx++;
            }
            if (!found) System.out.println("(No matches found)");
        } catch (IOException e) {
            System.err.println("Error searching notes: " + e.getMessage());
        }
    }

    private static void clearAllNotes() {
        System.out.print("Are you sure? This will delete ALL notes. Type 'YES' to confirm: ");
        String c = sc.nextLine().trim();
        if (!c.equals("YES")) { System.out.println("Aborted."); return; }
        try {
            writeBack(new ArrayList<>());
            System.out.println("All notes cleared.");
        } catch (IOException e) {
            System.err.println("Error clearing notes: " + e.getMessage());
        }
    }

    private static void writeBack(List<String> lines) throws IOException {
        try (FileWriter fw = new FileWriter(NOTES_FILE, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        }
    }
}
