import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReadFile {
    private File f = new File("");
    private String filepath = f.getAbsolutePath();
    SQLiteJDBC db = new SQLiteJDBC();

    public ReadFile(String filepath) {
        this.filepath = filepath;
        db.connect();
    }

    public void read(String filepath) throws SQLException {
        String[] list = new File(filepath).list();
        for (String e : list) {
            singleRead(filepath+"\\"+e);
        }
    }

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
        String name = lines.get(0);
        int num = Integer.valueOf(lines.get(1));
        int cid = db.insertCompound(name, num);
        for (int i=0; i<num; i++) {
            int lid = i;
            String ename = lines.get(i+2).replace("\"", "");
            db.insertCE(lid, ename, cid);
        }
        for (int j=num+2; j<lines.size(); j++) {
            String[] link = lines.get(j).split(" ");
            int left = Integer.valueOf(link[0]);
            int right = Integer.valueOf(link[1]);
            db.insertStruct(left, right, cid);
        }
    }

}

