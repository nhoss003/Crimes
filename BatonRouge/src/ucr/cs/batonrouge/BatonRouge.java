package ucr.cs.batonrouge;

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


public class  BatonRouge 
{
	static int flag=0;
public static void main(String[] args) throws Exception 
{
	
     //String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/BatonRouge/BatonRouge.csv";
      
String filename = "/home/nhoss003/BatonRouge/BatonRouge.csv";
     BatonRouge  parseCSVFile = new  BatonRouge();
       
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

private void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        String crime_type =null;
        Timestamp crawl_time=null;
    	String state = "LA";
    	String city="Baton Rouge";
    	String zip=null;
    	String address=null;
    	String date;
    	String incident_time=null;
    	String time=null;
    	double latitude= 0.0;
    	double longitude= 0.0;
    	String year=null;
    	 String month=null;
         String day=null;
    	String source = "https://data.brla.gov/Public-Safety/Baton-Rouge-Crime-Incidents/fabb-cnnu";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
      //Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
      Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
	
		
        while ((row = reader.readNext()) != null) 
        {
        	
        	if(flag < 1){
				flag++;
				continue;}
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	System.out.println(flag);
        	crime_type = row[0].replace("\"","") ;
			
        	
        	
     // if(flag> 5){break;} 
        	//incident_time = row[1].replace("\"","") ;
        	//  incident_time ="04/08/2016 09:37:00 PM" ;
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
        		 else{
        			 time="00:00:00";
        		 }
        	String[] str=date.split("/");
    		year= str[2];
            month= str[0];
            day=str[1];
            String date2 =year+"-"+month+"-"+day;
			incident_time = date2+" "+ time;
			
		address = row[3].replace("\"","") ;
		zip = row[4].replace("\"","") ;
	 
 String latlong=row[5];
		
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
		}else{
			latitude=0.0;
			longitude=0.0;
		}

		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,latitude,longitude,source_URL,incident_time) VALUES (?, ?, ?, ?, ?, ? , ?, ? ,?, ? )";
             PreparedStatement ps = myConn.prepareStatement(query);
     
             ps.setString(1,crime_type);
             ps.setTimestamp(2,crawl_time);
             ps.setString(3,address);
             ps.setString(4,city);
             ps.setString(5,state);
             if(!zip.isEmpty() && isZipcode(zip)){
             ps.setString(6,zip);
             }
             else{
            	 ps.setString(6,null);
             }
          
		  if((int)latitude !=0){
			  
             ps.setDouble(7,latitude);
             
		  }else{
            	 ps.setString(7,null);
            	 }
		  
		   if((int)longitude !=0){
			   
            ps.setDouble(8,longitude);
            }
	       else{
	    	   ps.setString(8,null);
	    	   }
             ps.setString(9,source);
             
             if(!incident_time.isEmpty()){
             ps.setTimestamp(10,Util.convertStringToTimestamp(incident_time));}
             else{
            	 ps.setTimestamp(10,null);
             }
            // ps.setString(9,accuracy);
             ps.executeUpdate();
             myConn.commit();
             ps.close();

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

