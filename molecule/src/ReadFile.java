import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.io.*;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ReadFile {
    SQLiteJDBC db = new SQLiteJDBC(); // Database initialization

    public ReadFile() {
        db.connect();
    }

    //Read all the txt files in the directory
    public void read(String filepath) throws SQLException {
        String[] list = new File(filepath).list();
        for (String e : list) {
            singleRead(filepath + "/" + e);
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
            String ename = lines.get(i + 2).replace("/", "").replace("\"", "");
            db.insertCE(lid, ename, cid); //Save LABEL OF VERTEX into compoundElement table
        }
        for (int j = num + 2; j < lines.size(); j++) {
            String[] link = lines.get(j).split(" ");
            int left = Integer.valueOf(link[0]);
            int right = Integer.valueOf(link[1]);
            db.insertStruct(left, right, cid); //Save adjacent list into structure table
        }
    }

    public boolean findCompound(String TextFile) {
        System.out.println("Searching for: "+ TextFile);
        ArrayList<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader(TextFile);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            while ((str = bf.readLine()) != null) {
                lines.add(str);
            }
            bf.close();
            fr.close();

            String name = lines.get(0); //Compound name
            int num = Integer.valueOf(lines.get(1));
            int cid = 0;

            if (cid != 0){
                return true;
            }
            else {
                return false;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");

            return false;
        } catch (IOException ex) {
            System.out.println("IO");

            return false;
        } catch (Exception ex2) {
            System.out.print("Incorrect format");

        }
        return false;
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
                String ename = items[2].replace("/", "").replace("\"", "");
                db.insertPT(eid, ename);
                System.out.println(eid + ":" + ename);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> toText(String TextFile) throws IOException {
        BufferedReader read = new BufferedReader(new FileReader(TextFile));
        String line;
        ArrayList<String> text = new ArrayList<>();
        while((line = read.readLine()) != null) {
//            System.out.println(line.replace("\"", ""));
            text.add(line.replace("\"", ""));
        }
        return text;
    }

    public Graph<String, DefaultEdge> toGraph(ArrayList<String> Text) throws IOException {
        int count = 0;
        int numberAtoms = 0;
        HashMap<String, Integer> formula = new HashMap<>();
        Vector<String> key = new Vector<>();
        Graph<String, DefaultEdge> theGraph = new Multigraph<>(DefaultEdge.class);

        for (String linefind : Text) {
            if (count == 0) {
                try {
                    numberAtoms = Integer.valueOf(linefind);
                    count++;
                }catch(Exception ex){
                    continue;
                }
            }
            if (count == 1) {

                numberAtoms = Integer.valueOf(linefind);
            } else if (count >= 2){
                key.addElement(linefind + (count - 2));
                if (count > 0 & count <= numberAtoms + 1) {

                    theGraph.addVertex(linefind + (count - 2));
                    Integer currentCount = formula.get(linefind);
                    if (currentCount == null) {
                        formula.put(linefind, 1);

                    } else {
                        formula.put(linefind, currentCount + 1);
                    }
                } else {
                    String[] edge = linefind.split(" ");
                    theGraph.addEdge(key.get(Integer.parseInt(edge[0])), key.get(Integer.parseInt(edge[1])));
                }
            }
            count++;
        }
        return theGraph;
    }

}

