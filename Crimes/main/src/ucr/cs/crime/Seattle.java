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


public class  Seattle extends Base{ 

	
public static void main(String[] args) throws Exception 
{
	
  //String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/seattle/seattle.csv";
      
	String filename = "/home/nhoss003/seattle/seattle.csv";
	Seattle  parseCSVFile = new  Seattle();
       
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
    	String state ="WA";
    	//String zip =null;
    	String city ="Seattle";
    	String address;
    	String year=null;
   	    String month=null;
        String day=null;
     	double latitude= 0.0;
    	double longitude= 0.0;
    	String incident_time=null;
    	
    	
    	String source = "https://data.seattle.gov/Public-Safety/Seattle-Police-Department-911-Incident-Response/3k2p-39jp?category=Public-Safety&view_name=Seattle-Police-Department-911-Incident-Response";
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
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	
        	  incident_time = row[7] ;

          	if(!incident_time.isEmpty()){
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
          	if(incident_time!=null && !incident_time.isEmpty()){
    			t = Util.convertStringToTimestamp(incident_time);
    			  if(t.after(t1) && t.before(t2)){
        		  
              crime_type = row[4];
              address = row[8];

      		
      		if(!row[13].isEmpty()){
      				latitude=Double.parseDouble(row[13].trim());
      		}
      		else{
      			latitude=0.0;
      			
      		}
      		if(!row[12].isEmpty()){
      				longitude= Double.parseDouble(row[12].trim());
      		}
      		else{
      			
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
        ps.setDouble(6,latitude);
        }
	   else{
		   ps.setString(6,null);
	   }
	   if((int)longitude !=0){
       ps.setDouble(7,longitude);
       }
      else{
    	  ps.setString(7,null);
    	  }
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

