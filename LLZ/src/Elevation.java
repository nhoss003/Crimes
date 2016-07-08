import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import java.io.IOException;

//import java.net.URLConnection;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Elevation
{

    private static double z1;

	private static double z2;

	private static double z3;

	private static double z4;
	
    private static LinkedList<String> keys = new LinkedList<String>();
    private static Map<String, String> proxies = new HashMap<String, String>();
    private static int keysUsed = 0;
    private static int numLimitFail = 0;
    
    public static void readKeysAndProxies(String filename)
    {
        keys.clear();
        proxies.clear();
        try
        {
            Scanner scanner = new Scanner(new File(filename));
            
            while(scanner.hasNextLine())
            {
                String[] line = scanner.nextLine().split(",");
                
                keys.add(line[0]);
                proxies.put(line[0], line[1]);
            }
            
            scanner.close();
            
            if(keys.size() == 0 || keys.size() != proxies.size())
            {
                System.out.println("File " + filename + " has no keys or isn't properly formatted.");
                System.exit(1);
            }
            
            //Configuration for using the first key's proxy.
            System.setProperty("http.proxyHost", proxies.get(keys.getFirst()));
            System.setProperty("http.proxyPort", "3128");
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File " + filename + " not found.");
            System.exit(1);
        }
    }
    
    private static void switchKey() throws Exception
    {
        keysUsed++;
        
        //If all of the keys have reached their usage limits, sleep until midnight.
        if(keysUsed == keys.size())
        {
          //  try
          //  {
                System.out.println("Query limit reached; sleeping.");
                System.out.println("But, I changed it,so doesn't sleep.");
                //Calendar cal = Calendar.getInstance();
                //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
              
             //   String time1=sdf.format(cal.getTime());
            //    String parts[]=time1.split(":");
//                long hrs=Long.parseLong(parts[0]);
//                long min=Long.parseLong(parts[1]);
//                long sec=Long.parseLong(parts[2]);
               
                //Thread.sleep(86500000-(hrs*min*sec*1000));
          //  }
          //  catch(InterruptedException e)
           // {
           //     System.out.println("Sleep interrupted.");
          //  }
            
            keysUsed = 0;
        }
        
        //Switch to the next key and its corresponding proxy.
        keys.add(keys.removeFirst());
        System.setProperty("http.proxyHost", proxies.get(keys.getFirst()));
    }
    
    public static void getElevation(LLZ llz) throws JSONException,
 	ClassNotFoundException, SQLException, IOException {

    	JSONObject response = getElevationFromGoogleAPI(llz.getLatitude(),
 		llz.getLongitude());
 if (response != null) {
 	JSONObject obj = new JSONObject(response.toString());
 	JSONObject jsonObject = (JSONObject) obj;
 	String status = (String) jsonObject.get(CommonConstants.STATUS);
 	if (status.equals(CommonConstants.OK)) {
 		JSONArray array = (JSONArray) jsonObject
 				.get(CommonConstants.RESULTS);
 		JSONObject jsonObject21 = (JSONObject) array.get(0);
 		llz.setElevation(Double.parseDouble((jsonObject21
 				.get(CommonConstants.ELEVATION)).toString()));
 		jsonObject21 = (JSONObject) array.get(1);
 		z1 = Double.parseDouble(jsonObject21.get(
 				CommonConstants.ELEVATION).toString());
 		jsonObject21 = (JSONObject) array.get(2);
 		z2 = Double.parseDouble(jsonObject21.get(
 				CommonConstants.ELEVATION).toString());
 		jsonObject21 = (JSONObject) array.get(3);
 		z3 = Double.parseDouble(jsonObject21.get(
 				CommonConstants.ELEVATION).toString());
 		jsonObject21 = (JSONObject) array.get(4);
 		z4 = Double.parseDouble(jsonObject21.get(
 				CommonConstants.ELEVATION).toString());
 		setElevationType(llz, z1, z2, z3, z4);
 	} 
// 	else if (status.equals(CommonConstants.OVER_QUERY_LIMIT)) {
// 		int responseCode = switchKey();
// 		if (responseCode == 0) {
// 			getElevation(LLZ);
// 		}
 		else {
 			System.out.println("Cannot get Elevation information.");
 			//System.exit(1);
 		}

 	}
 }
// }
    
    public static JSONObject getElevationFromGoogleAPI(double latitude,
			double longitude)
    {
        String strJSON = "";
        
        try
        {
            int responseCode = 0;
            
            String strURL ="https://maps.googleapis.com/maps/api/elevation/json?locations="
							+ latitude
							+ ","
							+ longitude
							+ "|"
							+ +(latitude + 0.002741)
							+ ","
							+ longitude
							+ "|"
							+ +(latitude - 0.002741)
							+ ","
							+ longitude
							+ "|"
							+ +latitude
							+ ","
							+ (longitude - 0.0033887)
							+ "|"
							+ +latitude
							+ ","
							+ (longitude + 0.0033887)
							+ "&key="
							+ keys.getFirst(); 
           /* String strURL ="https://maps.googleapis.com/maps/api/elevation/json?locations="
					+ latitude
					+ ","
					+ longitude
					+ "|"
					+ +(latitude + 0.002741)
					+ ","
					+ longitude
					+ "|"
					+ +(latitude - 0.002741)
					+ ","
					+ longitude
					+ "|"
					+ +latitude
					+ ","
					+ (longitude - 0.0033887)
					+ "|"
					+ +latitude
					+ ","
					+ (longitude + 0.0033887); */
            //System.out.println(strURL);
            URL url = new URL(strURL);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.connect();
            responseCode = httpConnection.getResponseCode();
            if(responseCode == 200)
            {
                //Read response result from the httpConnection via the bufferedReader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    strJSON += line;
                }
                bufferedReader.close();
                
                //Check if the response object is valid
                JSONObject jsonObj = new JSONObject(strJSON);
                String status = (String) jsonObj.get("status");
                if(status.equals("OK"))
                {
                    numLimitFail = 0;
                    return jsonObj;
                }
                else if(status.equals("OVER_QUERY_LIMIT"))
                {
                    System.out.println("Geocoding API error - response status: " + status);
                    if(numLimitFail < 10) //Access too frequently per second
                    {
                        numLimitFail += 1;
                        Thread.sleep(1000);
                        return getElevationFromGoogleAPI(latitude, longitude);
                    }
                    else //Over 2500 limit per day
                    {
                        numLimitFail = 0;
                        switchKey();
                        return getElevationFromGoogleAPI(latitude,longitude);
                    }
                }
                else
                {
                    System.out.println("Geocoding API error - response status: " + status);
                    numLimitFail = 0;
                    return null;
                }
            }
            else
            {
                System.out.println("Geocoding API connection fails.");
                numLimitFail = 0;
                return null;
            }
        }
        catch(Exception ex)
        {
            //System.out.println("Geocoding API error occurs.");
            return null;
        }
    }
    
	public static void setElevationType(LLZ llz, double z1, double z2,
			double z3, double z4) throws ClassNotFoundException, JSONException,
			SQLException, IOException {
		String elevationType = null;
		double z = llz.getElevation();
		if (z > z1
				&& z > z2
				&& z > z3
				&& z > z4
				&& ((z - z1) > 20 || (z - z2) > 20 || (z - z3) > 20 || (z - z4) > 20)) {
			elevationType = CommonConstants.HILL_TOP;
		} else if (((z - z1) < 10 || (z - z2) < 10 || (z - z3) < 10 || (z - z4) < 10)) {
			elevationType = CommonConstants.FLAT;
		} else if (((z - z1) < 10 || (z - z2) < 10 || (z - z3) < 10 || (z - z4) < 10)) {
			if (((z - z1) > 10 || (z - z2) > 10 || (z - z3) > 10 || (z - z4) > 10)) {
				elevationType = CommonConstants.MIDDLE;
			}
		} else if (z < z1
				&& z < z2
				&& z < z3
				&& z < z4
				&& ((z - z1) < 20 || (z - z2) < 20 || (z - z3) < 20 || (z - z4) < 20)) {
			elevationType = CommonConstants.HILL_BOTTOM;
		}
		llz.setElevationType(elevationType);
	}
    
    public static void main(String[] args) throws Exception
    {
    	LLZ llz = new LLZ();
    	double latitude=33.7391536;
    	double longitude=-122.0842702;
    	llz.setLatitude(latitude);
    	llz.setLongitude(longitude);
    	readKeysAndProxies("elevationkeys.txt");
    	getElevation(llz);
    	
    	System.out.println("elevation.java");
    	System.out.println(llz.getElevation());
    	System.out.println(llz.getElevationType());
     
    }
}