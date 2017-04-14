package com.ge.mdm.tools.common;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Read Excel files in JPA way, considering sheet as a simple database table with columns and rows.
 * First row is assumed to be header containing column names, which could be mapped to JPA @Entity.
 *
 * @author Stanislav Mamontov
 */
public class SheetEntityManager implements EntityManager {

    private static final Logger logger = LoggerFactory.getLogger(SheetEntityManager.class);

    private Sheet sheet;

    /*
     * Mappings between header name and its column index
     */
    public Map<String, Integer> header;


    public SheetEntityManager(Sheet sheet) {
        requireNotNull(sheet);
        this.sheet = sheet;
        this.header = readSheetHeader(sheet);
    }


    /*
     * Insert a row into spread sheet, determined by entity's PK (row number).
     * This method assumes there's no row at such position yet.
     */
    @Override
    public synchronized void persist(Object entity) {
        int id = id(entity);
        Row row = sheet.createRow(id); // overrides old row if it's exists
        writeRow(entity, row, true);
    }

    /*
     * Update a row of a spread sheet, determined by entity's PK (row number).
     * Overwrited only cells whcih have different value than in entity object.
     */
    @Override
    public synchronized <T> T merge(T entity) {
        int id = id(entity);
        Row row = sheet.getRow(id);
        writeRow(entity, row, false);
        return entity;
    }

    /*
     * Removes a row at position determined by by entity's PK (row number)
     * from a sheet.
     */
    @Override
    public synchronized void remove(Object entity) {
        int id = id(entity);

        Row row = sheet.getRow(id);
        if(row != null) {
            sheet.removeRow(row);
        }
    }


    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {

        if(primaryKey instanceof Integer) {
            int id = (Integer) primaryKey;
            Row row = sheet.getRow(id);
            return readRow(entityClass, row);
        } else {
            throw new IllegalArgumentException("The primary key must be an of integer type");
        }
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public FlushModeType getFlushMode() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void refresh(Object entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean contains(Object entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Query createQuery(String qlString) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Query createNamedQuery(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void joinTransaction() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object getDelegate() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public EntityTransaction getTransaction() {
        throw new UnsupportedOperationException("Not implemented yet");
    }



    public synchronized <T> List<T> findAll(Class<T> entityClass) {
        List<T> list = new ArrayList<T>();
        Iterator<Row> it = sheet.rowIterator();

        if(it.hasNext()) {
            it.next(); // skip header
        }

        while(it.hasNext()) {
            Row row = it.next();
            T entity = readRow(entityClass, row);
            if(entity != null) {
                list.add(entity);
            }
        }
        return Collections.unmodifiableList(list);
    }


    //!~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static Map<String, Integer> readSheetHeader(Sheet sheet) {
        // first row is always considered as header
        Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        if(firstRow == null) {
            throw new IllegalArgumentException("Sheet is empty");
        }

        Map<String, Integer> header = new LinkedHashMap<String, Integer>();

        Iterator<Cell> it = firstRow.cellIterator();

        while(it.hasNext()) {
            Cell cell = it.next();

            if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
                String name = cell.getStringCellValue();
                if(! name.equals("")) {
                    name = name.toUpperCase();
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


    private static EntityMetadata getEntityMetadata(Class<?> entityClass) {
        try {
            return EntityMetadata.get(entityClass);
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("Unable to read metadata from " + entityClass, e);
        }
    }

    private static <T> T getInstance(Class<T> entityClass) {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Failed create an instance of " + entityClass, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed create an instance of " + entityClass, e);
        }
    }

    private static Object invokeMethod(Method m, Object entity, Object ... args) {
        try {
            return m.invoke(entity, args);
        } catch(InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to call " + m, e);
        } catch(IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to call " + m, e);
        }
    }


    private <T> T readRow(Class<T> entityClass, Row row) {
        T entity = getInstance(entityClass);
        EntityMetadata md = getEntityMetadata(entityClass);

        for(Map.Entry<String, EntityMetadata.ColumnMetadata> entry: md.getColumnMetadataMap().entrySet()) {
            String name = entry.getKey();
            if(! header.containsKey(name)) {
                continue;
            }
            int columnIndex = header.get(name);

            EntityMetadata.ColumnMetadata cmd = entry.getValue();
            String cellValue = getCellValueAsString(row.getCell(columnIndex));
            if(! cmd.isNullable() && (cellValue == null || cellValue.equals(""))) {
                // non-nullable field not set, consider row as empty
                return null;
            }

            Class<?> fieldType = cmd.getEntityField().getType();
            if(cellValue != null) {
                invokeMethod(cmd.getEntityFieldSetter(), entity, TypeConverter.convert(cellValue, fieldType));
            }
        }

        // finally set an id field and return entity
        invokeMethod(md.getIdColumnMetadata().getEntityFieldSetter(), entity, row.getRowNum());
        return entity;
    }




    private void writeRow(Object entity, Row row, boolean forceOverwrite) {

        Class<?> entityClass = entity.getClass();
        EntityMetadata md = getEntityMetadata(entityClass);

        for(Map.Entry<String, EntityMetadata.ColumnMetadata> entry:
                md.getColumnMetadataMap().entrySet()) {
            String name = entry.getKey();
            EntityMetadata.ColumnMetadata cmd = entry.getValue();

            if(! header.containsKey(name)) {
                continue;
            }
            int columnIndex = header.get(name);
            String fieldStringValue = toString(invokeMethod(cmd.getEntityFieldGetter(), entity));
            Cell cell = row.getCell(columnIndex);

            if(! forceOverwrite) {
                String cellValue = getCellValueAsString(cell);
                if(cellValue == null && fieldStringValue == null ||
                        cellValue != null && cellValue.equals(fieldStringValue)) {
                    // okay, do not overwrite
                    continue;
                }
            }

            if(cell == null) {
                cell = row.createCell(columnIndex);
            }
            cell.setCellValue(fieldStringValue);
        }

    }

    private static String toString(Object o) {
        if(o == null) {
            return null;
        }
        return o.toString();
    }


    private static void requireNotNull(Object reference) {
        if(reference == null) {
            throw new NullPointerException();
        }
    }


    private static String getCellValueAsString(Cell cell) {
        if(cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // XXX
                } else {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }




    private static int id(Object entity) {
        if(entity == null) {
            throw new NullPointerException();
        }
        Class<?> entityClass = entity.getClass();
        EntityMetadata md = getEntityMetadata(entityClass);
        Method getter = md.getIdColumnMetadata().getEntityFieldGetter();
        return (Integer) invokeMethod(getter, entity);
    }



    private static class EntityMetadata {

        private ColumnMetadata idColumnMetadata;
        private Map<String, ColumnMetadata> columnMetadataMap;



        private EntityMetadata(Class<?> entityType) throws IntrospectionException {
            if(entityType == null) {
                throw new NullPointerException();
            }

            // TODO check for @Entity annotation
            readColumnMetadata(entityType);
        }



        private void readColumnMetadata(Class<?> entityType) throws IntrospectionException {

            columnMetadataMap = new HashMap<String, ColumnMetadata>();

            BeanInfo beanInfo = Introspector.getBeanInfo(entityType, Object.class);

            Set<ColumnMetadata> set = new HashSet<ColumnMetadata>();

            for(PropertyDescriptor propertyDescriptor: beanInfo.getPropertyDescriptors()) {
                ColumnMetadata md = getColumnMetadata(entityType, propertyDescriptor);

                if(md == null) { // not annotated, ignore field
                    continue;
                }

                if(md.columnName.equals("")) {
                    if(idColumnMetadata != null) {
                        throw new IllegalArgumentException("Two annotated @Column fields are defined as identifier: " +
                                idColumnMetadata.entityField + " and " + md.entityField);
                    }
                    idColumnMetadata = md;
                } else {
                    String key = md.columnName.toUpperCase();
                    if(columnMetadataMap.containsKey(key)) {
                        throw new IllegalArgumentException("Two annotated @Column fields have the same name " +
                                key + ": " + md.entityField + " and " + columnMetadataMap.get(key).entityField);
                    }
                    columnMetadataMap.put(key, md);
                }

            }

            if(idColumnMetadata == null) {
                throw new IllegalArgumentException("Entity " + entityType + " must has an identifier");
            }

        }




        private static boolean checkPropertyTypeSupported(Class<?> type) {
            return TypeConverter.isTypeSupported(type);
        }


        private static ColumnMetadata getColumnMetadata(Class<?> entityType, PropertyDescriptor pd) {
            Field field = null;
            try {
                field = entityType.getDeclaredField(pd.getName());
            } catch (NoSuchFieldException e) {
                return null;
                //throw new RuntimeException("WTF?? " + e);
            }

            Id idAnnotation = getFieldAnnotation(field, Id.class);
            Column columnAnnotation = getFieldAnnotation(field, Column.class);

            if(idAnnotation != null && columnAnnotation != null) {
                throw new IllegalArgumentException("Field should not be annotated both with @Column and @Id: " + field);
            } else if (idAnnotation == null && columnAnnotation == null) {
                return null;
            }

            if(idAnnotation != null) {
                if(field.getType() != int.class) {
                    throw new IllegalArgumentException("@Id-annotated property must be of type int" + field);
                }
            } else {
                if (!checkPropertyTypeSupported(field.getType())) {
                    throw new IllegalArgumentException("@Column-annotated field type not supported: " + field);
                }
            }



            ColumnMetadata md = new ColumnMetadata();
            md.entityField = field;

            Method getter = pd.getReadMethod();
            if(getter == null) {
                throw new IllegalArgumentException("@Column-annotated field must has getter method: " + field);
            }

            md.entityFieldGetter = getter;

            Method setter = pd.getWriteMethod();
            if(setter == null) {
                throw new IllegalArgumentException("@Column-annotated field must has setter method: " + field);
            }
            md.entityFieldSetter = setter;

            String name;

            if(idAnnotation != null) {
                name = "";
                md.isNullable = false;
            } else { // columnAnnotation always not null here
                name = columnAnnotation.name();
                if(name == null) {
                    name = field.getName();
                }

                if(name.equals("")) {
                    throw new IllegalArgumentException("@Column(name) should not be empty for field " + field);
                }

                md.isNullable = columnAnnotation.nullable();
            }

            md.columnName = name;
            return md;
        }






        private static <T extends Annotation> T getFieldAnnotation(Field field, Class<T> annotationType) {
            for(Annotation annotation: field.getDeclaredAnnotations()) {
                if(annotation.annotationType() == annotationType) {
                    return annotationType.cast(annotation);
                }
            }
            return null;
        }


        public ColumnMetadata getIdColumnMetadata() {
            return idColumnMetadata;
        }


        public Map<String, ColumnMetadata> getColumnMetadataMap() {
            return Collections.unmodifiableMap(columnMetadataMap);
        }


        public ColumnMetadata getColumnMetadata(String name) {
            if(name == null) {
                throw new NullPointerException("name is null");
            } else if (name.equals("")) {
                throw new IllegalArgumentException("Empty name");
            }
            return columnMetadataMap.get(name.toUpperCase());
        }

        //!~--------------------------------------------------------------------


        private static Map<Class<?>, EntityMetadata> metadataStore = new HashMap<Class<?>, EntityMetadata>();


        /*
         * TODO thread safety!!
         */
        public static EntityMetadata get(Class<?> entityType) throws IntrospectionException {
            if(! metadataStore.containsKey(entityType)) {
                EntityMetadata md = new EntityMetadata(entityType);
                metadataStore.put(entityType, md);
                return md;
            }
            return metadataStore.get(entityType);
        }



        public static class ColumnMetadata {

            private String columnName;
            private boolean isNullable;
            private Field entityField;
            private Method entityFieldGetter;
            private Method entityFieldSetter;


            public String getColumnName() {
                return columnName;
            }

            public boolean isNullable() {
                return isNullable;
            }

            public Field getEntityField() {
                return entityField;
            }

            public Method getEntityFieldGetter() {
                return entityFieldGetter;
            }

            public Method getEntityFieldSetter() {
                return entityFieldSetter;
            }


            @Override
            public String toString() {
                return "ColumnMetadata{" +
                        "columnName='" + columnName + '\'' +
                        ", isNullable=" + isNullable +
                        ", entityField=" + entityField +
                        ", entityFieldGetter=" + entityFieldGetter +
                        ", entityFieldSetter=" + entityFieldSetter +
                        "}";
            }
        }

    }



    private static class TypeConverter {


        private static final Set<Class<?>> supportedTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] {

                String.class,
                Integer.class, int.class,
                Long.class, long.class,
                Short.class, short.class,
                Float.class, float.class,
                Double.class, double.class,
                BigInteger.class,
                BigDecimal.class

        }));


        public static boolean isTypeSupported(Class<?> type) {
            return supportedTypes.contains(type);
        }

        public static <T> T convert(String any, Class<T> toType) {
            if(any == null) {
                return null;
            }

            if(toType == String.class) {
                return toType.cast(any);
            } else if(toType == Integer.class || toType == int.class) {
                return toType.cast(Integer.parseInt(any));
            } else if(toType == Long.class || toType == long.class) {
                return toType.cast(Long.parseLong(any));
            } else if(toType == Short.class || toType == short.class) {
                return toType.cast(Short.parseShort(any));
            } else if(toType == Float.class || toType == float.class) {
                return toType.cast(Float.parseFloat(any));
            } else if(toType == Double.class || toType == double.class) {
                return toType.cast(Double.parseDouble(any));
            } else if(toType == Byte.class || toType == byte.class) {
                return toType.cast(Byte.parseByte(any));
            } else if(toType == BigInteger.class) {
                return toType.cast(new BigInteger(any));
            } else if(toType == BigDecimal.class) {
                return toType.cast(new BigDecimal(any));
            } else {
                throw new IllegalArgumentException("Type not supported " + toType);
            }
        }
    }



}
