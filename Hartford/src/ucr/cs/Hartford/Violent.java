package ucr.cs.Hartford;


import java.io.FileReader;


import com.opencsv.CSVReader;



public class Violent {
	
	@SuppressWarnings({ "resource" })
	public  static void isViolent (Hartford ch) throws Exception
	{
		boolean csv;
		
		 CSVReader reader2;
		 CSVReader reader3;
		 String violent = "/home/nhoss003/dictionary/violent.csv";
		 String nonviolent = "/home/nhoss003/dictionary/nonviolent.csv";
//		    String violent = "/home/niloufar/Desktop/dictionary/violent.csv";
//		    String nonviolent = "/home/niloufar/Desktop/dictionary/nonviolent.csv";
			
			String lower;
			String s1;
			String s2;
			String s3;
	        	
	        String[] row2;
	        String[] row3;
	     
	        	 reader2 = new CSVReader(new FileReader(violent));
	        	 reader3 = new CSVReader(new FileReader(nonviolent));
	        	 csv = true;
	        	 ch.violent= 0;
	        	
	        	if(ch.getCrime_type()!=null){
	        		
	        	lower=ch.getCrime_type().toLowerCase().trim() ;
	        	s1=ch.getCrime_type().trim() ;
	        	
	     //   System.out.println(s1);
//	        	if(s1.matches(".*\\bGBI\\b.*") || s1.matches(".*\\bINJ\\b.*") || 
//	        			s1.matches(".*\\bADV\\b.*") || s1.matches(".*\\bAGG\\b.*") ||
//	        			s1.matches(".*\\bASLT\\b.*")|| s1.matches(".*\\bBATT\\b.*")|| 
//	        			s1.matches(".*\\bROBB\\b.*")||s1.matches(".*\\p{Punct}GBI\\b.*") || s1.matches(".*\\p{Punct}INJ\\b.*") || 
//	        			s1.matches(".*\\p{Punct}ADV\\b.*") || s1.matches(".*\\p{Punct}AGG\\b.*") ||
//	        			s1.matches(".*\\p{Punct}ASLT\\b.*")|| s1.matches(".*\\p{Punct}BATT\\b.*")|| 
//	        			s1.matches(".*\\p{Punct}ROBB\\b.*") || s1.matches(".*\\bGBI\\p{Punct}.*") || s1.matches(".*\\bINJ\\p{Punct}.*") || 
//	        			s1.matches(".*\\bADV\\p{Punct}.*") || s1.matches(".*\\bAGG\\p{Punct}.*") ||
//	        			s1.matches(".*\\bASLT\\p{Punct}.*")|| s1.matches(".*\\bBATT\\p{Punct}.*")|| 
//	        			s1.matches(".*\\bROBB\\p{Punct}.*")||s1.matches(".*\\b/GBI\\b.*") || s1.matches(".*\\b/INJ\\b.*") || 
//	        			s1.matches(".*\\b/ADV\\b.*") || s1.matches(".*\\b/AGG\\b.*") ||
//	        			s1.matches(".*\\b/ASLT\\b.*")|| s1.matches(".*\\b/BATT\\b.*")|| 
//	        			s1.matches(".*\\b/ROBB\\b.*")||s1.matches(".*\\bGBI/\\b.*") || s1.matches(".*\\bINJ/\\b.*") || 
//	        			s1.matches(".*\\bADV/\\b.*") || s1.matches(".*\\bAGG/\\b.*") ||
//	        			s1.matches(".*\\bASLT/\\b.*")|| s1.matches(".*\\bBATT/\\b.*")|| 
//	        			s1.matches(".*\\bROBB/\\b.*")
//	        			
//	        			)
//	        	{
//	        		 ch.violent = 1;
//	        
//	        	
//	        		csv= false;
//	        	}
//	        	
//	        	
 //         if(csv){
	        	 while ((row2 = reader2.readNext()) != null) 
	 	        {
	        		 
	        	s2=row2[0].toLowerCase().trim();
	        	
	        	if(lower.matches(".*\\b"+s2+"\\b.*")|| lower.matches(".*\\p{Punct}"+s2+"\\b.*") || lower.matches(".*\\b"+s2+"\\p{Punct}.*") || lower.matches(".*\\b/"+s2+"\\b.*") || lower.matches(".*\\b"+s2+"/\\b.*") ){
	        		
	        		 ch.violent = 1;
	        		
	        		break;
	        	}
	 	        }
          		//}
         // csv = true;
          if(s1.contains("DUI") || s1.contains("DWI")){
        	  ch.violent = 0;
        	 
        	  csv = false;
          }
          
          if(csv){
	        	 while ((row3 = reader3.readNext()) != null) 
	 	        {
	        		 
	        	s3 = row3[0].toLowerCase().trim();
	        	if(lower.matches(".*\\b"+ s3 + "\\b.*")){
	        	
	        		
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
	     
	}
	
}
	
