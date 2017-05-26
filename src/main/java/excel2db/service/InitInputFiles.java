package excel2db.service;

import java.io.File;
import java.io.IOException;

import excel2db.ApplicationException;
import org.apache.poi.ss.usermodel.Sheet;

public interface InitInputFiles {
    public Sheet initInputFiles(File inputFile) throws IOException, ApplicationException;
}

