package socrata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.opencsv.CSVReader;

public class Main extends Base {
	public static String filename = null;
	public static String fn = null;

	public boolean isZipcode(String str) {
		if (str.length() != 5)
			return false;
		else {
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	}

	public boolean isState(String str) {
		if (str.length() != 2)
			return false;
		else {
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isAlphabetic(str.charAt(i)))

				{
					return false;
				}
			}
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		// File folder = new File("/home/niloufar/Desktop/GeoIPCountryCSV/s");
		File folder = new File("/home/nhoss003/socrata2");
		// File folder = new File("/home/niloufar/Desktop/apple");
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);

		// for(File file : folder.listFiles())
		for (int i = 0; i < listOfFiles.length; i++) {

			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(".csv")) {
				System.out.printf(" %d ", i);
				fn = file.getName();
				// System.out.println(fn);
				// String filename = file.toString();
				filename = file.toString();
				// System.out.println(fn);
				// System.out.println(filename);
				String compare = file.getAbsolutePath();
			
				// file names smaller than L
//change it to ">0" for file names greater than L
				if (compare
						.compareTo("/home/nhoss003/socrata2/Lakewood-Police-Department+v76p-ut9w.csv") < 0) {

					System.out.println(compare);
					Main parseCSVFile = new Main();

					parseCSVFile.parseUsingOpenCSV(filename);
				}
			}
		}

	}

	private void parseUsingOpenCSV(String filename) throws Exception {

		flag = 0;
		// System.out.printf("flag is %d\n",flag);
		CSVReader reader0;
		CSVReader reader;
		Timestamp crawl_time = null;
		Timestamp t = null;
		Timestamp t1 = null;
		Timestamp t2 = null;

		String thisTime = "2016-09-18 00:00:00";
		String invalid = "2017-02-01 00:00:00";
		String state = null;
		String year = null;
		String month = null;
		String day = null;
		String city = null;
		String address = null;
		String zip = null;
		String incident_time = null;
		double latitude = 0.0;
		double longitude = 0.0;
		// https://moto.data.socrata.com/dataset/King-County-Sheriff-s-Office/4h35-4mtu
		int x = fn.lastIndexOf(".csv");
		String s = fn.substring(0, x).replace("+", "/");
		String source = "https://moto.data.socrata.com/dataset/" + s;
		System.out.println(source);
		try {
			reader0 = new CSVReader(new FileReader(filename));
			reader = new CSVReader(new FileReader(filename));
			String[] row;

			// Connection myConn =
			// DriverManager.getConnection("jdbc:mysql://localhost/business",
			// "root","home123");
			Connection myConn = DriverManager.getConnection(
					"jdbc:mysql://localhost/business", "root", "home123");

			myConn.setAutoCommit(false);
			t1 = Util.convertStringToTimestamp(thisTime);
			t2 = Util.convertStringToTimestamp(invalid);
			String previous_state = null;
			while ((row = reader0.readNext()) != null) {
				if (isState(row[9])) {
					state = row[9];
					previous_state = state;
					break;
				}
			}

			while ((row = reader.readNext()) != null) {

				if (flag < 1) {
					flag++;
					continue;
				}

				crawl_time = new java.sql.Timestamp(Calendar.getInstance()
						.getTime().getTime());
				flag++;

				incident_time = row[2];

				if (!incident_time.isEmpty()) {

					String[] splited_incident_time = incident_time
							.split("\\s+");

					SimpleDateFormat displayFormat = new SimpleDateFormat(
							"HH:mm:ss");
					SimpleDateFormat parseFormat = new SimpleDateFormat(
							"hh:mm:ss a");
					Date date = parseFormat.parse(splited_incident_time[1]
							+ " " + splited_incident_time[2]);
					String new_time = displayFormat.format(date);
					String[] str = splited_incident_time[0].split("/");
					year = str[2];
					month = str[0];
					day = str[1];
					String date2 = year + "-" + month + "-" + day;
					incident_time = date2 + " " + new_time;

					t = Util.convertStringToTimestamp(incident_time);
					if (t.after(t1) && t.before(t2)) {

						System.out.printf("%d  %s\n", flag, source);

						crime_type = row[3];
						if (row[3].length() > 100) {
							crime_type = row[3].substring(0, 99);

						}

						address = row[6];
						if (row[6].length() > 100) {
							address = row[6].substring(0, 99);

						}
						city = row[8].trim();
						if (row[8].length() > 40) {
							city = row[8].substring(0, 39);

						}
						if (!city.isEmpty()) {

							city = city.toLowerCase();
							city = city.substring(0, 1).toUpperCase()
									+ city.substring(1);
							String[] part = city.split("\\s+");
							if (part.length > 1) {

								city = "";
								for (int i = 0; i < part.length; ++i) {
									part[i] = part[i].substring(0, 1)
											.toUpperCase()
											+ part[i].substring(1);
									city += part[i] + " ";

								}

							}

						}

						if (isState(row[9])) {
							state = row[9];
							previous_state = state;
						} else {
							state = previous_state;
						}

						zip = row[10].trim();
						if (isZipcode(zip)) {

						} else {
							zip = null;
						}
						if (zip == null) {
							if (isZipcode(row[8])) {
								zip = row[8];
							}

						}

						if (!row[12].isEmpty()) {
							latitude = Double.parseDouble(row[12].trim());
						} else {
							latitude = 0.0;

						}
						if (!row[13].isEmpty()) {
							longitude = Double.parseDouble(row[13].trim());
						} else {

							longitude = 0.0;
						}

						Violent.isViolent(this);

						String query = "INSERT INTO `business`.`crimes` ( crime_type,crawl_time,address,city,state,zip,latitude,longitude,source_URL,incident_time, violent) VALUES (? ,? , ? ,?, ?, ?, ?, ? , ?, ? ,? )";
						PreparedStatement ps = myConn.prepareStatement(query);

						ps.setString(1, crime_type);
						ps.setTimestamp(2, crawl_time);
						ps.setString(3, address);
						ps.setString(4, city);
						ps.setString(5, state);
						ps.setString(6, zip);
						if ((int) latitude != 0) {
							ps.setDouble(7, latitude);
						} else {
							ps.setString(7, null);
						}
						if ((int) longitude != 0) {
							ps.setDouble(8, longitude);
						} else {
							ps.setString(8, null);
						}
						ps.setString(9, source);
						if (!incident_time.isEmpty()) {
							ps.setTimestamp(10, Util
									.convertStringToTimestamp(incident_time));
						} else {
							ps.setTimestamp(10, null);
						}
						ps.setInt(11, violent);
						ps.executeUpdate();
						myConn.commit();
						ps.close();

					}
				}
			}

			myConn.close();
			System.out
					.println("***Parsing the File has Completed Successfully, you can start other files***");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception e) {

			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

}
