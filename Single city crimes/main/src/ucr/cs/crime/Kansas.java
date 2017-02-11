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

import com.opencsv.CSVReader;


public class  Kansas extends Base
{

public static void main(String[] args) throws Exception 
{
	
   String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Kansas/kansas.csv";
      
//String filename = "/home/nhoss003/Kansas/kansas.csv";
     Kansas  parseCSVFile = new  Kansas();
       
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
    	String state = "MO";
    	String city="Kansas";	
    	String address;
    	String incident_time=null;
    Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        String thisTime= "2015-12-12 00:00:00";
        String invalid="2017-01-01 00:00:00";
    	
//    	String date;
//    	String time=null;
//    	
//    	String month=null;
//        String day=null;
    	
    	String source = "https://data.kcmo.org/Crime/KCPD-Crime-Data-2015/kbzx-7ehe";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
      Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
      //Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
	t1=Util.convertStringToTimestamp(thisTime);
		t2=Util.convertStringToTimestamp(invalid);
		
        while ((row = reader.readNext()) != null) 
        {
        	String year=null;
        	String date;
        	String time=null;
        	String month=null;
            String day=null;
            String zip = null;
        	
        	if(flag < 1){
				flag++;
				continue;}
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	
        	crime_type = row[9];
			
        	
        	
     // if(flag> 5){break;} 
  
         	
         	String[] parts= row[3].split("\\s+");
         	date = parts[0] ;
         	
        	time=row[4];
        	
        	if(!time.isEmpty()){
        	String [] ti = time.split(":");
        	if(ti[0].length()==1){
        		ti[0] = "0"+ti[0];
        	}
        	
        	time = ti[0]+":"+ti[1]+":00";
        	}else{
        		time = "00:00:00";
        	}
        	
        	String[] str=date.split("/");
    		year= str[2];
            month= str[0];
            day=str[1];
            String date2 =year+"-"+month+"-"+day;
			incident_time = date2+" "+ time;
		
			if(incident_time!=null && !incident_time.isEmpty()){
				t = Util.convertStringToTimestamp(incident_time);
				  if(t.after(t1) && t.before(t2)){
		address= row[11];
		city = row[12];
		String []  kansas;
		if(row[12].contains("KANSAS") || row[12].toLowerCase().contains("kansas") ){
			kansas = row[12].split("\\s+");
			city= kansas[0];
			
			
		}
		if(isZipcode(row[13])){
		zip =row[13];
		}

				
			
		Violent.isViolent(this);

		
		
		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,source_URL,incident_time, violent) VALUES ( ?, ?, ?, ?, ? , ?, ? ,?, ? )";
        PreparedStatement ps = myConn.prepareStatement(query);

        ps.setString(1,crime_type);
        ps.setTimestamp(2,crawl_time);
        ps.setString(3,address);
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

