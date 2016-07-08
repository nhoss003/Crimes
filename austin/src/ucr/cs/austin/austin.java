package ucr.cs.austin;
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


public class  austin 
{
	static int flag=0;
public static void main(String[] args) throws Exception 
{
	
     //String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Austin/austin.csv";
      
String filename = "/home/nhoss003/Austin/austin.csv";
     austin  parseCSVFile = new  austin();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}



private void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        String crime_type =null;
        Timestamp crawl_time=null;
    	String state = "TX";
    	String city="Austin";	
    	String address=null;
    	String date;
    	String incident_time=null;
    	String time=null;
    	double latitude= 0.0;
    	double longitude= 0.0;
    	String year=null;
    	 String month=null;
         String day=null;
    	String source = "https://data.austintexas.gov/Public-Safety/APD-Incident-Extract-YTD/b4y9-5x39";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
     // Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
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
        	String[] str=date.split("/");
    		year= str[2];
            month= str[0];
            day=str[1];
            String date2 =year+"-"+month+"-"+day;
			incident_time = date2+" "+ time;
			
		address= row[3].replace("\"","") ;
		if(! row[5].isEmpty()){
	 latitude = Double.parseDouble(row[5].replace("\"",""));}
		else{
			latitude=0.0;
		}
		if(! row[4].isEmpty()){
	 longitude = Double.parseDouble(row[4].replace("\"",""));}
		else{
			longitude=0.0;
		}
	 
	    
	

		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,latitude,longitude,source_URL,incident_time) VALUES ( ?, ?, ?, ?, ? , ?, ? ,?, ? )";
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

