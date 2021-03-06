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
import org.apache.commons.*;

import com.opencsv.CSVReader;


public class  Louisville extends Base{ 


public static void main(String[] args) throws Exception 
{
	
    String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/louisville/louisville.csv";
      
	//String filename = "/home/nhoss003/louisville/louisville.csv";
Louisville  parseCSVFile = new  Louisville();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}

public boolean isZipcode(String str)
{
    if(str.length()!=5 )
        return false;
    else
    {
        for(int i=0; i<str.length(); i++)
        {
            if(!Character.isDigit(str.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }
}

public void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
     
        Timestamp crawl_time=null;
        Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2016-06-26 00:00:00";
        String invalid="2017-01-01 00:00:00";
    	String state ="KY";
    	String zip =null;
    	String city ="Louisville";
    	String address;

    	String incident_time=null;
    	
    	
    	String source = "http://portal.louisvilleky.gov/dataset/crimedataall-data";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
     Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
    //  Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
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
        	
        	  incident_time = row[2] ;
        	 System.out.println(incident_time);
        	 if(incident_time!=null && !incident_time.isEmpty()){
     			t = Util.convertStringToTimestamp(incident_time);
     			  if(t.after(t1) && t.before(t2)){
        	
              crime_type = row[3];
              address= row[10];
              
              city=row[11];
              if(!city.isEmpty()){
              if(city.equals("LVIL")){
            	  city ="Louisville";
            	  
              }
            city= city.toLowerCase();
            if(city.length()>1){
          	city= city.substring(0, 1).toUpperCase() + city.substring(1);
            }
            else{
            	city= city.substring(0, 1).toUpperCase();
            }
              }
              
              if(isZipcode(row[12])){
              zip=row[12];
              }

		System.out.println(flag);
		
		
		Violent.isViolent(this);

		
			
			
			String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,source_URL,incident_time, violent) VALUES ( ?, ?, ?, ?, ? , ?, ? ,?, ? )";
	             PreparedStatement ps = myConn.prepareStatement(query);
	     
	             ps.setString(1,crime_type);
	             ps.setTimestamp(2,crawl_time);
	             ps.setString(3,address);
	             if(city.isEmpty()){
	            	 city="Louisville";
	             }
	             ps.setString(4,city);
	             ps.setString(5,state);
	             ps.setString(6,zip);
	             ps.setString(7,source);
	             if(!incident_time.isEmpty()){
	             ps.setTimestamp(8,Util.convertStringToTimestamp(incident_time));}
	             else{
	            	 ps.setTimestamp(8,null);
	             }
	             ps.setInt(9,violent);
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

