import java.io.File;

public class wget {


	public static void main(String[] args) throws Exception {
		/*
		 try {
             String target = new String("/home/niloufar/Desktop/wget/wget.sh");

             Runtime rt = Runtime.getRuntime();
             Process proc = rt.exec(target);
             proc.waitFor();
            
             BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
             String line = "";                       
             while ((line = reader.readLine())!= null) {
                     output.append(line + "\n");
             }
             System.out.println("### " + output);
     } catch (Throwable t) {
             t.printStackTrace();
     }
		//
		   p = Runtime.getRuntime().exec("host -t a " + domain);
		    p.waitFor();

		    BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getInputStream()));

		    String line = "";			
		    while ((line = reader.readLine())!= null) {
			sb.append(line + "\n");
		    }
		 //*/
		//wget  parseCSVFile = new  wget();
	      
		
		 try {
//             ProcessBuilder pb = new ProcessBuilder("/bin/sh","wget.sh");
//             Process p = pb.start();
//             p.waitFor();
//             System.out.println("Success");
//             
             File folder = new File("/home/niloufar/Desktop/wget/");
             File[] listOfFiles = folder.listFiles();
System.out.println("wget");
             //for(File file : folder.listFiles())
             for (int i = 0; i < listOfFiles.length; i++) {
               File file = listOfFiles[i];
               if (file.isFile() && file.getName().endsWith(".csv")) {
                // String content = FileUtils.readFileToString(file);
            	   String fn= file.getName();
            	 String filename = file.toString();
            	//   System.out.println(filename);
            	  
             //   parseCSVFile.parseUsingOpenCSV(filename);
            	
            	 if(fn.equals("losAngeles.csv")){
            	 losAngeles la=new losAngeles();
            	 la.parseUsingOpenCSV(filename);
            	 }
            	 if(fn.equals("Kansas.csv")){
            		 Kansas k=new Kansas();
                	 k.parseUsingOpenCSV(filename);
                	 }
            	   
               } 
             }
        } catch (Exception e) {
        e.printStackTrace();
     }
	}

}
