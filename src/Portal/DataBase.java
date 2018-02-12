/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Portal;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author abhij
 */
public class DataBase {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    Statement statement;
    
    DataBase()
    {
        try{             
            //MAKE SURE YOU KEEP THE ojdbc6.jar file in java/lib/ext folder
            

           // DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            
            Class.forName("oracle.jdbc.OracleDriver"); 
            con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","abhijith","abhijith"); 
            
             
           }
        catch (Exception e) 
        {
            System.out.println(e);
        }
    }
        //ip:username,password
        //return boolean
    public Boolean checkLogin(String uname,String pwd,String type)
    {
        try {
            pst=con.prepareStatement("select * from Login where user_id=? and password=? and type=?");        
            pst.setString(1, uname); //this replaces the 1st  "?" in the query for username
            pst.setString(2, pwd); 
            pst.setString(3,type);//this replaces the 2st  "?" in the query for password
            //executes the prepared statement
            rs=pst.executeQuery();
            if(rs.next())
            {
                //TRUE iff the query founds any corresponding data
                return true;
            }
            else
            {
                return false;
            }
        } catch (Exception e) {            
            System.out.println("error while validating"+e);
            return false;
        }
    }
    public Boolean checkUserId(String user_id) 
    {
        try {        
            pst=con.prepareStatement("select * from Login where user_id=?");
            pst.setString(1, user_id);
            rs= pst.executeQuery();
            if(rs.next())
            {
                return true;
            }
            else 
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
   
        return false;
    }
    public void insertIntoLogin(String user_id,String password,String name,String subject)
    {
         try {
            
           statement= con.createStatement();
           String s="TEACHER";
           int i=statement.executeUpdate("INSERT INTO LOGIN VALUES('"+user_id+"' ,'"+password+"' , '"+"TEACHER"+"' )");
          i=statement.executeUpdate("INSERT INTO FACULTY VALUES('"+user_id+"','"+name+"','"+subject+"')");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     public void insertIntoLogin(String user_id,String password,String name)
    {
        try {
            String s="STUDENT";
           statement= con.createStatement();
           int i=statement.executeUpdate("INSERT INTO LOGIN VALUES( '"+user_id+"' , '"+password+"' , '"+s+"')");
           i=statement.executeUpdate("INSERT INTO STUDENT VALUES('"+user_id+"' , '"+name+"')");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
     public Boolean checkSubjectAvalaible(String subject)
     {
        try {
            pst=con.prepareStatement("select * from Faculty where subject=?");
            pst.setString(1, subject);
            rs=pst.executeQuery();
            if(rs.next())
            {
                return false;
            }
            else
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
         return false;   
     }
    public void insertExamDetails(String user_id,int exam_id,String exam_name, String date)
    {
        try {
            statement = con.createStatement();
            statement.executeUpdate("INSERT INTO EXAM VALUES('"+user_id+"',"+exam_id+",'"+exam_name+"','"+date+"')");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void insertQuestion(String question,String option1,String option2,String option3,String option4,int qid,int ansOp,int exam_id)
    {
        try{
            statement = con.createStatement();
            statement.executeUpdate("INSERT INTO QUESTIONS VALUES( "+qid+",'"+question+"','"+option1+"','"+option2+"','"+option3+"','"+option4+"',"+ansOp+","+exam_id+")");
     
     
        } catch(SQLException ex){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public int getExamID()
    {
        int id=1;
        try{
            pst=con.prepareStatement("select exam_id from exam where exam_id=(select max(exam_id) from exam)");
 
            rs= pst.executeQuery();
            if(rs.next())
            {
                System.out.println("Executed");
                id=rs.getInt("exam_id")+1;
                
            }
            return id;
             
        }catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
           return -1;
    }
    
    public String [][] returnAvailableQuizzes(String user_id)
    {
        int count=0;
        String[][] available= new String[2][1];
        available[0][0]="No Quizzes Available";
        available[1][0]=String.valueOf(0);
        
        try{
            pst=con.prepareStatement("select count(*) from exam where exam_id in (select distinct exam_id from questions) and exam_id not in (select exam_id from marks where user_id = '"+user_id+"')");
            rs= pst.executeQuery();
            if(rs.next())
            {
                count=rs.getInt(1);
                
            }
            if(count==0) return available;
            available[0]= new String[count+1];
            available[1]= new String[count+1];
            available[0][0]="Select One";
            available[1][0]=String.valueOf(0);
            pst=con.prepareStatement("select exam_id,exam_name from exam where exam_id in (select distinct exam_id from questions) and exam_id not in (select exam_id from marks where user_id = '"+user_id+"')");
            rs= pst.executeQuery();
            int i=1;
            while(rs.next())
            {
                available[0][i]=rs.getString(2);
                available[1][i]=String.valueOf(rs.getInt(1));
                i++;
            }
            
        }catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        return available;
    }
    
    public String[] returnQuestionDetails(int qid,int exam_id)
    {
        String questionDetails[]= new String[6];
        
        try {
            pst=con.prepareStatement("select question,option1,option2,option3,option4,ansop from questions"
                    + " where exam_id = "+exam_id+" and q_id="+qid);
            rs= pst.executeQuery();
            
            if(rs.next())
            {
                questionDetails[0]=rs.getString(1);
                questionDetails[1]=rs.getString(2);
                questionDetails[2]=rs.getString(3);
                questionDetails[3]=rs.getString(4);
                questionDetails[4]=rs.getString(5);
                questionDetails[5]=String.valueOf(rs.getInt(6));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return questionDetails;  
    }
    
    public int incrementMarks(String user_id,int exam_id)
    {
        int x=0;
        try{
            pst=con.prepareStatement("select count(*) from marks where user_id='"+user_id+"' and exam_id = " +exam_id+" ");
            rs= pst.executeQuery();
            if(rs.next())
                x=rs.getInt(1);
            else 
                return 0;
            if(x==0)
            {
                statement = con.createStatement();
                statement.executeUpdate("INSERT INTO MARKS VALUES('"+user_id+"',"+exam_id+","+x+")");
                
            }
            statement = con.createStatement();
            statement.executeUpdate("update marks set marks=marks+1 where user_id='"+user_id+"' and "+"exam_id="+exam_id);
            
            
        } catch(SQLException ex){
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public String [][] returnAvailableResults(String user_id)
    {
        int count=0;
        String[][] available= new String[1][1];
        available[0][0]="No Quizzes Available";
      
        try{
            pst=con.prepareStatement("select count(*) from marks where user_id = '"+user_id+"'");
            rs= pst.executeQuery();
            if(rs.next())
            {
                count=rs.getInt(1);
                
            }
            if(count==0) return available;
           available = new String[count][5];
            pst=con.prepareStatement("select m.exam_id,e.exam_name,"
                    + "f.subject, f.name,marks from exam e,marks m,faculty f "
                    + "where m.exam_id=e.exam_id and e.user_id=f.user_id and m.user_id='"+user_id+"'");
            rs= pst.executeQuery();
            int i=0;
            while(rs.next())
            {
                available[i][0]=String.valueOf(rs.getInt(1));
                available[i][1]=rs.getString(2);
                available[i][2]=rs.getString(3);
                available[i][3]=rs.getString(4);
                available[i][4]=String.valueOf(rs.getInt(5));
                i++;
            }
            
        }catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        return available;
    }
    
    public String[][] facultyConductedQuizzes(String user_id)
    {
        int count=0;
        String[][] available= new String[2][1];
        available[0][0]="No Quizzes Posted";
        available[1][0]=String.valueOf(0);
        
        try{
            pst=con.prepareStatement("select count(*) from exam where user_id = '"+user_id+"'");
            rs= pst.executeQuery();
            if(rs.next())
            {
                count=rs.getInt(1);
                
            }
            if(count==0) return available;
            available[0]= new String[count+1];
            available[1]= new String[count+1];
            available[0][0]="Select One";
            available[1][0]=String.valueOf(0);
            pst=con.prepareStatement("select exam_id,exam_name from exam where  user_id = '"+user_id+"'");
            rs= pst.executeQuery();
            int i=1;
            while(rs.next())
            {
                available[0][i]=rs.getString(2);
                available[1][i]=String.valueOf(rs.getInt(1));
                i++;
            }
            
        }catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        return available;
    }
    
    public String[][] marksOfStudentsForExam(int exam_id){
        int count=0;
        String[][] available= new String[1][1];
        available[0][0]="None";
      
        try{
            pst=con.prepareStatement("select count(*) from marks where exam_id = "+exam_id);
            rs= pst.executeQuery();
            if(rs.next())
            {
                count=rs.getInt(1);
                
            }
            if(count==0) return available;
            available = new String[count][3];
            pst=con.prepareStatement("select m.user_id,s.name,marks from marks m,student s "
                    + "where exam_id= "+exam_id+" and m.user_id=s.user_id");
            rs= pst.executeQuery();
            int i=0;
            while(rs.next())
            {
                available[i][0]=rs.getString(1);
                available[i][1]=rs.getString(2);
                available[i][2]=String.valueOf(rs.getInt(3));
                i++;
            }
            
        }catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        return available;
    }
}
