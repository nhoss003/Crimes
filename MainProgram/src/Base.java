import java.io.File;

public class Base {

	static int flag=0;
	
	public int violent = 0;
	
	protected String crime_type=null;
	
	public String getCrime_type() {
		
		return crime_type;
	}
	public static void main(String[] args) throws Exception  {
		 try {
//           ProcessBuilder pb = new ProcessBuilder("/bin/sh","wget.sh");
//           Process p = pb.start();
//           p.waitFor();
//           System.out.println("Success");
//           
           File folder = new File("/home/niloufar/Desktop/wget/");
           File[] listOfFiles = folder.listFiles();
           System.out.println("Base");
           //for(File file : folder.listFiles())
           for (int i = 0; i < listOfFiles.length; i++) {
             File file = listOfFiles[i];
             if (file.isFile() && file.getName().endsWith(".csv")) {
              // String content = FileUtils.readFileToString(file);
          	   String fn= file.getName();
          	 String filename = file.toString();
          	//   System.out.println(filename);
          	  
           //   parseCSVFile.parseUsingOpenCSV(filename);
          	
          	 if(fn.equals("losAngeles")){
          	 losAngeles la=new losAngeles();
          	 la.parseUsingOpenCSV(filename);
          	 }
          	 if(fn.equals("Kansas")){
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
