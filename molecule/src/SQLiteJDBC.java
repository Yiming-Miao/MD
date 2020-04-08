import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteJDBC {
    Connection c = null;
    Statement stmt = null;

    public void connect() { //Connect the database
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:molecule.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        // System.out.println("Opened database successfully");
    }

    public void createTable() throws SQLException { //Create four tables
        stmt = c.createStatement();
        String sql0 = "CREATE TABLE periodicTable" +
                "(EID           INT         PRIMARY KEY     NOT NULL," +
                " ENAME         VARCHAR                     NOT NULL)";
        stmt.executeUpdate(sql0);
        String sql1 = "CREATE TABLE compound" +
                "(CID           INTEGER   PRIMARY KEY  AUTOINCREMENT," +
                " CNAME         VARCHAR                     NOT NULL," +
                " ENUMBER       INT                         NOT NULL)";
        stmt.executeUpdate(sql1);
        String sql2 = "CREATE TABLE compoundElement" +
                "(LID           INT                         NOT NULL," +
                " EID           INT                         NOT NULL," +
                " CID           INT                         NOT NULL," +
                "CONSTRAINT cons1 FOREIGN KEY (EID) REFERENCES periodicTable(EID)," +
                "CONSTRAINT cons2 FOREIGN KEY (CID) REFERENCES compound(CID))";
        stmt.executeUpdate(sql2);
        String sql3 = "CREATE TABLE structure" +
                "(LID1           INT                         NOT NULL," +
                " LID2           INT                         NOT NULL," +
                " CID            INT                         NOT NULL," +
                "CONSTRAINT cons3 FOREIGN KEY (CID) REFERENCES compound(CID))";
        stmt.executeUpdate(sql3);
        stmt.close();
        c.close();
    }

    //Four insert functions
    public void insertPT(int eid, String ename) throws SQLException {
        stmt = c.createStatement();
        String sql = "INSERT INTO periodicTable (EID, ENAME) " +
                "VALUES (" + eid + ", '" + ename + "');";
        stmt.executeUpdate(sql);
    }

    public int insertCompound(String cname, int enumber) throws SQLException {
        stmt = c.createStatement();
        String sql = "INSERT INTO compound (CID, CNAME, ENUMBER) " +
                "VALUES (NULL,'" + cname.replace("'", "''") + "', " + enumber + ");";
//        System.out.println(sql);
        stmt.executeUpdate(sql);
        int cid = 0;
        ResultSet rs = stmt.executeQuery("SELECT * FROM compound WHERE CNAME='" +  cname.replace("'", "''") + "';");
        while (rs.next()) {
            cid = rs.getInt("CID");
//            System.out.println(cid);
            break;
        }
        return cid;
    }

    public void insertCE(int lid, String ename, int cid) throws SQLException {
        stmt = c.createStatement();
        int eid = 0;
        ResultSet rs = stmt.executeQuery("SELECT * FROM periodicTable WHERE ENAME='" + ename + "';");
        while (rs.next()) {
            eid = rs.getInt("EID");
            break;
        }
        String sql = "INSERT INTO compoundElement (LID, EID, CID) " +
                "VALUES (" + lid + ", " + eid + ", " + cid + ");";
//        System.out.println(sql);
        stmt.executeUpdate(sql);
    }

    public void insertStruct(int lid1, int lid2, int cid) throws SQLException {
        stmt = c.createStatement();
        String sql = "INSERT INTO structure (LID1, LID2, CID) " +
                "VALUES (" + lid1 + ", " + lid2 + ", " + cid + ");";
//        System.out.println(sql);
        stmt.executeUpdate(sql);
    }

    public boolean findMolecule(String FileName) throws SQLException, IOException {
        ReadFile rf = new ReadFile();
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM compound'" + "';");
        Graph<String, DefaultEdge> input = rf.toGraph(rf.toText(FileName));
        while (rs.next()) {
            ArrayList<String> existedMolecule = toText(rs.getInt("CID"));
            Graph<String, DefaultEdge> molecule = rf.toGraph(existedMolecule);

            if (molecule.vertexSet().size() != input.vertexSet().size()) {
                continue;
            }

            if (molecule.edgeSet().size() != input.edgeSet().size()) {
                continue;
            }

            GraphIso pair = new GraphIso(input, molecule);

            if (pair.checkSGI()) {
                System.out.println("Found " + rs.getString("CNAME"));
                return true;
            }
        }

        System.out.println("Molecule not found.");
        return false;
    }

    public ArrayList<String> toText(int cid) throws SQLException {
        ArrayList<String> text = new ArrayList<>();
        stmt = c.createStatement();
        Statement newState = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM compound WHERE CID='" + cid + "';");
        text.add(rs.getString("CNAME"));
        text.add(rs.getString("ENUMBER"));
        rs = stmt.executeQuery("SELECT * FROM compoundElement WHERE CID='" + cid + "';");
        while (rs.next()) {
            text.add(newState.executeQuery("SELECT * FROM periodicTable WHERE EID='" + rs.getInt("EID") + "';").getString("ENAME").replace("\"", ""));
        }
        rs = stmt.executeQuery("SELECT * FROM structure WHERE CID='" + cid + "';");
        while (rs.next()) {
            text.add(rs.getString("LID1") + " " + rs.getString("LID2"));
        }
        return text;
    }
}