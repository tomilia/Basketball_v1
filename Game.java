
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.UndoableEdit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tommylee
 */
public class Game extends javax.swing.JFrame implements Runnable{
        Thread t;
        DBConnection db;
    int min=12,sec=0,minsec=0;
    String str="",nstr="",mstr="",dstr="";
    int cnt=0,cnt2=0;
    int teamA_id;
    int teamB_id;
    String teamA_name;
    String teamB_name;
    int score_teamA=0;
    int score_teamB=0;
    ArrayList<Object> teamAstagescore=new ArrayList<>();
    ArrayList<Object> teamBstagescore=new ArrayList<>();
    int stage=1;
    UndoableEdit undo;
    int options=-1;
    PlayerStat firstTeamA[]=new PlayerStat[5];
        PlayerStat firstTeamB[]=new PlayerStat[5];
            java.util.List<PlayerStat> teamA=new ArrayList<>();
        java.util.List<PlayerStat> teamB=new ArrayList<>();
        
            String col[] = {"號碼","球員名稱","得分", "助攻", "籃板", "犯規"};
              String logger[] = {"局","時間","參賽隊","球員","事件","結果"};
        DefaultTableModel tableModela = new DefaultTableModel(col, 0);
        DefaultTableModel tableModelb = new DefaultTableModel(col, 0);
        DefaultTableModel logtable=new DefaultTableModel(logger,0);
          String st[] = {"參賽隊","局 1","局 2","局 3","局 4"};
         DefaultTableModel stageX = new DefaultTableModel(st, 0);
         PlayerStat sortTeamA[];
          PlayerStat sortTeamB[];
        int foul_teamA=0;
        int foul_teamB=0;
        int timeout_teamA=0;
        int timeout_teamB=0;
        int seasonx;
        LinkedList<LogAction> gameListA;
        LinkedList<LogAction> gameListB;
        String lastChangedteam;
        PlayerStat currentSubingPlayer;
        PlayerStat previousSubingPlayer;
        JButton previousRightClick;
        JTextField firstName = new JTextField();
JComboBox subout = new JComboBox();
JComboBox  subin = new JComboBox();
DefaultComboBoxModel sub1=new DefaultComboBoxModel();
DefaultComboBoxModel sub2=new DefaultComboBoxModel();

        final JComponent[] inputs = new JComponent[] {
        new JLabel("換出球員"),
        subout,
       
        new JLabel("換入球員"),
        subin
};
    /**
     * Creates new form Game
     * @param teamA_id
     * @param teamB_id
     */
         /* 
   0 int twopointin=0;
   1 int twopointshoottime=0;
   2 int threepointin=0;
   3 int threepointshoottime=0;
   4 int freepointin=0;
   5 int freepointshoottime=0;
   6 int attackbasket=0;
   7 int deferencebasket=0;
   8 int fastattacksuccess=0;
   9 int fastattacktotal=0;  
  10 int blockshot=0;
  11 int assist=0;
  12 int steal=0;
  13 int turnover=0;
  14 int foul=0;
  15 int totalscore=0;
*/
    public Game() {
        initComponents();
    }
    public Game(int teamA_id,int teamB_id,int seasonx)
    {
             this.teamA_id=teamA_id;
        this.teamB_id=teamB_id;
        this.seasonx=seasonx;
        initPlayerObj();
        GameSubDialog sd=new GameSubDialog(teamA_id,teamB_id,seasonx,teamA_name,teamB_name,teamA,teamB);
        sd.setAlwaysOnTop(true);
        sd.setModal(true);
        
        sd.setVisible(true);
 
    }
       private void Swap(PlayerStat[] array, int indexA, int indexB)
    {
        PlayerStat tmp = array[indexA];
        array[indexA] = array[indexB];
        array[indexB] = tmp;
    }
    public Game(int teamA_id,int teamB_id,int seasonx,PlayerStat[] firstTeamA,PlayerStat[] firstTeamB)
    {
        gameListA=new LinkedList<>();
        t=new Thread(this);
        this.teamA_id=teamA_id;
        this.teamB_id=teamB_id;
        this.seasonx=seasonx;
 this.firstTeamA=firstTeamA;
        this.firstTeamB=firstTeamB;
        initPlayerObj();
 
     
        

  
        initStageTable();
        initComponents();
        resetTimer();
    }
    private void initStageTable(){
        
       teamAstagescore.add(teamA_name);
       for(int a=1;a<5;a++)teamAstagescore.add(0);
        teamBstagescore.add(teamB_name);
       for(int a=1;a<5;a++)teamBstagescore.add(0);
       stageX.addRow(teamAstagescore.toArray());
       stageX.addRow(teamBstagescore.toArray());
    }
    private void initPlayerObj(){
                 try {
                db=new DBConnection();
       teamA_name=db.getteamNamebyID(this.teamA_id);
       teamB_name=db.getteamNamebyID(this.teamB_id);
       
               teamA=db.getPlayer(this.teamA_name,seasonx);
               teamB=db.getPlayer(this.teamB_name,seasonx);
                  sortTeamA=new PlayerStat[teamA.size()];
                sortTeamB=new PlayerStat[teamB.size()];
                teamA.toArray(sortTeamA);
                teamB.toArray(sortTeamB);
        
     for (int i = sortTeamA.length - 1; i > 0; --i)
            for (int j = 0; j < i; ++j)
                if (sortTeamA[j].getPlayerNum() > sortTeamA[j + 1].getPlayerNum())//sorted 
                    //
                    Swap(sortTeamA, j, j + 1);
       initializeSortedPlayerFirstTeam(sortTeamA,firstTeamA);
     for (int i = sortTeamB.length - 1; i > 0; --i)
            for (int j = 0; j < i; ++j)
                if (sortTeamB[j].getPlayerNum() > sortTeamB[j + 1].getPlayerNum())
                    Swap(sortTeamB, j, j + 1);
            initializeSortedPlayerFirstTeam(sortTeamB,firstTeamB);
            } catch (SQLException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    private void exportcsv(){
        JFileChooser chooser = new JFileChooser();
            int retrival = chooser.showSaveDialog(null);
                if (retrival == JFileChooser.APPROVE_OPTION) {
                   try{
                       File fp=new File(chooser.getSelectedFile()+".csv");
                        if ( fp.exists() ) {
           String msg = "檔案 \"{0}\" 已存在!\n要進行覆蓋?";
           msg = MessageFormat.format( msg, new Object[] { fp.getName() } );
           String title = chooser.getDialogTitle();
           int option = JOptionPane.showConfirmDialog( this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
           if ( option == JOptionPane.NO_OPTION ) {
           return;
           } // end if
           } // end if
          OutputStreamWriter bw=new OutputStreamWriter(new FileOutputStream(fp), StandardCharsets.UTF_8);
          bw.write('\uFEFF');
          bw.write("參賽隊:,"+teamA_name+"\n");
            String colx[] = {"Name","No","PTS", "2 Points FG", "FGA","%","3 Points FG","FGA","%","Free throw FG"
             ,"FGA","%","OR","DR","TR","BS","AS","ST","TO","F","EFF"};
            bw.write("\n");
          for(int b=0;b<colx.length;b++)
           bw.write(colx[b]+",");
          bw.write("\n");
           int []temp=new int[17];
           int eff;
           
         for(int x=0;x<teamA.size();x++)
         {
            bw.write(teamA.get(x).getPlayerName()+","+teamA.get(x).getPlayerNum()+",");
                  
            eff=(teamA.get(x).getOptions(0)*2+teamA.get(x).getOptions(2)*3+teamA.get(x).getOptions(4)*1)+teamA.get(x).getOptions(6)+teamA.get(x).getOptions(7)+teamA.get(x).getOptions(10)+teamA.get(x).getOptions(11)+teamA.get(x).getOptions(12)-(teamA.get(x).getOptions(1)-teamA.get(x).getOptions(0))-(teamA.get(x).getOptions(3)-teamA.get(x).getOptions(2))-(teamA.get(x).getOptions(5)-teamA.get(x).getOptions(4))-teamA.get(x).getOptions(13);
                
        temp [0]= teamA.get(x).getOptions(0)*2+teamA.get(x).getOptions(2)*3+teamA.get(x).getOptions(4)*1;
        temp [1]= teamA.get(x).getOptions(0);
        temp [2]= teamA.get(x).getOptions(1);
        //percent 100 2point
        temp [3]= teamA.get(x).getOptions(2);
        temp [4]= teamA.get(x).getOptions(3);
        //percent 100 3point
        temp [5]= teamA.get(x).getOptions(4);
        temp [6]= teamA.get(x).getOptions(5);
        //free throw
        temp [7]= teamA.get(x).getOptions(6);
        temp [8]= teamA.get(x).getOptions(7);
        //robound total
        temp [9]=teamA.get(x).getOptions(10);
        temp [10]=teamA.get(x).getOptions(11);
        temp [11]=teamA.get(x).getOptions(12);
        temp [12]=teamA.get(x).getOptions(13);
        temp [13]=teamA.get(x).getOptions(14);
        temp[14]=teamA.get(x).getOptions(15);
        temp[15]=teamA.get(x).getOptions(16);
        temp [16]=eff;
                      double percent2point,percent3point,freethrowpoint;
                   percent2point=(temp[2]==0)?0:((double)temp[1])/(int)temp[2];
                  percent3point=(temp[4]==0)?0:((double)temp[3])/(int)temp[4];
                  freethrowpoint=(temp[6]==0)?0:((double)temp[5])/(int)temp[6];   
             bw.write(temp[0]+","+temp[1]+","+temp[2]+","+(percent2point*100)+"%"+","+temp[3]+","+temp[4]+","+percent3point*100+"%"+
             ","+temp[5]+","+temp[6]+","+(freethrowpoint*100)+"%"+","+temp [7]+","+temp [8]+","+(temp [7]+temp [8])+","+
             temp[9]+","+temp[10]+","+temp[11]+","+temp[12]+","+(temp[13]+temp[14]+temp[15])+","+eff);
             bw.write("\n");
         }
         bw.write("\n參賽隊:,"+teamB_name+"\n");
                   bw.write("\n");
          for(int b=0;b<colx.length;b++)
           bw.write(colx[b]+",");
          bw.write("\n");
         for(int x=0;x<teamB.size();x++)
         {
            bw.write(teamB.get(x).getPlayerName()+","+teamB.get(x).getPlayerNum()+",");
                  
            eff=(teamB.get(x).getOptions(0)*2+teamB.get(x).getOptions(2)*3+teamB.get(x).getOptions(4)*1)+teamB.get(x).getOptions(6)+teamB.get(x).getOptions(7)+teamB.get(x).getOptions(10)+teamB.get(x).getOptions(11)+teamB.get(x).getOptions(12)-(teamB.get(x).getOptions(1)-teamB.get(x).getOptions(0))-(teamB.get(x).getOptions(3)-teamB.get(x).getOptions(2))-(teamB.get(x).getOptions(5)-teamB.get(x).getOptions(4))-teamB.get(x).getOptions(13);
                
        temp [0]= teamB.get(x).getOptions(0)*2+teamB.get(x).getOptions(2)*3+teamB.get(x).getOptions(4)*1;
        temp [1]= teamB.get(x).getOptions(0);
        temp [2]= teamB.get(x).getOptions(1);
        //percent 100 2point
        temp [3]= teamB.get(x).getOptions(2);
        temp [4]= teamB.get(x).getOptions(3);
        //percent 100 3point
        temp [5]= teamB.get(x).getOptions(4);
        temp [6]= teamB.get(x).getOptions(5);
        //free throw
        temp [7]= teamB.get(x).getOptions(6);
        temp [8]= teamB.get(x).getOptions(7);
        //robound total
        temp [9]=teamB.get(x).getOptions(10);
        temp [10]=teamB.get(x).getOptions(11);
        temp [11]=teamB.get(x).getOptions(12);
        temp [12]=teamB.get(x).getOptions(13);
        temp [13]=teamB.get(x).getOptions(14);
        temp[14]=teamB.get(x).getOptions(15);
        temp[15]=teamB.get(x).getOptions(16);
        temp [16]=eff;
                      double percent2point,percent3point,freethrowpoint;
                   percent2point=(temp[2]==0)?0:((double)temp[1])/(int)temp[2];
                  percent3point=(temp[4]==0)?0:((double)temp[3])/(int)temp[4];
                  freethrowpoint=(temp[6]==0)?0:((double)temp[5])/(int)temp[6];   
             bw.write(temp[0]+","+temp[1]+","+temp[2]+","+(percent2point*100)+"%"+","+temp[3]+","+temp[4]+","+percent3point*100+"%"+
             ","+temp[5]+","+temp[6]+","+(freethrowpoint*100)+"%"+","+temp [7]+","+temp [8]+","+(temp [7]+temp [8])+","+
             temp[9]+","+temp[10]+","+temp[11]+","+temp[12]+","+(temp[13]+temp[14]+temp[15])+","+eff);
             bw.write("\n");
         }
           bw.close();
                   }
                   catch(Exception e)
                   {
                       
                   }
                }
                
    }
    private void initializeSortedPlayerFirstTeam(PlayerStat[] sortTeamX,PlayerStat[] firstTeamX){
        try{
        for(PlayerStat x:firstTeamX)
            for(PlayerStat y:sortTeamX)
                if(x.getplayerId()==y.getplayerId())y.setFirstTeamX();
        
        for(PlayerStat h:sortTeamX)System.out.println("suc:"+h.getFirstTeamX());
        }
        catch(Exception e){}
    }
    private void subsititute(PlayerStat previoussub,PlayerStat currentsub,String teamX){
     if(teamX.equals(teamA_name))
        for(int tmp=0;tmp<teamA.size();tmp++)
        {
              if(teamA.get(tmp).getplayerId()==currentsub.getplayerId())
            for(int f=0;f<firstTeamA.length;f++)
            {
               if(firstTeamA[f].getplayerId()==previoussub.getplayerId())
               {
                   firstTeamA[f]=currentsub;
             switch (f) {
             case 0:
                 playerOneA.setText("#"+firstTeamA[f].getPlayerNum()+" "+firstTeamA[f].getPlayerName());
                 break;
             case 1:
                 playerOneA1.setText("#"+firstTeamA[f].getPlayerNum()+" "+firstTeamA[f].getPlayerName());
                 break;
             case 2:
                 playerOneA2.setText("#"+firstTeamA[f].getPlayerNum()+" "+firstTeamA[f].getPlayerName());
                 break;
             case 3:
                 playerOneA3.setText("#"+firstTeamA[f].getPlayerNum()+" "+firstTeamA[f].getPlayerName());
                 break;
             case 4:
                 playerOneA4.setText("#"+firstTeamA[f].getPlayerNum()+" "+firstTeamA[f].getPlayerName());
                 break;
             default:
                 break;
         }
                     addLog(stage,timelabel.getText(),teamA_name,previoussub.getPlayerNum()+" "+previoussub.getPlayerName()+"-"+firstTeamA[f].getPlayerNum()+" "+firstTeamA[f].getPlayerName(),25);
                 
    
                    LogAction e=new LogAction(previoussub,firstTeamA[f]);
                    gameListA.push(e);
                    //WARNING
                       
               }
            }   
             
          
        }
     else
     {
        for(int tmp=0;tmp<teamB.size();tmp++)
        {
              if(teamB.get(tmp).getplayerId()==currentsub.getplayerId())
            for(int f=0;f<firstTeamB.length;f++)
            {
               if(firstTeamB[f].getplayerId()==previoussub.getplayerId())
               {
                   firstTeamB[f]=currentsub;
                 switch (f) {
case 0:
 playerOneB.setText("#"+firstTeamB[f].getPlayerNum()+" "+firstTeamB[f].getPlayerName());
 break;
case 1:
 playerOneB1.setText("#"+firstTeamB[f].getPlayerNum()+" "+firstTeamB[f].getPlayerName());
 break;
case 2:
 playerOneB2.setText("#"+firstTeamB[f].getPlayerNum()+" "+firstTeamB[f].getPlayerName());
 break;
case 3:
 playerOneB3.setText("#"+firstTeamB[f].getPlayerNum()+" "+firstTeamB[f].getPlayerName());
 break;
case 4:
 playerOneB4.setText("#"+firstTeamB[f].getPlayerNum()+" "+firstTeamB[f].getPlayerName());
                     break;
                 default:
                     break;
             }
                 addLog(stage,timelabel.getText(),teamB_name,previoussub.getPlayerNum()+" "+previoussub.getPlayerName(),25);
                     addLog(stage,timelabel.getText(),teamB_name,firstTeamB[f].getPlayerNum()+" "+firstTeamB[f].getPlayerName(),26);
                     LogAction e=new LogAction(previoussub);
                    gameListA.push(e);
                    e=new LogAction(firstTeamA[f]);
                    gameListA.push(e);
               
       
               }
            }   
             
          
        } 
     }
  
    }
    
    private void rightClickButtonAction(JButton playerX,PlayerStat y,String teamX)
    {
        //checkpoint
        java.awt.Color teamX_color;
        playerX.setBackground(Color.ORANGE);
        currentSubingPlayer=y;
        
        if(teamX.equals(teamA_name))
        teamX_color=new java.awt.Color(0, 153, 153);
        else
        teamX_color=new java.awt.Color(153, 0, 0);
        if(currentSubingPlayer==previousSubingPlayer&&currentSubingPlayer.getFirstTeamX()){currentSubingPlayer=null;previousSubingPlayer=null;previousRightClick.setBackground(new java.awt.Color(39, 139, 54));return;}
         if(currentSubingPlayer==previousSubingPlayer&&!currentSubingPlayer.getFirstTeamX()){currentSubingPlayer=null;previousSubingPlayer=null;previousRightClick.setBackground(teamX_color);return;}
        try{
            
        if(!currentSubingPlayer.getFirstTeamX()&&!previousSubingPlayer.getFirstTeamX())
        {
        previousRightClick.setBackground(teamX_color);
        }
        if(currentSubingPlayer.getFirstTeamX()&&previousSubingPlayer.getFirstTeamX())
             previousRightClick.setBackground(new java.awt.Color(39, 139, 54));
       if(!currentSubingPlayer.getFirstTeamX()&&previousSubingPlayer.getFirstTeamX())
       {
           previousRightClick.setBackground(teamX_color);
           playerX.setBackground(new java.awt.Color(39, 139, 54));
           currentSubingPlayer.setFirstTeamX();
           previousSubingPlayer.setoff();
           System.out.print("<->:"+currentSubingPlayer.getPlayerName()+" "+previousSubingPlayer.getPlayerName());
           subsititute(previousSubingPlayer,currentSubingPlayer,teamX);
           currentSubingPlayer=null;
           
       }
         if(currentSubingPlayer.getFirstTeamX()&&!previousSubingPlayer.getFirstTeamX())
       {
           playerX.setBackground(teamX_color);
  
           previousRightClick.setBackground(new java.awt.Color(39, 139, 54));
           currentSubingPlayer.setoff();
           previousSubingPlayer.setFirstTeamX();
           System.out.print("X<->:"+currentSubingPlayer.getPlayerName()+" "+previousSubingPlayer.getPlayerName());
           subsititute(currentSubingPlayer,previousSubingPlayer,teamX);
          currentSubingPlayer=null;
           
       }
        }
        
        catch(Exception e){}
        previousRightClick=playerX;
        previousSubingPlayer=currentSubingPlayer;
     
    }
    private void setHighlighted(JButton playerX,PlayerStat x)
    {
        playerX.setText(String.valueOf(x.getPlayerNum()));
        if(x.getFirstTeamX())playerX.setBackground(new java.awt.Color(39, 139, 54));
    }
    private void endgame(){
        int p=1;
        for(int x=0;x<teamA.size();x++)
        {
            p=db.insertStat(teamA.get(x),teamA_name,teamB_name);
              if(p==-1) { 
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "網路連線失敗", "Warning",
        JOptionPane.WARNING_MESSAGE);
            return;
        }
        }
        for(int x=0;x<teamB.size();x++)
        {
            p=db.insertStat(teamB.get(x),teamA_name,teamB_name);
                if(p==-1) { 
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "網路連線失敗", "Warning",
        JOptionPane.WARNING_MESSAGE);
            return;
        }
        }

    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
 private void initComponents() {

      jLabel8 = new javax.swing.JLabel();
      buttonGroup1 = new javax.swing.ButtonGroup();
      timeStart = new javax.swing.JButton();
      resetTimer = new javax.swing.JButton();
      timeoutteamA = new javax.swing.JLabel();
      timeoutteamB = new javax.swing.JLabel();
      options16 = new javax.swing.JButton();
      options1 = new javax.swing.JButton();
      options10 = new javax.swing.JButton();
      options4 = new javax.swing.JButton();
      optionsx=new javax.swing.JButton();
      options8 = new javax.swing.JButton();
      options14 = new javax.swing.JButton();
      options11 = new javax.swing.JButton();
      options15 = new javax.swing.JButton();
      options7 = new javax.swing.JButton();
      options6 = new javax.swing.JButton();
      options9 = new javax.swing.JButton();
      jSeparator1 = new javax.swing.JSeparator();
      jScrollPane1 = new javax.swing.JScrollPane();
      teamBstatTable = new javax.swing.JTable();
      jScrollPane2 = new javax.swing.JScrollPane();
      log = new javax.swing.JTable();
      jScrollPane3 = new javax.swing.JScrollPane();
      teamAstatTable = new javax.swing.JTable();
      jSeparator2 = new javax.swing.JSeparator();
      options0 = new javax.swing.JButton();
      options2 = new javax.swing.JButton();
      options3 = new javax.swing.JButton();
      options13 = new javax.swing.JButton();
      teamAPlayer2 = new javax.swing.JButton();
      teamAPlayer4 = new javax.swing.JButton();
      teamAPlayer1 = new javax.swing.JButton();
      teamAPlayer6 = new javax.swing.JButton();
      teamAPlayer3 = new javax.swing.JButton();
      teamAPlayer10 = new javax.swing.JButton();
      teamAPlayer5 = new javax.swing.JButton();
      teamAPlayer9 = new javax.swing.JButton();
      teamAPlayer7 = new javax.swing.JButton();
      teamAPlayer8 = new javax.swing.JButton();
      teamBPlayer2 = new javax.swing.JButton();
      teamBPlayer4 = new javax.swing.JButton();
      teamBPlayer1 = new javax.swing.JButton();
      teamBPlayer6 = new javax.swing.JButton();
      teamBPlayer3 = new javax.swing.JButton();
      teamBPlayer10 = new javax.swing.JButton();
      teamBPlayer11 = new javax.swing.JButton();
      teamBPlayer12 = new javax.swing.JButton();
      teamBPlayer13 = new javax.swing.JButton();
      teamBPlayer14 = new javax.swing.JButton();
      teamBPlayer15 = new javax.swing.JButton();
      teamBPlayer5 = new javax.swing.JButton();
      teamBPlayer9 = new javax.swing.JButton();
      teamBPlayer7 = new javax.swing.JButton();
      teamBPlayer8 = new javax.swing.JButton();
      options12 = new javax.swing.JButton();
      notification = new javax.swing.JLabel();
      jButton1 = new javax.swing.JButton();
      Rewind = new javax.swing.JButton();
      TimeoutTeamA = new javax.swing.JButton();
      TimeoutTeamB = new javax.swing.JButton();
      back = new javax.swing.JButton();
      jPanel1 = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();
      jLabel2 = new javax.swing.JLabel();
      foulteamA = new javax.swing.JLabel();
      jLabel4 = new javax.swing.JLabel();
      timeoutA = new javax.swing.JLabel();
      jLabel10 = new javax.swing.JLabel();
      timeoutB = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      foulteamB = new javax.swing.JLabel();
      timelabel = new javax.swing.JLabel();
      jPanel3 = new javax.swing.JPanel();
      teamAlabel = new javax.swing.JLabel();
      jPanel18 = new javax.swing.JPanel();
      jPanel21 = new javax.swing.JPanel();
      jPanel16 = new javax.swing.JPanel();
      jPanel20 = new javax.swing.JPanel();
      jPanel23 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jPanel15 = new javax.swing.JPanel();
      jPanel17 = new javax.swing.JPanel();
      jPanel19 = new javax.swing.JPanel();
      jPanel22 = new javax.swing.JPanel();
      teamBlabel = new javax.swing.JLabel();
      jPanel4 = new javax.swing.JPanel();
      teamAscore = new javax.swing.JLabel();
      jPanel25 = new javax.swing.JPanel();
      jPanel12 = new javax.swing.JPanel();
      jPanel5 = new javax.swing.JPanel();
      jPanel11 = new javax.swing.JPanel();
      jPanel9 = new javax.swing.JPanel();
      jPanel14 = new javax.swing.JPanel();
      jLabel3 = new javax.swing.JLabel();
      jPanel13 = new javax.swing.JPanel();
      stageLabel = new javax.swing.JLabel();
      jPanel24 = new javax.swing.JPanel();
      jPanel7 = new javax.swing.JPanel();
      jPanel6 = new javax.swing.JPanel();
      jPanel8 = new javax.swing.JPanel();
      jPanel10 = new javax.swing.JPanel();
      teamBscore = new javax.swing.JLabel();
      plus1A = new javax.swing.JButton();
      minus1A = new javax.swing.JButton();
      plus1B = new javax.swing.JButton();
      minus1B = new javax.swing.JButton();
      jButton2 = new javax.swing.JButton();
      addSec=new javax.swing.JButton();
      jButton3 = new javax.swing.JButton();
      addMilliSecond = new javax.swing.JButton();
      minusMilliSecond = new javax.swing.JButton();
      addMin=new javax.swing.JButton();
      minusMin=new javax.swing.JButton();
      playerOneA = new javax.swing.JButton();
      playerOneA1 = new javax.swing.JButton();
      playerOneA2 = new javax.swing.JButton();
      playerOneA3 = new javax.swing.JButton();
      playerOneA4 = new javax.swing.JButton();
      playerOneB1 = new javax.swing.JButton();
      playerOneB2 = new javax.swing.JButton();
      playerOneB3 = new javax.swing.JButton();
      playerOneB4 = new javax.swing.JButton();
      playerOneB = new javax.swing.JButton();
      subA5 = new javax.swing.JButton();
      subA1 = new javax.swing.JButton();
      subA2 = new javax.swing.JButton();
      subA3 = new javax.swing.JButton();
      subA4 = new javax.swing.JButton();
      subB5 = new javax.swing.JButton();
      subB1 = new javax.swing.JButton();
      subB2 = new javax.swing.JButton();
      subB3 = new javax.swing.JButton();
      subB4 = new javax.swing.JButton();
      jScrollPane5 = new javax.swing.JScrollPane(stageTable, JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jScrollPane4 = new javax.swing.JScrollPane();
      stageTable = new javax.swing.JTable();
      teamAPlayer11 = new javax.swing.JButton();
      teamAPlayer12 = new javax.swing.JButton();
      teamAPlayer13 = new javax.swing.JButton();
      teamAPlayer14 = new javax.swing.JButton();
      teamAPlayer15 = new javax.swing.JButton();
      minusSec=new javax.swing.JButton();
              jLabel8.setText("暫停");

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

      timeStart.setText("開始計時");
      timeStart.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              timeStartActionPerformed(evt);
          }
      });
      getContentPane().add(timeStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 120, -1, -1));

      resetTimer.setText("取消計時");
      resetTimer.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              resetTimerActionPerformed(evt);
          }
      });
      getContentPane().add(resetTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 120, -1, -1));
      getContentPane().add(timeoutteamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, -1));

      timeoutteamB.setToolTipText("");
      getContentPane().add(timeoutteamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 60, 50, -1));

      options16.setText("技術犯規");
      options16.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options16ActionPerformed(evt);
          }
      });
      getContentPane().add(options16, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 360, 80, -1));

      options1.setText("兩分球失誤");
      options1.setMaximumSize(new java.awt.Dimension(99, 29));
      options1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options1ActionPerformed(evt);
          }
      });
      getContentPane().add(options1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 210, 240, -1));

      options10.setText("蓋帽");
      options10.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options10ActionPerformed(evt);
          }
      });
      getContentPane().add(options10, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 390, 240, -1));

      options4.setText("罰球命中");
      options4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options4ActionPerformed(evt);
          }
      });
      getContentPane().add(options4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 510, 120, 40));
      optionsx.setText("罰球失誤");
      optionsx.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              optionsxActionPerformed(evt);
          }
      });
      getContentPane().add(optionsx, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 510, 120, 40));

      options8.setText("快攻成功");
      options8.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options8ActionPerformed(evt);
          }
      });
      getContentPane().add(options8, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 480, 120, -1));

      options14.setText("進攻犯規");
      options14.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options14ActionPerformed(evt);
          }
      });
      getContentPane().add(options14, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 360, 80, -1));

      options11.setText("助攻");
      options11.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options11ActionPerformed(evt);
          }
      });
      getContentPane().add(options11, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 300, 240, 30));

      options15.setText("防守犯規");
      options15.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options15ActionPerformed(evt);
          }
      });
      getContentPane().add(options15, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 360, 80, -1));

      options7.setText("防守籃板");
      options7.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options7ActionPerformed(evt);
          }
      });
      getContentPane().add(options7, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 450, 120, -1));

      options6.setText("進攻籃板");
      options6.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options6ActionPerformed(evt);
          }
      });
      getContentPane().add(options6, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 450, 120, -1));

      options9.setText("快攻失敗");
      options9.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options9ActionPerformed(evt);
          }
      });
      getContentPane().add(options9, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 480, 120, -1));
      getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

      teamBstatTable.setModel(tableModelb);
      int counterb=1;
      for(PlayerStat x:teamB)
      {

          Object[] objs = {x.playerNum,x.playerName,0,0,0,0};
          tableModelb.addRow(objs);
      }
      jScrollPane1.setViewportView(teamBstatTable);

      getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 560, 350, 190));

      log.setModel(logtable);
      jScrollPane2.setViewportView(log);
            final RowPopup pop=new RowPopup(log);
        log.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if(SwingUtilities.isRightMouseButton(evt)){
                        log.setRowSelectionInterval(log.rowAtPoint(evt.getPoint()), log.rowAtPoint(evt.getPoint()));
                       
             pop.show(evt.getComponent(), evt.getX(), evt.getY());
        }
            }
        });
      getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 560, 690, 190));
     
      int countera=1;
      for(PlayerStat x:teamA)
      {

          Object[] objs = {x.playerNum,x.playerName,x.getOptions(17),0,0,0};
          tableModela.addRow(objs);
      }
      teamAstatTable.setModel(tableModela);
      jScrollPane3.setViewportView(teamAstatTable);

      getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 340, 190));
      getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

      options0.setText("兩分球命中");
      options0.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options0ActionPerformed(evt);
          }
      });
      getContentPane().add(options0, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 180, 240, 30));

      options2.setText("三分球命中");
      options2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options2ActionPerformed(evt);
          }
      });
      getContentPane().add(options2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 240, 240, -1));

      options3.setText("三分球失誤");
      options3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options3ActionPerformed(evt);
          }
      });
      getContentPane().add(options3, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 270, 240, 30));

      options13.setText("失誤");
      options13.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options13ActionPerformed(evt);
          }
      });
      getContentPane().add(options13, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 420, 240, -1));

      teamAPlayer2.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer2.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer2.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer2,sortTeamA[1]);
      }
      catch(Exception e){

      }
      teamAPlayer2.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer2MousePressed(evt);
          }
      });
      teamAPlayer2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer2ActionPerformed(evt);
          }
      });
      setHighlighted(teamAPlayer2,sortTeamA[1]);
      getContentPane().add(teamAPlayer2, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 510, 35, 35));

      teamAPlayer4.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer4.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer4.setForeground(new java.awt.Color(255, 255, 255));
      try{
          setHighlighted(teamAPlayer4,sortTeamA[3]);
      }
      catch(Exception e){

      }
      teamAPlayer4.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer4MousePressed(evt);
          }
      });
      teamAPlayer4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer4ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer4, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 510, 35, 35));

      teamAPlayer1.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer1.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer1.setForeground(new java.awt.Color(255, 255, 255));
      teamAPlayer1.setText(String.valueOf(sortTeamA[0].getPlayerNum()));
      try{

          setHighlighted(teamAPlayer3,sortTeamA[2]);
      }
      catch(Exception e){

      }
      teamAPlayer1.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer1MousePressed(evt);
          }
      });
      teamAPlayer1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer1ActionPerformed(evt);
          }
      });
      setHighlighted(teamAPlayer1,sortTeamA[0]);
      getContentPane().add(teamAPlayer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 35, 35));

      teamAPlayer6.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer6.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer6.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer6,sortTeamA[5]);
      }
      catch(Exception e){

      }
      teamAPlayer6.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer6MousePressed(evt);
          }
      });
      teamAPlayer6.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer6ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer6, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 510, 35, 35));

      teamAPlayer3.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer3.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer3.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer3,sortTeamA[2]);
      }
      catch(Exception e){

      }
      teamAPlayer3.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer3MousePressed(evt);
          }
      });
      teamAPlayer3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer3ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 510, 35, 35));

      teamAPlayer10.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer10.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer10.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer10,sortTeamA[9]);
      }
      catch(Exception e){

      }
      teamAPlayer10.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer10MousePressed(evt);
          }
      });
      teamAPlayer10.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer10ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer10, new org.netbeans.lib.awtextra.AbsoluteConstraints(315, 510, 35,35));

      teamAPlayer5.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer5.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer5.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer5,sortTeamA[4]);
      }
      catch(Exception e){

      }
      teamAPlayer5.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer5MousePressed(evt);
          }
      });
      teamAPlayer5.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer5ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 510, 35, 35));

      teamAPlayer9.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer9.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer9.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer9,sortTeamA[8]);
      }
      catch(Exception e){

      }
      teamAPlayer9.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer9MousePressed(evt);
          }
      });
      teamAPlayer9.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer9ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer9, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 510, 35,35));

      teamAPlayer7.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer7.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer7.setForeground(new java.awt.Color(255, 255, 255));
      try{
          setHighlighted(teamAPlayer7,sortTeamA[6]);
      }
      catch(Exception e){

      }
      teamAPlayer7.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer7MousePressed(evt);
          }
      });
      teamAPlayer7.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer7ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer7, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 510, 35,35));

      teamAPlayer8.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer8.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer8.setForeground(new java.awt.Color(255, 255, 255));
      try{
          setHighlighted(teamAPlayer8,sortTeamA[7]);
      }
      catch(Exception e){

      }
      teamAPlayer8.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer8MousePressed(evt);
          }
      });
      teamAPlayer8.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer8ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer8, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 510, 35,35));

      teamBPlayer2.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer2.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer2.setForeground(new java.awt.Color(255, 255, 255));
      teamBPlayer2.setText("2");
  
      teamBPlayer2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer2ActionPerformed(evt);
          }
      });

        teamBPlayer2.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer2MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer2, new org.netbeans.lib.awtextra.AbsoluteConstraints(845, 510, 35,35));

      teamBPlayer4.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer4.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer4.setForeground(new java.awt.Color(255, 255, 255));
      teamBPlayer4.setText("4");
      teamBPlayer4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer4ActionPerformed(evt);
          }
      });
              teamBPlayer4.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer4MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer4, new org.netbeans.lib.awtextra.AbsoluteConstraints(915, 510, 35,35));

      teamBPlayer1.setBackground(new java.awt.Color(153, 0, 0));
      
      teamBPlayer1.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12));;
      teamBPlayer1.setMargin( new Insets(0, 0, 0, 0) );// NOI18N
      teamBPlayer1.setForeground(new java.awt.Color(255, 255, 255));
      
        
      teamBPlayer1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer1ActionPerformed(evt);
          }
      });
        teamBPlayer1.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer1MousePressed(evt);
          }
      });
    
      getContentPane().add(teamBPlayer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 510, 35,35));

      teamBPlayer6.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer6.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer6.setForeground(new java.awt.Color(255, 255, 255));
      teamBPlayer6.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer6ActionPerformed(evt);
          }
      });
      teamBPlayer6.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer6MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer6, new org.netbeans.lib.awtextra.AbsoluteConstraints(985, 510,35,35));

      teamBPlayer3.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer3.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer3.setForeground(new java.awt.Color(255, 255, 255));
  
      teamBPlayer3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer3ActionPerformed(evt);
          }
      });
              teamBPlayer3.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer3MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer3, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 510, 35,35));

      teamBPlayer10.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer10.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer10.setForeground(new java.awt.Color(255, 255, 255));
     
      teamBPlayer10.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer10ActionPerformed(evt);
          }
      });
         teamBPlayer10.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer10MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1125, 510, 35,35));
      teamBPlayer11.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer11.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer11.setForeground(new java.awt.Color(255, 255, 255));
   
      teamBPlayer11.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer11ActionPerformed(evt);
          }
      });
         teamBPlayer11.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer11MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 510, 35,35));
             teamBPlayer12.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer12.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer12.setForeground(new java.awt.Color(255, 255, 255));

      teamBPlayer12.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer12ActionPerformed(evt);
          }
      });
         teamBPlayer12.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer12MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1195, 510, 35,35));
      
      teamBPlayer13.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer13.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer13.setForeground(new java.awt.Color(255, 255, 255));
      teamBPlayer13.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer13ActionPerformed(evt);
          }
      });
         teamBPlayer13.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer13MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 510, 35,35));
      
      teamBPlayer14.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer14.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer14.setForeground(new java.awt.Color(255, 255, 255));
      
      teamBPlayer14.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer14ActionPerformed(evt);
          }
      });
         teamBPlayer14.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer14MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1265, 510, 35,35));
      teamBPlayer15.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer15.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer15.setForeground(new java.awt.Color(255, 255, 255));
      
      teamBPlayer15.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer15ActionPerformed(evt);
          }
      });
         teamBPlayer15.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer15MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer15, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 510, 35,35));
      teamBPlayer5.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer5.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12));
     // NOI18N
      teamBPlayer5.setForeground(new java.awt.Color(255, 255, 255));
      teamBPlayer5.setText("5");
      teamBPlayer5.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer5ActionPerformed(evt);
          }
      });
        teamBPlayer5.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer5MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer5, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 510, 35,35));

      teamBPlayer9.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer9.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer9.setForeground(new java.awt.Color(255, 255, 255));

      teamBPlayer9.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer9ActionPerformed(evt);
          }
      });
            teamBPlayer9.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer9MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 510, 35,35));

      teamBPlayer7.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer7.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer7.setForeground(new java.awt.Color(255, 255, 255));
      teamBPlayer7.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer7ActionPerformed(evt);
          }
      });
        teamBPlayer7.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer7MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 510, 35,35));

      teamBPlayer8.setBackground(new java.awt.Color(153, 0, 0));
      teamBPlayer8.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamBPlayer8.setForeground(new java.awt.Color(255, 255, 255));
      
      teamBPlayer8.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamBPlayer8ActionPerformed(evt);
          }
      });
          teamBPlayer8.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamBPlayer8MousePressed(evt);
          }
      });
      getContentPane().add(teamBPlayer8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1055, 510, 35,35));

      options12.setText("搶斷");
      options12.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              options12ActionPerformed(evt);
          }
      });
      getContentPane().add(options12, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 330, 240, 30));
          try{
          setHighlighted(teamBPlayer9,sortTeamB[8]);
            setHighlighted(teamBPlayer10,sortTeamB[9]);
              setHighlighted(teamBPlayer11,sortTeamB[10]);
                setHighlighted(teamBPlayer12,sortTeamB[11]);
                  setHighlighted(teamBPlayer13,sortTeamB[12]);
                    setHighlighted(teamBPlayer14,sortTeamB[13]);
                setHighlighted(teamBPlayer15,sortTeamB[14]);
      }
      catch(Exception e){

      }
      try{
          teamBPlayer1.setText(String.valueOf(sortTeamB[0].getPlayerNum()));
          teamBPlayer2.setText(String.valueOf(sortTeamB[1].getPlayerNum()));
          teamBPlayer3.setText(String.valueOf(sortTeamB[2].getPlayerNum()));
          teamBPlayer4.setText(String.valueOf(sortTeamB[3].getPlayerNum()));
          teamBPlayer5.setText(String.valueOf(sortTeamB[4].getPlayerNum()));
          teamBPlayer6.setText(String.valueOf(sortTeamB[5].getPlayerNum()));
          teamBPlayer7.setText(String.valueOf(sortTeamB[6].getPlayerNum()));
          teamBPlayer8.setText(String.valueOf(sortTeamB[7].getPlayerNum()));
          teamBPlayer9.setText(String.valueOf(sortTeamB[8].getPlayerNum()));
          teamBPlayer10.setText(String.valueOf(sortTeamB[9].getPlayerNum()));
          teamBPlayer11.setText(String.valueOf(sortTeamB[10].getPlayerNum()));
          teamBPlayer12.setText(String.valueOf(sortTeamB[11].getPlayerNum()));
          teamBPlayer13.setText(String.valueOf(sortTeamB[12].getPlayerNum()));
          teamBPlayer14.setText(String.valueOf(sortTeamB[13].getPlayerNum()));
          teamBPlayer15.setText(String.valueOf(sortTeamB[14].getPlayerNum()));
      }catch(Exception e){
          
      }
       try{
           teamAPlayer1.setBorder(null);
           teamAPlayer2.setBorder(null);
           teamAPlayer3.setBorder(null);
           teamAPlayer4.setBorder(null);
           teamAPlayer5.setBorder(null);
           teamAPlayer6.setBorder(null);
           teamAPlayer7.setBorder(null);
           teamAPlayer8.setBorder(null);
           teamAPlayer9.setBorder(null);
           teamAPlayer10.setBorder(null);
           teamAPlayer11.setBorder(null);
           teamAPlayer12.setBorder(null);
           teamAPlayer13.setBorder(null);
           teamAPlayer14.setBorder(null);
           teamAPlayer15.setBorder(null);
       }catch(Exception e)
       {
           
       }
       try{
           teamBPlayer1.setBorder(null);
           teamBPlayer2.setBorder(null);
           teamBPlayer3.setBorder(null);
           teamBPlayer4.setBorder(null);
           teamBPlayer5.setBorder(null);
           teamBPlayer6.setBorder(null);
           teamBPlayer7.setBorder(null);
           teamBPlayer8.setBorder(null);
           teamBPlayer9.setBorder(null);
           teamBPlayer10.setBorder(null);
           teamBPlayer11.setBorder(null);
           teamBPlayer12.setBorder(null);
           teamBPlayer13.setBorder(null);
           teamBPlayer14.setBorder(null);
           teamBPlayer15.setBorder(null);
       }catch(Exception e)
       {
           
       }
       
       try{

          setHighlighted(teamBPlayer1,sortTeamB[0]);
          setHighlighted(teamBPlayer2,sortTeamB[1]);
           setHighlighted(teamBPlayer3,sortTeamB[2]);
            setHighlighted(teamBPlayer4,sortTeamB[3]);
             setHighlighted(teamBPlayer5,sortTeamB[4]);
              setHighlighted(teamBPlayer6,sortTeamB[5]);
               setHighlighted(teamBPlayer7,sortTeamB[6]);
                setHighlighted(teamBPlayer8,sortTeamB[7]);
                 setHighlighted(teamBPlayer9,sortTeamB[8]);
                  setHighlighted(teamBPlayer10,sortTeamB[9]);
                   setHighlighted(teamBPlayer11,sortTeamB[10]);
                    setHighlighted(teamBPlayer12,sortTeamB[11]);
                     setHighlighted(teamBPlayer13,sortTeamB[12]);
                      setHighlighted(teamBPlayer14,sortTeamB[13]);
                       setHighlighted(teamBPlayer15,sortTeamB[14]);
             
      }
      catch(Exception e){

      }
      
      notification.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
      getContentPane().add(notification, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 150, 230, 30));

      jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/floppy-disk.png"))); // NOI18N
      jButton1.setText("儲存球員數據");
      jButton1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              jButton1ActionPerformed(evt);
          }
      });
      getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 0, 140, 50));

      Rewind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reply.png"))); // NOI18N
      Rewind.setText("取消步驟");
      Rewind.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              RewindActionPerformed(evt);
          }
      });
      getContentPane().add(Rewind, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, 40));

      TimeoutTeamA.setBackground(new java.awt.Color(0, 153, 153));
      TimeoutTeamA.setForeground(new java.awt.Color(255, 255, 255));
      TimeoutTeamA.setText("Time Out");
      TimeoutTeamA.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              TimeoutTeamAActionPerformed(evt);
          }
      });
      getContentPane().add(TimeoutTeamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, 140, -1));

      TimeoutTeamB.setBackground(new java.awt.Color(153, 0, 0));
      TimeoutTeamB.setForeground(new java.awt.Color(255, 255, 255));
      TimeoutTeamB.setText("Time Out");
      TimeoutTeamB.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              TimeoutTeamBActionPerformed(evt);
          }
      });
      getContentPane().add(TimeoutTeamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 120, 130, -1));

      back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home-icon-silhouette.png"))); // NOI18N
      back.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              backActionPerformed(evt);
          }
      });
      getContentPane().add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 60, 40));
      getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, -1, -1));

      jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

      jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
      jLabel2.setText("犯規");
      jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, -1));

      foulteamA.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      foulteamA.setText("0");
      jPanel2.add(foulteamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

      jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
      jLabel4.setText("暫停");
      jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 50, -1));

      timeoutA.setFont(new java.awt.Font("Lucida Grande", 0, 25)); // NOI18N
      timeoutA.setText("0");
      jPanel2.add(timeoutA, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 20, -1));

      jLabel10.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
      jLabel10.setText("暫停");
      jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, 39, -1));

      timeoutB.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      timeoutB.setText("0");
      jPanel2.add(timeoutB, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, -1, -1));

      jLabel6.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
      jLabel6.setText("犯規");
      jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, -1, -1));

      foulteamB.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      foulteamB.setText("0");
      jPanel2.add(foulteamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 50, -1, -1));

      timelabel.setFont(new java.awt.Font("Silom", 0, 24)); // NOI18N
      timelabel.setText("88:88:88");
      timelabel.setToolTipText("");
      jPanel2.add(timelabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, -1, -1));

      teamAlabel.setHorizontalAlignment(SwingConstants.CENTER);
      teamAlabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      teamAlabel.setText(teamA_name);
      teamAlabel.setAlignmentX(15.0F);
      jPanel3.add(teamAlabel);

      jPanel18.setAlignmentX(2.0F);
      jPanel18.setAlignmentY(2.0F);

      jPanel21.setAlignmentX(2.0F);
      jPanel21.setAlignmentY(2.0F);
      jPanel18.add(jPanel21);

      jPanel3.add(jPanel18);

      jPanel16.setAlignmentX(2.0F);
      jPanel16.setAlignmentY(2.0F);
      jPanel3.add(jPanel16);

      jPanel20.setAlignmentX(2.0F);
      jPanel20.setAlignmentY(2.0F);
      jPanel3.add(jPanel20);

      jPanel23.setAlignmentX(2.0F);
      jPanel23.setAlignmentY(2.0F);
      jPanel3.add(jPanel23);

      jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
      jLabel1.setText("時間：");
      jPanel3.add(jLabel1);

      jPanel15.setAlignmentX(2.0F);
      jPanel15.setAlignmentY(2.0F);
      jPanel3.add(jPanel15);

      jPanel17.setAlignmentX(2.0F);
      jPanel17.setAlignmentY(2.0F);
      jPanel3.add(jPanel17);

      jPanel19.setAlignmentX(2.0F);
      jPanel19.setAlignmentY(2.0F);
      jPanel3.add(jPanel19);

      jPanel22.setAlignmentX(2.0F);
      jPanel22.setAlignmentY(2.0F);
      jPanel3.add(jPanel22);

      teamBlabel.setHorizontalAlignment(SwingConstants.CENTER);
      teamBlabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      teamBlabel.setText(teamB_name);
      jPanel3.add(teamBlabel);

      jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 510, 40));

      jPanel4.add(teamAscore,FlowLayout.LEFT);

      teamAscore.setHorizontalAlignment(SwingConstants.CENTER);
      teamAscore.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      teamAscore.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      teamAscore.setText("0");
      teamAscore.setAlignmentX(15.0F);
      jPanel4.add(teamAscore);

      jPanel25.setAlignmentX(2.0F);
      jPanel25.setAlignmentY(2.0F);
      jPanel4.add(jPanel25);

      jPanel12.setAlignmentX(2.0F);
      jPanel12.setAlignmentY(2.0F);
      jPanel4.add(jPanel12);

      jPanel5.setAlignmentX(2.0F);
      jPanel5.setAlignmentY(2.0F);
      jPanel4.add(jPanel5);

      jPanel11.setAlignmentX(2.0F);
      jPanel11.setAlignmentY(2.0F);
      jPanel4.add(jPanel11);

      jPanel9.setAlignmentX(2.0F);
      jPanel9.setAlignmentY(2.0F);
      jPanel4.add(jPanel9);

      jPanel14.setAlignmentX(2.0F);
      jPanel14.setAlignmentY(2.0F);
      jPanel4.add(jPanel14);

      jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 22)); // NOI18N
      jLabel3.setText("局");
      jPanel4.add(jLabel3);

      jPanel13.setAlignmentX(2.0F);
      jPanel13.setAlignmentY(2.0F);
      jPanel4.add(jPanel13);

      stageLabel.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
      stageLabel.setText("1");
      jPanel4.add(stageLabel);

      jPanel24.setAlignmentX(2.0F);
      jPanel24.setAlignmentY(2.0F);
      jPanel4.add(jPanel24);

      jPanel7.setAlignmentX(2.0F);
      jPanel7.setAlignmentY(2.0F);
      jPanel4.add(jPanel7);

      jPanel6.setAlignmentX(2.0F);
      jPanel6.setAlignmentY(2.0F);
      jPanel4.add(jPanel6);

      jPanel8.setAlignmentX(2.0F);
      jPanel8.setAlignmentY(2.0F);
      jPanel4.add(jPanel8);

      jPanel10.setAlignmentX(2.0F);
      jPanel10.setAlignmentY(2.0F);
      jPanel4.add(jPanel10);

      teamBscore.setHorizontalAlignment(SwingConstants.CENTER);
      teamBscore.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
      teamBscore.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      teamBscore.setText("0");
      jPanel4.add(teamBscore);

      jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 560, 40));

      getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 840, 110));

      plus1A.setBackground(new java.awt.Color(0, 153, 153));
      plus1A.setForeground(new java.awt.Color(255, 255, 255));
      plus1A.setText("+1分");
      plus1A.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              plus1AActionPerformed(evt);
          }
      });
      getContentPane().add(plus1A, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, 80, 40));

      minus1A.setBackground(new java.awt.Color(0, 153, 153));
      minus1A.setForeground(new java.awt.Color(255, 255, 255));
      minus1A.setText("-1分");
      minus1A.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              minus1AActionPerformed(evt);
          }
      });
      getContentPane().add(minus1A, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, 80, 40));

      plus1B.setBackground(new java.awt.Color(153, 0, 0));
      plus1B.setForeground(new java.awt.Color(255, 255, 255));
      plus1B.setText("+1分");
      plus1B.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              plus1BActionPerformed(evt);
          }
      });
      getContentPane().add(plus1B, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 190, 80, 40));

      minus1B.setBackground(new java.awt.Color(153, 0, 0));
      minus1B.setForeground(new java.awt.Color(255, 255, 255));
      minus1B.setText("-1分");
      minus1B.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              minus1BActionPerformed(evt);
          }
      });
      getContentPane().add(minus1B, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 230, 80, 40));
      addSec.setText("+1秒");
      addSec.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
             addSecActionPerformed(evt);
          }
      });     

      getContentPane().add(addSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 60, 80, 40));  
             minusSec.setText("-1秒");
      minusSec.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
             minusSecActionPerformed(evt);
          }
      });
      getContentPane().add(minusSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 110, 80, 40));
      jButton2.setText("+1局");
      jButton2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              jButton2ActionPerformed(evt);
          }
      });
      getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 60, 140, 40));

      jButton3.setText("-1局");
      jButton3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              jButton3ActionPerformed(evt);
          }
      });
      
      getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 110, 140, 40));
    addMin.setText("+1分鐘");
      addMin.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              addMinActionPerformed(evt);
          }
      });
      
      getContentPane().add(addMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 160, 70, 40));
          minusMin.setText("-1分鐘");
      minusMin.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              minusMinActionPerformed(evt);
          }
      });
      
      getContentPane().add(minusMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 160, 70, 40));
      addMilliSecond.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
      addMilliSecond.setText("+1ms");
      addMilliSecond.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              addMilliSecondActionPerformed(evt);
          }
      });
      getContentPane().add(addMilliSecond, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 120, 70, 30));

      minusMilliSecond.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
      minusMilliSecond.setText("-1ms");
      minusMilliSecond.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              minusMilliSecondActionPerformed(evt);
          }
      });
      getContentPane().add(minusMilliSecond, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 120, 70, 30));

      playerOneA.setBackground(new java.awt.Color(0, 153, 153));
      playerOneA.setForeground(new java.awt.Color(255, 255, 255));
      playerOneA.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneAActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneA, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 340, 60));
      playerOneA.setText("#"+String.valueOf(firstTeamA[0].getPlayerNum())+" "+String.valueOf(firstTeamA[0].getPlayerName()));

      playerOneA1.setBackground(new java.awt.Color(0, 153, 153));
      playerOneA1.setForeground(new java.awt.Color(255, 255, 255));
      playerOneA1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneA1ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneA1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 260, 340, 60));
      playerOneA1.setText("#"+String.valueOf(firstTeamA[1].getPlayerNum())+" "+String.valueOf(firstTeamA[1].getPlayerName()));

      playerOneA2.setBackground(new java.awt.Color(0, 153, 153));
      playerOneA2.setForeground(new java.awt.Color(255, 255, 255));
      playerOneA2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneA2ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneA2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, 340, 60));
      playerOneA2.setText("#"+String.valueOf(firstTeamA[2].getPlayerNum())+" "+String.valueOf(firstTeamA[2].getPlayerName()));

      playerOneA3.setBackground(new java.awt.Color(0, 153, 153));
      playerOneA3.setForeground(new java.awt.Color(255, 255, 255));
      playerOneA3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneA3ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneA3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 380, 340, 60));
      playerOneA3.setText("#"+String.valueOf(firstTeamA[3].getPlayerNum())+" "+String.valueOf(firstTeamA[3].getPlayerName()));

      playerOneA4.setBackground(new java.awt.Color(0, 153, 153));
      playerOneA4.setForeground(new java.awt.Color(255, 255, 255));
      playerOneA4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneA4ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneA4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 440, 340, 60));
      playerOneA4.setText("#"+String.valueOf(firstTeamA[4].getPlayerNum())+" "+String.valueOf(firstTeamA[4].getPlayerName()));

      playerOneB1.setBackground(new java.awt.Color(153, 0, 0));
      playerOneB1.setForeground(new java.awt.Color(255, 255, 255));
      playerOneB1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneB1ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneB1, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 260, 340, 60));
      playerOneB1.setText("#"+String.valueOf(firstTeamB[1].getPlayerNum())+" "+String.valueOf(firstTeamB[1].getPlayerName()));

      playerOneB2.setBackground(new java.awt.Color(153, 0, 0));
      playerOneB2.setForeground(new java.awt.Color(255, 255, 255));
      playerOneB2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneB2ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneB2, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 320, 340, 60));
      playerOneB2.setText("#"+String.valueOf(firstTeamB[2].getPlayerNum())+" "+String.valueOf(firstTeamB[2].getPlayerName()));

      playerOneB3.setBackground(new java.awt.Color(153, 0, 0));
      playerOneB3.setForeground(new java.awt.Color(255, 255, 255));
      playerOneB3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneB3ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneB3, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 380, 340, 60));
      playerOneB3.setText("#"+String.valueOf(firstTeamB[3].getPlayerNum())+" "+String.valueOf(firstTeamB[3].getPlayerName()));

      playerOneB4.setBackground(new java.awt.Color(153, 0, 0));
      playerOneB4.setForeground(new java.awt.Color(255, 255, 255));
      playerOneB4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneB4ActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneB4, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 440, 340, 60));
      playerOneB4.setText("#"+String.valueOf(firstTeamB[4].getPlayerNum())+" "+String.valueOf(firstTeamB[4].getPlayerName()));

      playerOneB.setBackground(new java.awt.Color(153, 0, 0));
      playerOneB.setForeground(new java.awt.Color(255, 255, 255));
      playerOneB.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              playerOneBActionPerformed(evt);
          }
      });
      getContentPane().add(playerOneB, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 200, 340, 60));
      playerOneB.setText("#"+String.valueOf(firstTeamB[0].getPlayerNum())+" "+String.valueOf(firstTeamB[0].getPlayerName()));

      subA5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subA5.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subA5ActionPerformed(evt);
          }
      });
      getContentPane().add(subA5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 40, 40));

      subA1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subA1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subA1ActionPerformed(evt);
          }
      });
      getContentPane().add(subA1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 40, 40));

      subA2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subA2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subA2ActionPerformed(evt);
          }
      });
      getContentPane().add(subA2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 40, 40));

      subA3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subA3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subA3ActionPerformed(evt);
          }
      });
      getContentPane().add(subA3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 40, 40));

      subA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subA4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subA4ActionPerformed(evt);
          }
      });
      getContentPane().add(subA4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 40, 40));

      subB5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subB5.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subB5ActionPerformed(evt);
          }
      });
      getContentPane().add(subB5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 450, 40, 40));

      subB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subB1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subB1ActionPerformed(evt);
          }
      });
      getContentPane().add(subB1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 210, 40, 40));

      subB2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subB2.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subB2ActionPerformed(evt);
          }
      });
      getContentPane().add(subB2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 270, 40, 40));

      subB3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subB3.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subB3ActionPerformed(evt);
          }
      });
      getContentPane().add(subB3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 330, 40, 40));

      subB4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
      subB4.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              subB4ActionPerformed(evt);
          }
      });
      getContentPane().add(subB4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 390, 40, 40));

      stageTable.setModel(stageX);
      stageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      

              
      jScrollPane4.setViewportView(stageTable);

      jScrollPane5.setViewportView(jScrollPane4);

      getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 260, 130));
   
      teamAPlayer11.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer11.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer11.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer11,sortTeamA[10]);
      }
      catch(Exception e){

      }
      teamAPlayer11.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer11MousePressed(evt);
          }
      });
      teamAPlayer11.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer11ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 510, 35,35));

      teamAPlayer12.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer12.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer12.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer12,sortTeamA[11]);
      }
      catch(Exception e){

      }
      teamAPlayer12.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer12MousePressed(evt);
          }
      });
      teamAPlayer12.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer12ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer12, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 510, 35,35));

      teamAPlayer13.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer13.setMargin(new Insets(0, 0, 0, 0));
      teamAPlayer13.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer13.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer13,sortTeamA[12]);
      }
      catch(Exception e){

      }
      teamAPlayer13.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer13MousePressed(evt);
          }
      });
      teamAPlayer13.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer13ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer13, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 510, 35,35));

      teamAPlayer14.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer14.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer14.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer14,sortTeamA[13]);
      }
      catch(Exception e){

      }
      teamAPlayer14.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer14MousePressed(evt);
          }
      });
      teamAPlayer14.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer14ActionPerformed(evt);
          }
      });
      
      getContentPane().add(teamAPlayer14, new org.netbeans.lib.awtextra.AbsoluteConstraints(455, 510, 35,35));
      teamAPlayer15.setBackground(new java.awt.Color(0, 153, 153));
      teamAPlayer15.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 12)); // NOI18N
      teamAPlayer15.setForeground(new java.awt.Color(255, 255, 255));
      try{

          setHighlighted(teamAPlayer15,sortTeamA[14]);
      }
      catch(Exception e){

      }
      teamAPlayer15.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mousePressed(java.awt.event.MouseEvent evt) {
              teamAPlayer15MousePressed(evt);
          }
      });
      teamAPlayer15.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              teamAPlayer15ActionPerformed(evt);
          }
      });
      getContentPane().add(teamAPlayer15, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 510, 35,35));
  
      pack();
  }// </editor-fold>                        
  
  /*  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        timeStart = new javax.swing.JButton();
        resetTimer = new javax.swing.JButton();
        timeoutteamA = new javax.swing.JLabel();
        timeoutteamB = new javax.swing.JLabel();
        options16 = new javax.swing.JButton();
        options1 = new javax.swing.JButton();
        options10 = new javax.swing.JButton();
        options4 = new javax.swing.JButton();
        options8 = new javax.swing.JButton();
        options14 = new javax.swing.JButton();
        options11 = new javax.swing.JButton();
        options15 = new javax.swing.JButton();
        options7 = new javax.swing.JButton();
        options6 = new javax.swing.JButton();
        options9 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        teamBstatTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        log = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        teamAstatTable = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        options0 = new javax.swing.JButton();
        options2 = new javax.swing.JButton();
        options3 = new javax.swing.JButton();
        options13 = new javax.swing.JButton();
        teamAPlayer2 = new javax.swing.JButton();
        teamAPlayer4 = new javax.swing.JButton();
        teamAPlayer1 = new javax.swing.JButton();
        teamAPlayer6 = new javax.swing.JButton();
        teamAPlayer3 = new javax.swing.JButton();
        teamAPlayer10 = new javax.swing.JButton();
        teamAPlayer5 = new javax.swing.JButton();
        teamAPlayer9 = new javax.swing.JButton();
        teamAPlayer7 = new javax.swing.JButton();
        teamAPlayer8 = new javax.swing.JButton();
        teamBPlayer2 = new javax.swing.JButton();
        teamBPlayer4 = new javax.swing.JButton();
        teamBPlayer1 = new javax.swing.JButton();
        teamBPlayer6 = new javax.swing.JButton();
        teamBPlayer3 = new javax.swing.JButton();
        teamBPlayer10 = new javax.swing.JButton();
        teamBPlayer5 = new javax.swing.JButton();
        teamBPlayer9 = new javax.swing.JButton();
        teamBPlayer7 = new javax.swing.JButton();
        teamBPlayer8 = new javax.swing.JButton();
        options12 = new javax.swing.JButton();
        notification = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        Rewind = new javax.swing.JButton();
        TimeoutTeamA = new javax.swing.JButton();
        TimeoutTeamB = new javax.swing.JButton();
        back = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        foulteamA = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        timeoutA = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        timeoutB = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        foulteamB = new javax.swing.JLabel();
        timelabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        teamAlabel = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        teamBlabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        teamAscore = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        stageLabel = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        teamBscore = new javax.swing.JLabel();
        plus1A = new javax.swing.JButton();
        minus1A = new javax.swing.JButton();
        plus1B = new javax.swing.JButton();
        minus1B = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        addMilliSecond = new javax.swing.JButton();
        minusMilliSecond = new javax.swing.JButton();
        playerOneA = new javax.swing.JButton();
        playerOneA1 = new javax.swing.JButton();
        playerOneA2 = new javax.swing.JButton();
        playerOneA3 = new javax.swing.JButton();
        playerOneA4 = new javax.swing.JButton();
        playerOneB1 = new javax.swing.JButton();
        playerOneB2 = new javax.swing.JButton();
        playerOneB3 = new javax.swing.JButton();
        playerOneB4 = new javax.swing.JButton();
        playerOneB = new javax.swing.JButton();
        subA5 = new javax.swing.JButton();
        subA1 = new javax.swing.JButton();
        subA2 = new javax.swing.JButton();
        subA3 = new javax.swing.JButton();
        subA4 = new javax.swing.JButton();
        subB5 = new javax.swing.JButton();
        subB1 = new javax.swing.JButton();
        subB2 = new javax.swing.JButton();
        subB3 = new javax.swing.JButton();
        subB4 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane(stageTable, JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane4 = new javax.swing.JScrollPane();
        stageTable = new javax.swing.JTable();
        teamAPlayer11 = new javax.swing.JButton();
        teamAPlayer12 = new javax.swing.JButton();
        teamAPlayer13 = new javax.swing.JButton();
        teamAPlayer14 = new javax.swing.JButton();

        jLabel8.setText("暫停");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        timeStart.setText("開始計時");
        timeStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeStartActionPerformed(evt);
            }
        });
        getContentPane().add(timeStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 120, -1, -1));

        resetTimer.setText("取消計時");
        resetTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetTimerActionPerformed(evt);
            }
        });
        getContentPane().add(resetTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 120, -1, -1));
        getContentPane().add(timeoutteamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, -1));

        timeoutteamB.setToolTipText("");
        getContentPane().add(timeoutteamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 60, 50, -1));

        options16.setText("技術犯規");
        options16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options16ActionPerformed(evt);
            }
        });
        getContentPane().add(options16, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 360, 80, -1));

        options1.setText("兩分球失誤");
        options1.setMaximumSize(new java.awt.Dimension(99, 29));
        options1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options1ActionPerformed(evt);
            }
        });
        getContentPane().add(options1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 210, 240, -1));

        options10.setText("蓋帽");
        options10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options10ActionPerformed(evt);
            }
        });
        getContentPane().add(options10, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 390, 240, -1));

        options4.setText("罰球");
        options4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options4ActionPerformed(evt);
            }
        });
        getContentPane().add(options4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 510, 240, 40));

        options8.setText("快攻成功");
        options8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options8ActionPerformed(evt);
            }
        });
        getContentPane().add(options8, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 480, 120, -1));

        options14.setText("進攻犯規");
        options14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options14ActionPerformed(evt);
            }
        });
        getContentPane().add(options14, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 360, 80, -1));

        options11.setText("助攻");
        options11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options11ActionPerformed(evt);
            }
        });
        getContentPane().add(options11, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 300, 240, 30));

        options15.setText("防守犯規");
        options15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options15ActionPerformed(evt);
            }
        });
        getContentPane().add(options15, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 360, 80, -1));

        options7.setText("防守籃板");
        options7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options7ActionPerformed(evt);
            }
        });
        getContentPane().add(options7, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 450, 120, -1));

        options6.setText("進攻籃板");
        options6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options6ActionPerformed(evt);
            }
        });
        getContentPane().add(options6, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 450, 120, -1));

        options9.setText("快攻失敗");
        options9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options9ActionPerformed(evt);
            }
        });
        getContentPane().add(options9, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 480, 120, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        teamBstatTable.setModel(tableModelb);
        int counterb=1;
        for(PlayerStat x:teamB)
        {

            Object[] objs = {counterb++,x.playerNum,x.playerName,0,0,0,0};
            tableModelb.addRow(objs);
        }
        jScrollPane1.setViewportView(teamBstatTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 560, 350, 190));

        log.setModel(logtable);
        jScrollPane2.setViewportView(log);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 560, 690, 190));

        int countera=1;
        for(PlayerStat x:teamA)
        {

            Object[] objs = {countera++,x.playerNum,x.playerName,x.getOptions(17),0,0,0};
            tableModela.addRow(objs);
        }
        teamAstatTable.setModel(tableModela);
        jScrollPane3.setViewportView(teamAstatTable);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 340, 190));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        options0.setText("兩分球命中");
        options0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options0ActionPerformed(evt);
            }
        });
        getContentPane().add(options0, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 180, 240, 30));

        options2.setText("三分球命中");
        options2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options2ActionPerformed(evt);
            }
        });
        getContentPane().add(options2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 240, 240, -1));

        options3.setText("三分球失誤");
        options3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options3ActionPerformed(evt);
            }
        });
        getContentPane().add(options3, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 270, 240, 30));

        options13.setText("失誤");
        options13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options13ActionPerformed(evt);
            }
        });
        getContentPane().add(options13, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 420, 240, -1));

        teamAPlayer2.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer2.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer2,sortTeamA[1]);
        }
        catch(Exception e){

        }
        teamAPlayer2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer2MousePressed(evt);
            }
        });
        teamAPlayer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer2ActionPerformed(evt);
            }
        });
        setHighlighted(teamAPlayer2,sortTeamA[1]);
        getContentPane().add(teamAPlayer2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 510, 37, 38));

        teamAPlayer4.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer4.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer4.setForeground(new java.awt.Color(255, 255, 255));
        try{
            setHighlighted(teamAPlayer4,sortTeamA[3]);
        }
        catch(Exception e){

        }
        teamAPlayer4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer4MousePressed(evt);
            }
        });
        teamAPlayer4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer4ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer4, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 510, 37, 38));

        teamAPlayer1.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer1.setForeground(new java.awt.Color(255, 255, 255));
        teamAPlayer1.setText(String.valueOf(sortTeamA[0].getPlayerNum()));
        try{

            setHighlighted(teamAPlayer3,sortTeamA[2]);
        }
        catch(Exception e){

        }
        teamAPlayer1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer1MousePressed(evt);
            }
        });
        teamAPlayer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer1ActionPerformed(evt);
            }
        });
        setHighlighted(teamAPlayer1,sortTeamA[0]);
        getContentPane().add(teamAPlayer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 37, 38));

        teamAPlayer6.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer6.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer6.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer6,sortTeamA[5]);
        }
        catch(Exception e){

        }
        teamAPlayer6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer6MousePressed(evt);
            }
        });
        teamAPlayer6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer6ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 510, 37, 38));

        teamAPlayer3.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer3.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer3.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer3,sortTeamA[2]);
        }
        catch(Exception e){

        }
        teamAPlayer3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer3MousePressed(evt);
            }
        });
        teamAPlayer3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer3ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 510, 37, 38));

        teamAPlayer10.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer10.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer10.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer10,sortTeamA[9]);
        }
        catch(Exception e){

        }
        teamAPlayer10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer10MousePressed(evt);
            }
        });
        teamAPlayer10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer10ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer10, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 510, 37, 38));

        teamAPlayer5.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer5.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer5.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer5,sortTeamA[4]);
        }
        catch(Exception e){

        }
        teamAPlayer5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer5MousePressed(evt);
            }
        });
        teamAPlayer5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer5ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, 37, 38));

        teamAPlayer9.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer9.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer9.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer9,sortTeamA[8]);
        }
        catch(Exception e){

        }
        teamAPlayer9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer9MousePressed(evt);
            }
        });
        teamAPlayer9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer9ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer9, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 510, 37, 38));

        teamAPlayer7.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer7.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer7.setForeground(new java.awt.Color(255, 255, 255));
        try{
            setHighlighted(teamAPlayer7,sortTeamA[6]);
        }
        catch(Exception e){

        }
        teamAPlayer7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer7MousePressed(evt);
            }
        });
        teamAPlayer7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer7ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer7, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 510, 37, 38));

        teamAPlayer8.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer8.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer8.setForeground(new java.awt.Color(255, 255, 255));
        try{
            setHighlighted(teamAPlayer8,sortTeamA[7]);
        }
        catch(Exception e){

        }
        teamAPlayer8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer8MousePressed(evt);
            }
        });
        teamAPlayer8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer8ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 510, 37, 38));

        teamBPlayer2.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer2.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer2.setText("2");
        teamBPlayer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer2ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer2, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 510, 45, 45));

        teamBPlayer4.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer4.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer4.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer4.setText("4");
        teamBPlayer4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer4ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer4, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 510, 45, 45));

        teamBPlayer1.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer1.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer1.setText("1");
        teamBPlayer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer1ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 510, 45, 45));

        teamBPlayer6.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer6.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer6.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer6.setText("6");
        teamBPlayer6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer6ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 510, 45, 45));

        teamBPlayer3.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer3.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer3.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer3.setText("3");
        teamBPlayer3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer3ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer3, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 510, 45, 45));

        teamBPlayer10.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer10.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer10.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer10.setText("10");
        teamBPlayer10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer10ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1290, 510, 45, 45));

        teamBPlayer5.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer5.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer5.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer5.setText("5");
        teamBPlayer5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer5ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 510, 45, 45));

        teamBPlayer9.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer9.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer9.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer9.setText("9");
        teamBPlayer9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer9ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 510, 45, 45));

        teamBPlayer7.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer7.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer7.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer7.setText("7");
        teamBPlayer7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer7ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 510, 45, 45));

        teamBPlayer8.setBackground(new java.awt.Color(153, 0, 0));
        teamBPlayer8.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamBPlayer8.setForeground(new java.awt.Color(255, 255, 255));
        teamBPlayer8.setText("8");
        teamBPlayer8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamBPlayer8ActionPerformed(evt);
            }
        });
        getContentPane().add(teamBPlayer8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 510, 45, 45));

        options12.setText("搶斷");
        options12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options12ActionPerformed(evt);
            }
        });
        getContentPane().add(options12, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 330, 240, 30));

        notification.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        getContentPane().add(notification, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 150, 230, 30));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/floppy-disk.png"))); // NOI18N
        jButton1.setText("儲存球員數據");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 0, 140, 50));

        Rewind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reply.png"))); // NOI18N
        Rewind.setText("上一步");
        Rewind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RewindActionPerformed(evt);
            }
        });
        getContentPane().add(Rewind, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, 40));

        TimeoutTeamA.setBackground(new java.awt.Color(0, 153, 153));
        TimeoutTeamA.setForeground(new java.awt.Color(255, 255, 255));
        TimeoutTeamA.setText("Time Out");
        TimeoutTeamA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TimeoutTeamAActionPerformed(evt);
            }
        });
        getContentPane().add(TimeoutTeamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, 140, -1));

        TimeoutTeamB.setBackground(new java.awt.Color(153, 0, 0));
        TimeoutTeamB.setForeground(new java.awt.Color(255, 255, 255));
        TimeoutTeamB.setText("Time Out");
        TimeoutTeamB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TimeoutTeamBActionPerformed(evt);
            }
        });
        getContentPane().add(TimeoutTeamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 120, 130, -1));

        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home-icon-silhouette.png"))); // NOI18N
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        getContentPane().add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 60, 40));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, -1, -1));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel2.setText("犯規");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, -1));

        foulteamA.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        foulteamA.setText("0");
        jPanel2.add(foulteamA, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel4.setText("暫停");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 50, -1));

        timeoutA.setFont(new java.awt.Font("Lucida Grande", 0, 25)); // NOI18N
        timeoutA.setText("0");
        jPanel2.add(timeoutA, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 20, -1));

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel10.setText("暫停");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, 39, -1));

        timeoutB.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        timeoutB.setText("0");
        jPanel2.add(timeoutB, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, -1, -1));

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel6.setText("犯規");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, -1, -1));

        foulteamB.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        foulteamB.setText("0");
        jPanel2.add(foulteamB, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 50, -1, -1));

        timelabel.setFont(new java.awt.Font("Silom", 0, 24)); // NOI18N
        timelabel.setText("88:88:88");
        timelabel.setToolTipText("");
        jPanel2.add(timelabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, -1, -1));

        teamAlabel.setHorizontalAlignment(SwingConstants.CENTER);
        teamAlabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        teamAlabel.setText(teamA_name);
        teamAlabel.setAlignmentX(15.0F);
        jPanel3.add(teamAlabel);

        jPanel18.setAlignmentX(2.0F);
        jPanel18.setAlignmentY(2.0F);

        jPanel21.setAlignmentX(2.0F);
        jPanel21.setAlignmentY(2.0F);
        jPanel18.add(jPanel21);

        jPanel3.add(jPanel18);

        jPanel16.setAlignmentX(2.0F);
        jPanel16.setAlignmentY(2.0F);
        jPanel3.add(jPanel16);

        jPanel20.setAlignmentX(2.0F);
        jPanel20.setAlignmentY(2.0F);
        jPanel3.add(jPanel20);

        jPanel23.setAlignmentX(2.0F);
        jPanel23.setAlignmentY(2.0F);
        jPanel3.add(jPanel23);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel1.setText("時間：");
        jPanel3.add(jLabel1);

        jPanel15.setAlignmentX(2.0F);
        jPanel15.setAlignmentY(2.0F);
        jPanel3.add(jPanel15);

        jPanel17.setAlignmentX(2.0F);
        jPanel17.setAlignmentY(2.0F);
        jPanel3.add(jPanel17);

        jPanel19.setAlignmentX(2.0F);
        jPanel19.setAlignmentY(2.0F);
        jPanel3.add(jPanel19);

        jPanel22.setAlignmentX(2.0F);
        jPanel22.setAlignmentY(2.0F);
        jPanel3.add(jPanel22);

        teamBlabel.setHorizontalAlignment(SwingConstants.CENTER);
        teamBlabel.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        teamBlabel.setText(teamB_name);
        jPanel3.add(teamBlabel);

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 510, 40));

        jPanel4.add(teamAscore,FlowLayout.LEFT);

        teamAscore.setHorizontalAlignment(SwingConstants.CENTER);
        teamAscore.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        teamAscore.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        teamAscore.setText("0");
        teamAscore.setAlignmentX(15.0F);
        jPanel4.add(teamAscore);

        jPanel25.setAlignmentX(2.0F);
        jPanel25.setAlignmentY(2.0F);
        jPanel4.add(jPanel25);

        jPanel12.setAlignmentX(2.0F);
        jPanel12.setAlignmentY(2.0F);
        jPanel4.add(jPanel12);

        jPanel5.setAlignmentX(2.0F);
        jPanel5.setAlignmentY(2.0F);
        jPanel4.add(jPanel5);

        jPanel11.setAlignmentX(2.0F);
        jPanel11.setAlignmentY(2.0F);
        jPanel4.add(jPanel11);

        jPanel9.setAlignmentX(2.0F);
        jPanel9.setAlignmentY(2.0F);
        jPanel4.add(jPanel9);

        jPanel14.setAlignmentX(2.0F);
        jPanel14.setAlignmentY(2.0F);
        jPanel4.add(jPanel14);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 22)); // NOI18N
        jLabel3.setText("局");
        jPanel4.add(jLabel3);

        jPanel13.setAlignmentX(2.0F);
        jPanel13.setAlignmentY(2.0F);
        jPanel4.add(jPanel13);

        stageLabel.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        stageLabel.setText("1");
        jPanel4.add(stageLabel);

        jPanel24.setAlignmentX(2.0F);
        jPanel24.setAlignmentY(2.0F);
        jPanel4.add(jPanel24);

        jPanel7.setAlignmentX(2.0F);
        jPanel7.setAlignmentY(2.0F);
        jPanel4.add(jPanel7);

        jPanel6.setAlignmentX(2.0F);
        jPanel6.setAlignmentY(2.0F);
        jPanel4.add(jPanel6);

        jPanel8.setAlignmentX(2.0F);
        jPanel8.setAlignmentY(2.0F);
        jPanel4.add(jPanel8);

        jPanel10.setAlignmentX(2.0F);
        jPanel10.setAlignmentY(2.0F);
        jPanel4.add(jPanel10);

        teamBscore.setHorizontalAlignment(SwingConstants.CENTER);
        teamBscore.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        teamBscore.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        teamBscore.setText("0");
        jPanel4.add(teamBscore);

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 560, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 840, 110));

        plus1A.setBackground(new java.awt.Color(0, 153, 153));
        plus1A.setForeground(new java.awt.Color(255, 255, 255));
        plus1A.setText("+1分");
        plus1A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plus1AActionPerformed(evt);
            }
        });
        getContentPane().add(plus1A, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, 80, 40));

        minus1A.setBackground(new java.awt.Color(0, 153, 153));
        minus1A.setForeground(new java.awt.Color(255, 255, 255));
        minus1A.setText("-1分");
        minus1A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minus1AActionPerformed(evt);
            }
        });
        getContentPane().add(minus1A, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, 80, 40));

        plus1B.setBackground(new java.awt.Color(153, 0, 0));
        plus1B.setForeground(new java.awt.Color(255, 255, 255));
        plus1B.setText("+1分");
        plus1B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plus1BActionPerformed(evt);
            }
        });
        getContentPane().add(plus1B, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 190, 80, 40));

        minus1B.setBackground(new java.awt.Color(153, 0, 0));
        minus1B.setForeground(new java.awt.Color(255, 255, 255));
        minus1B.setText("-1分");
        minus1B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minus1BActionPerformed(evt);
            }
        });
        getContentPane().add(minus1B, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 230, 80, 40));

        jButton2.setText("+1局");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 60, 140, 40));

        jButton3.setText("-1局");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 110, 140, 40));

        addMilliSecond.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        addMilliSecond.setText("+1ms");
        addMilliSecond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMilliSecondActionPerformed(evt);
            }
        });
        getContentPane().add(addMilliSecond, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 120, 70, 30));

        minusMilliSecond.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        minusMilliSecond.setText("-1ms");
        minusMilliSecond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minusMilliSecondActionPerformed(evt);
            }
        });
        getContentPane().add(minusMilliSecond, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 120, 70, 30));

        playerOneA.setBackground(new java.awt.Color(0, 153, 153));
        playerOneA.setForeground(new java.awt.Color(255, 255, 255));
        playerOneA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneAActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneA, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 340, 60));
        playerOneA.setText("#"+String.valueOf(firstTeamA[0].getPlayerSelectNum())+" "+String.valueOf(firstTeamA[0].getPlayerName())+" "+String.valueOf(firstTeamA[0].getPlayerNum()));

        playerOneA1.setBackground(new java.awt.Color(0, 153, 153));
        playerOneA1.setForeground(new java.awt.Color(255, 255, 255));
        playerOneA1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneA1ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneA1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 260, 340, 60));
        playerOneA1.setText("#"+String.valueOf(firstTeamA[1].getPlayerSelectNum())+" "+String.valueOf(firstTeamA[1].getPlayerName())+" "+String.valueOf(firstTeamA[1].getPlayerNum()));

        playerOneA2.setBackground(new java.awt.Color(0, 153, 153));
        playerOneA2.setForeground(new java.awt.Color(255, 255, 255));
        playerOneA2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneA2ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneA2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, 340, 60));
        playerOneA2.setText("#"+String.valueOf(firstTeamA[2].getPlayerSelectNum())+" "+String.valueOf(firstTeamA[2].getPlayerName())+" "+String.valueOf(firstTeamA[2].getPlayerNum()));

        playerOneA3.setBackground(new java.awt.Color(0, 153, 153));
        playerOneA3.setForeground(new java.awt.Color(255, 255, 255));
        playerOneA3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneA3ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneA3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 380, 340, 60));
        playerOneA3.setText("#"+String.valueOf(firstTeamA[3].getPlayerSelectNum())+" "+String.valueOf(firstTeamA[3].getPlayerName())+" "+String.valueOf(firstTeamA[3].getPlayerNum()));

        playerOneA4.setBackground(new java.awt.Color(0, 153, 153));
        playerOneA4.setForeground(new java.awt.Color(255, 255, 255));
        playerOneA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneA4ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneA4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 440, 340, 60));
        playerOneA4.setText("#"+String.valueOf(firstTeamA[4].getPlayerSelectNum())+" "+String.valueOf(firstTeamA[4].getPlayerName())+" "+String.valueOf(firstTeamA[4].getPlayerNum()));

        playerOneB1.setBackground(new java.awt.Color(153, 0, 0));
        playerOneB1.setForeground(new java.awt.Color(255, 255, 255));
        playerOneB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneB1ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneB1, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 260, 340, 60));
        playerOneB1.setText("#"+String.valueOf(firstTeamB[1].getPlayerSelectNum())+" "+String.valueOf(firstTeamB[1].getPlayerName())+" "+String.valueOf(firstTeamB[1].getPlayerNum()));

        playerOneB2.setBackground(new java.awt.Color(153, 0, 0));
        playerOneB2.setForeground(new java.awt.Color(255, 255, 255));
        playerOneB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneB2ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneB2, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 320, 340, 60));
        playerOneB2.setText("#"+String.valueOf(firstTeamB[2].getPlayerSelectNum())+" "+String.valueOf(firstTeamB[2].getPlayerName())+" "+String.valueOf(firstTeamB[2].getPlayerNum()));

        playerOneB3.setBackground(new java.awt.Color(153, 0, 0));
        playerOneB3.setForeground(new java.awt.Color(255, 255, 255));
        playerOneB3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneB3ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneB3, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 380, 340, 60));
        playerOneB3.setText("#"+String.valueOf(firstTeamB[3].getPlayerSelectNum())+" "+String.valueOf(firstTeamB[3].getPlayerName())+" "+String.valueOf(firstTeamB[3].getPlayerNum()));

        playerOneB4.setBackground(new java.awt.Color(153, 0, 0));
        playerOneB4.setForeground(new java.awt.Color(255, 255, 255));
        playerOneB4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneB4ActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneB4, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 440, 340, 60));
        playerOneB4.setText("#"+String.valueOf(firstTeamB[4].getPlayerSelectNum())+" "+String.valueOf(firstTeamB[4].getPlayerName())+" "+String.valueOf(firstTeamB[4].getPlayerNum()));

        playerOneB.setBackground(new java.awt.Color(153, 0, 0));
        playerOneB.setForeground(new java.awt.Color(255, 255, 255));
        playerOneB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerOneBActionPerformed(evt);
            }
        });
        getContentPane().add(playerOneB, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 200, 340, 60));
        playerOneB.setText("#"+String.valueOf(firstTeamB[0].getPlayerSelectNum())+" "+String.valueOf(firstTeamB[0].getPlayerName())+" "+String.valueOf(firstTeamB[0].getPlayerNum()));

        subA5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subA5ActionPerformed(evt);
            }
        });
        getContentPane().add(subA5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 40, 40));

        subA1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subA1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subA1ActionPerformed(evt);
            }
        });
        getContentPane().add(subA1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 40, 40));

        subA2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subA2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subA2ActionPerformed(evt);
            }
        });
        getContentPane().add(subA2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 40, 40));

        subA3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subA3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subA3ActionPerformed(evt);
            }
        });
        getContentPane().add(subA3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 40, 40));

        subA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subA4ActionPerformed(evt);
            }
        });
        getContentPane().add(subA4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 40, 40));

        subB5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subB5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subB5ActionPerformed(evt);
            }
        });
        getContentPane().add(subB5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 450, 40, 40));

        subB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subB1ActionPerformed(evt);
            }
        });
        getContentPane().add(subB1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 210, 40, 40));

        subB2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subB2ActionPerformed(evt);
            }
        });
        getContentPane().add(subB2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 270, 40, 40));

        subB3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subB3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subB3ActionPerformed(evt);
            }
        });
        getContentPane().add(subB3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 330, 40, 40));

        subB4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exchange-arrows.png"))); // NOI18N
        subB4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subB4ActionPerformed(evt);
            }
        });
        getContentPane().add(subB4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 390, 40, 40));

        stageTable.setModel(stageX);
        stageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jScrollPane4.setViewportView(stageTable);

        jScrollPane5.setViewportView(jScrollPane4);

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 260, 130));

        teamAPlayer11.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer11.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer11.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer11,sortTeamA[10]);
        }
        catch(Exception e){

        }
        teamAPlayer11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer11MousePressed(evt);
            }
        });
        teamAPlayer11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer11ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer11, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 510, 37, 38));

        teamAPlayer12.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer12.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer12.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer12,sortTeamA[11]);
        }
        catch(Exception e){

        }
        teamAPlayer12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer12MousePressed(evt);
            }
        });
        teamAPlayer12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer12ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer12, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 510, 37, 38));

        teamAPlayer13.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer13.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer13.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer13,sortTeamA[12]);
        }
        catch(Exception e){

        }
        teamAPlayer13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer13MousePressed(evt);
            }
        });
        teamAPlayer13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer13ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer13, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 510, 37, 38));

        teamAPlayer14.setBackground(new java.awt.Color(0, 153, 153));
        teamAPlayer14.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        teamAPlayer14.setForeground(new java.awt.Color(255, 255, 255));
        try{

            setHighlighted(teamAPlayer14,sortTeamA[13]);
        }
        catch(Exception e){

        }
        teamAPlayer14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                teamAPlayer14MousePressed(evt);
            }
        });
        teamAPlayer14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamAPlayer14ActionPerformed(evt);
            }
        });
        getContentPane().add(teamAPlayer14, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 510, 37, 38));

        pack();
    }// </editor-fold>//GEN-END:initComponents
  */ 
 
    private void reverseteamAction()
    {
        int tmp=gameListA.size()-log.getSelectedRow()-1;
        String teamname=log.getValueAt(log.getSelectedRow(), 2).toString();
     switch(log.getValueAt(log.getSelectedRow(), 4).toString())
     {
         case "投籃":
            switch(log.getValueAt(log.getSelectedRow(), 5).toString())
            {
                case "兩分命中":
                 System.out.print("inex:"+gameListA.get(log.getSelectedRow()).getPlayer().getPlayerName());
                 gameListA.get(tmp).getPlayer().setReverseOptions(0);

                 generalReverseAction(0,teamname);
                 break;
                case "兩分失誤":
                 gameListA.get(tmp).getPlayer().setReverseOptions(1);
                 break;
                case "三分命中":
                 gameListA.get(tmp).getPlayer().setReverseOptions(2);
                  
                 generalReverseAction(2,teamname);
                 break;
                case "三分失誤":
                 gameListA.get(tmp).getPlayer().setReverseOptions(3);
                 break;
            }
            break;
            case "罰球":
                 switch(log.getValueAt(log.getSelectedRow(), 5).toString())
            {
               case "罰球命中":
             gameListA.get(tmp).getPlayer().setReverseOptions(4);
             generalReverseAction(4,teamname);
             break;
               case "罰球失誤":
             gameListA.get(tmp).getPlayer().setReverseOptions(5);
             break;
            }
                 break;
            case "進攻籃板":
             gameListA.get(tmp).getPlayer().setReverseOptions(6);
                break;
            case "防守籃板":
             gameListA.get(tmp).getPlayer().setReverseOptions(7);
                break;
            case "快攻":
                 switch(log.getValueAt(log.getSelectedRow(), 5).toString())
                 {
                     case "成功":
                    gameListA.get(tmp).getPlayer().setReverseOptions(8);
                     break;
                     case "失敗":
                    gameListA.get(tmp).getPlayer().setReverseOptions(9);
                     break;
                 }
                break;
            case "蓋帽":
                gameListA.get(tmp).getPlayer().setReverseOptions(10);
                break;
            case "助攻":
                gameListA.get(tmp).getPlayer().setReverseOptions(11);
                break;
            case "搶斷":
                gameListA.get(tmp).getPlayer().setReverseOptions(12);
                break;
            case "失誤":
                gameListA.get(tmp).getPlayer().setReverseOptions(13);
                break;
            case "進攻犯規":
                gameListA.get(tmp).getPlayer().setReverseOptions(14);
                generalReverseAction(14,teamname);
                break;
            case "防守犯規":
                gameListA.get(tmp).getPlayer().setReverseOptions(15);
                generalReverseAction(15,teamname);
                break;
            case "技術犯規":
                gameListA.get(tmp).getPlayer().setReverseOptions(16);
                generalReverseAction(16,teamname);
                break;
            case "暫停":
               
                generalReverseAction(19,teamname);
                break;
            case "補分":
                switch(log.getValueAt(log.getSelectedRow(), 5).toString())
                {
                    case "+1":
                    
                    generalReverseAction(20,teamname);
                    break;
                    case "-1":
                    
                    generalReverseAction(21,teamname);
                    break; 
                }
                break;
            case "局數":
                switch(log.getValueAt(log.getSelectedRow(), 5).toString())
                {
                    case "+1":
                    
                    generalReverseAction(30,teamname);
                    break;
                    case "-1":

                    generalReverseAction(31,teamname);
                    break; 
                }
                break;
     }
     gameListA.remove(tmp);
     if(teamname.equals(teamA_name))
     {
         
         reloadTable(teamAstatTable,tableModela,teamA);
     }
     else
     {
         reloadTable(teamBstatTable,tableModelb,teamB);
     }
     removeTableRow();
    }
    private void setGeneralTrace(int type,LogAction b)
    {
        
    }
    private void removeTableRow()
    {
        System.out.println("row"+logtable.getRowCount());
        int rowtmp=log.getSelectedRow();
        logtable.removeRow(rowtmp);
        log.setRowSelectionInterval(rowtmp-1,rowtmp-1);
        
    }
    private void addLog(int stage,String time,String team,String player,int options)
    {
        String event="-",eventresult;
        switch(options)
        {
            case 0:case 1:case 2:case 3:
            event="投籃";
            break;
            case 4:case 5:
            event="罰球";
            break;
            case 6:
            event="進攻籃板";
            break;
            case 7:
            event="防守籃板";
            break;
            case 8:case 9:
            event="快攻";
            break;
            case 10:
            event="蓋帽";
            break;
            case 11:
            event="助攻";
            break;
            case 12:
            event="搶斷";
            break;
            case 13:
            event="失誤";
            break;
            case 14:
            event="進攻犯規";
            break;
            case 15:
            event="防守犯規";
            break;
            case 16:
            event="技術犯規";
            break;
            case 19:
            event="暫停";
            break;
            case 20:case 21:case 22:case 23:
            event="補分";
            break;
            case 25:
                event="換出";
                break;
            case 26:
                event="換入";
                break;
            case 30:case 31:
                event="局數";
                break;
        }
        switch(options)
        {
            case 0:eventresult="兩分命中";break;
            case 1:eventresult="兩分失誤";break;
            case 2:eventresult="三分命中";break;
            case 3:eventresult="三分失誤";break;
            case 4:eventresult="罰球命中";break;
            case 5:eventresult="罰球失誤";break;
            case 8:eventresult="成功";break;
            case 9:eventresult="失敗";break;
            case 20:eventresult="+1";break;
            case 21:eventresult="-1";break;
            case 22:eventresult="+3";break;
            case 23:eventresult="-3";break;
            case 30:eventresult="+1";break;
            case 31:eventresult="-1";break;
            default:eventresult="-";break;
        }
        Object[] eventlog={stage,time,team,player,event,eventresult};
        
       logtable.addRow(eventlog);
       log.changeSelection(log.getRowCount() - 1, 0, false, false);
    }
    private void reloadTable(javax.swing.JTable table,DefaultTableModel model,java.util.List<PlayerStat> teamx)
    {
        table.removeAll();
        model.setRowCount(0);
        int counter=1;
          for(PlayerStat x:teamx)
        {
           
            Object[] objs = {x.playerNum,x.playerName,x.getOptions(17),x.getOptions(11),x.getOptions(6)+x.getOptions(7),x.getOptions(14)+x.getOptions(15)+x.getOptions(16)};
             
            model.addRow(objs);
        }
        table.setModel(model);
    }
    private void pauseAndresumeTimer()
    {
          if(timeStart.getText().equals("開始計時"))
        {
            timeStart.setText("暫停計時");cnt++;
            if(cnt==1)t.start();
            else t.resume();
            
        }
        else
        {
            timeStart.setText("開始計時");
            t.suspend();
            printList();
        }
    }
    private void timeStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeStartActionPerformed
        // TODO add your handling code here:
      pauseAndresumeTimer();
    }//GEN-LAST:event_timeStartActionPerformed

    private void resetTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetTimerActionPerformed
        // TODO add your handling code here:
        t.suspend();
        timeStart.setText("開始計時");
        resetTimer();
    }//GEN-LAST:event_resetTimerActionPerformed

    private void addMilliSecondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMilliSecondActionPerformed
        // TODO add your handling code here:
        addMT();
    }//GEN-LAST:event_addMilliSecondActionPerformed
    private void addMinActionPerformed(java.awt.event.ActionEvent evt)
    {
        addMin();
    }
       private void minusMinActionPerformed(java.awt.event.ActionEvent evt)
    {
        minusMin();
    }
    private void minusMilliSecondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusMilliSecondActionPerformed
        // TODO add your handling code here:
        minusMT();
    }//GEN-LAST:event_minusMilliSecondActionPerformed

    private void options16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options16ActionPerformed
        // tech foul
        resetOption();
        options=16;
         notification.setText("技術犯規－請選擇球員");
    }//GEN-LAST:event_options16ActionPerformed

    private void options8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options8ActionPerformed
        // fast atk success
        resetOption();
        options=8;
        notification.setText("快攻成功－請選擇球員");
    }//GEN-LAST:event_options8ActionPerformed

    private void options14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options14ActionPerformed
        // atk foul
                resetOption();
        options=14;
        notification.setText("進攻犯規－請選擇球員");
    }//GEN-LAST:event_options14ActionPerformed

    private void options15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options15ActionPerformed
        // def foul
          resetOption();
        options=15;
         notification.setText("防守犯規－請選擇球員");
    }//GEN-LAST:event_options15ActionPerformed

    private void options7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options7ActionPerformed
        // def basket
        resetOption();
        options=7;
         notification.setText("防守籃板－請選擇球員");
    }//GEN-LAST:event_options7ActionPerformed

    private void options6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options6ActionPerformed
        // atk basket
        resetOption();
        options=6;
        notification.setText("進攻籃板－請選擇球員");
    }//GEN-LAST:event_options6ActionPerformed

    private void options9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options9ActionPerformed
        // fast atk fail
                resetOption();
        options=9;
        notification.setText("快攻失敗－請選擇球員");
    }//GEN-LAST:event_options9ActionPerformed

    private void options0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options0ActionPerformed
        // 2 point in
        resetOption();
        options=0;
        notification.setText("兩分球命中－請選擇球員");
    }//GEN-LAST:event_options0ActionPerformed

    private int[] generalAction(int option,String referteam)
    {
         int general[]=new int[3];
if(referteam.equals(teamA_name))
    {
        //general[0]=score_teamA,general[1]=foul_teamA
       
        switch(option)
        {
            case 0:
                score_teamA+=2;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+2);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                
                break;
            case 2:
                score_teamA+=3;
                 
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+3);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
            case 4:
                score_teamA+=1;
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+1);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
            case 14:case 15:case 16:
                foul_teamA+=1;
                foulteamA.setText(String.valueOf(foul_teamA));
                break;
            case 19:
                  timeout_teamA+=1;
                 
        timeoutA.setText(String.valueOf(timeout_teamA));
        break;
        case 20:
             score_teamA+=1;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+1);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
              case 21:
             score_teamA-=1;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-1);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
                      case 22:
             score_teamA+=3;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+3);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
                      case 23:
             score_teamA-=3;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-3);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
                      case 30:
            stageX.setValueAt(score_teamA, 0, stage);
            stageX.setValueAt(score_teamB, 1, stage);
            stage++;
            stageLabel.setText(String.valueOf(stage));
            if(stage>4){
                stageX.addColumn("局 "+stage);
                stageX.setValueAt(0, 0, stage);
                stageX.setValueAt(0, 1, stage);
                teamAstagescore.add(0);
                teamBstagescore.add(0);
             }
              break;
                      case 31:
                stage--;
                stageLabel.setText(String.valueOf(stage));
                break;
        }
        general[0]=score_teamA;general[1]=foul_teamA;general[2]=timeout_teamA;
    }
else 
{
     switch(option)
        {
            case 0:
                score_teamB+=2;
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+2);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
            case 2:
                score_teamB+=3;
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+3);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
            case 4:
                score_teamB+=1;
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+1);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
            case 14:case 15:case 16:
                foul_teamB+=1;
                foulteamB.setText(String.valueOf(foul_teamB));
                break;
            case 19:
              timeout_teamB+=1;
        timeoutB.setText(String.valueOf(timeout_teamB));
        break;
         case 20:
             score_teamB+=1;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+1);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
              case 21:
             score_teamB-=1;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-1);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
                      case 22:
             score_teamB+=3;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+3);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
                      case 23:
             score_teamA-=3;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-3);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
                
                 case 30:
            stageX.setValueAt(score_teamA, 0, stage);
            stageX.setValueAt(score_teamB, 1, stage);
            stage++;
            stageLabel.setText(String.valueOf(stage));
            if(stage>4){
                stageX.addColumn("局 "+stage);
                stageX.setValueAt(0, 0, stage);
                stageX.setValueAt(0, 1, stage);
                teamAstagescore.add(0);
                teamBstagescore.add(0);
             }
              break;
                      case 31:
                stage--;
                stageLabel.setText(String.valueOf(stage));
                break;
        }
      general[0]=score_teamB;general[1]=foul_teamB;general[2]=timeout_teamB;
}
   return general;             
    }
 private int[] generalReverseAction(int option,String referteam)
    {
         int general[]=new int[3];
if(referteam.equals(teamA_name))
    {
        //general[0]=score_teamA,general[1]=foul_teamA
       
        switch(option)
        {
            case 0:
                score_teamA-=2;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-2);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                
                break;
            case 2:
                score_teamA-=3;
                 
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-3);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
            case 4:
                score_teamA-=1;
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-1);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
            case 14:case 15:case 16:
                foul_teamA-=1;
                foulteamA.setText(String.valueOf(foul_teamA));
                break;
            case 19:
                  timeout_teamA-=1;
                 
        timeoutA.setText(String.valueOf(timeout_teamA));
        break;
        case 20:
             score_teamA-=1;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-1);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
              case 21:
             score_teamA+=1;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+1);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
                      case 22:
             score_teamA-=3;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)-3);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
               case 23:
             score_teamA+=3;
                
                teamAstagescore.set(stage, (int)teamAstagescore.get(stage)+3);
                stageX.setValueAt(teamAstagescore.get(stage), 0, stage);
                teamAscore.setText(String.valueOf(score_teamA));
                break;
               case 30:
                stage--;
                stageLabel.setText(String.valueOf(stage));
                t.suspend();
                resetTimer();
                break;
               case 31:
                stage++;
                stageLabel.setText(String.valueOf(stage));
                t.suspend();
                resetTimer();
                break;
            
        }
        general[0]=score_teamA;general[1]=foul_teamA;general[2]=timeout_teamA;
    }
else 
{
     switch(option)
        {
            case 0:
                score_teamB-=2;
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-2);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
            case 2:
                score_teamB-=3;
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-3);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
            case 4:
                score_teamB-=1;
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-1);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
            case 14:case 15:case 16:
                foul_teamB-=1;
                foulteamB.setText(String.valueOf(foul_teamB));
                break;
            case 19:
              timeout_teamB-=1;
        timeoutB.setText(String.valueOf(timeout_teamB));
        break;
         case 20:
             score_teamB-=1;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-1);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
              case 21:
             score_teamB+=1;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+1);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
                      case 22:
             score_teamB-=3;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)-3);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
             case 23:
             score_teamA+=3;
                
                teamBstagescore.set(stage, (int)teamBstagescore.get(stage)+3);
                stageX.setValueAt(teamBstagescore.get(stage), 1, stage);
                teamBscore.setText(String.valueOf(score_teamB));
                break;
             case 30:
                stage--;
                stageLabel.setText(String.valueOf(stage));
                t.suspend();
                resetTimer();
                break;
               case 31:
                stage++;
                stageLabel.setText(String.valueOf(stage));
                t.suspend();
                resetTimer();
                break;
        }
      general[0]=score_teamB;general[1]=foul_teamB;general[2]=timeout_teamB;
}
   return general;             
    }
    private void subPlayerAction(PlayerStat currentSubingPlayer,PlayerStat teamx){

    }
    private void teamPlayAction(PlayerStat teamx,List<PlayerStat> teamX,javax.swing.JTable table,DefaultTableModel model)
{
            System.out.println(options);
              teamx.setOptions(options);
        
          
       
          int[][] saveOptionsval=new int[teamX.size()][18];
          for(int x=0;x<teamX.size();x++)
          {
              for(int a=0;a<18;a++)
              {
              saveOptionsval[x][a]=teamX.get(x).getOptions(a);
           
              }
           
          }
          tableModela.fireTableDataChanged();
          generalAction(options,teamx.getTeam());
          System.out.print("YO:"+teamx.getPlayerName());
    
          
          LogAction logact=new LogAction(teamx);
          gameListA.push(logact);
          System.out.println("sizee:"+gameListA.size());
          reloadTable(table,model,teamX);
          addLog(stage,timelabel.getText(),teamx.getTeam(),teamx.getPlayerNum()+" "+teamx.getPlayerName(),options);
          
}
    private void teamAPlayer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer2ActionPerformed
        // TODO add your handling code here:
              if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[1],teamA,teamAstatTable,tableModela);
     
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer2ActionPerformed

    private void teamAPlayer4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer4ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[3],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer4ActionPerformed

    private void teamAPlayer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer1ActionPerformed
        // TODO add your handling code here:
        if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[0],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
      
    }//GEN-LAST:event_teamAPlayer1ActionPerformed
  
    private void teamAPlayer6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer6ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[5],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer6ActionPerformed

    private void teamAPlayer3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer3ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[2],teamA,teamAstatTable,tableModela);
           }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer3ActionPerformed

    private void teamAPlayer10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer10ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[9],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer10ActionPerformed

    private void teamAPlayer5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer5ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[4],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer5ActionPerformed

    private void teamAPlayer9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer9ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[8],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer9ActionPerformed

    private void teamAPlayer7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer7ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[6],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer7ActionPerformed

    private void teamAPlayer8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer8ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[7],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer8ActionPerformed

    private void teamBPlayer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer2ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[1],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer2ActionPerformed
private void teamAPlayer15ActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[14],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }   
 private void teamAPlayer15MousePressed(java.awt.event.MouseEvent evt){
     if(SwingUtilities.isRightMouseButton(evt)){
             rightClickButtonAction(teamAPlayer15,sortTeamA[14],teamA_name);
        }
 }
    private void teamBPlayer4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer4ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[3],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer4ActionPerformed

    private void teamBPlayer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer1ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[0],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer1ActionPerformed

    private void teamBPlayer6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer6ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[5],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer6ActionPerformed

    private void teamBPlayer3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer3ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[2],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer3ActionPerformed

    private void teamBPlayer10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer10ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[9],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer10ActionPerformed

        private void teamBPlayer11ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[10],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }   
            private void teamBPlayer12ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[11],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }
           private void teamBPlayer13ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[12],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }
            private void teamBPlayer14ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[13],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    } 
          private void teamBPlayer15ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[14],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }           
    private void teamBPlayer5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer5ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[4],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer5ActionPerformed

    private void teamBPlayer9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer9ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[8],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer9ActionPerformed

    private void teamBPlayer7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer7ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[6],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer7ActionPerformed

    private void teamBPlayer8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamBPlayer8ActionPerformed
        // TODO add your handling code here:
                if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamB[7],teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamBPlayer8ActionPerformed

    private void options1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options1ActionPerformed
        // 2 point total/miss
        resetOption();
        options=1;
        notification.setText("兩分球失誤－請選擇球員");
    }//GEN-LAST:event_options1ActionPerformed

    private void options2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options2ActionPerformed
        // 3 point in
        resetOption();
        options=2;
        notification.setText("三分球命中－請選擇球員");
    }//GEN-LAST:event_options2ActionPerformed

    private void options3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options3ActionPerformed
        // 3 point total/miss
        resetOption();
        options=3;
        notification.setText("三分球失誤－請選擇球員");
    }//GEN-LAST:event_options3ActionPerformed

    private void options4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options4ActionPerformed
        // free point in 1 
        resetOption();
        options=4;
        notification.setText("罰球命中－請選擇球員");
    }//GEN-LAST:event_options4ActionPerformed
    private void optionsxActionPerformed(java.awt.event.ActionEvent evt)
    {
               resetOption();
        options=5;
        notification.setText("罰球失誤－請選擇球員");
    }
    private void options10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options10ActionPerformed
        // blockshot
        resetOption();
        options=10;
        notification.setText("蓋帽－請選擇球員");
    }//GEN-LAST:event_options10ActionPerformed

    private void options11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options11ActionPerformed
        // assist
        resetOption();
        options=11;
        notification.setText("助攻－請選擇球員");
    }//GEN-LAST:event_options11ActionPerformed

    private void options12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options12ActionPerformed
        // steal
                resetOption();
        options=12;
        notification.setText("搶斷－請選擇球員");
    }//GEN-LAST:event_options12ActionPerformed

    private void options13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options13ActionPerformed
        // turnover
                resetOption();
        options=13;
        notification.setText("失誤－請選擇球員");
    }//GEN-LAST:event_options13ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    int selectedOption = JOptionPane.showConfirmDialog(null, 
                                  "儲存球員數據至數據庫?", 
                                  "Choose", 
                                  JOptionPane.YES_NO_OPTION); 
if (selectedOption == JOptionPane.YES_OPTION) {
     endgame(); 
 
}
    int gameselectedOption = JOptionPane.showConfirmDialog(null, 
                                  "匯出本場數據(excel)?", 
                                  "Choose", 
                                  JOptionPane.YES_NO_OPTION);
    if (gameselectedOption == JOptionPane.YES_OPTION) {
     exportcsv(); 
}
int selectedOption2 = JOptionPane.showConfirmDialog(null, 
                                  "回到主目錄?", 
                                  "Choose",   JOptionPane.YES_NO_OPTION); 

  if (selectedOption2 == JOptionPane.YES_OPTION) {
  this.setVisible(false);
         try {
             basketballmainframe mf=new basketballmainframe();
             mf.setVisible(true);
         } catch (SQLException ex) {
             Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
         }
  }
              // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void RewindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RewindActionPerformed
        // TODO add your handling code here:
        reverseteamAction();
       
      
    }//GEN-LAST:event_RewindActionPerformed

    private void TimeoutTeamBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TimeoutTeamBActionPerformed
        // TODO add your handling code here:
        generalAction(19,teamB_name);
        LogAction tmp=new LogAction(teamB_name,timeout_teamA,timeout_teamB,score_teamA,score_teamB);
        gameListA.add(tmp);
        addLog(stage,timelabel.getText(),teamB_name,"-",19);
        pauseAndresumeTimer();
    }//GEN-LAST:event_TimeoutTeamBActionPerformed

    private void TimeoutTeamAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TimeoutTeamAActionPerformed
        // TODO add your handling code here:
        generalAction(19,teamA_name);
        LogAction tmp=new LogAction(teamA_name,timeout_teamA,timeout_teamB,score_teamA,score_teamB);
        gameListA.add(tmp);
        addLog(stage,timelabel.getText(),teamA_name,"-",19);
        pauseAndresumeTimer();
    }//GEN-LAST:event_TimeoutTeamAActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        try {
            // TODO add your handling code here:
            basketballmainframe mf=new basketballmainframe();
            this.setVisible(false);
            mf.setVisible(true);

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_backActionPerformed

    private void plus1AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plus1AActionPerformed
        // TODO add your handling code here:
        int i=1;
         generalAction(20,teamA_name);
           addLog(stage,timelabel.getText(),teamA_name,"-",20);
           LogAction tmp=new LogAction(teamA_name,(int)teamAstagescore.get(stage),(int)teamBstagescore.get(stage),i,score_teamA,score_teamB);
           gameListA.push(tmp);
    }//GEN-LAST:event_plus1AActionPerformed

    private void minus1AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minus1AActionPerformed
        // TODO add your handling code here:
        int i=-1;
         generalAction(21,teamA_name);
         addLog(stage,timelabel.getText(),teamA_name,"-",21);
         LogAction tmp=new LogAction(teamA_name,(int)teamAstagescore.get(stage),(int)teamBstagescore.get(stage),i,score_teamA,score_teamB);
           gameListA.push(tmp);
    }//GEN-LAST:event_minus1AActionPerformed

    private void plus1BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plus1BActionPerformed
        // TODO add your handling code here:
        int i=+1;
         generalAction(20,teamB_name);
         addLog(stage,timelabel.getText(),teamB_name,"-",20);
         LogAction tmp=new LogAction(teamB_name,(int)teamBstagescore.get(stage),(int)teamBstagescore.get(stage),i,score_teamA,score_teamB);
           gameListA.push(tmp);
    }//GEN-LAST:event_plus1BActionPerformed

    private void minus1BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minus1BActionPerformed
        // TODO add your handling code here:
        int i=-1;
         generalAction(21,teamB_name);
            addLog(stage,timelabel.getText(),teamB_name,"-",21);
            LogAction tmp=new LogAction(teamB_name,(int)teamBstagescore.get(stage),(int)teamBstagescore.get(stage),i,score_teamA,score_teamB);
           gameListA.push(tmp);
    }//GEN-LAST:event_minus1BActionPerformed
private void stagePlus(){
        generalAction(30,teamA_name);
           addLog(stage,timelabel.getText(),"-","-",30);
           LogAction tmp=new LogAction(stage);
           gameListA.push(tmp);
            t.suspend();
           resetTimer();
}
private void stageMinus()
{
    generalAction(31,teamB_name);
     addLog(stage,timelabel.getText(),"-","-",31);
                  LogAction tmp=new LogAction(stage);
           gameListA.push(tmp);
            t.suspend();
           resetTimer();
}
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int selectedOption = JOptionPane.showConfirmDialog(null, 
                                  "加1局？", 
                                  "Choose", 
                                  JOptionPane.YES_NO_OPTION); 
if (selectedOption == JOptionPane.YES_OPTION) {
     stagePlus();
}
   
    }//GEN-LAST:event_jButton2ActionPerformed
 private void addSecActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
     addSec();
   
    }
 private void minusSecActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
     minusSec();
   
    }
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(stage==1)return;
               int selectedOption = JOptionPane.showConfirmDialog(null, 
                                  "減1局？", 
                                  "Choose", 
                                  JOptionPane.YES_NO_OPTION); 
if (selectedOption == JOptionPane.YES_OPTION) {
      
       stageMinus();
}
           
    }//GEN-LAST:event_jButton3ActionPerformed

    private void playerOneA1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneA1ActionPerformed
        // TODO add your handling code here:
        if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamA.get(firstTeamA[1].getPlayerSelectNum()-1),teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneA1ActionPerformed

    private void playerOneBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneBActionPerformed
        // TODO add your handling code here:
         if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamB.get(firstTeamB[0].getPlayerSelectNum()-1),teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneBActionPerformed

    private void playerOneB4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneB4ActionPerformed
        // TODO add your handling code here:
         if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamB.get(firstTeamB[4].getPlayerSelectNum()-1),teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneB4ActionPerformed

    private void playerOneA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneA4ActionPerformed
        // TODO add your handling code here:
        if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamA.get(firstTeamA[4].getPlayerSelectNum()-1),teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneA4ActionPerformed

    private void playerOneAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneAActionPerformed
        // TODO add your handling code here:
        if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamA.get(firstTeamA[0].getPlayerSelectNum()-1),teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneAActionPerformed

    private void playerOneB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneB1ActionPerformed
        // TODO add your handling code here:
         if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamB.get(firstTeamB[1].getPlayerSelectNum()-1),teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneB1ActionPerformed

    private void playerOneB3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneB3ActionPerformed
        // TODO add your handling code here:
         if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamB.get(firstTeamB[3].getPlayerSelectNum()-1),teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneB3ActionPerformed

    private void playerOneB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneB2ActionPerformed
        // TODO add your handling code here:
        if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamB.get(firstTeamB[2].getPlayerSelectNum()-1),teamB,teamBstatTable,tableModelb);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneB2ActionPerformed

    private void playerOneA3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneA3ActionPerformed
        // TODO add your handling code here:
          if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamA.get(firstTeamA[3].getPlayerSelectNum()-1),teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneA3ActionPerformed

    private void playerOneA2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerOneA2ActionPerformed
        // TODO add your handling code here:
          if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(teamA.get(firstTeamA[2].getPlayerSelectNum()-1),teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_playerOneA2ActionPerformed

    private void subA1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subA1ActionPerformed
       sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamA.length;x++)sub1.addElement(firstTeamA[x].getPlayerSelectNum()+" "+firstTeamA[x].getPlayerName()+" "+firstTeamA[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamA.size();x++)sub2.addElement(teamA.get(x).getPlayerSelectNum()+" "+teamA.get(x).getPlayerName()+" "+teamA.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(0);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
    String savedName=firstTeamA[0].getPlayerNum()+" "+firstTeamA[0].getPlayerName();
        firstTeamA[0]=teamA.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamA[0].getPlayerName());
    playerOneA.setText("#"+firstTeamA[0].getPlayerNum()+" "+firstTeamA[0].getPlayerName());
    addLog(stage,timelabel.getText(),teamA_name,savedName+"<->"+firstTeamA[0].getPlayerNum()+" "+firstTeamA[0].getPlayerName(),25);
    LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subA1ActionPerformed

    private void subB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subB1ActionPerformed
        // TODO add your handling code here:
        sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamB.length;x++)sub1.addElement(firstTeamB[x].getPlayerSelectNum()+" "+firstTeamB[x].getPlayerName()+" "+firstTeamB[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamB.size();x++)sub2.addElement(teamB.get(x).getPlayerSelectNum()+" "+teamB.get(x).getPlayerName()+" "+teamB.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(0);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
     String savedName=firstTeamB[3].getPlayerNum()+" "+firstTeamB[3].getPlayerName();
        firstTeamB[0]=teamB.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamB[3].getPlayerName());
    playerOneB.setText("#"+firstTeamB[0].getPlayerSelectNum()+" "+firstTeamB[0].getPlayerName()+" "+firstTeamB[0].getPlayerNum());
     addLog(stage,timelabel.getText(),teamB_name,savedName+"<->"+firstTeamB[0].getPlayerNum()+" "+firstTeamB[0].getPlayerName(),25);
        LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subB1ActionPerformed

    private void subB3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subB3ActionPerformed
           sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamB.length;x++)sub1.addElement(firstTeamB[x].getPlayerSelectNum()+" "+firstTeamB[x].getPlayerName()+" "+firstTeamB[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamB.size();x++)sub2.addElement(teamB.get(x).getPlayerSelectNum()+" "+teamB.get(x).getPlayerName()+" "+teamB.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(2);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
    String savedName=firstTeamB[2].getPlayerNum()+" "+firstTeamB[2].getPlayerName();
        firstTeamB[2]=teamB.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamB[2].getPlayerName());
    playerOneB2.setText("#"+firstTeamB[2].getPlayerSelectNum()+" "+firstTeamB[2].getPlayerName()+" "+firstTeamB[2].getPlayerNum());
     addLog(stage,timelabel.getText(),teamB_name,savedName+"<->"+firstTeamB[2].getPlayerNum()+" "+firstTeamB[2].getPlayerName(),25);
        LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}        // TODO add your handling code here:
    }//GEN-LAST:event_subB3ActionPerformed

    private void subB5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subB5ActionPerformed
           sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamB.length;x++)sub1.addElement(firstTeamB[x].getPlayerSelectNum()+" "+firstTeamB[x].getPlayerName()+" "+firstTeamB[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamB.size();x++)sub2.addElement(teamB.get(x).getPlayerSelectNum()+" "+teamB.get(x).getPlayerName()+" "+teamB.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(4);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
    String savedName=firstTeamB[4].getPlayerNum()+" "+firstTeamB[4].getPlayerName();
        firstTeamB[4]=teamB.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamB[4].getPlayerName());
    playerOneB4.setText("#"+firstTeamB[4].getPlayerSelectNum()+" "+firstTeamB[4].getPlayerName()+" "+firstTeamB[4].getPlayerNum());
     addLog(stage,timelabel.getText(),teamB_name,savedName+"<->"+firstTeamB[4].getPlayerNum()+" "+firstTeamB[4].getPlayerName(),25);
        LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}        // TODO add your handling code here:
    }//GEN-LAST:event_subB5ActionPerformed

    private void subA2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subA2ActionPerformed
   sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamA.length;x++)sub1.addElement(firstTeamA[x].getPlayerSelectNum()+" "+firstTeamA[x].getPlayerName()+" "+firstTeamA[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamA.size();x++)sub2.addElement(teamA.get(x).getPlayerSelectNum()+" "+teamA.get(x).getPlayerName()+" "+teamA.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(1);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
    String savedName=firstTeamA[1].getPlayerNum()+" "+firstTeamA[1].getPlayerName();
        firstTeamA[1]=teamA.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamA[1].getPlayerName());
    playerOneA1.setText("#"+firstTeamA[1].getPlayerSelectNum()+" "+firstTeamA[1].getPlayerName()+" "+firstTeamA[1].getPlayerNum());
       addLog(stage,timelabel.getText(),teamA_name,savedName+"<->"+firstTeamA[1].getPlayerNum()+" "+firstTeamA[1].getPlayerName(),25);
          LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subA2ActionPerformed

    private void subA3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subA3ActionPerformed
sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamA.length;x++)sub1.addElement(firstTeamA[x].getPlayerSelectNum()+" "+firstTeamA[x].getPlayerName()+" "+firstTeamA[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamA.size();x++)sub2.addElement(teamA.get(x).getPlayerSelectNum()+" "+teamA.get(x).getPlayerName()+" "+teamA.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(2);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
       String savedName=firstTeamA[2].getPlayerNum()+" "+firstTeamA[2].getPlayerName();
        firstTeamA[2]=teamA.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamA[2].getPlayerName());
    playerOneA2.setText("#"+firstTeamA[2].getPlayerSelectNum()+" "+firstTeamA[2].getPlayerName()+" "+firstTeamA[2].getPlayerNum());
       addLog(stage,timelabel.getText(),teamA_name,savedName+"<->"+firstTeamA[2].getPlayerNum()+" "+firstTeamA[2].getPlayerName(),25);
          LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subA3ActionPerformed

    private void subA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subA4ActionPerformed
sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamA.length;x++)sub1.addElement(firstTeamA[x].getPlayerSelectNum()+" "+firstTeamA[x].getPlayerName()+" "+firstTeamA[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamA.size();x++)sub2.addElement(teamA.get(x).getPlayerSelectNum()+" "+teamA.get(x).getPlayerName()+" "+teamA.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(3);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
    String savedName=firstTeamA[3].getPlayerNum()+" "+firstTeamA[3].getPlayerName();
        firstTeamA[3]=teamA.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamA[3].getPlayerName());
    playerOneA3.setText("#"+firstTeamA[3].getPlayerSelectNum()+" "+firstTeamA[3].getPlayerName()+" "+firstTeamA[3].getPlayerNum());
       addLog(stage,timelabel.getText(),teamA_name,savedName+"<->"+firstTeamA[3].getPlayerNum()+" "+firstTeamA[3].getPlayerName(),25);
          LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subA4ActionPerformed

    private void subA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subA5ActionPerformed
sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamA.length;x++)sub1.addElement(firstTeamA[x].getPlayerSelectNum()+" "+firstTeamA[x].getPlayerName()+" "+firstTeamA[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamA.size();x++)sub2.addElement(teamA.get(x).getPlayerSelectNum()+" "+teamA.get(x).getPlayerName()+" "+teamA.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(4);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
    String savedName=firstTeamA[4].getPlayerNum()+" "+firstTeamA[4].getPlayerName();
        firstTeamA[4]=teamA.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamA[4].getPlayerName());
    playerOneA4.setText("#"+firstTeamA[4].getPlayerSelectNum()+" "+firstTeamA[4].getPlayerName()+" "+firstTeamA[4].getPlayerNum());
           addLog(stage,timelabel.getText(),teamA_name,savedName+"<->"+firstTeamA[4].getPlayerNum()+" "+firstTeamA[4].getPlayerName(),25);
              LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subA5ActionPerformed

    private void subB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subB2ActionPerformed
        // TODO add your handling code here:
           sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamB.length;x++)sub1.addElement(firstTeamB[x].getPlayerSelectNum()+" "+firstTeamB[x].getPlayerName()+" "+firstTeamB[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamB.size();x++)sub2.addElement(teamB.get(x).getPlayerSelectNum()+" "+teamB.get(x).getPlayerName()+" "+teamB.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(1);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
        String savedName=firstTeamB[1].getPlayerNum()+" "+firstTeamB[1].getPlayerName();
        firstTeamB[1]=teamB.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamB[1].getPlayerName());
    playerOneB1.setText("#"+firstTeamB[1].getPlayerSelectNum()+" "+firstTeamB[1].getPlayerName()+" "+firstTeamB[1].getPlayerNum());
     addLog(stage,timelabel.getText(),teamB_name,savedName+"<->"+firstTeamB[1].getPlayerNum()+" "+firstTeamB[1].getPlayerName(),25);
        LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}
    }//GEN-LAST:event_subB2ActionPerformed

    private void subB4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subB4ActionPerformed
           sub1.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<firstTeamB.length;x++)sub1.addElement(firstTeamB[x].getPlayerSelectNum()+" "+firstTeamB[x].getPlayerName()+" "+firstTeamB[x].getPlayerNum());
      subout.setModel(sub1);
      sub2.removeAllElements(); // TODO add your handling code here:
      for(int x=0;x<teamB.size();x++)sub2.addElement(teamB.get(x).getPlayerSelectNum()+" "+teamB.get(x).getPlayerName()+" "+teamB.get(x).getPlayerNum());
      subin.setModel(sub2);
      subout.setSelectedIndex(3);
      subin.setSelectedIndex(0);
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
if (result == JOptionPane.OK_OPTION) {
        String savedName=firstTeamB[3].getPlayerNum()+" "+firstTeamB[3].getPlayerName();
        firstTeamB[3]=teamB.get(subin.getSelectedIndex());//subin
    System.out.print(firstTeamB[3].getPlayerName());
    playerOneB3.setText("#"+firstTeamB[3].getPlayerSelectNum()+" "+firstTeamB[3].getPlayerName()+" "+firstTeamB[3].getPlayerNum());
     addLog(stage,timelabel.getText(),teamB_name,savedName+"<->"+firstTeamB[3].getPlayerNum()+" "+firstTeamB[3].getPlayerName(),25);
        LogAction e=new LogAction();
    gameListA.add(e);
} else {
    System.out.println("User canceled / closed the dialog, result = " + result);
}        // TODO add your handling code here:
    }//GEN-LAST:event_subB4ActionPerformed


    private void teamAPlayer1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer1MousePressed
        // TODO add your handling code here:
        if(SwingUtilities.isRightMouseButton(evt)){
             rightClickButtonAction(teamAPlayer1,sortTeamA[0],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer1MousePressed

    private void teamAPlayer2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer2MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer2,sortTeamA[1],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer2MousePressed

    private void teamAPlayer3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer3MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer3,sortTeamA[2],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer3MousePressed

    private void teamAPlayer4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer4MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer4,sortTeamA[3],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer4MousePressed

    private void teamAPlayer5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer5MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
           rightClickButtonAction(teamAPlayer5,sortTeamA[4],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer5MousePressed

    private void teamAPlayer6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer6MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer6,sortTeamA[5],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer6MousePressed

    private void teamAPlayer7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer7MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
           rightClickButtonAction(teamAPlayer7,sortTeamA[6],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer7MousePressed

    private void teamAPlayer9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer9MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer9,sortTeamA[8],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer9MousePressed

    private void teamAPlayer10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer10MousePressed
        // TODO add your handling code here:
            if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer10,sortTeamA[9],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer10MousePressed

    private void teamAPlayer11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer11MousePressed
        // TODO add your handling code here:
         if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer11,sortTeamA[10],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer11MousePressed

    private void teamAPlayer11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer11ActionPerformed
        // TODO add your handling code here:
         if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[10],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer11ActionPerformed

    private void teamAPlayer12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer12MousePressed
        // TODO add your handling code here:
         if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer12,sortTeamA[11],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer12MousePressed

    private void teamAPlayer12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer12ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[11],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer12ActionPerformed

    private void teamAPlayer13MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer13MousePressed
        // TODO add your handling code here:
      if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer13,sortTeamA[12],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer13MousePressed

    private void teamAPlayer13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer13ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[12],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer13ActionPerformed

    private void teamAPlayer14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer14MousePressed
        // TODO add your handling code here:
         if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer14,sortTeamA[13],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer14MousePressed

    private void teamAPlayer14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teamAPlayer14ActionPerformed
        // TODO add your handling code here:
           if(options==-1)notification.setText("請先選擇事件");
       else {
           notification.setText("");
           try{
           teamPlayAction(sortTeamA[13],teamA,teamAstatTable,tableModela);
            }catch(Exception e){
               
           }
          options=-1;
       }
    }//GEN-LAST:event_teamAPlayer14ActionPerformed

    private void teamAPlayer8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamAPlayer8MousePressed
        // TODO add your handling code here:
       if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamAPlayer8,sortTeamA[7],teamA_name);
        }
    }//GEN-LAST:event_teamAPlayer8MousePressed
    private void teamBPlayer1MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer1,sortTeamB[0],teamB_name);
        }
    }
        private void teamBPlayer2MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer2,sortTeamB[1],teamB_name);
        }
    }
            private void teamBPlayer3MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer3,sortTeamB[2],teamB_name);
        }
    }
                private void teamBPlayer4MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer4,sortTeamB[3],teamB_name);
        }
    }
                private void teamBPlayer5MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer5,sortTeamB[4],teamB_name);
        }
    }
       private void teamBPlayer6MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer6,sortTeamB[5],teamB_name);
        }
    } 
       private void teamBPlayer7MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer7,sortTeamB[6],teamB_name);
        }
    }  
        private void teamBPlayer8MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer8,sortTeamB[7],teamB_name);
        }
    }  
        private void teamBPlayer9MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer9,sortTeamB[8],teamB_name);
        }
    }
        private void teamBPlayer10MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer10,sortTeamB[9],teamB_name);
        }
    }   
         private void teamBPlayer11MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer11,sortTeamB[10],teamB_name);
        }
    }
           private void teamBPlayer12MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer12,sortTeamB[11],teamB_name);
        }
    }
          private void teamBPlayer13MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer13,sortTeamB[12],teamB_name);
        }
    } 
        private void teamBPlayer14MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer14,sortTeamB[13],teamB_name);
        }
    }
         private void teamBPlayer15MousePressed(java.awt.event.MouseEvent evt){
          if(SwingUtilities.isRightMouseButton(evt)){
            rightClickButtonAction(teamBPlayer15,sortTeamB[14],teamB_name);
        }
    }   
    public void run() {
                try{
                    while(true)
                    {
                        minsec--;
                        if(minsec<1)
                        {
                            minsec=999;
                            sec--;
                        }
                        if(sec<0)
                        {
                            sec=59;
                            min--;
                        }
                        if(min<0)
                        {
                            
                            stagePlus();
                           
                           
                            
                        }
                        setTimeCounter();
                        setMTcounter();
                        displayTimer();
                        Thread.sleep(1);
                    }
                } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
    public String printList()
    {
        cnt2++;
        dstr="\n"+cnt2+") "+timelabel.getText();
        System.out.print(dstr);
        return dstr;
    }
    /**
     * @param args the command line arguments
     */
    
    private timer time=new timer();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Rewind;
    private javax.swing.JButton TimeoutTeamA;
    private javax.swing.JButton TimeoutTeamB;
    private javax.swing.JButton addMilliSecond;
    private javax.swing.JButton back;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel foulteamA;
    private javax.swing.JLabel foulteamB;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable log;
    private javax.swing.JButton minus1A;
    private javax.swing.JButton minus1B;
    private javax.swing.JButton minusMilliSecond;
    private javax.swing.JLabel notification;
    private javax.swing.JButton options0;
    private javax.swing.JButton options1;
    private javax.swing.JButton options10;
    private javax.swing.JButton options11;
    private javax.swing.JButton options12;
    private javax.swing.JButton options13;
    private javax.swing.JButton options14;
    private javax.swing.JButton options15;
    private javax.swing.JButton options16;
    private javax.swing.JButton options2;
    private javax.swing.JButton options3;
    private javax.swing.JButton options4;
    private javax.swing.JButton options6;
    private javax.swing.JButton options7;
    private javax.swing.JButton options8;
    private javax.swing.JButton options9;
    private javax.swing.JButton playerOneA;
    private javax.swing.JButton playerOneA1;
    private javax.swing.JButton playerOneA2;
    private javax.swing.JButton playerOneA3;
    private javax.swing.JButton playerOneA4;
    private javax.swing.JButton playerOneB;
    private javax.swing.JButton playerOneB1;
    private javax.swing.JButton playerOneB2;
    private javax.swing.JButton playerOneB3;
    private javax.swing.JButton playerOneB4;
    private javax.swing.JButton plus1A;
    private javax.swing.JButton plus1B;
    private javax.swing.JButton resetTimer;
    private javax.swing.JLabel stageLabel;
    private javax.swing.JTable stageTable;
    private javax.swing.JButton subA1;
    private javax.swing.JButton subA2;
    private javax.swing.JButton subA3;
    private javax.swing.JButton subA4;
    private javax.swing.JButton subA5;
    private javax.swing.JButton subB1;
    private javax.swing.JButton subB2;
    private javax.swing.JButton subB3;
    private javax.swing.JButton subB4;
    private javax.swing.JButton subB5;
    private javax.swing.JButton teamAPlayer1;
    private javax.swing.JButton teamAPlayer10;
    private javax.swing.JButton teamAPlayer11;
    private javax.swing.JButton teamAPlayer12;
    private javax.swing.JButton teamAPlayer13;
    private javax.swing.JButton teamAPlayer14;
    private javax.swing.JButton teamAPlayer2;
    private javax.swing.JButton teamAPlayer3;
    private javax.swing.JButton teamAPlayer4;
    private javax.swing.JButton teamAPlayer5;
    private javax.swing.JButton teamAPlayer6;
    private javax.swing.JButton teamAPlayer7;
    private javax.swing.JButton teamAPlayer8;
    private javax.swing.JButton teamAPlayer9;
    private javax.swing.JLabel teamAlabel;
    private javax.swing.JLabel teamAscore;
    private javax.swing.JTable teamAstatTable;
    private javax.swing.JButton teamBPlayer1;
    private javax.swing.JButton teamBPlayer10;
    private javax.swing.JButton teamBPlayer2;
    private javax.swing.JButton teamBPlayer3;
    private javax.swing.JButton teamBPlayer4;
    private javax.swing.JButton teamBPlayer5;
    private javax.swing.JButton teamBPlayer6;
    private javax.swing.JButton teamBPlayer7;
    private javax.swing.JButton teamBPlayer8;
    private javax.swing.JButton teamBPlayer9;
    private javax.swing.JLabel teamBlabel;
    private javax.swing.JLabel teamBscore;
    private javax.swing.JTable teamBstatTable;
    private javax.swing.JButton timeStart;
    private javax.swing.JLabel timelabel;
    private javax.swing.JLabel timeoutA;
    private javax.swing.JLabel timeoutB;
    private javax.swing.JLabel timeoutteamA;
    private javax.swing.JLabel timeoutteamB;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JButton teamAPlayer15;
      private javax.swing.JButton teamBPlayer11;
        private javax.swing.JButton teamBPlayer12;
          private javax.swing.JButton teamBPlayer13;
            private javax.swing.JButton teamBPlayer14;
              private javax.swing.JButton teamBPlayer15;
              private javax.swing.JButton optionsx;
              private javax.swing.JButton addMin;
               private javax.swing.JButton minusMin;
             private javax.swing.JButton addSec;
             private javax.swing.JButton minusSec;
    private void resetOption()
    {
        options=-1;
    }
    private void resetTimer() {
        if(stage<=4){
       min=10;sec=0;minsec=0;
        nstr="10:00:0";
        }
        else
        {
            min=5;sec=0;minsec=0;
        nstr="05:00:0";
        }
        mstr="000";
        dstr="";
        if(timeStart.getText().equals("暫停計時"))
        {
            timeStart.setText("開始計時");
        }
        displayTimer();
    }
    private void displayTimer(){
        timelabel.setText(nstr);
    }
    private void setTimeCounter(){
        nstr="";
        if(min<10)
        {
            nstr="0"+min;
        }
      
        else
        {
            nstr=""+min;
        }
        if(sec<10)
        {
            nstr+=":0"+sec;
        }
          else if (sec==60)
        {
            nstr+=":00";
        }
        else
        {
            nstr+=":"+sec;
        }
    }
    private void setMTcounter()
    {
            nstr+=":"+minsec/100;
        
    }
    private void addMin(){
        if(min<9){
            min++;
            if(sec<10)
                 nstr="0"+min+":0"+sec+":"+minsec/100;
            else
            nstr="0"+min+":"+sec+":"+minsec/100;
            displayTimer();
            
        }
    }
       private void minusMin(){
        if(min>0){
            min--;
            if(sec<10)
                 nstr="0"+min+":0"+sec+":"+minsec/100;
            else
            nstr="0"+min+":"+sec+":"+minsec/100;
            displayTimer();
            
        }
    }
    private void addMT(){
       if(min<10) {
    minsec+=100;
    nstr=nstr.substring(0,nstr.length()-2);
    nstr+=":"+minsec/100;
    if(minsec/100>9)
    {
        sec++;
        minsec=0;
            nstr=nstr.substring(0,nstr.length()-5);
            nstr+=sec;
            nstr+=":"+minsec/100;

    }
            if(sec>59)
        {
            minsec=0;
            sec=0;
            
            min++;
            nstr="";
            nstr+=min;
            nstr+=":0"+sec;
            nstr+=":"+minsec/100;
        }
    System.out.println(nstr);
    displayTimer();
        }
}
    private void minusMT()
    {
         if(min>=0) {
    
 
    nstr=nstr.substring(0,nstr.length()-2);
    nstr+=":"+minsec/100;
    if(minsec/100==0)
    {
     
        if(sec==0){sec=60;min--;}
        sec--;
        minsec=900;
           nstr="";
           if(min<10)
            nstr+="0"+min;
            nstr+=":"+sec;
            nstr+=":"+minsec/100;
            
    }
    minsec-=100;
  
    System.out.println(nstr);
    displayTimer();
        }
    }
     private void addSec()
    {
          if(min<=9){
            sec++;

            nstr="";
            if(sec>59)
            {
            min++;
            sec=0;
             if(min==10)
            {
                minsec=0;
            }
            }

            if(min<10)
            nstr+="0"+min;
            else
                nstr+=min;
            if(sec<10)
            nstr+=":0"+sec;
            else
                nstr+=":"+sec;
            nstr+=":"+minsec/100;
            
            displayTimer();
            
        }
    }
      private void minusSec()
    {
         if(min>=0) {
    sec--;
    nstr="";
    if(sec<0)
    {
        min--;
        sec=59;
    }
                if(min<10)
            nstr+="0"+min;
            else
                nstr+=min;
            if(sec<10)
            nstr+=":0"+sec;
            else
                nstr+=":"+sec;
            nstr+=":"+minsec/100;
    
    displayTimer();
        }
    }
}
