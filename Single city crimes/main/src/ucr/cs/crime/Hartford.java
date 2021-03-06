package ucr.cs.crime;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import com.opencsv.CSVReader;


public class  Hartford extends Base
{
	
public static void main(String[] args) throws Exception 
{
	
  // String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Hartford/hartford.csv";
      
String filename = "/home/nhoss003/Hartford/hartford.csv";
Hartford  parseCSVFile = new  Hartford();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}



public void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        Timestamp crawl_time=null;
       Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2016-06-20 00:00:00";
        String invalid="2017-01-01 00:00:00";
       
      
    	String state ="CT";
    	String city= "Hartford";	
    	

    	String incident_time=null;
    	
    	double latitude= 0.0;
    	double longitude= 0.0;
    	
    	 	String address=null;
    	String date;
    	
    	String time=null;
    	
    	String year=null;
    	 String month=null;
         String day=null;
    	
    	String source = "https://data.hartford.gov/Public-Safety/Police-Incidents-01012005-to-Current/889t-nwfu";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
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
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	String [] part = row[4].split("-");
         crime_type = part[1];
        	address= row[3];
       
        	
        date = row[1] ;
        	time=row[2];
        	if(time.length()==4){
        		String part1=time.substring(0,2);
        		String part2=time.substring(2);
        		time=part1+":"+part2+":00";
        		 }else if(time.length()==3){
        			 String part1=time.substring(0,1);
             		String part2=time.substring(1);
             		time=part1+":"+part2+":00";
        		 }else if(time.length()==2){
              		time="00:"+time+":00";
         		 }
        		 else if(time.length()==1){
        			 time="00:0"+time+":00";
        		 }
        	String[] str=date.split("/");
    		year= str[2];
            month= str[0];
            day=str[1];
            String date2 =year+"-"+month+"-"+day;
			incident_time = date2+" "+ time;
			if(incident_time!=null && !incident_time.isEmpty()){
				t = Util.convertStringToTimestamp(incident_time);
				  if(t.after(t1) && t.before(t2)){
		String latlong=row[11];
		
		if(!latlong.isEmpty()){
			if(latlong.contains("(")){
	        	String [] part1 = latlong.split("\\(");
	        	String [] part2 = part1[1].split(",");
				
				latitude=Double.parseDouble(part2[0].trim());
			//	System.out.println(latitude);
				
				longitude= Double.parseDouble(part2[1].trim().replace(")",""));
			//System.out.println(longitude);
		}
			else{
				latitude=0.0;
				longitude=0.0;
			}
		}
		else{
				latitude=0.0;
				longitude=0.0;
			}
        
		
		System.out.println(flag);
		
		
		Violent.isViolent(this);

		
			
			
			String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,latitude,longitude,source_URL,incident_time, violent) VALUES ( ?,?, ?, ?, ?, ? , ?, ? ,?, ? )";
	             PreparedStatement ps = myConn.prepareStatement(query);
	     
	             ps.setString(1,crime_type);
	             ps.setTimestamp(2,crawl_time);
	             ps.setString(3,address);
	             ps.setString(4,city);
	             ps.setString(5,state);
	             
			  if((int)latitude !=0){
	             ps.setDouble(6,latitude);}
			   else{ps.setString(6,null);}
			   if((int)longitude !=0){
	            ps.setDouble(7,longitude);}
		       else{ps.setString(7,null);}
	             ps.setString(8,source);
	             if(!incident_time.isEmpty()){
	             ps.setTimestamp(9,Util.convertStringToTimestamp(incident_time));}
	             else{
	            	 ps.setTimestamp(9,null);
	             }
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
        }
        catch (SQLException ex) {
    		ex.printStackTrace();
    	} 
    	 catch(Exception e)
        {
            
            System.out.println(e.toString());
            e.printStackTrace();
        }
}

}

