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


public class  oakland 
{
	static int flag=0;
public int violent = 0;
	
	String crime_type=null;
	
	public String getCrime_type() {
		return crime_type;
	}
public static void main(String[] args) throws Exception 
{
	
    // String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Oakland/oakland.csv";
      
String filename = "/home/nhoss003/Oakland/oakland.csv";
     oakland  parseCSVFile = new  oakland();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}

public boolean isDate (String str) {
	if(!str.isEmpty()){
	if(!str.equals("2016")){
		return false;
	}}
	return true;
	
}


private void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        Timestamp crawl_time=null;
    	String state = "CA";
    	String city="Oakland";	
    	String address;
    	//String zip;
    	String incident_time=null;
    	//String accuracy=null;
    	double latitude= 0.0;
    	double longitude= 0.0;
    	
    	String source = "https://data.oaklandnet.com/Public-Safety/CrimeWatch-Maps-Past-90-Days/ym6k-rx7a";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
    //  Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
      Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
	
		
        while ((row = reader.readNext()) != null) 
        {
        	String year=null;
        	
        	if(flag <1){
				flag++;
				continue;}
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	
        	crime_type = row[0].replace("\"","") ;
			
        	
        	
     // if(flag> 5){break;} 
        	//incident_time = row[1].replace("\"","") ;
        	//  incident_time ="04/08/2016 09:37:00 PM" ;
        	incident_time = row[1] ;
        	
        	if(!incident_time.isEmpty()){
        	String[] splited_incident_time = incident_time.split("\\s+");
        	
        	//System.out.println(s);
        	SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm:ss a");
            Date date = parseFormat.parse(splited_incident_time[1]+ " "+splited_incident_time[2]);
            String new_time= displayFormat.format(date);
            String[] str=splited_incident_time[0].split("/");
            year= str[2];
            String month= str[0];
            String day=str[1];
            String date2 =year+"-"+month+"-"+day;
			incident_time = date2+" "+ new_time;
			
        	}
        	
			
	//	System.out.println(incident_time);
        	
			
			if(crime_type.isEmpty()){
			
				crime_type = row[2].replace("\"","") ;
			}
			//System.out.println(crime_type);
		address= row[3].replace("\"","") ;
		
		String latlong=row[4];
		
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
		
		if(!incident_time.isEmpty()){
			
	if (isDate(year)){
				
			
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

