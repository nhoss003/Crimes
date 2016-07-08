package ucr.cs.saltLakeCity;

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


public class  saltLakeCity{ 

	static int flag=0;
    public int violent = 0;
	
	String crime_type=null;
	
	public String getCrime_type() {
		return crime_type;
	}
public static void main(String[] args) throws Exception 
{
	
   //String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/saltLakeCity/saltLakeCity.csv";
      
String filename = "/home/nhoss003/saltLakeCity/saltLakeCity.csv";
saltLakeCity  parseCSVFile = new  saltLakeCity();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}

private void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        Timestamp crawl_time=null;
        Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2013-12-31 00:00:00";
        String invalid="2017-01-01 00:00:00";
    	String state = "UT";
    	//String zip = null;
    	String city = "Salt Lake City";
    	String address;

    	String incident_time = null;
    	
    	
    	String source = "https://data.slcgov.com/Package?package=city-of-salt-lake-police-cases-ytd-2014";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
    // Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
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
        	
        	  incident_time = row[4] ;
        		if(!incident_time.isEmpty()){
                	  t = Util.convertStringToTimestamp(incident_time);}
        	  if(t.after(t1)&& t.before(t2)){
        		  
              crime_type = row[2];
              if(row[2].length()>100){
        		  crime_type= row[2].substring(0,99);
        		
        	  }
              address= row[7];
              
              city=row[8].trim();
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
              
           

		System.out.println(flag);
		
		
		Violent.isViolent(this);

		
			
			
			String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,source_URL,incident_time, violent) VALUES ( ?, ?, ?, ? , ?, ? ,?, ? )";
	             PreparedStatement ps = myConn.prepareStatement(query);
	     
	             ps.setString(1,crime_type);
	             ps.setTimestamp(2,crawl_time);
	             ps.setString(3,address);
	             if(city.isEmpty()){
	            	 city="Louisville";
	             }
	             ps.setString(4,city);
	             ps.setString(5,state);
	      
	             ps.setString(6,source);
	             if(!incident_time.isEmpty()){
	             ps.setTimestamp(7,Util.convertStringToTimestamp(incident_time));}
	             else{
	            	 ps.setTimestamp(7,null);
	             }
	             ps.setInt(8,violent);
	             ps.executeUpdate();
	             myConn.commit();
	             ps.close();
        
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

