package com.home.client;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.home.util.JdbcUtil;

public class SaveBinaryFilesClientTest {

	public static void main(String[] args) {
		String sql="insert into storebinaryfile_table(file_name, file_size_in_kb, file_extension, file_content)values(?,?,?,?)";
		Path dir = Paths.get("InputBinaryFiles");
		try(Connection connection = JdbcUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);
				Stream<Path> list = Files.list(dir)) {
			System.out.println("Following files are saved into database");
			List<Path> pathList = list.collect(Collectors.toList());
			for (Path path : pathList) {
				System.out.println(path.getFileName());
				File file = path.toFile();
				String fileName = file.getName();
				long filelength = file.length();
				long fileLengthInKB = filelength/1024;
				ps.setString(1, fileName);
				ps.setLong(2, fileLengthInKB);
				ps.setString(3, fileName.substring(fileName.lastIndexOf(".")+1));
				FileInputStream fis=new FileInputStream(file);
				ps.setBinaryStream(4, fis,filelength);
				ps.addBatch();
			}
			System.out.println("----------------------------------------");
			int[] executeBatch = ps.executeBatch();
			for (int i : executeBatch) {
				System.out.println(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
