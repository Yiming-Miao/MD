import java.sql.SQLException;
import java.io.*;
import java.util.ArrayList;

public class Main {
    private static SQLiteJDBC db = new SQLiteJDBC();

    public static void main(String[] args) throws SQLException, IOException {
        ReadFile rf = new ReadFile();
        db.connect();
        try {
            db.createTable();
            rf.readCSV("elements.csv");
            System.out.println("An empty database is created.");
        } catch (Exception ex) {
            System.out.println("Existed database detected.");
        }

//        rf.singleRead("water.txt");
//        rf.singleRead("water2.txt");
//        rf.singleRead("test.txt");


//        GraphIso iso = new GraphIso(rf.toGraph(rf.toText("water.txt")), rf.toGraph(rf.toText("water2.txt")));
//        System.out.println(iso.checkSGI());
        db.findMolecule("water2.txt");
    }

}
