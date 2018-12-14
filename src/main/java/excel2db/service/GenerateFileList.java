package excel2db.service;

import java.util.HashSet;

public interface GenerateFileList {
    public HashSet<String> getFileList();

    public HashSet<String> getSheetList();
}

