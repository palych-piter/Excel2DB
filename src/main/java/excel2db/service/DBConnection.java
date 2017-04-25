
package excel2db.service;

public interface  DBConnection {

    public void establishDBConnection();

    interface CreateTable {
        public void createTable();
    }
}
