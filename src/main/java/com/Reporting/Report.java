package com.Reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;


public class Report {
	public static int Pass,Fail,Total,passcount,failcount,totalpass,totalfail;

	static String htmlvalue="<thead bgcolor='#3c1d6b'><tr><th><font color=\"#ffffff\">Application Name</font></th><th><font color=\"#ffffff\">Planned count</font></th><th bgcolor='limegreen'><font color=\"#ffffff\">Passed</font></th><th bgcolor='limegreen'><font color=\"#ffffff\">% Passed</font></th><th bgcolor='red'><font color=\"#ffffff\">Failed</font></th><th bgcolor='red'><font color=\"#ffffff\">% Failed</font></th></tr></thead>";

	public static String dir;
	public static Properties props;

	public static void pathdir() {
		dir = System.getProperty("user.dir");
		System.out.println(dir);
	}

	public static void properties() throws IOException{

		File configFile = new File("path.properties");
		

		FileReader reader = new FileReader(configFile);

		props = new Properties();

		// load the properties file:
		props.load(reader);
	}
	public static void main(String[] args) throws IOException {
		System.out.println("Jar started executing..");
		pathdir();
		properties();
		FileInputStream Fis = new FileInputStream(dir+props.getProperty("Report"));
		HSSFWorkbook wb = new HSSFWorkbook(Fis);
		for (int shcount=0; shcount<=wb.getNumberOfSheets(); shcount++){
			try{

				//count =0;
				HSSFSheet sh= wb.getSheetAt(shcount);
				Pass=0;
				Fail=0;
				int Planned = 0;
				double passpercentage = 0;
				double failpercentage = 0;

				for (int i=1;i<=sh.getLastRowNum();i++){

					DataFormatter formatter = new DataFormatter();
					String Passcount = formatter.formatCellValue(sh.getRow(i).getCell(1));

					passcount = Integer.parseInt(Passcount);
					Pass=Pass+passcount;
					String Failcount = formatter.formatCellValue(sh.getRow(i).getCell(2));
					failcount = Integer.parseInt(Failcount);
					Fail=Fail+failcount;


				}
				DataFormatter formatter = new DataFormatter();
				String Appname = formatter.formatCellValue(sh.getRow(0).getCell(0));

				Planned = Pass+Fail;

				passpercentage = ((double)Pass*100)/(double)Planned;
				failpercentage = ((double)Fail*100)/(double)Planned;


				htmlvalue=htmlvalue.concat("<tr><td><b>"+Appname+"</b></td><td>"+String.valueOf(Planned)+"</td><td>"+String.valueOf(Pass)+"</td><td>"+String.format("%.2f%%",passpercentage)+"</td><td>"+String.valueOf(Fail)+"</td><td>"+String.format("%.2f%%",failpercentage)+"</td></tr>");
				totalpass=totalpass+Pass;
				totalfail=totalfail+Fail;

			}
			catch (Exception e) {
			}


		}
		wb.close();
		int totalpassfail = totalpass+totalfail;

		double totalpasspercentage = ((double)totalpass*100)/(double)totalpassfail;
		double totalfailpercentage = ((double)totalfail*100)/(double)totalpassfail;

		htmlvalue=htmlvalue.concat("<tr style='background-color:#999999'><td><b>Total count</td><td><b>"+String.valueOf(totalpassfail)+"</td><td><b>"+String.valueOf(totalpass)+"</td><td><b>"+String.format("%.2f%%",totalpasspercentage)+"</td><td><b>"+String.valueOf(totalfail)+"</td><td><b>"+String.format("%.2f%%",totalfailpercentage)+"</td></tr>");	


		File htmlTemplateFile = new File(dir+props.getProperty("ReportTemplete"));
		Charset.forName("UTF-8");
		String htmlString = FileUtils.readFileToString(htmlTemplateFile);
		htmlString = htmlString.replace("${table}", htmlvalue);
		htmlString = htmlString.replace("bgcolor='#3c1d6b'", "style='background-color:#3c1d6b'");
		File newHtmlFile = new File(dir+props.getProperty("Reporthtml"));
		FileUtils.writeStringToFile(newHtmlFile, htmlString);
		System.out.println("Jar finished executing..");
	}

}

