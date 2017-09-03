package excel2db.service;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public interface GetFirstRow {

    public JSONObject getFirstRow (String tableName) throws SQLException, JSONException;

}
