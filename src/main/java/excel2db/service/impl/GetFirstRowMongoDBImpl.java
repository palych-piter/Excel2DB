package excel2db.service.impl;

import com.mongodb.client.MongoCollection;
import excel2db.service.GetFirstRow;
import org.bson.Document;
import org.json.JSONArray;
import com.mongodb.util.JSON;
import org.json.JSONException;
import org.json.JSONObject;

import static excel2db.excel2db.mongoDB;

public class GetFirstRowMongoDBImpl implements GetFirstRow {

    public JSONObject getFirstRow(String tableName) throws JSONException {

        //get the collection
        MongoCollection<Document> mongoCollection = mongoDB.getCollection("test");
        //get the first document
        Document firstDoc = mongoCollection.find().first();

        JSON json =new JSON();
        String convertedJson = json.serialize(firstDoc);

        JSONObject returnValue = new JSONObject(convertedJson);

        return returnValue;

    }
}
