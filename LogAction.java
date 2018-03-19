
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
    private PlayerStat teamx;
    private int[][] teamX;
    private int team_scoreA;
    private int team_scoreB;
    private int foul_teamA;
    private int foul_teamB;
    private boolean checksub=false;
    private String team_name;
    private int timeoutA,timeoutB;
    private int type;
    private int givenMarks;
    public LogAction(int[][] teamXx,String team_name,int team_scoreA,int team_scoreB,int foul_scoreA,int foul_scoreB){
        this.teamX=teamXx;
        
        this.team_name=team_name;
        this.team_scoreA=team_scoreA;
        this.team_scoreB=team_scoreB;
      this.foul_teamA=foul_scoreA;
      this.foul_teamB=foul_scoreB;
      this.type=1;
    }
    public LogAction(String team_name,int timeoutA,int timeoutB,int a,int b)
    {
        this.timeoutA=timeoutA;
        this.timeoutB=timeoutB;
        this.type=2;
          this.team_scoreA=a;
        this.team_scoreB=b;
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
