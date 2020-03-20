import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Integer;

import org.json.*;

public class GetMolecule {

    static ArrayList<String> periodicTable; // Periodic table

    public GetMolecule() {
        periodicTable = new ArrayList<>();
        initializePeriodicTable();
    }

    // Get string result from URL content
    public StringBuilder readURL(String urlToRead) throws IOException {
        StringBuilder rst = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.setRequestMethod("GET");
        BufferedReader read = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        String line;
        while ((line = read.readLine()) != null) {
            rst.append(line);
        }
        read.close();
        return rst;
    }


    //Pull a molecule from the PubChem database by ID
    public void getMolecule(int CID) {
        FileWriter prewrite = null;
        try {
            //URL with CID
            String urlToRead1 = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/" + CID + "/JSON";

            JSONObject obj0 = new JSONObject("" + readURL(urlToRead1));
            String obj4 = obj0.getJSONObject("Record").getString("RecordTitle");
            if (obj4.equals("CID " + CID)) {
                System.out.println("Invalid molecule");
                return;
            }
            System.out.println(obj4.toString());

            //URL of the molecular structure of CID
            String urlToRead2 = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + CID + "/JSON";

            //Get the bonds of this compound
            JSONObject obj = new JSONObject(String.valueOf(readURL(urlToRead2)));
            JSONArray PC_Compounds = obj.getJSONArray("PC_Compounds");
            JSONObject obj2 = PC_Compounds.getJSONObject(0);
            JSONArray element = obj2.getJSONObject("atoms").optJSONArray("element");
            JSONArray lid1 = obj2.getJSONObject("bonds").getJSONArray("lid1");
            JSONArray lid2 = obj2.getJSONObject("bonds").getJSONArray("lid2");
            JSONArray order = obj2.getJSONObject("bonds").optJSONArray("order");

            String[] elements = new String[element.length()];
            String[] lid1s = new String[lid1.length()];
            String[] lid2s = new String[lid2.length()];
            String[] orders = new String[order.length()];
            for (int i = 0; i < element.length(); i++) {
                elements[i] = element.get(i).toString();
            }
            for (int i = 0; i < lid1.length(); i++) {
                lid1s[i] = lid1.get(i).toString();
                lid2s[i] = lid2.get(i).toString();
                orders[i] = order.get(i).toString();
            }

            // Save the info of compound into txt file with the required format
            prewrite = new FileWriter("./molecules/" + obj4.toString() + ".txt");
            BufferedWriter write = new BufferedWriter(prewrite);
            write.write(obj4);
            write.newLine();
            write.write(String.valueOf(element.length()));
            for (String s : elements) {
                write.newLine();
                write.write(periodicTable.get(Integer.parseInt(s)));
            }
            for (int i = 0; i < lid1s.length; i++) {
                for (int j = 0; j < Integer.parseInt(orders[i]); j++) {
                    write.newLine();
                    write.write((Integer.parseInt(lid1s[i]) - 1) + " " + (Integer.parseInt(lid2s[i]) - 1));
                }
            }
            write.close();
            System.out.println(CID + " Successful");
            
        } catch (IOException e) {
            System.out.println("File Name/IO Error");
        }
    }

    //Read the csv of Periodic Table from PubChem
    public void initializePeriodicTable() {
        try {
            int counter = 0;
            String line;

            BufferedReader read = new BufferedReader(new FileReader("elements.csv"));
            while ((line = read.readLine()) != null) {
                Scanner scan = new Scanner(line);
                scan.useDelimiter(",");
                while (scan.hasNext()) {
                    counter++;
                    if (counter == 3) {
                        periodicTable.add(scan.next());
                    } else {
                        scan.next();
                    }
                }
                counter = 0;
            }
        } catch (IOException ex) {
            System.out.println("IO Error");
        }
    }
}
