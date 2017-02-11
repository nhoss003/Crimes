package ucr.cs.crime;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import com.opencsv.CSVReader;


public class  SanDiegoCounty extends Base
{
	
public static void main(String[] args) throws Exception 
{
	
    //  String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/SD/SD.csv";
      
	String filename = "/home/nhoss003/sanDiegoCounty/SD.csv";
	SanDiegoCounty  parseCSVFile = new  SanDiegoCounty();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}
public void parseUsingOpenCSV(String filename) throws Exception
{	



        CSVReader reader;
        Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2016-06-20 00:00:00";
        String invalid="2017-01-01 00:00:00";
        Timestamp crawl_time=null;
    	String state = "CA";
    	String city;	
    	String address;
    	String zip;
    	String incident_time;
    	String accuracy=null;
    	
    	String source = "http://www.sandag.org/index.asp?projectid=446&fuseaction=projects.detail";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
     //Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
       Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
	t1=Util.convertStringToTimestamp(thisTime);
		t2=Util.convertStringToTimestamp(invalid);
		
        while ((row = reader.readNext()) != null) 
        {
        	if(flag < 1){
				flag++;
				continue;}
   
        	 crime_type= row[1].replace("\"","") ;
        	
        	incident_time = row[2].replace("\"","") ;
//        	String[] splited_incident_time = incident_time.split("\\s+");
//                  	
//        	SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
//            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm:ss a");
//            Date date = parseFormat.parse(splited_incident_time[1]+ " "+splited_incident_time[2]);
//            String new_time= displayFormat.format(date);
//			incident_time = splited_incident_time[0]+" "+ new_time;
//			
        	String[] splited_incident_time = incident_time.split("\\s+");
        	String[] date1 = splited_incident_time[0].split("/");

        	        	if(date1[0].length()==1){
        	

        	        		date1[0]= "0"+date1[0];
        			

        	        	}
        	        	if(date1[1].length()==1){
        	        		date1[1]= "0"+date1[1];
        	        	}
        	        	String[] time1 = splited_incident_time[1].split(":");	
        	        	if(time1[0].length()==1){
        	        		time1[0]= "0"+time1[0];
        	        	}

        incident_time = date1[0]+"/"+date1[1]+"/"+date1[2]+" "+time1[0]+":"+time1[1]+":"+time1[2];
        if(incident_time!=null && !incident_time.isEmpty()){
			t = Util.convertStringToTimestamp(incident_time);
			  if(t.after(t1) && t.before(t2)){
			
			address= row[3].replace("\",\"","") ;
			//address= row[3].replace("\'","") ;
			//System.out.println(address);
			city= row[5].replace("\"","") ;
			zip = row[4].replace("\"","") ;
			
			Violent.isViolent(this);
			
		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,source_URL,incident_time,locationAccuracy,violent) VALUES ( ?,?, ?, ?, ?, ? , ?, ? ,?, ? )";
             PreparedStatement ps = myConn.prepareStatement(query);
     
             ps.setString(1,crime_type);
             ps.setTimestamp(2,crawl_time);
             ps.setString(3,address);
             ps.setString(4,city);
             ps.setString(5,state);
             ps.setString(6,zip);
             //ps.setDouble(7,pos[0]);
            // ps.setDouble(8,pos[1]);
             ps.setString(7,source);
             ps.setTimestamp(8,Util.convertStringToTimestamp(incident_time));
             ps.setString(9,accuracy);
             ps.setInt(10,violent);
             ps.executeUpdate();
             myConn.commit();
             ps.close();
			  }
			  }
                
                
                }    
        
		
	myConn.close();
	System.out.println("***Parsing the File has Completed Successfully, you can start other files***");
        }
        catch (FileNotFoundException e) 
        {
                System.err.println(e.getMessage());
        }
        catch (IOException e) 
        {
                System.err.println(e.getMessage());
        } catch (SQLException ex) {
    		ex.printStackTrace();
    	}  catch(Exception e)
        {
            
            System.out.println(e.toString());
            e.printStackTrace();
        }
}

}



