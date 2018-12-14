package excel2db.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.*;

import excel2db.ApplicationException;
import excel2db.service.InitInputFiles;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitInputFilesImpl implements InitInputFiles {

    //now implement reading from settings file

    private Workbook workbook;
    private File inputSheetFile;
    private String fileExtension;

    private Sheet[] sheets;


    public File getInputSheetFile() {
        return inputSheetFile;
    }

    public void setInputSheetFile(File inputSheetFile) {
        this.inputSheetFile = inputSheetFile;
    }

    public static final Logger logger = LoggerFactory.getLogger(InitInputFilesImpl.class);


    //fabric method pattern, we don't know which object should be created
    //in advance and define the object type based on an extension
    @Override
    public Sheet[] initInputFiles(File inputFile)
            throws ApplicationException, IOException {

        setInputSheetFile(inputFile);

        logger.info("Reading input file '{}'...", inputSheetFile.getAbsolutePath());


        fileExtension = FilenameUtils.getExtension(inputSheetFile.getName());
        if (fileExtension.equalsIgnoreCase("xlsx")) {
            workbook = createWorkbook(openOPCPackage(inputSheetFile));
        } else {
            workbook = createWorkbook(openNPOIFSFileSystemPackage(inputSheetFile));
        }
        List<Sheet> sheetsList = new ArrayList<>();
        for(int i=0; i<workbook.getNumberOfSheets(); i++) {
            sheetsList.add(workbook.getSheetAt(i));
        }
        sheets = sheetsList.toArray(new Sheet[0]);
        if (sheets.length <= 0) {
            throw new ApplicationException("Workbook does not contain sheets");
        }

        return sheets;

    }


    private static OPCPackage openOPCPackage(File file)
            throws ApplicationException {
        try {
            return OPCPackage.open(file);
        } catch (InvalidFormatException e) {
            throw new ApplicationException("Unable to open OPC package from " + file.getAbsolutePath(), e);
        }
    }


    private static POIFSFileSystem openNPOIFSFileSystemPackage(File file) throws ApplicationException {
        try {
            return new POIFSFileSystem(file);
        } catch (IOException e) {
            throw new ApplicationException("Unable to open NPOIFSFileSystem package from " + file.getAbsolutePath(), e);
        }
    }


    private static Workbook createWorkbook(OPCPackage pkg) throws ApplicationException {
        try {
            return new XSSFWorkbook(pkg);
        } catch (IOException e) {
            throw new ApplicationException("Unable to create workbook from OPC package", e);
        }
    }


    private static Workbook createWorkbook(POIFSFileSystem pkg) throws ApplicationException {
        try {
            return new HSSFWorkbook(pkg);
        } catch (IOException e) {
            throw new ApplicationException("Unable to create workbook from NPOIFSFileSystem package", e);
        }
    }

    public static Map<String, Integer> readSheetHeader(Sheet sheet) {
        // first row is always considered as header
        Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        if(firstRow == null) {
            throw new IllegalArgumentException("Sheet is empty");
        }

        Map<String, Integer> header = new LinkedHashMap<String, Integer>();

        Iterator<Cell> it = firstRow.cellIterator();

        while(it.hasNext()) {
            Cell cell = it.next();

            if(cell.getCellType() == CellType.STRING) {
                String name = cell.getStringCellValue();
                if(! name.equals("")) {
                    //name = name.toUpperCase();
                    if(! header.containsKey(name)) {
                        header.put(name, cell.getColumnIndex());
                    } else {
                        logger.warn("Ignoring duplicate header name {} at ({}, {})",
                                name, cell.getRowIndex(), cell.getColumnIndex());
                    }
                }
            } else {
                logger.warn("Ignoring header cell with non-string type {} at ({}, {})",
                        cell.getCellType(), cell.getRowIndex(), cell.getColumnIndex());
            }

        }
        return header;
    }

}
