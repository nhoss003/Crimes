import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;



public class LLZ {
	private	int myId ;
	private boolean universal_flag = false;
    private String crime_type=null;
    private String crawl_time = null;
    private String address=null;
    private String city;
    private String state;
    private String zip=null;
	public void setMyId(int myId) {
		this.myId = myId;
	}

	public void setUniversal_flag(boolean universal_flag) {
		this.universal_flag = universal_flag;
	}

	public int getMyId() {
		return myId;
	}

	public boolean isUniversal_flag() {
		return universal_flag;
	}



	private double latitude = 0.0;
	private double longitude = 0.0;
	private String incident_time = null;
	private String locationAccuracy = null;
	private double elevation = 0.0;
	private String elevationType = null;
	private String country= null;
	
	
	public void setCrime_type(String crime_type) {
		this.crime_type = crime_type;
	}

	public void setCrawl_time(String crawl_time) {
		this.crawl_time = crawl_time;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}


	public void setIncident_time(String incident_time) {
		this.incident_time = incident_time;
	}


	public void setLocationAccuracy(String locationAccuracy) {
		this.locationAccuracy = locationAccuracy;
	}


	public void setCountry(String country) {
		this.country = country;
	}

	public String getCrime_type() {
		return crime_type;
	}

	public String getCrawl_time() {
		return crawl_time;
	}

	public String getIncident_time() {
		return incident_time;
	}

	public String getLocationAccuracy() {
		return locationAccuracy;
	}

	public String getCountry() {
		return country;
	}
	
	
	public String getState() {
		return state;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getZip() {
		return zip;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public String getElevationType() {
		return elevationType;
	}

	public void setElevationType(String elevationType) {
		this.elevationType = elevationType;
	}
	int quota=0;
	public static void main(String[] args) throws SQLException {
		Location.readKeysAndProxies("geolocationkeys.txt");
		Elevation.readKeysAndProxies("elevationkeys.txt");

	 LLZ l = new LLZ();

	 l.mainFunc();
	
	}

	
	
public void mainFunc () throws SQLException {

		
		Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
	
				myConn.setAutoCommit(false);
				
				
				Statement myStmt = myConn.createStatement();
				
		ResultSet myRs = myStmt.executeQuery("select * from crimes where (address is null or address='' or latitude is null or latitude='' or longitude is null or longitude='' or zip is null or zip='') and id > 703500");
		
		//ResultSet myRs = myStmt.executeQuery("select * from crimes where (address is null or address='' or latitude is null or latitude='' or longitude is null or longitude='' or zip is null or zip='')");
		//	ResultSet myRs = myStmt.executeQuery("select * from crimes where (address is null or latitude is null or longitude is null or zip is null) ");
				
				
				while(myRs.next()){
					
					universal_flag=false;
					myId =  myRs.getInt("id");
					
					latitude= 0.0;
					longitude= 0.0;
				
				     address = myRs.getString("address").trim();
				     
				     city = myRs.getString("city").trim();
				     String previousCity=city;
				     state = myRs.getString("state").trim();
				    
						latitude = myRs.getDouble("latitude");
						longitude =  myRs.getDouble("longitude");

					zip =myRs.getString("zip");
					String previousZip=zip;
					if(( address==null || address.isEmpty() ) && (int)latitude==0 && (int)longitude==0)
					{
						System.out.println("only zip");
						continue;
						
					}
					
					locationAccuracy = myRs.getString("locationAccuracy");
			//if(!address.isEmpty()){
				//		LocationCache.ifAlreadyExists_addr(this);
						//}
			//else if((int)latitude !=0 ){
			//else{
					if((int)latitude !=0){
				LocationCache.ifAlreadyExists_LL(this);
					}else{
						LocationCache.ifAlreadyExists_addr(this);
						
					}
			//}
						
					
					try {
					if(!universal_flag){
						quota++;
						if(!address.isEmpty()){
							
						String concatAddress= address+","+city+","+state;
						
						ApiCall.geocodeCall(concatAddress,this);
						
						ApiCall.elevationCall(this);
						
						 LocationCache.insertInto(this);
						}
						else if((int)latitude !=0 ){
							
							ApiCall.reverseGeocodeCall(this);
							ApiCall.elevationCall(this);
							LocationCache.insertInto(this);
						}
						
						
					}
						} catch (ClassNotFoundException e) {
							
							e.printStackTrace();
						} catch (JSONException e) {
							
							e.printStackTrace();
						} catch (IOException e) {
							
							e.printStackTrace();
						} catch (Exception e) {
							
							e.printStackTrace();
						}

			
				if(!address.isEmpty() || (int)latitude!=0){
				String query2 = "UPDATE `business`.`crimes` SET address=?, latitude=? , longitude=? , zip=? , city=? ,locationAccuracy=? where id ="+ myId;
						
						//"where latitude is null or longitude is null or zip is null";
				PreparedStatement ps2 = myConn.prepareStatement(query2);

				ps2.setString(1,address);
				
                if((int)latitude==0){
				ps2.setString(2,null);
				}else{
				ps2.setDouble(2,latitude);
				}
				if((int)longitude==0){
				ps2.setString(3,null);
				}else{
				ps2.setDouble(3,longitude);
				}
				
				if(zip!=null && !zip.isEmpty()){
					ps2.setString(4,zip);
					}
					else{
						ps2.setString(4,previousZip);
					}
				if(city!=null && !city.isEmpty()){
					
				ps2.setString(5,city);
				
				}
				else{
					ps2.setString(5,previousCity);
					}
				
				ps2.setString(6,locationAccuracy);
				
				// ps2.addBatch();
				 
                 //ps2.executeBatch();
                 
				ps2.executeUpdate();
				 
				myConn.commit();
				ps2.close();

				}
 	
					System.out.printf("Quota %d\n",quota);
 	
			}
				myConn.close();	
   }
}
