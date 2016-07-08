
package ucr.cs.Pittsburgh;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;





public class Violent {
	
	
	public  static void isViolent (Pittsburgh ch) throws Exception
	{
		boolean csv;
		
			String pure;
			String lower;      //crime_type
			String s1;		  //crime_type
			
			
			String s2;
			String s3;
	
	        
	       	Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
		
			Statement myStmt = myConn.createStatement();
			Statement myStmt1 = myConn.createStatement();
			ResultSet myRs = myStmt.executeQuery("select violent from violent");
			ResultSet myRs1 = myStmt1.executeQuery("select nonviolent from nonviolent");
	
	       
	       
	     
	        	// reader2 = new CSVReader(new FileReader(violent));
	        	// reader3 = new CSVReader(new FileReader(nonviolent));
	        	 csv = true;
	        	 ch.violent= 0;
	        	
	        	if(ch.getCrime_type()!=null){
	        		
	        	lower=ch.getCrime_type().toLowerCase().trim() ;
	        	s1=ch.getCrime_type().trim() ;

//violent

			while(myRs.next()){

			pure = myRs.getString("violent");
			
			s2=pure.toLowerCase().trim();
			
			if(lower.matches(".*\\b"+s2+"\\b.*")|| lower.matches(".*\\p{Punct}"+s2+"\\b.*") || lower.matches(".*\\b"+s2+"\\p{Punct}.*") || lower.matches(".*\\b/"+s2+"\\b.*") || lower.matches(".*\\b"+s2+"/\\b.*") ){
	        		
	        		 ch.violent = 1;
	        		
	        		break;
	        	}
			
		}
		

          	
          if(s1.contains("DUI") || s1.contains("DWI")){
        	  ch.violent = 0;
        	 
        	  csv = false;
          }
          //nonviolent
          
          if(csv){
			  
			  	while(myRs1.next())
	 	        {
					
	        	pure = myRs1.getString("nonviolent"); 
	        	s3 = pure.toLowerCase().trim();
	        	if(lower.matches(".*\\b"+ s3 + "\\b.*"))
	        	{
	        	
	        		
	        		 ch.violent = 0;
	        	     csv = false;
	        		 break;
	        	}
	 	        }
       		}
       		
       		
       		
          if(csv){
        	  if((lower.contains("assault") || lower.contains("aslt") || lower.contains("batt")|| lower.contains("battery")) && lower.contains("simple"))
        	  {
        		  ch.violent = 0;
        	  }
        	  
          }
          if(lower.equals("assault") || lower.equals("aslt") || lower.equals("batt")|| lower.equals("battery")){
        	  ch.violent=0;
          }
          
	        	
	       }
	        	myConn.close();
	}
	
}
	
