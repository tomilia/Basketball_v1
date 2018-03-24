/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tommylee
 */
public class PlayerStat {
    int playerNum;
    String playerName;
    boolean firstTeam;
    boolean firstTeamX=false;
    String team_name;
    int [] statistics =new int [18];
    int player_id;
    int player_selectnum;
 /* 
   0 int twopointin=0;
   1 int twopointshoottime=0;
   2 int threepointin=0;
   3 int threepointshoottime=0;
   4 int freepointin=0;
   5 int freepointshoottime=0*;
   6 int attackbasket=0;
   7 int deferencebasket=0;
   8 int fastattacksuccess=0;
   9 int fastattackfailure=0;  
  10 int blockshot=0;
  11 int assist=0;
  12 int steal=0;
  13 int turnover=0;
  14 int atkfoul=0;
  15 int deffoul=0;
  16 int techfoul=0;
  17 int totalscore=0;
*/
    public PlayerStat(int playerNum,String playerName, boolean firstTeam,String team)
    {
        this.team_name=team;
        this.playerNum=playerNum;
        this.playerName=playerName;
        this.firstTeam=firstTeam;
        for(int x=0;x<18;x++)
        statistics[x]=0;
    }

   public PlayerStat(int player_id, int player_num, String player_name, boolean player_first_team, String team_name) {
               this.team_name=team_name;
               this.player_id=player_id;
        this.playerNum=player_num;
        this.playerName=player_name;
        this.firstTeam=player_first_team;
        for(int x=0;x<18;x++)
        statistics[x]=0;//To change body of generated methods, choose Tools | Templates.
    }
      public PlayerStat(int num,int player_id, int player_num, String player_name, boolean player_first_team, String team_name) {
               this.team_name=team_name;
               this.player_id=player_id;
        this.playerNum=player_num;
        this.playerName=player_name;
        this.firstTeam=player_first_team;
        for(int x=0;x<18;x++)
        statistics[x]=0;
        this.player_selectnum=num;//To change body of generated methods, choose Tools | Templates.
    }
    public int getPlayerNum()
   {
       return playerNum;
   }
    public void setFirstTeamX(){
        firstTeamX=true;
    }
       public void setPlayerNum(int x){
        this.playerNum=x;
    }
       public void setPlayerName(String tmp){
        this.playerName=tmp;
    }
     public void setoff(){
        firstTeamX=false;
    }
    public boolean getFirstTeamX(){
        return firstTeamX;
    }
     public int getPlayerSelectNum()
   {
       return player_selectnum;
   }
       public String getPlayerName()
   {
       return playerName;
   }
          public boolean getFirstTeam()
   {
       return firstTeam;
   }
          public String getTeam()
   {
       return team_name;
   }
    public void setOptions(int option){
      switch(option)
      {
        case 0:
        statistics[0]++;
        statistics[1]++;
        break;
                case 1:
        statistics[1]++;
        break;
                case 2:
        statistics[2]++;
        statistics[3]++;
        break;
                case 3:
        statistics[3]++;
        break;
                case 4:
        statistics[4]++;
        statistics[5]++;
        break;
                case 5:
        statistics[5]++;
        break;
                case 6:
        statistics[6]++;
        break;
                case 7:
        statistics[7]++;
        break;
                case 8:
        statistics[8]++;
        statistics[9]++;
        break;
           case 9:
        statistics[9]++;
        break;
           case 10:
        statistics[10]++;
        break;
           case 11:
        statistics[11]++;
        break;
           case 12:
        statistics[12]++;
        break;
           case 13:
        statistics[13]++;
        break;
           case 14:
        statistics[14]++;
   
        break;
           case 15:
        statistics[15]++;
   
        break;
           case 16:
        statistics[16]++;
     
        break;
      
      }
      statistics[17]=statistics[0]*2+statistics[2]*3+statistics[4];
    }
    public void setReverseOptions(int option){
      switch(option)
      {
        case 0:
        statistics[0]--;
        statistics[1]--;
        break;
                case 1:
        statistics[1]--;
        break;
                case 2:
        statistics[2]--;
        statistics[3]--;
        break;
                case 3:
        statistics[3]--;
        break;
                case 4:
        statistics[4]--;
        statistics[5]--;
        break;
                case 5:
        statistics[5]--;
        break;
                case 6:
        statistics[6]--;
        break;
                case 7:
        statistics[7]--;
        break;
                case 8:
        statistics[8]--;
        statistics[9]--;
        break;
           case 9:
        statistics[9]--;
        break;
           case 10:
        statistics[10]--;
        break;
           case 11:
        statistics[11]--;
        break;
           case 12:
        statistics[12]--;
        break;
           case 13:
        statistics[13]--;
        break;
           case 14:
        statistics[14]--;
   
        break;
           case 15:
        statistics[15]--;
   
        break;
           case 16:
        statistics[16]--;
     
        break;
      
      }
      statistics[17]=statistics[0]*2+statistics[2]*3+statistics[4];
    }
    public int getOptions(int options)
    {
        return statistics[options];
    }
 
    public int getplayerId()
    {
        return player_id;
    }

}
