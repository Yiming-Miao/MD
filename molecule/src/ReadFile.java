import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {
    SQLiteJDBC db = new SQLiteJDBC(); // Database initialization

    public ReadFile(String filepath) {
        db.connect();
    }

    //Read all the txt files in the directory
    public void read(String filepath) throws SQLException {
        String[] list = new File(filepath).list();
        for (String e : list) {
            singleRead(filepath + "\\" + e);
        }
    }

    //Read a single txt file line by line and save the information of the compound into database
    public void singleRead(String filename) throws SQLException {
        ArrayList<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            while ((str = bf.readLine()) != null) {
                lines.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = lines.get(0); //Compound name
        int num = Integer.valueOf(lines.get(1)); //Total atomic number
        int cid = db.insertCompound(name, num); //Save into compound table
        for (int i = 0; i < num; i++) {
            int lid = i;
            String ename = lines.get(i + 2).replace("\"", "");
            db.insertCE(lid, ename, cid); //Save LABEL OF VERTEX into compoundElement table
        }
        for (int j = num + 2; j < lines.size(); j++) {
            String[] link = lines.get(j).split(" ");
            int left = Integer.valueOf(link[0]);
            int right = Integer.valueOf(link[1]);
            db.insertStruct(left, right, cid); //Save adjacent list into structure table
        }
    }

    // Read the periodical table into database
    public void readCSV(String filePath) {
        db.connect();
        List<List<String>> data = new ArrayList<>();
        String line = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String line0 = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] items = line.split(",");
                int eid = Integer.valueOf(items[0]);
                String ename = items[2].replace("\"", "");
                db.insertPT(eid, ename);
//                System.out.println(eid + ":" + ename);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

}

