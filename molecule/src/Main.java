import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class Main {
    private static SQLiteJDBC db = new SQLiteJDBC();

    public static void main(String[] args) throws SQLException {
        Main.db.connect();
        Main.db.createTable();

        File f = new File("");
        String filepath = f.getAbsolutePath() + "\\molecules";
        ReadFile rf = new ReadFile(filepath);

        String periodic_path = f.getAbsolutePath() + "\\elements.csv";
        rf.readCSV(periodic_path);

//        GetMolecule pulltest = new GetMolecule();
//        for (int i = 1; i < 1001; i++)
//            pulltest.getMolecule(i); // Download 1,000 known compounds from PubChem into database


        rf.read(filepath);// Add all compounds from PubChem into database
        rf.singleRead("C:\\Users\\mym10\\Desktop\\water.txt"); //Add a txt file to the database

    }


}
