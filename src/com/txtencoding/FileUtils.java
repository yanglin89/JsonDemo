package com.txtencoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * 文件读写工具类
 * @author sushaolei
 *
 */
public class FileUtils {
	

	/**
	 * 递归列出所有文件
	 * 
	 * @param root
	 * @param allFiles
	 */
	public static void listFile(File root, List<File> allFiles) {
		if (root.exists() && root.isDirectory()) {
			File[] children = root.listFiles();
			if (children != null && children.length > 0) {
				for (File child : children) {
					if (child.isFile()) {
						// System.out.println(child);
						if (!child.isHidden()) {
							allFiles.add(child);
						}

					} else {
						listFile(child, allFiles);
					}
				}
			}
		} else {
			System.err.println("文件不存在，或不是文件夹！");
		}
	}
	
	/**
	 * 递归列出所有文件
	 * 
	 * @param root
	 * @param allFiles
	 */
	public static void listFile(File root, List<File> allFiles,int maxDepth) {
		maxDepth--;
		if (root.exists() && root.isDirectory()) {
			File[] children = root.listFiles();
			if (children != null && children.length > 0) {
				for (File child : children) {
					if (child.isFile()) {
						// System.out.println(child);
						if (!child.isHidden()) {
							
							System.out.println(child);
						}

					} else {
						if(maxDepth>0){
							listFile(child, allFiles,maxDepth);
						}else{
							allFiles.add(child);
						}
					}
				}
			}
		} else {
			System.err.println("文件不存在，或不是文件夹！");
		}
	}
	/**
	 * 将文本内容按行读取到list中去
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static List<String> readTxt2List(String path,String charSet) {
		List<String> result = new ArrayList<String>();
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fis,charSet));
			String line = null;
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return result;
	}
	
	public static String readTxt(String path,String charSet) {
		StringBuilder result = new StringBuilder();
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fis,charSet));
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return result.toString();
	}

	/**
	 * 将文本信息写到文件中去
	 * 
	 * @param content
	 * @param savePath
	 */
	public static void writeTxt2File(String content, String savePath,boolean isAppend) {
		File file = new File(savePath);
		file.getParentFile().mkdirs();
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			fos = new FileOutputStream(file,isAppend);
			pw = new PrintWriter(fos);
			pw.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String[][] readCsv(String csvFile,String charSet){
		
		String[][] result = null;
		try {
			List<String> data = readTxt2List(csvFile,charSet);
			int len = data.size();
			result = new String[len][];
			for(int i = 0;i<len;i++){
				String line = data.get(i);
				String [] lineData = line.split("[,，]");
				result[i] = lineData;
			}
		} catch (Throwable e) {
			System.err.println("errFile："+csvFile);
			e.printStackTrace();
		}
		
		return result;
	}

	public  static Properties loadProps(String filePath) throws Exception{
//		filePath="config/conf.properties";
		Properties props = new Properties();
		props.load(FileUtils.class.getClassLoader().getResourceAsStream(filePath));
		return props;
	}
	
	public static void main(String[] args) throws Exception {
		Properties props =loadProps("");
		String s = props.getProperty("service_url");
		System.out.println(s);
	}
	
	
}





