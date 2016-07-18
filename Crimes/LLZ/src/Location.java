
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location
{
    protected static LinkedList<String> keys = new LinkedList<String>();
    protected static Map<String, String> proxies = new HashMap<String, String>();
    protected static int keysUsed = 0;
    protected static int numLimitFail = 0;
    
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
    
    protected static void switchKey() throws Exception
    {
        keysUsed++;
        
        //If all of the keys have reached their usage limits, sleep until midnight.
        if(keysUsed == keys.size())
        {
            try
            {
                System.out.println("Query limit reached; sleeping.");
                            
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
              
                String time1=sdf.format(cal.getTime());
                String parts[]=time1.split(":");
                long hrs=Long.parseLong(parts[0]);
                long min=Long.parseLong(parts[1]);
                long sec=Long.parseLong(parts[2]);
               
                Thread.sleep(86500000-(hrs*min*sec*1000));
            }
            catch(InterruptedException e)
            {
                System.out.println("Sleep interrupted.");
            }
            
            keysUsed = 0;
        }
        
        //Switch to the next key and its corresponding proxy.
        keys.add(keys.removeFirst());
        System.setProperty("http.proxyHost", proxies.get(keys.getFirst()));
    }
    
    public static JSONObject getGeoResponse(String address)
    {
        String strJSON = "";
        
        try
        {
            int responseCode = 0;
           String strURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=" + keys.getFirst();
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
                        return getGeoResponse(address);
                    }
                    else //Over 2500 limit per day
                    {
                        numLimitFail = 0;
                        switchKey();
                        return getGeoResponse(address);
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
    public static String getLocationAccuracy(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONObject geometryObj = (JSONObject)jsonObject.get("geometry");
        String accuracy = geometryObj.get("location_type").toString();
        return accuracy;
        
      
    }
    
    public static double[] getCoordinate(JSONObject responseJSONObj) throws Exception
    {
    
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
    	
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONObject geometryObj = (JSONObject)jsonObject.get("geometry");
        JSONObject locationObj = (JSONObject)geometryObj.get("location");
        double latitude = Double.parseDouble(locationObj.get("lat").toString());
        double longitude = Double.parseDouble(locationObj.get("lng").toString());
        
        return new double[] {latitude, longitude};
    }
    
    public static String getStandardAddress(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        String strAddr = jsonObject.get("formatted_address").toString();
        return strAddr;
    }
    
    public static String getComponentByType(JSONArray components, String targetType)
    {
        for(int i=0; i<components.length(); i++)
        {
            try
            {
                JSONObject compObj = (JSONObject)components.get(i);
                JSONArray types = (JSONArray)compObj.get("types");
                for(int j=0; j<types.length(); j++)
                {
                    if(types.get(j).toString().equals(targetType))
                    {
                        return compObj.get("short_name").toString();
                    }
                }
            }
            catch(JSONException ex)
            {
                ex.printStackTrace();
            }
        }
        return "";
    }
    
    public static String getComponentByTypeCity(JSONArray components, String targetType)
    {
        for(int i=0; i<components.length(); i++)
        {
            try
            {
                JSONObject compObj = (JSONObject)components.get(i);
                JSONArray types = (JSONArray)compObj.get("types");
                for(int j=0; j<types.length(); j++)
                {
                    if(types.get(j).toString().equals(targetType))
                    {
                        return compObj.get("long_name").toString();
                    }
                }
            }
            catch(JSONException ex)
            {
                ex.printStackTrace();
            }
        }
        return "";
    }
    
    public static String getStandardHouseNo(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONArray components = (JSONArray)jsonObject.get("address_components");
        String strHouseNo = getComponentByType(components, "street_number");
        
        return strHouseNo;
    }
    
    public static String getStandardStreet(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONArray components = (JSONArray)jsonObject.get("address_components");
        String strStreet = getComponentByType(components, "route");
        
        return strStreet;
    }
    
    public static String getStandardCity(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONArray components = (JSONArray)jsonObject.get("address_components");
       // String strCity = getComponentByType(components, "locality");
       String strCity = getComponentByTypeCity(components, "locality");
        
        return strCity;
    }
    
    public static String getStandardState(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONArray components = (JSONArray)jsonObject.get("address_components");
        String strState = getComponentByType(components, "administrative_area_level_1");
        
        return strState;
    }
    
    public static String getStandardZipcode(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONArray components = (JSONArray)jsonObject.get("address_components");
        String strZipcode = getComponentByType(components, "postal_code");
        
        return strZipcode;
    }
    
    public static String getStandardCountry(JSONObject responseJSONObj) throws Exception
    {
        if(responseJSONObj == null)
        {
            System.out.println("Geocoding API error occurs.");
            return null;
        }
        
        JSONArray jsonArray = (JSONArray)responseJSONObj.get("results");
        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        JSONArray components = (JSONArray)jsonObject.get("address_components");
        String strCountry = getComponentByType(components, "country");
        
        return strCountry;
    }
    
    /*
    public static double[] getPosition(String address) throws Exception
    {
        int responseCode = 0;
        String query = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=false";
        URL url = new URL(query);
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.connect();
        responseCode = httpConnection.getResponseCode();
        if(responseCode == 200)
        {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(httpConnection.getInputStream());
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/GeocodeResponse/status");
            String status = (String)expr.evaluate(document, XPathConstants.STRING);
            if(status.equals("OK"))
            {
                expr = xpath.compile("//geometry/location/lat");
                String latitude = (String)expr.evaluate(document, XPathConstants.STRING);
                expr = xpath.compile("//geometry/location/lng");
                String longitude = (String)expr.evaluate(document, XPathConstants.STRING);
                return new double[] {Double.parseDouble(latitude), Double.parseDouble(longitude)};
            }
            else
            {
                //throw new Exception("Error from the API - response status: " + status);
                System.out.println("Error from the API - response status: " + status);
                Thread.sleep(1000);
                if(status.equals("OVER_QUERY_LIMIT"))
                    return getPosition(address);
            }
        }
        return null;
    }
    */
    
    public static void main(String[] args) throws Exception
    {
        //String addr = "900 University Ave Riverside CA 92521";
        //String addr = "2453 wingfield hills rd shops c sparks nv 89436";
        //String addr = "24 hours t carl s jr green burrito 1489 hulsey way manteca ca 95336";
        //String addr = "91335 hours services get directions branch chatsworth 20520 devonshire st chatsworth ca 91311";
        //String addr = "5769 lone tree way formattedaddress 5769 lone tree way antioch ca 94531";
       //String addr = "1600 Amphitheatre Parkway, Mountain View, CA";
    	String addr="600 central ave,Riverside,CA";
        
        //double latLongs[] = getPosition(addr);
        //System.out.println("Latitude: " + Double.toString(latLongs[0]) + " and Longitude: " + Double.toString(latLongs[1]));
        
        readKeysAndProxies("geolocationkeys.txt");
        JSONObject response = getGeoResponse(addr);
        double latLongs[] = getCoordinate(response);
        if(latLongs != null)
            System.out.println("Latitude: " + Double.toString(latLongs[0]) + " and Longitude: " + Double.toString(latLongs[1]));
        String stdAddress = getStandardAddress(response);
        if(stdAddress != null)
            System.out.println(stdAddress);
        String stdHouseNo = getStandardHouseNo(response);
        if(stdHouseNo != null)
            System.out.println(stdHouseNo);
        String stdStreet = getStandardStreet(response);
        if(stdStreet != null)
            System.out.println(stdStreet);
        String stdCity = getStandardCity(response);
        if(stdCity != null)
            System.out.println(stdCity);
        String stdState = getStandardState(response);
        if(stdState != null)
            System.out.println(stdState);
        String stdZipcode = getStandardZipcode(response);
        if(stdZipcode != null)
            System.out.println(stdZipcode);
        String stdCountry = getStandardCountry(response);
        if(stdCountry != null)
            System.out.println(stdCountry);
        String stdlocAcc = getLocationAccuracy(response);
        if(stdlocAcc != null)
            System.out.println(stdlocAcc);
        
        
        
    }
    
}
