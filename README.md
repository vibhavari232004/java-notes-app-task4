# Java Notes App – Task 4 (Java Developer Internship)

A simple **command-line Notes Manager** built in Java for **Task 4 – Java File I/O**.  
The app stores all notes in a local text file (`notes.txt`) using **FileWriter** and reads them back with **FileReader / BufferedReader**.

---

## ✨ Features
- ➕ Add multi-line notes (press Enter on an empty line to finish)
- 📜 List all saved notes with timestamps
- 🗑️ Delete a note by number
- ✏️ Edit an existing note
- 🔍 Search notes by keyword
- 🧹 Clear all notes
- 💾 Data persists in `notes.txt`

---

## 🛠️ Tech Used
- Java (File I/O, Exception Handling)
- **FileWriter** – for writing/append to file  
- **FileReader / BufferedReader** – for reading from file  
- Works entirely in the **terminal / console**

---

## ⚙️ Installation & Run
1. Clone this repo:
   ```bash
   git clone https://github.com/<your-username>/java-notes-app-task4.git
   cd java-notes-app-task4


Compile the Java file:

javac NotesApp.java

Run the program:

java NotesApp

Output:-

=== Simple Notes App (File I/O) ===
1) Add note
2) List all notes
3) Delete note
4) Edit note
5) Search notes
6) Clear all notes
0) Exit
