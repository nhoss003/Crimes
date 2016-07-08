
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


public class  losAngeles extends Base
{
//	static int flag=0;
//public int violent = 0;
//	
//	String crime_type=null;
//	
//	public String getCrime_type() {
//		return crime_type;
//	}
public static void main(String[] args) throws Exception 
{
	
    String filename = "/home/niloufar/Desktop/GeoIPCountryCSV/LosAngeles/losAngeles.csv";
      
//String filename = "/home/nhoss003/LosAngeles/losAngeles.csv";
     losAngeles  parseCSVFile = new  losAngeles();
       
        parseCSVFile.parseUsingOpenCSV(filename);
       
    
}


public void parseUsingOpenCSV(String filename) throws Exception
{		flag=0;
        CSVReader reader;
        Timestamp crawl_time=null;
    	String state = "CA";
    	String city="Los Angeles";	
    	String address;
    	String incident_time=null;
    	double latitude= 0.0;
    	double longitude= 0.0;

    	System.out.println("losAngeles");
    	String source = "https://data.lacity.org/A-Safe-City/Crimes-2012-2015/s9rj-h3s6";
        try 
        {
        	
        reader = new CSVReader(new FileReader(filename));
        String[] row;
        
     Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","1234");
    //  Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
       
        
		myConn.setAutoCommit(false);
	
		
        while ((row = reader.readNext()) != null) 
        {
        	String year=null;
        	String date;
        	String time=null;
        	String month=null;
            String day=null;
           
        	
        	if(flag < 1){
				flag++;
				continue;}
        	if(flag > 100){
				break;}
        	crawl_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        	flag++;
        	
        	crime_type = row[8];
			
        	
        	
     // if(flag> 5){break;} 
  
         	
        	date = row[2] ;
        	time=row[3];
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

		address= row[11].replaceAll("\\s+", " ").trim();
	
	

		
		 String latlong=row[13];
			
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

				
			
		Violent.isViolent(this);

		
		
		String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,latitude,longitude,source_URL,incident_time, violent) VALUES ( ? ,?, ?, ?, ?, ? , ?, ? ,?, ? )";
        PreparedStatement ps = myConn.prepareStatement(query);

        ps.setString(1,crime_type);
        ps.setTimestamp(2,crawl_time);
        ps.setString(3,address);
        ps.setString(4,city);
        ps.setString(5,state);
     
        
        if((int)latitude !=0){
			  
            ps.setDouble(6,latitude);
            
		  }else{
           	 ps.setString(6,null);
           	 }
		  
		   if((int)longitude !=0){
			   
           ps.setDouble(7,longitude);
           }
	       else{
	    	   ps.setString(7,null);
	    	   }
        
        
        
	  
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
