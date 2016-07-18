package ucr.cs.crime;
import java.io.File;

public class Main {


	public static void main(String[] args) throws Exception {
	
	      
		
		 try {
        
             File folder = new File("cityCrime/");
             File[] listOfFiles = folder.listFiles();
			System.out.println("main");
             //for(File file : folder.listFiles())
             for (int i = 0; i < listOfFiles.length; i++) {
               File file = listOfFiles[i];
               if (file.isFile() && file.getName().endsWith(".csv")) {
                // String content = FileUtils.readFileToString(file);
            	   String fn= file.getName();
            	 String filename = file.toString();
            	 System.out.println(fn);
            	  
             //   parseCSVFile.parseUsingOpenCSV(filename);
            	 if(fn.equals("Austin.csv"))
            	 {
             		
            		 Austin a=new Austin();
                	 a.parseUsingOpenCSV(filename);
                	 
                	 
                	 }
                	 if(fn.equals("BatonRouge.csv")){
                		
                		 BatonRouge b=new BatonRouge();
                    	 b.parseUsingOpenCSV(filename);
                    	 
                	 }
            	
            	 if(fn.equals("Boston.csv")){
            		
            		 Boston b=new Boston();
            	 b.parseUsingOpenCSV(filename);
            	 
            	 }
            	 if(fn.equals("Chattanooga.csv")){
            		
            		 Chattanooga c=new Chattanooga();
                	 c.parseUsingOpenCSV(filename);
                	 
                	 }   	 
            	 if(fn.equals("Chicago.csv"))
                	 {
                  		
            		 Chicago c=new Chicago();
                    	 c.parseUsingOpenCSV(filename);
                    	 
                    	 
                    	 }
                    	 if(fn.equals("Cincinnati.csv")){
                    		
                    		 Cincinnati c=new Cincinnati();
                        	 c.parseUsingOpenCSV(filename);
                        	 
                    	 }
                	
                	 if(fn.equals("Dallas.csv")){
                		
                		 Dallas d=new Dallas();
                	 d.parseUsingOpenCSV(filename);
                	 
                	 }
                	 if(fn.equals("Denver.csv")){
                		
                		 Denver d=new Denver();
                    	 d.parseUsingOpenCSV(filename);
                    	 
                    	 }
                	 if(fn.equals("Hartford.csv"))
                	 {
                 		
                		 Hartford a=new Hartford();
                    	 a.parseUsingOpenCSV(filename);
                    	 
                    	 
                    	 }
                    	 if(fn.equals("Kansas.csv")){
                    		
                    		 Kansas b=new Kansas();
                        	 b.parseUsingOpenCSV(filename);
                        	 
                    	 }
                	
                	 if(fn.equals("LosAngeles.csv")){
                		
                		 LosAngeles b=new LosAngeles();
                	 b.parseUsingOpenCSV(filename);
                	 
                	 }
                	 if(fn.equals("LosAngelesCounty.csv")){
                		
                		 LosAngelesCounty c=new LosAngelesCounty();
                    	 c.parseUsingOpenCSV(filename);
                    	 
                    	 }   	 
                	 if(fn.equals("Louisville.csv"))
                    	 {
                      		
                		 Louisville c=new Louisville();
                        	 c.parseUsingOpenCSV(filename);
                        	 
                        	 
                        	 }
                        	 if(fn.equals("NewYork.csv")){
                        		
                        		 NewYork c=new NewYork();
                            	 c.parseUsingOpenCSV(filename);
                            	 
                        	 }
                    	
                    	 if(fn.equals("Oakland.csv")){
                    		
                    		 Oakland d=new Oakland();
                    	 d.parseUsingOpenCSV(filename);
                    	 
                    	 }
                    	 if(fn.equals("Philadelphia.csv")){
                    		
                    		 Philadelphia d=new Philadelphia();
                        	 d.parseUsingOpenCSV(filename);
                        	 
                        	 }
                       	 if(fn.equals("Pittsburgh.csv")){
                     		
                       		Pittsburgh b=new Pittsburgh();
                        	 b.parseUsingOpenCSV(filename);
                        	 
                    	 }
                	
                	 if(fn.equals("Portland.csv")){
                		
                		 Portland b=new Portland();
                	 b.parseUsingOpenCSV(filename);
                	 
                	 }
                	 if(fn.equals("Providence.csv")){
                		
                		 Providence c=new Providence();
                    	 c.parseUsingOpenCSV(filename);
                    	 
                    	 }   	 
                	 if(fn.equals("Raleigh.csv"))
                    	 {
                      		
                		 Raleigh c=new Raleigh();
                        	 c.parseUsingOpenCSV(filename);
                        	 
                        	 
                        	 }
                        	 if(fn.equals("SaltLakeCity.csv")){
                        		
                        		 SaltLakeCity c=new SaltLakeCity();
                            	 c.parseUsingOpenCSV(filename);
                            	 
                        	 }
                    	
                    	 if(fn.equals("SanFrancisco.csv")){
                    		
                    		 SanFrancisco d=new SanFrancisco();
                    	 d.parseUsingOpenCSV(filename);
                    	 
                    	 }
                    	 if(fn.equals("SantaMonica.csv")){
                    		
                    		 SantaMonica d=new SantaMonica();
                        	 d.parseUsingOpenCSV(filename);
                        	 
                        	 }
                    	 if(fn.equals("Seattle.csv")){
                     		
                    		 Seattle d=new Seattle();
                        	 d.parseUsingOpenCSV(filename);
                        	 
                        	 }
               } 
             }
        } catch (Exception e) {
        e.printStackTrace();
     }
	}

}
