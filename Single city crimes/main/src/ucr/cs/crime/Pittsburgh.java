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


public class  Pittsburgh extends Base{ 


public static void main(String[] args) throws Exception 
{
	
   // String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/Pittsburgh/Pittsburgh.csv";
      
    String filename = "/home/nhoss003/Pittsburgh/Pittsburgh.csv";
	Pittsburgh  parseCSVFile = new  Pittsburgh();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
     
}


public void parseUsingOpenCSV(String filename) throws Exception
{
        CSVReader reader;
        Timestamp crawl_time=null;
        Timestamp t=null;
        Timestamp t1=null;
        Timestamp t2=null;
        boolean invalidIncidentTime= false;
        String thisTime= "2015-07-04 00:00:00";
        String invalid="2017-01-01 00:00:00";
    	String state ="PA";
    	
    	String city ="Pittsburgh";
   String address=null;
//     	double latitude= 0.0;
//    	double longitude= 0.0;
    	String incident_time=null;
    	
    	
    	String source = "https://data.wprdc.org/dataset/pittsburgh-police-incident-blotter";
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
        	
        	 String i_time = row[5] ;

          	if(!i_time.isEmpty()){
          		
          		
          	if(i_time.contains("T")){	
          	String[] splited_incident_time = i_time.split("T");
          	if(splited_incident_time[0].contains("-")){
          	
          		 String[] str=splited_incident_time[0].split("-");
          		for(int i=0; i<str[0].length(); i++)
                {
                    if(!Character.isDigit(str[0].charAt(i)))
                    {
                    	invalidIncidentTime=true;
                       break;
                    }
                }
          		if(!invalidIncidentTime){
          			incident_time=splited_incident_time[0]+" "+splited_incident_time[1];
          		}
          	}
          	}
            }
          	if(incident_time!=null && !incident_time.isEmpty()){
    			t = Util.convertStringToTimestamp(incident_time);
    			  if(t.after(t1) && t.before(t2)){
        	
              crime_type = row[4];
        	  if(row[4].length()>100){
        		  crime_type= row[4].substring(0,99);
        		
        	  }
            

             address=row[6];

		System.out.println(flag);
		
		
		Violent.isViolent(this);

		
			
			
			String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,source_URL,incident_time, violent) VALUES ( ?, ?, ?, ? , ?, ? ,?, ? )";
	             PreparedStatement ps = myConn.prepareStatement(query);
	     
	             ps.setString(1,crime_type);
	             ps.setTimestamp(2,crawl_time);
	             ps.setString(3,address);
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

