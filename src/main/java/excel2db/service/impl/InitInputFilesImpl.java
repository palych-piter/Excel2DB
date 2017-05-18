package excel2db.service.impl;

import java.io.File;
import java.io.IOException;

import com.ge.mdm.tools.common.ApplicationException;
import excel2db.service.InitInputFiles;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitInputFilesImpl implements InitInputFiles {

    //now implement reading from settings file

    private Workbook workbook;
    private File inputSheetFile;
    private String fileExtension;

    public static Sheet sheet;


    public File getInputSheetFile() {
        return inputSheetFile;
    }

    public void setInputSheetFile(File inputSheetFile) {
        this.inputSheetFile = inputSheetFile;
    }

    public static final Logger logger = LoggerFactory.getLogger(InitInputFilesImpl.class);

    @Override
    public Sheet initInputFiles(File inputFile)
            throws ApplicationException, IOException {

        setInputSheetFile(inputFile);

        logger.info("Reading input file '{}'...", inputSheetFile.getAbsolutePath());


        fileExtension = FilenameUtils.getExtension(inputSheetFile.getName());
        if (fileExtension.equalsIgnoreCase("xlsx")) {
            workbook = createWorkbook(openOPCPackage(inputSheetFile));
        } else {
            workbook = createWorkbook(openNPOIFSFileSystemPackage(inputSheetFile));
        }

        sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new ApplicationException("Workbook does not contain a sheet with index 0");
        }

        return sheet;

    }


    private static OPCPackage openOPCPackage(File file)
            throws ApplicationException {
        try {
            return OPCPackage.open(file);
        } catch (InvalidFormatException e) {
            throw new ApplicationException("Unable to open OPC package from " + file.getAbsolutePath(), e);
        }
    }


    private static NPOIFSFileSystem openNPOIFSFileSystemPackage(File file) throws ApplicationException {
        try {
            return new NPOIFSFileSystem(file);
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


    private static Workbook createWorkbook(NPOIFSFileSystem pkg) throws ApplicationException {
        try {
            return new HSSFWorkbook(pkg);
        } catch (IOException e) {
            throw new ApplicationException("Unable to create workbook from NPOIFSFileSystem package", e);
        }
    }


}
