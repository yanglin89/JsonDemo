package com.txtencoding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;



public class FileUtil {

    public static String[][] readDocTable(String filePath){
        if(filePath.endsWith(".doc")){
            return readDoc2003Table(filePath);
        }
        if(filePath.endsWith(".docx")){
            return readDoc2007Table(filePath);
        }

        return null;
    }


    /**
     * 读取word表格数据
     * @param filePath
     * @return
     */
    public static String[][] readDoc2003Table(String filePath) {
        String[][] resultData = new String[100][];
        int destPos = 0;
        FileInputStream in = null;
        HWPFDocument hwpf = null;
        try {
            in = new FileInputStream(filePath);
            hwpf = new HWPFDocument(in);
            Range range = hwpf.getRange();// 得到文档的读取范围
            TableIterator it = new TableIterator(range);
            while (it.hasNext()) {
                Table tb = (Table) it.next();
                String[][] resultData1 = new String[tb.numRows()][];

                for (int i = 0; i < tb.numRows(); i++) {
                    TableRow tr = tb.getRow(i);
                    String[] rowData = new String[tr.numCells()];
                    for (int j = 0; j < tr.numCells(); j++) {
                        TableCell td = tr.getCell(j);
                        // 取得单元格的内容
                        for (int k = 0; k < td.numParagraphs(); k++) {
                            Paragraph para = td.getParagraph(k);
                            String s = para.text().trim();
                            rowData[j] = s;

                        }
                    }
                    resultData1[i] = rowData;
                }
                if(destPos+resultData1.length>resultData.length){
                    String[][] resultData2 = new String[destPos+resultData1.length][];
                    System.arraycopy(resultData, 0, resultData2, resultData.length, resultData.length);
                    resultData = resultData2;
                }

                System.arraycopy(resultData1, 0, resultData, destPos, resultData1.length);
                destPos+=tb.numRows();
            }

        } catch (Exception e) {
//			e.printStackTrace();
        }finally{
            try {
                if(in!=null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            in=null;
        }

        return resultData;
    }

    public static String[][] readDoc2007Table(String filePath) {
        String[][] resultData = new String[100][];
        int destPos = 0;
        FileInputStream in = null;
        XWPFDocument hpfdoc = null;
        try {
            in = new FileInputStream(filePath);
            hpfdoc = new XWPFDocument(in);
            Iterator<XWPFTable> it = hpfdoc.getTablesIterator();
            while (it.hasNext()) {
                XWPFTable tb = it.next();
                String[][] resultData1 = new String[tb.getNumberOfRows()][];
                for (int i = 0; i < resultData.length; i++) {
                    XWPFTableRow tr = tb.getRow(i);
                    List<XWPFTableCell> cells= tr.getTableCells();
                    String[] rowData = new String[cells.size()];
                    for (int j = 0; j < rowData.length; j++) {
                        XWPFTableCell td = cells.get(j);
                        List<XWPFParagraph>	paragraphs=td.getParagraphs();
                        // 取得单元格的内容
                        for (int k = 0; k < paragraphs.size(); k++) {
                            XWPFParagraph para = paragraphs.get(k);
                            String s = para.getText().trim();
                            rowData[j] = s;

                        }
                    }
                    resultData1[i] = rowData;
                }
                if(destPos+resultData1.length>resultData.length){
                    String[][] resultData2 = new String[destPos+resultData1.length][];
                    System.arraycopy(resultData, 0, resultData2, resultData.length, resultData.length);
                    resultData = resultData2;
                }

                System.arraycopy(resultData1, 0, resultData, destPos, resultData1.length);
                destPos+=tb.getNumberOfRows();
            }

        } catch (Exception e) {
//			e.printStackTrace();
        }finally{
            try {
                if(in!=null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultData;
    }




    private static Map<String,String> docBuf = new HashMap<String,String>();
    public static String readDocWithBuf(String filePath) {
        String text = docBuf.get(filePath);
        if(text==null){
            text=readDoc(filePath);
            if(docBuf.size()>5000){
                docBuf.clear();
            }
            docBuf.put(filePath, text);
        }
        return text;
    }

    /**
     * 读取word文本信息
     *
     * @param filePath
     * @return
     */
    public static String readDoc(String filePath) {
        String text = null;
        File file = new File(filePath);

        String extName = filePath.substring(filePath.lastIndexOf('.'));
        if (file.exists() && file.isFile()) {
            if (extName.equals(".doc")) {
            	text = getDocText(filePath);
            } else if (extName.equals(".docx")) {
//                text = getDocxText(filePath);
            }
        }
        
        return text;

    }

    public static String getDocText(String filePath) {
        InputStream is = null;
        WordExtractor ex = null;
        try {
            is = new FileInputStream(new File(filePath));
            ex = new WordExtractor(is);
            return ex.getText();
        } catch (Exception e) {
            // System.out.println("getDocText::"+filePath+"\r\n"+e.getMessage());
            // e.printStackTrace();
        	throw new RuntimeException(e);
        } finally {
            try {
                if (ex != null)
                    ex.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ex= null;
            is=null;
        }
    }

//    public static String getDocxText(String filePath) {
//        OPCPackage opcPackage = null;
//        POIXMLTextExtractor extractor = null;
//        try {
//            opcPackage = POIXMLDocument.openPackage(filePath);
//            extractor = new XWPFWordExtractor(opcPackage);
//            String s = extractor.getText();
//            return s;
//        } catch (Exception e) {
//            // System.out.println("getDocxText::"+filePath+"\r\n"+e.getMessage());
//            // e.printStackTrace();
//            // return "";
//            throw new RuntimeException(e);
//        } finally {
//            try {
//                if (opcPackage != null)
//                    opcPackage.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (extractor != null)
//                        extractor.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            opcPackage=null;
//            extractor=null;
//        }
//    }

    /**
     * 读取记事本格式文件内容
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String readTxt(String path,String charset) throws Exception {
        StringBuffer result = new StringBuffer();
        FileInputStream fis = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis,charset));
        String line = null;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return result.toString();
    }


    public static boolean readTxtAndHandle(String path,String charset,LineHandler handler) {
        FileInputStream fis = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(path);
            br=new BufferedReader(new InputStreamReader(fis,charset));
            String line = null;
            int lineIndex=0;
            while ((line = br.readLine()) != null) {
                if(!handler.doStringHandler(line,lineIndex)){
                	return false;
                }
                lineIndex++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(br!=null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    if(fis!=null)
                        fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static interface LineHandler{
        public boolean doStringHandler(String line,int lineIndex);
        public boolean doStringArrayHandler(String[] lineArray,int lineIndex);
    }


    /**
     * 读取sheet页中的文本信息
     *
     * @param sheet
     * @return
     */
    public static String[][] readSheetOfExcel(Sheet sheet) {
        int rowCnt = sheet.getLastRowNum() + 1;
        if (rowCnt == 1) {
            return null;
        }
        String[][] sheetData = new String[rowCnt][];

        for (int i = 0; i < rowCnt; i++) {
            Row curRow = sheet.getRow(i);
            if (curRow == null) {
                continue;
            }
            int colCnt = curRow.getLastCellNum();
            if (colCnt <= 0) {
                continue;
            }
            String[] rowData = new String[colCnt];
            for (int j = 0; j < colCnt; j++) {
                rowData[j] = getCellStrVal(curRow.getCell(j));
            }
            sheetData[i] = rowData;
        }

        return sheetData;
    }
    
    /**
     * 读取sheet页内容并逐行处理
     * @param sheet
     * @param handler
     * @return
     * @throws Exception 
     */
    public static boolean readSheetOfExcelWithLineHandler(Sheet sheet,LineHandler handler) throws Exception {
        int rowCnt = sheet.getLastRowNum() + 1;
        if (rowCnt == 1) {
            return true;
        }
        for (int i = 0; i < rowCnt; i++) {
            Row curRow = sheet.getRow(i);
            String[] rowData = null;
            if (curRow != null) {
            	int colCnt = curRow.getLastCellNum();
                if (colCnt > 0) {
                	rowData = new String[colCnt];
                	 for (int j = 0; j < colCnt; j++) {
                         rowData[j] = getCellStrVal(curRow.getCell(j));
                     }
                }
            }
            
			if(!handler.doStringArrayHandler(rowData,i)){
				return false;
			}
        }
        return true;
    }

    /**
     * 获取excel文件中的sheet列表
     *
     * @param filePath
     * @return
     */
    public static Sheet[] getSheetsFromExcel(String filePath) {
        Sheet[] sheets = new Sheet[0];
        String fileType = filePath.substring(filePath.lastIndexOf("."));
        if (fileType.equals(".xls") || fileType.equals(".xlsx") || fileType.equals(".et")||fileType.equals(".XLS") || fileType.equals(".XLSX")) {
            File file = new File(filePath);
            if (file.isFile()) {
                Workbook workbook = null;
                InputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    workbook = (".xlsx".equals(fileType)) ? new XSSFWorkbook(fis): new HSSFWorkbook(fis);
                    int sheetCnt = workbook.getNumberOfSheets();
                    sheets = new Sheet[sheetCnt];
                    for (int i = 0; i < sheetCnt; i++) {
                        sheets[i] = workbook.getSheetAt(i);
                    }

                } catch (Exception e) {
                	System.err.println("读取文件失败！"+filePath);
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                        if (workbook != null)
                            workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return sheets;
    }

    /**
     * 获取单元格文本信息
     *
     * @param cell
     * @return
     */
    private static String getCellStrVal(Cell cell) {

        if (cell == null) {
            return "";
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
                && DateUtil.isCellDateFormatted(cell)) {
            Date d = DateUtil.getJavaDate(cell.getNumericCellValue());
            return d.toLocaleString();
        }else {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue().trim();
        }
    }

    public static Workbook loadExcel(String excelPath) {
        File file = new File(excelPath);
        if (!file.exists()) {
            return null;
        }
        String fileType = excelPath.substring(excelPath.lastIndexOf("."));
        Workbook workbook = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            if (".xls".equals(fileType)) {
                workbook = new HSSFWorkbook(fis);
            } else if (".xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(fis);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return workbook;
    }

    public static Workbook createWorkbook(String excelPath) {
        System.out.println(excelPath);
        String fileType = excelPath.substring(excelPath.lastIndexOf("."));
        if (".xls".equals(fileType)) {
            return new HSSFWorkbook();
        } else if (".xlsx".equals(fileType)) {
            return new XSSFWorkbook();
        }
        return null;
    }

    public static void saveWorkbook(Workbook workbook, String excelPath) {
        File file = new File(excelPath);
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static CellStyle createNormalCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setBorderTop((short) 1);
        return cellStyle;
    }

    public static void insertIntoExcel(String[] data, Sheet sheet,
                                       CellStyle cellStyle) {

        int curRowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(curRowNum);
        for (int i = 0; i < data.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(data[i].replace("\"", ""));
            cell.setCellStyle(cellStyle);
        }
    }

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



    public static String getTxtCharset(String fileName) {

        InputStream in = null;
        BufferedInputStream bin = null;
        try {
            in = new FileInputStream(fileName);
            bin = new BufferedInputStream(in);
            int x = bin.read();
            int y = bin.read();
            int z = bin.read();

            if(x==0xef && y==0xbb && z==0xbf){
                return "utf-8";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                bin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "gbk";
    }

    public static String getTxtCharset1(String fileName) throws Exception{

        InputStream in = new FileInputStream(fileName);
        byte[]head = new byte[3];
        in.read(head);
        if(head[0]==-1 && head[1]==-2){
            return "UTF-16BE";
        }else if(head[0]==-2 && head[1]==-1){
            return "Unicode";
        }else if(head[0]==-17 && head[1]==-69 && head[2]==-65){
            return "utf-8";
        }else {
            return "gbk";
        }
    }

    /**
     *
     * @author ： Rain
     * <p> return_type : String</p>
     * <p> Description : 获取案件编号</p>
     * <p> Company : 北京锐安科技有限公司</p>
     * <p> @date 2016年12月12日 上午11:30:48 </p>
     */
    public static String getCaseNo(String filePath){
        String caseNo = "";
        //异常处理，保证程序的正常执行
        try {
            String filePathArray[] = filePath.split("/");
            caseNo = filePathArray[2];//14
            //System.out.println(caseNo);
            //System.out.println(getCaseNameFlag(caseNo));
            if(getCaseNameFlag(caseNo)){
                caseNo = filePathArray[3];
            }
            Pattern pattern = Pattern.compile("(A\\d{20,22})");//具体规则待定
            Matcher matcher = pattern.matcher(filePath);
            if(matcher.find()){
                caseNo = matcher.group(1);
            }
        } catch (Exception e) {
            //发生异常  自动生成案件编号   时间+序列号
           // caseNo = CommonUtil.getCaseNo();
        }
        return caseNo;
    }

    public static String getCaseNoByName(String filePath){

       /* String caseNo = "";

        //异常处理，保证程序的正常执行
        try {
            caseNo = filePath.split("/")[7];//13

        } catch (Exception e) {
            //发生异常  自动生成案件编号   时间+序列号
            caseNo = CommonUtil.getCaseNo();
        }
        return caseNo;*/
       return null;
    }

	/*public static String getCaseNo(String filePath){
		String caseNo = "";
		//异常处理，保证程序的正常执行
		try {
			String filePathArray[] = filePath.split("/");
			caseNo = filePathArray[14];//14
			//System.out.println(caseNo);
			//System.out.println(getCaseNameFlag(caseNo));
			if(getCaseNameFlag(caseNo)){
				caseNo = filePathArray[15];
			}
			Pattern pattern = Pattern.compile("(A\\d{20,22})");//具体规则待定
			Matcher matcher = pattern.matcher(caseNo);
			if(matcher.find()){
				caseNo = matcher.group(1);
			}
		} catch (Exception e) {
			//发生异常  自动生成案件编号   时间+序列号
			caseNo = CommonUtil.getCaseNo();
		}
		return caseNo;
	}

	public static String getCaseNoByName(String filePath){
		String caseNo = "";
		//异常处理，保证程序的正常执行
		try {
			caseNo = filePath.split("/")[13];//13
		} catch (Exception e) {
			//发生异常  自动生成案件编号   时间+序列号
			caseNo = CommonUtil.getCaseNo();
		}
		return caseNo;
	}
	*/

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

    public static void main(String[] args) throws Exception {

        String str = "\\\\10.2.174.120\\f\\upload\\2016年10月\\2017-03-02\\2017-03-02\\安徽省公安厅\\蔡猛涉嫌组织领导传销活动案+ A3401221000002014090049\\资金数据";
        str = str.replaceAll("\\\\", "/");
        System.out.println(str);
        System.out.println(getCaseNo(str));
        System.out.println(getCaseNoByName(str));
		/*System.setErr(new PrintStream("D:\\su20161117\\20161125\\err1.txt"));
		System.setOut(new PrintStream("D:\\su20161117\\20161125\\out1.txt"));

		String str = "\\\\10.2.174.120\\ftp上传\\localUser\\share\\上海数据\\四流（1）";
		File root = new File(str);
		listAndParseCsv(root);
		System.out.println("all is over");*/
    }


    public static void p_out(String content){
        System.out.println(content);
        writeTxt2File(content, "D:\\su20161117\\20161125\\out.txt",true);
    }

//	public static void p_out(String[][] data){
//		for(String[] row :data){
//			String content = Arrays.toString(row);
//			System.out.println(content);
//			writeTxt2File(content, "D:\\su20161117\\20161125\\out.txt");
//		}
//	}

    public static void p_err(String content){
        System.out.println(content);
        writeTxt2File(content, "D:\\su20161117\\20161125\\fail.txt",true);
    }

    public static boolean getCaseNameFlag(String caseName){
        boolean flag  = false;
        if(caseName.indexOf("大队")>-1
                ||caseName.indexOf("中队")>-1
                ||caseName.indexOf("支队")>-1
                ||caseName.indexOf("公安局")>-1
                ||caseName.indexOf("root")>-1
                ||caseName.indexOf("文件夹")>-1){
            flag = true;
        }
        return flag;
    }

}
