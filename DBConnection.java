import java.awt.List;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tommylee
 */
public class DBConnection{
private static String dbURL = "jdbc:derby://58.176.222.168:1527/Season;user=administrator;password=administrator";
private static String tableName = "SEASON";
private static String teamTable="TEAM";
private static String updateStat="insert into STAT  (TWO_POINT_IN," +
"TWO_POINT_TOTAL," +
"THREE_POINT_IN," +
"THREE_POINT_TOTAL," +
"FREE_POINT_IN," +
"FREE_POINT_TOTAL," +
"ATKBASKET," +
"DEFBASKET," +
"FAST_ATK_SUCCESS," +
"FAST_ATK_FAIL," +
"BLOCKSHOT," +
"ASSIST," +
"STEAL," +
"TURNOVER," +
"ATKFOUL," +
"DEFFOUL," +
"TECHFOUL," +
"TEAM_A," +
"TEAM_B," +
"PLAYER_ID,TIME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;
    public DBConnection() throws SQLException{
     try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(dbURL); 
             System.out.print("Connection created.");
        }
        catch (Exception except)
        {
            try{
            conn = DriverManager.getConnection("jdbc:derby:season;ifexists=true;");
            }
            catch(Exception e)
            {
                conn = DriverManager.getConnection("jdbc:derby:season;create=true;");
            }
         DatabaseMetaData dbmd = conn.getMetaData();

             conn.prepareStatement("CREATE TABLE SEASON (SEASON_ID INT PRIMARY KEY,SEASON_NAME VARCHAR(128));");
             
         
      
        }
       
    }
public long insertSeason(String season_name)
    {
        try
        {
conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("insert into SEASON (SEASON_NAME) values (?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, season_name);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
if (rs.next()) {
   long productId = rs.getLong(1);
   stmt.close();
   return productId;
}
            
            
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return -1;
}
public long insertTeam(String team_name,long season_id)
    {
        try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("insert into TEAM (TEAM_NAME,SEASON_ID) values (?,?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, team_name);
                    statement.setLong(2, season_id);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
if (rs.next()) {
   long productId = rs.getLong(1);
   stmt.close();
   return productId;
}

            
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return -1;
}

public long insertPlayer(PlayerStat temp,long team_id)
    {
        try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("insert into PLAYERS (PLAYER_NAME,PLAYER_NUM,PLAYER_FIRST_TEAM,TEAM_ID) values (?,?,?,?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, temp.getPlayerName());
                    statement.setInt(2, temp.getPlayerNum());
                    statement.setBoolean(3, temp.getFirstTeam());
                    statement.setLong(4, team_id);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
if (rs.next()) {
   long player_id = rs.getLong(1);
   stmt.close();
   return player_id;
}
            
            
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return -1;
}
public Map<Integer,String> selection()
    {
               Map<Integer,String> listSeason=new HashMap<Integer,String>();
        try
        {
conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int i=1; i<=numberCols; i++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
            }

            System.out.println("\n-------------------------------------------------");

            while(results.next())
            {
                listSeason.put( results.getInt(1), results.getString(2));
                
            }
            results.close();
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return listSeason;
    }

 public Map<Integer,String> getTeamList(int i) {
     Map<Integer,String> map = new HashMap<Integer,String>();
        try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("SELECT TEAM_ID,TEAM_NAME FROM TEAM WHERE SEASON_ID=(?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, i);
           ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int x=1; x<=numberCols; x++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(x)+"\t\t");  
            }

            System.out.println("\n-------------------------------------------------");

            while(rest.next())
            {
                int team_id=rest.getInt(1);
                String team = rest.getString(2);
              
                map.put(team_id, team);
            }
            rest.close();
   stmt.close();


            
            return map;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return map;//To change body of generated methods, choose Tools | Templates.
    }

public java.util.List<PlayerStat> getPlayer(Object team_name,int season_id) {
         java.util.List<PlayerStat> listPlayer=new ArrayList<PlayerStat>();
     try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("select PLAYERS.PLAYER_NUM,PLAYERS.PLAYER_NAME,PLAYERS.PLAYER_FIRST_TEAM,TEAM.TEAM_NAME,PLAYERS.PLAYER_ID"
                            + " from PLAYERS JOIN TEAM ON PLAYERS.TEAM_ID=TEAM.TEAM_ID JOIN SEASON ON SEASON.SEASON_ID = TEAM.SEASON_ID WHERE TEAM.TEAM_NAME=(?) AND "
                            + "SEASON.SEASON_ID=(?) ",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, (String)team_name);
                    statement.setInt(2,season_id);
           ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int x=1; x<=numberCols; x++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(x)+"\t\t");  
            }

            System.out.println("\n-------------------------------------------------");
int counter=1;
            while(rest.next())
            {
                
                int player_num = rest.getInt(1);
                String player_name=rest.getString(2);
                boolean player_first_team=rest.getBoolean(3);
                String team_namex=rest.getString(4);
                int player_id = rest.getInt(5);
                PlayerStat temp=new PlayerStat(counter,player_id,player_num,player_name,player_first_team,team_namex);
                listPlayer.add(temp);
                counter++;
            }
            rest.close();
   stmt.close();
   return listPlayer;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
     
    return listPlayer;     //To change body of generated methods, choose Tools | Templates.
    }
int getSeasonIDbyname(Object selectedItem)
{
        int required_id;
        try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM SEASON WHERE SEASON_NAME=(?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, (String)selectedItem);
           ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount();
         int id=-1;
             while(rest.next())
            {
               id  = rest.getInt(1);
                System.out.print("Test:" +id);
            }
            System.out.println("\n--------x-----------------------------------------");

            rest.close();
   stmt.close();
   return id;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return -1;
}
   public String getteamNamebyID(int team_id)
    {
        try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("SELECT TEAM_NAME FROM TEAM WHERE TEAM_ID=(?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1,team_id);
           ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount();
         String name="";
             while(rest.next())
            {
               name  = rest.getString(1);
                System.out.print("Test:" +name);
            }
            System.out.println("\n--------x-----------------------------------------");

            rest.close();
   stmt.close();
   return name;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return "";
    }
    int getteamIDbyname(Object selectedItem,Object selectedSeason) {
        int required_id;
        try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM TEAM WHERE TEAM_NAME=(?) AND SEASON_ID=(?)",
                                      Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, (String)selectedItem);
                    statement.setInt(2, (int)selectedSeason);
           ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount();
         int id=-1;
             while(rest.next())
            {
               id  = rest.getInt(1);
                System.out.print("Test:" +id);
            }
            System.out.println("\n--------x-----------------------------------------");

            rest.close();
   stmt.close();
   return id;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return -1;
         //To change body of generated methods, choose Tools | Templates.
    }


    int insertStat(PlayerStat player,String team_Aname,String team_Bname) {
       try
        {
            conn = DriverManager.getConnection(dbURL); 
            stmt = conn.createStatement();
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
    String strDate = sdfDate.format(now);
                    PreparedStatement statement = conn.prepareStatement(updateStat,
                                      Statement.RETURN_GENERATED_KEYS);
                    for(int x=0;x<17;x++)
                    statement.setInt(x+1, player.getOptions(x));
                    
                    statement.setString(18,team_Aname);
                    statement.setString(19,team_Bname);
                    statement.setInt(20,player.getplayerId() );
                    statement.setString(21,strDate);
                    
           statement.execute();
   stmt.close();
        }
        catch (SQLException sqlExcept)
        {
             sqlExcept.printStackTrace();
            return -1;
        }
        return 1;
    }

    ResultSet getStatByPlayerId(int player_id) {
         try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("SELECT DISTINCT STAT.TWO_POINT_IN,STAT.TWO_POINT_TOTAL,STAT.THREE_POINT_IN,STAT.THREE_POINT_TOTAL,STAT.FREE_POINT_IN,STAT.FREE_POINT_TOTAL,STAT.ATKBASKET,STAT.DEFBASKET,STAT.BLOCKSHOT,STAT.FAST_ATK_SUCCESS,STAT.FAST_ATK_FAIL,STAT.BLOCKSHOT,STAT.ASSIST,STAT.STEAL,STAT.TURNOVER,STAT.ATKFOUL,STAT.DEFFOUL,STAT.TECHFOUL,STAT.TEAM_A,STAT.TEAM_B,STAT.PLAYER_ID,CAST(STAT.\"TIME\" AS VARCHAR(128)) FROM STAT \n" +
" WHERE PLAYER_ID=(?) ",
                                      Statement.RETURN_GENERATED_KEYS);
             statement.setInt(1, player_id);
            System.out.println("\n--------x-----------------------------------------");
          ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount();
         int id=-1;
          
   stmt.close();
   return rest;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    public Object[] getTotalByPlayerId(int player_id)
    {
        
       Object[] result=new Object[21];
          try
        {
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("SELECT PLAYERS.PLAYER_NAME,SUM(TWO_POINT_IN*2+THREE_POINT_IN*3+FREE_POINT_IN*1), SUM(TWO_POINT_IN),SUM(TWO_POINT_TOTAL)," +
"SUM(THREE_POINT_IN),SUM(THREE_POINT_TOTAL),SUM(FREE_POINT_IN),SUM(FREE_POINT_TOTAL)," +
"SUM(ATKBASKET),SUM(DEFBASKET),SUM(ATKBASKET)+SUM(DEFBASKET),SUM(FAST_ATK_SUCCESS),SUM(FAST_ATK_FAIL)+SUM(FAST_ATK_SUCCESS)," +
"SUM(BLOCKSHOT),SUM(ASSIST),SUM(STEAL),SUM(TURNOVER),SUM(ATKFOUL)+SUM(DEFFOUL)+" +
"SUM(TECHFOUL) " +
" FROM STAT JOIN PLAYERS ON STAT.PLAYER_ID=PLAYERS.PLAYER_ID WHERE STAT.PLAYER_ID=(?) GROUP BY PLAYERS.PLAYER_NAME ",Statement.RETURN_GENERATED_KEYS);
             statement.setInt(1, player_id);
          ResultSet rest= statement.executeQuery();
       ResultSetMetaData rsmd = rest.getMetaData();
            int numberCols = rsmd.getColumnCount()-4;
      double percent2point,percent3point,freethrowpoint;
while(rest.next()){
           
         result[0]=rest.getString(1);
         result[1]=rest.getInt(2);
         result[2]=rest.getInt(3);
         result[3]=rest.getInt(4);
              String temp=result[2].toString();
         double z=Double.parseDouble(temp);  
 percent2point=((int)(result[3])==0)?0:(double)z/(int)result[3];
   
         result[4]=percent2point*100+"%";
         result[5]=rest.getInt(5);
         result[6]=rest.getInt(6);
         temp=result[6].toString();
         z=Double.parseDouble(temp);
 percent3point=((int)(result[6])==0)?0:(double)z/(int)result[6];
         result[7]=percent3point*100+"%";
         result[8]=rest.getInt(7);
         result[9]=rest.getInt(8);
         temp=result[9].toString();
         z=Double.parseDouble(temp);
 freethrowpoint=((int)result[9]==0)?0:(double)z/(int)result[9];
         result[10]=freethrowpoint*100+"%";

          for(int x=9;x<19;x++)
          {
              result[x+2]=rest.getInt(x);
          }
}
          System.out.print("\n");
          
           for (int c=1; c<result.length ;c++) {
               System.out.println(result[c]);
           }
   stmt.close();
return result;
        }
        catch (Exception sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
          return result;
    }
    void execQ(ArrayList<String> Query)
    {
       for(String x:Query)
       {
           try {
               PreparedStatement statement = conn.prepareStatement(x,
                       Statement.RETURN_GENERATED_KEYS);
               statement.execute();
           } catch (SQLException ex) {
               Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }
    void deleteAllTeam(int seasonId) {
       try
        {
            stmt = conn.createStatement();
                    PreparedStatement statement = conn.prepareStatement("DELETE FROM STAT WHERE STAT.PLAYER_ID IN( SELECT STAT.PLAYER_ID FROM STAT JOIN PLAYERS ON STAT.PLAYER_ID=PLAYERS.PLAYER_ID WHERE TEAM_ID IN(SELECT PLAYERS.TEAM_ID FROM PLAYERS JOIN TEAM ON PLAYERS.TEAM_ID=TEAM.TEAM_ID WHERE SEASON_ID IN(SELECT TEAM.SEASON_ID FROM TEAM JOIN SEASON ON SEASON.SEASON_ID=TEAM.SEASON_ID WHERE SEASON.SEASON_ID=(?))))",
                                      Statement.RETURN_GENERATED_KEYS);
                          PreparedStatement statement2 = conn.prepareStatement("DELETE FROM PLAYERS WHERE PLAYERS.TEAM_ID IN( SELECT PLAYERS.TEAM_ID FROM PLAYERS JOIN TEAM ON PLAYERS.TEAM_ID=TEAM.TEAM_ID WHERE SEASON_ID IN(SELECT TEAM.SEASON_ID FROM TEAM JOIN SEASON ON TEAM.SEASON_ID=SEASON.SEASON_ID WHERE SEASON.SEASON_ID=(?)))",
                                      Statement.RETURN_GENERATED_KEYS);
                           PreparedStatement statement3 = conn.prepareStatement("DELETE FROM TEAM WHERE TEAM.SEASON_ID=(?) ",
                                      Statement.RETURN_GENERATED_KEYS);
             statement.setInt(1, seasonId);
             statement2.setInt(1, seasonId);
             statement3.setInt(1, seasonId);
            System.out.println("\n--------x-----------------------------------------");
         statement.execute();
         statement2.execute();
         statement3.execute();
          
   stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
  //To change body of generated methods, choose Tools | Templates.
    }

    void deleteSeason(int seasonId) {
      
              try
        {
             deleteAllTeam(seasonId);
             conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM SEASON WHERE SEASON_ID=(?) ",
                                      Statement.RETURN_GENERATED_KEYS);
             statement.setInt(1, seasonId);
             statement.execute();
             stmt.close();
        }
              catch(SQLException e)
              {
                  e.printStackTrace();
              }
       //To change body of generated methods, choose Tools | Templates.
    }
}