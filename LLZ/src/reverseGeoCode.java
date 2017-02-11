
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class reverseGeoCode extends Location
{

    
    public static JSONObject getReverseGeoResponse(LLZ llz)
    {
    
        String strJSON = "";
        
        try
        {
            int responseCode = 0;
            
           String strURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +llz.getLatitude()+","+ llz.getLongitude() + "&key=" + keys.getFirst();
           // String strURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8");
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
                        return getReverseGeoResponse(llz);
                    }
                    else //Over 2500 limit per day
                    {
                        numLimitFail = 0;
                        switchKey();
                        return getReverseGeoResponse(llz);
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
   
    

    public static void main(String[] args) throws Exception
    {
    	
    }
    
}
