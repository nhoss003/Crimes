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


public class  Chattanooga extends Base
{
	
	
public static void main(String[] args) throws Exception 
{
	
    //String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Chattanooga/chattanooga.csv";
      
	String filename = "/home/nhoss003/Chattanooga/Chattanooga.csv";
	Chattanooga  parseCSVFile = new  Chattanooga();
       
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
    	String state ="TN";
    	String city= "Chattanooga";	
    	String address;
    	String zip=null;
    	String [] z;
    	String incident_time=null;
    	//String accuracy=null;
    	double latitude= 0.0;
    	double longitude= 0.0;
    	
    	String source = "https://data.chattlibrary.org/Public-Safety/Police-Incident-Data/jstk-5mri";
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
        	String year=null;
        	
        	if(flag < 1){
				flag++;
				continue;}
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	
        	crime_type = row[8];
        	address= row[0];
        	if(!row[1].isEmpty()){
        	city= row[1].replace(",","");
        	city= city.toLowerCase();
        	city= city.substring(0, 1).toUpperCase() + city.substring(1);
        	}
        	state= row[2];
        	
			if(row[3].contains("-")){
        	z = row[3].split("-");
        	if(row[3].length()==1 ||row[3].length()==2){
        		zip =null;
        	
        	}
        	else if(isZipcode(z[0].trim())){
        		zip = z[0].trim();
        	}
        	else{
        		zip = null;
        	}
			}
			else if (row[3].length()!=0){
				if(isZipcode(row[3].trim())){
				zip = row[3].trim();}
				
			}
     // if(flag> 5){break;} 
        	//incident_time = row[1].replace("\"","") ;
        	//  incident_time ="04/08/2016 09:37:00 PM" ;
        	incident_time = row[4] ;
        	
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
        	
        	if(incident_time!=null && !incident_time.isEmpty()){
    			t = Util.convertStringToTimestamp(incident_time);
    			  if(t.after(t1) && t.before(t2)){
		
		
		if(!row[13].isEmpty()){
				latitude=Double.parseDouble(row[13].trim());
		}
		else{
			latitude=0.0;
			
		}
		if(!row[14].isEmpty()){
				longitude= Double.parseDouble(row[14].trim());
		}
		else{
			
			longitude=0.0;
		}
       Violent.isViolent(this);
			
		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,latitude,longitude,source_URL,incident_time, violent) VALUES ( ?, ?,?, ?, ?, ?, ? , ?, ? ,?, ? )";
             PreparedStatement ps = myConn.prepareStatement(query);
     
             ps.setString(1,crime_type);
             ps.setTimestamp(2,crawl_time);
             ps.setString(3,address);
             ps.setString(4,city);
             ps.setString(5,state);
             ps.setString(6,zip);
		  if((int)latitude !=0){
             ps.setDouble(7,latitude);}
		   else{ps.setString(7,null);}
		   if((int)longitude !=0){
            ps.setDouble(8,longitude);}
	       else{ps.setString(8,null);}
             ps.setString(9,source);
             if(!incident_time.isEmpty()){
             ps.setTimestamp(10,Util.convertStringToTimestamp(incident_time));}
             else{
            	 ps.setTimestamp(10,null);
             }
            ps.setInt(11,violent);
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

