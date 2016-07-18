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



public class SanFrancisco extends Base
{
	private double latitude = 0.0;

	private double longitude = 0.0;
	
	private String state = "CA";
	
	private String city="San Francisco";	
	
	private String address=null;
	
	private String zip=null;
	

public static void main(String[] args) throws Exception 
{
	
     String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/SF/SF.csv";
      
	//String filename = "/home/nhoss003/sanFrancisco/SF.csv";
	SanFrancisco parseCSVFile = new SanFrancisco();
       
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
    	String incident_time;
    	String date1=null;
    	String time1=null;
    	String lat=null;
		String longi=null;
    	String accuracy=null;
 
    	
    	String source = "https://data.sfgov.org/Public-Safety/SFPD-Incidents-from-1-January-2003/tmnf-yvry";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
       
       Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
       
        
		myConn.setAutoCommit(false);
		t1=Util.convertStringToTimestamp(thisTime);
		t2=Util.convertStringToTimestamp(invalid);
		
        while ((row = reader.readNext()) != null) 
        {
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	zip = null;

        	accuracy=null;
        	
        	
        	if(flag <1){
				flag++;
				continue;}
        	
			 crime_type= row[1].replace("\"","") ;
			
			
		  date1=row[4].replace("\"","") ;
		  time1=row[5].replace("\"","") ;
		  
		  
		  String[] month = date1.split("/");
		  
		  String[] hour = time1.split(":");
		  
		  
		  if((month[0].equals("03"))&& month[1].matches("0[1-9]{1}")&&(hour[0].equals("02"))){
			  incident_time = date1+" "+"03:00:00";
		  }else{
			  
		  incident_time = date1+" "+time1+":00";
		  
		  }
		  if(incident_time!=null && !incident_time.isEmpty()){
				t = Util.convertStringToTimestamp(incident_time);
				  if(t.after(t1) && t.before(t2)){
		  
		  address= row[8].replace("\"","") ;
		  longi = row[9].replace("\"","") ;
		  lat = row[10].replace("\"","") ;
		  double latitudeDouble = Double.parseDouble(lat);
		  double longitudeDouble = Double.parseDouble(longi);
		  latitude = latitudeDouble;
		  longitude = longitudeDouble;

		if(address.length()!=0){

	Violent.isViolent(this);
		String query = "INSERT INTO `business`.`crimes` (crime_type,crawl_time,address,city,state,zip,latitude,longitude,source_URL,incident_time,locationAccuracy,violent) VALUES ( ?,?, ?, ?, ?, ?, ?, ? , ?, ? ,?, ? )";
             PreparedStatement ps = myConn.prepareStatement(query);
     
             ps.setString(1,crime_type);
             ps.setTimestamp(2,crawl_time);
             ps.setString(3,address);
             ps.setString(4,city);
             ps.setString(5,state);
         	 ps.setString(6,null);	
             ps.setDouble(7,latitude);
             ps.setDouble(8,longitude);
             ps.setString(9,source);
             ps.setTimestamp(10,Util.convertStringToTimestamp(incident_time));
             ps.setString(11,accuracy);
             ps.setInt(12,violent);
             ps.executeUpdate();
             myConn.commit();
             ps.close();
		}
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
