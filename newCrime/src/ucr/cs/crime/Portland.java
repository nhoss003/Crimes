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


public class  Portland extends Base{ 

	
	
public static void main(String[] args) throws Exception 
{
	
 //  String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Portland/Portland.csv";
      
String filename = "/home/nhoss003/Portland/Portland.csv";
Portland  parseCSVFile = new  Portland();
       
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
      String thisTime= "2014-04-04 00:00:00";
        
        String invalid="2017-01-01 00:00:00";
    	String state="OR";
    	String zip =null;
    	String city="Portland";
    	String address=null;
    	String year=null;
   	    String month=null;
        String day=null;
//     	double latitude= 0.0;
//    	double longitude= 0.0;
    	String incident_time=null;
    	
    	
    	String source = "http://www.civicapps.org/datasets/crime-incidents";
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
        	while ((row = reader.readNext()) != null) 
            {

            	
            	if(flag < 1){
    				flag++;
    				continue;}
            	
            	incident_time=null;
            	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            	flag++;
            	
            	
            //	String[] time=row[3].split("+");
            	 // incident_time = time[0].trim() ;
            	
            	incident_time =row[1].trim()+" "+row[2].trim() ;
            	
            	
              	if(incident_time!=null && !incident_time.isEmpty()){
              		
              	String[] splited_incident_time = incident_time.split("\\s+");
              	
              	//System.out.println(s);
              	
                  String[] str=splited_incident_time[0].split("/");
                  year= str[2];
                  month= str[0];
                  day=str[1];
                  String date2 =year+"-"+month+"-"+day;
      			incident_time = date2+" "+ splited_incident_time[1];
              //	}
             // 	if(incident_time!=null && !incident_time.isEmpty()){
            	  t = Util.convertStringToTimestamp(incident_time);
            	//  }
            	  if(t.after(t1) && t.before(t2)){
            		  
                  crime_type = row[3];

                String st= row[4];
                if(st!=null && st.contains(",")){
                address=st.substring(0,st.indexOf(","));
                }
                if(st.matches(".*\\d+.*"))
                {
                	String s=st.substring(st.lastIndexOf(" ")+1);
                	//System.out.printf("zip %s \n",s);
                if(	isZipcode(s)){
                	zip =s;
                }else{
                	zip=null;
                }
                
                }
                else
                {zip=null;}
    		System.out.println(flag);
    		
    		
    		Violent.isViolent(this);

    	//	if(incident_time!=null && !incident_time.isEmpty()){
    			
    			
    			String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,source_URL,incident_time, violent) VALUES (  ? , ?, ?, ?, ? , ?, ? ,?, ? )";
    	             PreparedStatement ps = myConn.prepareStatement(query);
    	     
    	             ps.setString(1,crime_type);
    	             ps.setTimestamp(2,crawl_time);
    	             ps.setString(3,address);
    	             ps.setString(4,city);
    	             ps.setString(5,state);
    	            
    	             ps.setString(6,zip);
    	             ps.setString(7,source);
    	             
    	             ps.setTimestamp(8,Util.convertStringToTimestamp(incident_time));
    	           
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

