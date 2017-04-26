package excel2db.service.impl;

import excel2db.excel2db;
import excel2db.service.CreateTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateTablePostgresImpl implements CreateTable {

    public void createTable() throws SQLException
    {
        String sqlTableDropStatement = "DROP TABLE IF EXISTS excel2db;";


        StringBuilder sqlTableCreateStatement = new StringBuilder();

        sqlTableCreateStatement.append("CREATE TABLE excel2db(");


        for (String headerColumnName : excel2db.sheetEntityManager.header.keySet()) {
            sqlTableCreateStatement.append("\"" + headerColumnName + "\"" + " text, ");
        }


        sqlTableCreateStatement.setLength(sqlTableCreateStatement.length() - 2);

        sqlTableCreateStatement.append(") WITH (OIDS=FALSE);");


        PreparedStatement pstmtDrop = excel2db.connection.prepareStatement(sqlTableDropStatement);
        pstmtDrop.execute();

        PreparedStatement pstmtCreate = excel2db.connection.prepareStatement(sqlTableCreateStatement.toString());
        pstmtCreate.execute();

    }

}
