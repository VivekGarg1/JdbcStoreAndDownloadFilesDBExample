package com.home.client;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.home.util.JdbcUtil;

public class DownloadFilesClientTest {

	public static void main(String[] args) {
		String sql="select * from storetextfile_table";
		Path dir = Paths.get("DownloadFiles");
		try(Connection connection = JdbcUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			System.out.println("Following files are download from database");
			while (rs.next()) {
				int fileId = rs.getInt("file_id");
				String fileName = rs.getString("file_name");
				long fileLengthInKB = rs.getLong("file_size_in_kb");
				String fileExt = rs.getString("file_extension");
				System.out.println("File Id: "+fileId);
				System.out.println("File Name: "+fileName);
				System.out.println("File size in KB: "+fileLengthInKB);
				System.out.println("File extension: "+fileExt);
				Clob clob = rs.getClob("file_content");
				InputStream inputStream=clob.getAsciiStream();
				System.out.println("------------------------------");
				Files.copy(inputStream, Paths.get("DownloadFiles/"+rs.getString("file_name")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
