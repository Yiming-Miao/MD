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
//        Main.db.createTable();

        File f = new File("");
//        String periodic_path = f.getAbsolutePath()+"\\elements.csv";
//        readCSV(periodic_path);
//
        String filepath = f.getAbsolutePath()+"\\molecules";
        ReadFile rf = new ReadFile(filepath);
        // rf.read(filepath);
        rf.singleRead("C:\\Users\\mym10\\Desktop\\water.txt");

        // GetMolecule pulltest = new GetMolecule();
        // for (int i = 1; i < 1001; i++) GetMolecule.getMolecule(i);

    }

    public static void readCSV(String filePath){
        Main.db.connect();
        List<List<String>> data=new ArrayList<>();
        String line=null;
        try {
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String line0 = bufferedReader.readLine();
            while((line=bufferedReader.readLine())!=null){
                String[] items=line.split(",");
                int eid = Integer.valueOf(items[0]);
                String ename = items[2].replace("\"", "");
                Main.db.insertPT(eid, ename);
                System.out.println(eid + ":" + ename);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

}
