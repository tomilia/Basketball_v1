
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tommylee
 */
public class LogAction {
    private PlayerStat targetx;
    private PlayerStat targety;
    private int[][] teamX;
    private int stage;
    private int team_scoreA;
    private int team_scoreB;
    private int foul_teamA;
    private int foul_teamB;
    private boolean checksub=false;
    private String team_name;
    private int timeoutA,timeoutB;
    private int type;
    private int givenMarks;
    public LogAction(PlayerStat targetx){
        this.targetx=targetx;
    }
    public LogAction(String team_name,int timeoutA,int timeoutB,int a,int b)
    {
        this.timeoutA=timeoutA;
        this.timeoutB=timeoutB;
        this.type=2;
          this.team_scoreA=a;
        this.team_scoreB=b;
    }
        public LogAction(PlayerStat a,PlayerStat b)
    {
      this.targetx=a;
      this.targety=b;
    }
       public LogAction(int stage)
    {
        this.stage=stage;
    }
     public LogAction(String team_name,int timeoutA,int timeoutB,int x,int a,int b)
    {
        this.timeoutA=timeoutA;
        this.timeoutB=timeoutB;
        this.type=3;
        this.givenMarks=x;
          this.team_scoreA=a;
        this.team_scoreB=b;
    }
     public LogAction()
     {
         checksub=true;
         
     }
     public boolean reCheckSub(){
         return checksub;
     }
     public PlayerStat getPlayer()
     {
         return targetx;
     }
     public int returnGivenMarks(){
         return givenMarks;
     }
    public int getTimeoutA(){
        return timeoutA;
    }
    public int getType(){
        return type;
    }
    public int getTimeoutB(){
        return timeoutB;
    }
    public int[][] getTeam(){
        
        return teamX;
    }
    public int getFoulteamA(){
        return foul_teamA;
    }
    public int getScoreteamA(){
        return team_scoreA;
    }
        public int getFoulteamB(){
        return foul_teamB;
    }
            public int getScoreteamB(){
        return team_scoreB;
    }
            public String getTeamName(){
                return team_name;
            }
}
