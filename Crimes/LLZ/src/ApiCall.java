import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;



public class ApiCall {

	

public static boolean isZipcode(String str)
{
if(str.length()!=5 && str.length()!=9)
    return false;
else
{
    for(int i=0; i<str.length(); i++)
    {
        if(!Character.isDigit(str.charAt(i)))
        {
            return false;
        }
    }
    return true;
}
}
	
	public static void elevationCall(LLZ llz) throws ClassNotFoundException, JSONException, SQLException, IOException{
		
		Elevation.getElevation(llz);
	}
	public static void reverseGeocodeCall(LLZ llz)throws Exception{
		
		JSONObject response = reverseGeoCode.getReverseGeoResponse(llz);
		String housenumber=Location.getStandardHouseNo(response);
		String street= Location.getStandardStreet(response);
		String addr= housenumber+" "+street;
		llz.setAddress(addr);
		String strSTD = Location.getStandardZipcode(response);
        if(strSTD!=null && !strSTD.isEmpty())
        {
         if(isZipcode(strSTD))
         {
            llz.setZip(strSTD);
         }
        }
         
         String stdCountry=Location.getStandardCountry(response);
         if(stdCountry != null){
			llz.setCountry(stdCountry );
         }
         
         String stdState=Location.getStandardState(response);
         if(stdState != null){
			llz.setState(stdState );
         }
         
         String stdCity = Location.getStandardCity(response);
         if(stdCity != null){
			llz.setCity(stdCity);
         } 
         
         String stdAccuracy = Location.getLocationAccuracy(response);
         //if(stdAccuracy != null){
        
			llz.setLocationAccuracy(stdAccuracy);
	}
	
public static void geocodeCall(String addr,LLZ llz) throws ClassNotFoundException, JSONException, SQLException, IOException{
		
	 try {
	
	
	JSONObject response = Location.getGeoResponse(addr);
    double[] pos = Location.getCoordinate(response);

 
    if(pos != null)
    {
    	
    	 llz.setLatitude(pos[0]);
    	 llz.setLongitude(pos[1]);
    	
    	 String strSTD = Location.getStandardZipcode(response);
           if(strSTD!=null && !strSTD.isEmpty())
           {
            if(isZipcode(strSTD))
            {
               llz.setZip(strSTD);
            }
           }
            
            String stdCountry=Location.getStandardCountry(response);
            if(stdCountry != null){
			llz.setCountry(stdCountry );
            }
            
            String stdState=Location.getStandardState(response);
            if(stdState != null){
			llz.setState(stdState );
            }
            
            String stdCity = Location.getStandardCity(response);
            if(stdCity != null){
			llz.setCity(stdCity);
            } 
            
            String stdAccuracy = Location.getLocationAccuracy(response);
            //if(stdAccuracy != null){
           
			llz.setLocationAccuracy(stdAccuracy);
           // }
		
	 }
 
	}catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LLZ llz = new LLZ();
		try {
			geocodeCall("600 central ave, riverside,ca",llz);
			elevationCall(llz);
			System.out.printf("latitude %f ,longitude %f ,zip %s, elevation %f ,elevationType %s , locationAccuracy %s \n",llz.getLatitude(),llz.getLongitude(),llz.getZip(),llz.getElevation(),llz.getElevationType(),llz.getLocationAccuracy());
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
