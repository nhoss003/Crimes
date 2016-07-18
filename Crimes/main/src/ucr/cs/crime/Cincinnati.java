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


public class  Cincinnati extends Base{ 


public static void main(String[] args) throws Exception 
{
	
  // String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Cincinnati/Cincinnati.csv";
      
String filename = "/home/nhoss003/Cincinnati/Cincinnati.csv";
Cincinnati  parseCSVFile = new  Cincinnati();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}


public void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        Timestamp crawl_time=null;
        Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2015-07-12 00:00:00";
        String invalid="2017-01-01 00:00:00";
    	String state;
    	//String zip =null;
    	String city;
    	String address;
    	String year=null;
   	    String month=null;
        String day=null;
//     	double latitude= 0.0;
//    	double longitude= 0.0;
    	String incident_time=null;
    	
    	
    	String source = "https://data.cincinnati-oh.gov/Safer-Streets/Police-Crime-Incident-Data/w7vh-beui";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
   //  Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
      Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
		t1=Util.convertStringToTimestamp(thisTime);
		t2=Util.convertStringToTimestamp(invalid);
        while ((row = reader.readNext()) != null) 
        {

        	
        	if(flag < 1){
				flag++;
				continue;}
        	
        	incident_time=null;
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        //	String[] time=row[3].split("+");
        	 // incident_time = time[0].trim() ;
        	if(row[3]!=null && !row[3].isEmpty()){
        	incident_time =row[3].substring(0, 22).trim() ;
        	}
          	if(incident_time!=null){
          	String[] splited_incident_time = incident_time.split("\\s+");
          	
          	//System.out.println(s);
          	SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
              SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm:ss a");
              Date date = parseFormat.parse(splited_incident_time[1]+ " "+splited_incident_time[2]);
              String new_time= displayFormat.format(date);
              String[] str=splited_incident_time[0].split("/");
              year= str[2];
              month= str[0];
              day=str[1];
              String date2 =year+"-"+month+"-"+day;
  			incident_time = date2+" "+ new_time;
          	}
          	if(incident_time!=null){
        	  t = Util.convertStringToTimestamp(incident_time);}
        	  if(t.after(t1) && t.before(t2)){
        		  
              crime_type = row[1];
              city=row[8];
              
              if(!city.isEmpty()){
                  city= city.toLowerCase();
                  city= city.substring(0, 1).toUpperCase() + city.substring(1);
                  String [] part = city.split("\\s+");
                  if(part.length>1){
                  	 city="";
                  	for(int i=0 ; i< part.length ; ++i){
                  		part[i] = part[i].substring(0, 1).toUpperCase() + part[i].substring(1);
                  		city += part[i]+" ";
                  	}
                  	
                  }
                	
                  }
              state=row[9];
            String block1=row[5].trim();
            String block2=row[6].trim();
            String street=row[7].trim();
            address= block1 + " " + block2 + " " + street;

            
		System.out.println(flag);
		
		
		Violent.isViolent(this);

		if(incident_time!=null){
			
			
			String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,source_URL,incident_time, violent) VALUES ( ?, ?, ?, ? , ?, ? ,?, ? )";
	             PreparedStatement ps = myConn.prepareStatement(query);
	     
	             ps.setString(1,crime_type);
	             ps.setTimestamp(2,crawl_time);
	             ps.setString(3,address);
	             ps.setString(4,city);
	             ps.setString(5,state);
//	             if((int)latitude !=0){
//	                 ps.setDouble(5,latitude);}
//	         	   else{ps.setString(5,null);}
//	         	   if((int)longitude !=0){
//	                ps.setDouble(6,longitude);}
//	               else{ps.setString(6,null);}
	             ps.setString(6,source);
	             
	             ps.setTimestamp(7,Util.convertStringToTimestamp(incident_time));
	           
	             ps.setInt(8,violent);
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

