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


public class  Boston extends Base
{

public static void main(String[] args) throws Exception 
{
	
    String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/boston/boston.csv";
      
//String filename = "/home/nhoss003/Boston/boston.csv";
     Boston  parseCSVFile = new  Boston();
       
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
        Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2016-06-20 00:00:00";
        String invalid="2017-01-01 00:00:00";
        Timestamp crawl_time=null;
    	String state = "MA";
    	String city="Boston";
    	//String zip=null;
    	String address=null;
    	
    	String incident_time=null;
    	//String time=null;
    	double latitude= 0.0;
    	double longitude= 0.0;
    	String year=null;
    	 String month=null;
         String day=null;
    	String source = "https://data.cityofboston.gov/Public-Safety/Crime-Incident-Reports/7cdf-6fgx";
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
        	System.out.println(flag);
        	crime_type = row[0].replace("\"","") ;
			
        	System.out.println(flag);
        	
     // if(flag> 5){break;} 
        //	incident_time = row[1].replace("\"","").trim() ;
        	// incident_time ="08/10/2014 10:50:00 AM" ;
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
            month= str[0];
            day=str[1];
            String date2 =year+"-"+month+"-"+day;
			incident_time = date2+" "+ new_time;
        	}
        	if(incident_time!=null && !incident_time.isEmpty()){
    			t = Util.convertStringToTimestamp(incident_time);
    			  if(t.after(t1) && t.before(t2)){
		address = row[2].replace("\"","") ;
		
	 
         String latlong=row[3];
		
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
		
		Violent.isViolent(this);

		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,latitude,longitude,source_URL,incident_time,violent) VALUES ( ?,?,?, ?, ?, ? , ?, ? ,?, ? )";
        PreparedStatement ps = myConn.prepareStatement(query);

        ps.setString(1,crime_type);
        ps.setTimestamp(2,crawl_time);
        ps.setString(3,address);
        ps.setString(4,city);
        ps.setString(5,state);
      //  ps.setString(6,zip);
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

