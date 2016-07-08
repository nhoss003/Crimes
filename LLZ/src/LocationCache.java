//import java.io.IOException;
//import java.io.ObjectInputStream.GetField;
import java.sql.*;


//import org.json.JSONException;



public class LocationCache {

	public static void ifAlreadyExists_LL(LLZ llz) throws SQLException{

	Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
	//System.out.printf("id %d beggining of ll function\n", llz.getMyId());
	Statement myStmt = myConn.createStatement();
	/*
	ResultSet myRs = myStmt.executeQuery("select zipcode from locationCache" +
			"where ABS(latitude-"+llz.getLatitude()+")<= 0.00001 and " +
					"ABS(longitude-"+llz.getLongitude()+")<=0.00001");   */
	ResultSet myRs = myStmt.executeQuery("select streetaddr,zipcode,locationAccuracy  from crimesLocCache where ABS(latitude-"+llz.getLatitude()+")<= 0.0001 and ABS(longitude-"+llz.getLongitude()+")<=0.0001");

	while(myRs.next()){
		llz.setUniversal_flag(true);
		System.out.printf(" ifAlreadyExists_llz %d\n",llz.getMyId());
		
		//System.out.printf("id %d exists in cache, ll function, latitude %f , longitude %f\n", llz.getMyId(),llz.getLatitude(),llz.getLongitude());
		llz.setAddress(myRs.getString("streetaddr"));
		if(myRs.getInt("zipcode")== 0){
			llz.setZip(null);
		}
		else{
		llz.setZip(Integer.toString(myRs.getInt("zipcode")));
		}
		llz.setLocationAccuracy(myRs.getString("locationAccuracy"));
		
	}
	//System.out.printf("id %d reached end of ll function\n", llz.getMyId());
	myConn.close();	
	}
	
	
	
	public static void ifAlreadyExists_addr(LLZ llz) throws SQLException{

		Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
		//System.out.printf("id %d beggining of addr function\n", llz.getMyId());
		Statement myStmt = myConn.createStatement();
		ResultSet myRs = myStmt.executeQuery("select * from crimesLocCache where streetaddr LIKE '%"+llz.getAddress()+"%' and state= '"+llz.getState()+"' and city= '"+llz.getCity()+"'");

		while(myRs.next()){
			llz.setUniversal_flag(true);
			System.out.printf(" ifAlreadyExists_addr %d\n",llz.getMyId());
		//	System.out.printf("id %d exists in cache, addr function\n", llz.getMyId());
			llz.setLatitude(myRs.getDouble("latitude"));
			llz.setLongitude(myRs.getDouble("longitude"));
			
			if(myRs.getInt("zipcode")== 0){
				llz.setZip(null);
			}
			else{
			      llz.setZip(Integer.toString(myRs.getInt("zipcode")));
			}
			
			llz.setLocationAccuracy(myRs.getString("locationAccuracy"));
			
			
		}
		//System.out.printf("id %d reached end of addr function\n", llz.getMyId());
		myConn.close();	
		}
	
	
	
	
	public static void insertInto (LLZ llz) throws SQLException {
		
//Connection myConn2 = DriverManager.getConnection("jdbc:mysql://localhost/homeDB", "root","home123");
Connection myConn2 = DriverManager.getConnection("jdbc:mysql://localhost/business", "root","home123");
		//System.out.printf(" id %d beginning of insert function\n", llz.getMyId());
		myConn2.setAutoCommit(false);
		String query2 = "INSERT INTO `business`.`crimesLocCache` (latitude ,longitude ," +
				"streetaddr,city,state,zipcode,country," +
				"elevation,elevationType, locationAccuracy) " +
				"VALUES ( ?, ?, ?, ?, ? , ?, ? ,?, ? ,?) ON DUPLICATE KEY UPDATE latitude=latitude, longitude=longitude";
		PreparedStatement ps2 = myConn2.prepareStatement(query2);
//System.out.println("insert in process");
		System.out.printf(" insertInto %d\n",llz.getMyId());
		ps2.setDouble(1,llz.getLatitude());
		ps2.setDouble(2,llz.getLongitude());
		ps2.setString(3,llz.getAddress());
		ps2.setString(4,llz.getCity());
		ps2.setString(5,llz.getState());
		if(llz.getZip()!=null){
		ps2.setInt(6,Integer.parseInt(llz.getZip()));
		}
		else{
			ps2.setString(6,null);
		}
		ps2.setString(7,llz.getCountry());
		ps2.setDouble(8,llz.getElevation());
		ps2.setString(9,llz.getElevationType());
		ps2.setString(10,llz.getLocationAccuracy());
		ps2.executeUpdate();
		myConn2.commit();
		ps2.close();
		//System.out.printf("id %d reached end of insert function\n", llz.getMyId());
		myConn2.close();	
		    	
	}
	
	public static void main(String[] args) {
		
		LLZ llz = new LLZ();
		llz.setLatitude(37.422476);
		llz.setLongitude(-122.084250);
		try {
			ifAlreadyExists_LL(llz);
			//System.out.printf("zip iz %s \n",llz.getZip());
			//System.out.printf("accuracy iz %s \n",llz.getLocationAccuracy());
			llz.setAddress("567 ave");
			
			System.out.println("addr");
			
			ifAlreadyExists_addr(llz);
			System.out.printf("lat iz %f \n",llz.getLatitude());
			System.out.printf("long iz %f \n",llz.getLongitude());
			System.out.printf("zip iz %s \n",llz.getZip());
			System.out.printf("accuracy iz %s \n",llz.getLocationAccuracy());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	/*	
		LLZ llz= new LLZ();
	

		try {
			for(int i=1; i<=3 ; ++i){
			llz.setZip(null);
			
			
		if(i==1){
		llz.setLatitude(37.762917);
		llz.setLongitude(-122.412174);
		}
		else{
			llz.setLatitude(37.722659);
			llz.setLongitude(-122.412470);
		}
			ifAlreadyExists(llz);
			System.out.println(llz.getZip());
		//	if(llz.getZip()==null){
			if(flag = false){
				try {
					Elevation.getElevation(llz);
					System.out.println("elevation api called because it didn't exists");
					insertInto(llz);
				} catch (ClassNotFoundException e) {
					
					e.printStackTrace();
				} catch (JSONException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			insertInto(llz);
			
		}
		}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		*/
	}

}
