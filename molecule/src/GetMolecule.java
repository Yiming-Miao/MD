import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Integer;

import org.json.*;

public class GetMolecule {

    //Have a periodic table dictionary available, as the elements in each PubChem molecule JSON file is represented by their atomic numbers
    static ArrayList<String> periodicTable;
    public GetMolecule() {
        periodicTable = new ArrayList<>();
        initializePeriodicTable();
    }

    //Get a batch of (number) molecules from PubChem, starting from CID number (start)
    public static void getBatch(int number, int start) {
        for (int i = start; i < start+number; i++) {
            String textfile = getMolecule(i);
        }
    }

    //Pull a molecule from the PubChem database through a compound ID (CID)
    //PubChem has a URL format to displays a text file (in .txt, .json, etc) and can be parsed through
    //One URL contains the name of the molecule, another URL contains the structure of the molecule
    public static String getMolecule(int CID) {
        FileWriter prewrite=null;
        try {
            String result = new String();

            //Set up a HTTP connection to the URL and use GET to pull the text data from the URL link via getInputStream
            //The URL has the compound ID in it
            String urlToRead = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/";
            urlToRead += CID;
            urlToRead += "/JSON/";
            URL url = new URL(urlToRead);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");

            //Read through the getInputStream and combine it into a string
            BufferedReader read = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String line;
            while ((line = read.readLine()) != null) {
                result += line;
            }

            //Because the string still has a JSON structure, we extract specific data from the file
            //In this link, we only need the Record -> RecordTitle, which is the name of the (CID) molecule
            //Some CIDs have an unnamed molecule that belongs to a different CID, so we ignore those cases and print "Invalid molecule"
            JSONObject obj0 = new JSONObject(result);
            String obj4 = obj0.getJSONObject("Record").getString("RecordTitle");
            if (obj4.equals("CID " + CID)) {
                System.out.println("Invalid molecule");
                return null;
            }
            System.out.println(obj4.toString());

            //Make a new URL (with the same CID) to find the corresponding molecular structure
            //Repeat the GET and combining of the string similar to the first URL processing
            String result2 = new String();
            String urlToRead2 = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/";
            urlToRead2 += CID;
            urlToRead2 += "/JSON";
            URL url2 = new URL(urlToRead2);
            HttpURLConnection connect2 = (HttpURLConnection) url2.openConnection();
            connect2.setRequestMethod("GET");
            BufferedReader read2 = new BufferedReader(new InputStreamReader(connect2.getInputStream()));
            String line2;
            while ((line2 = read2.readLine()) != null) {
                result2 += line2;
            }
            read2.close();

            //From the JSON string, get all the elements of each atom in the molecule
            //Get the first vertex of each edge (bond) in the molecule
            //Get the second vertex of each edge (bond) in the molecule
            //Get the order of each edge (bond), example: an order of 2 means there are two edges between the same two vertexes (double bond)
            JSONObject obj = new JSONObject(result2);
            JSONArray PC_Compounds = obj.getJSONArray("PC_Compounds");
            JSONObject obj2 = PC_Compounds.getJSONObject(0);
            JSONArray element = obj2.getJSONObject("atoms").optJSONArray("element");
            JSONArray aid1 = obj2.getJSONObject("bonds").getJSONArray("aid1");
            JSONArray aid2 = obj2.getJSONObject("bonds").getJSONArray("aid2");
            JSONArray order = obj2.getJSONObject("bonds").optJSONArray("order");

            //Convert the JSON arrays to regular arrays
            String elements[] = new String[element.length()];
            String aid1s[] = new String[aid1.length()];
            String aid2s[] = new String[aid2.length()];
            String orders[] = new String[order.length()];
            for (int i = 0; i < element.length(); i++) {
                elements[i] = element.get(i).toString();
            }
            for (int i = 0; i < aid1.length(); i++) {
                aid1s[i] = aid1.get(i).toString();
                aid2s[i] = aid2.get(i).toString();
                orders[i] = order.get(i).toString();
            }

            //Make a new text file to store the name of the molecule (first line) and the structural data in adjacency list form (second line onwards)
            //NOTE: Before you do this, make a folder called molecules in moleculeDB
            prewrite = new FileWriter("./molecules/" +obj4.toString()+".txt");
            BufferedWriter write = new BufferedWriter(prewrite);
            write.write(obj4);
            write.newLine();
            write.write(String.valueOf(element.length()));
            for (int i = 0; i < elements.length; i++) {
                write.newLine();
                write.write(periodicTable.get(Integer.parseInt(elements[i])));
            }
            for (int i = 0; i < aid1s.length; i++) {
                for (int j = 0; j < Integer.parseInt(orders[i]); j++) {
                    write.newLine();
                    write.write((Integer.parseInt(aid1s[i])-1) + " " + (Integer.parseInt(aid2s[i])-1));
                }
            }
            write.close();
            System.out.println(CID+ " Successful");
            return (obj4.toString()+".txt");
        } catch (MalformedURLException ex) {
            System.out.println("Bad URL Error");
        } catch (ProtocolException ex2) {
            System.out.println("HTTP Protocol Error");
        } catch (IOException ex3) {
            System.out.println("File Name/IO Error");
        } catch (JSONException ex4) {
            System.out.println("JSON Error");
        } finally {
            //If there is some write IO error, we flush the empty file made
            try {
                if (prewrite != null) {
                    prewrite.flush();
                    prewrite.close();
                }
            } catch (IOException ex5) {
                //Do nothing
            }
        }
        return null;
    }

    //Load/populate the Periodic Table by reading from a csv file downloaded online
    //csv from https://introcs.cs.princeton.edu/java/32class/Element.java.html
    public static void initializePeriodicTable() {
        try {
            int counter = 0;
            String line;

            //Read the CSV
            BufferedReader read = new BufferedReader(new FileReader("elements.csv"));
            while ((line = read.readLine())!= null) {
                Scanner scan = new Scanner(line);
                scan.useDelimiter(",");

                //Scan each line, as the element after the 3rd comma is the element name
                while(scan.hasNext()){
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
