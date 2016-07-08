package ucr.cs.crimeLA;


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
import org.json.JSONObject;
//import java.sql.Connection;

import com.opencsv.CSVReader;





public class LA_Final
{
	static int flag=0;
public static void main(String[] args) throws Exception 
{
	
      // String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/LA/last30days/14june/PART_I_AND_II_CRIMES.csv";
      
	String filename = "/home/nhoss003/laCounty/last30days/14june/PART_I_AND_II_CRIMES.csv";
        LA_Final parseCSVFile = new LA_Final();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}
public boolean isZipcode(String str)
{
    if(str.length()!=5 && str.length()!=9)
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

private void parseUsingOpenCSV(String filename) throws Exception
{
//int flag=0;	



        CSVReader reader;
        Timestamp crawl_time=null;
    	String state = "CA";
    	String city;	
    	String address;
    	String zip;
    	String incident_time;
    	String concatAddress= null;
    	double[] pos = null;
 
    	
    	String source = "http://shq.lasdnews.net/CrimeStats/CAASS/desc.html";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
       // Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
        Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
	
		
        while ((row = reader.readNext()) != null) 
        {
        	if(flag < 2430){
				flag++;
				continue;}
     if(row[16].equals("N")){
        	
        	incident_time = row[1].replace("\"","") ;
//        	String[] splited_incident_time = incident_time.split("\\s+");
//                  	
//        	SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
//            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm:ss a");
//            Date date = parseFormat.parse(splited_incident_time[1]+ " "+splited_incident_time[2]);
//            String new_time= displayFormat.format(date);
//			incident_time = splited_incident_time[0]+" "+ new_time;
//			
			
			String crime_type= row[2].replace("\"","") ;
			
		
			
			address= row[6].replace("\"","") ;
			city= row[7].replace("\"","") ;
			zip = row[8].replace("\"","") ;
			concatAddress= address+","+city+","+state;
			
	
			if(address.length()!=0){

				Location.readKeysAndProxies("keys.txt");
				JSONObject response = Location.getGeoResponse(concatAddress);
                pos = Location.getCoordinate(response);
              
                if(pos != null)
                {
                	
                	if(zip.length()==0){
   String strSTD = Location.getStandardZipcode(response);
                    if(strSTD!=null && !strSTD.isEmpty())
                    {
                        if(isZipcode(strSTD))
                            zip = strSTD;
                    }
               
                }
                	String accuracy = Location.getLocationAccuracy(response);
                	
	System.out.println(accuracy);
	
		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,latitude,longitude,source_URL,incident_time,locationAccuracy) VALUES ( ?, ?, ?, ?, ?, ?, ? , ?, ? ,?, ? )";
             PreparedStatement ps = myConn.prepareStatement(query);
     
             ps.setString(1,crime_type);
             ps.setTimestamp(2,crawl_time);
             ps.setString(3,address);
             ps.setString(4,city);
             ps.setString(5,state);
             ps.setString(6,zip);
             ps.setDouble(7,pos[0]);
             ps.setDouble(8,pos[1]);
             ps.setString(9,source);
             ps.setTimestamp(10,Util.convertStringToTimestamp(incident_time));
             ps.setString(11,accuracy);
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
        } catch (SQLException ex) {
    		ex.printStackTrace();
    	}  catch(Exception e)
        {
            
            System.out.println(e.toString());
            e.printStackTrace();
        }
}

}