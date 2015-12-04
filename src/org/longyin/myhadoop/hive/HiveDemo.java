package org.longyin.myhadoop.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveDemo {
	public static void main(String[] args) throws Exception {
		Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
		Connection connection = DriverManager.getConnection("jdbc:hive://centos:10000/default", "", "");
		Statement stmt = connection.createStatement();
		String querySQL="select * from default.tb1";
		ResultSet resut = stmt.executeQuery(querySQL);
		while (resut.next()) {
			System.out.println(resut.getInt(1));
		}
	}

}
