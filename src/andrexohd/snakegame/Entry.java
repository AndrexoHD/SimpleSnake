package andrexohd.snakegame;

// class Leaderboard {
//     private Entry[] leaderboard;
//     final int maxSize = 5;

//     public Leaderboard() {
//         this.leaderboard = new Entry[maxSize];
//     }

//     public void addEntryToLeaderboard(String name, int score) {
//         Entry newEntry = new Entry(name, score);
//         for (int i = 0; i < leaderboard.length; i++) {
//             // If the entry at index i is null
//             if (leaderboard[i] == null) {
//                 leaderboard[i] = newEntry;
//                 return;
//             }
//             // If the new entry has a higher score than the current entry
//             if (newEntry.score > leaderboard[i].score) {
//                 Entry temp = leaderboard[i];
//                 leaderboard[i] = newEntry;
//                 newEntry = temp;
//             }
//         }
//     }

//     public Entry[] getLeaderboard() {
//         return leaderboard;
//     }

//     // for testing:
//     public static void main(String[] args) {
//         Leaderboard leaderboardObj = new Leaderboard();
//         leaderboardObj.addEntryToLeaderboard("test", 5);
//         leaderboardObj.addEntryToLeaderboard("testorinho", 550);
//         leaderboardObj.addEntryToLeaderboard("Gondela", 20);
//         // leaderboardObj.addEntryToLeaderboard("Gondelaro", 21);
//         // leaderboardObj.addEntryToLeaderboard("Gondel", 10);
//         // leaderboardObj.addEntryToLeaderboard("Gon", 40);
//         for (Entry entry : leaderboardObj.getLeaderboard()) {
//             if (entry != null) {
//                 System.out.println(entry);
//             }
//         }
//     }
// }

public class Entry {
    String name;
    int score;
    static final int width = 25;

    public Entry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String printAsLine() {
        String entry = name;
        String scoreString = Integer.toString(score);
        for (int i = 0; i < width-name.length() - scoreString.length(); i++) {
            entry += ".";
        }
        entry += score;
        return entry;
    }

}
