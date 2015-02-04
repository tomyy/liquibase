package liquibase.actionlogic.core;

import liquibase.Scope;
import liquibase.action.Action;
import liquibase.action.core.InsertOrUpdateDataAction;
import liquibase.actionlogic.AbstractActionLogic;
import liquibase.actionlogic.ActionResult;
import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.exception.ActionPerformException;
import liquibase.exception.ValidationErrors;
import liquibase.statement.core.InsertOrUpdateStatement;
import liquibase.structure.core.Column;
import liquibase.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class InsertOrUpdateLogic extends AbstractActionLogic {

    @Override
    protected Class<? extends Action> getSupportedAction() {
        return InsertOrUpdateDataAction.class;
    }

    protected abstract String getRecordCheck(InsertOrUpdateStatement insertOrUpdateStatement, Database database, String whereClause);

    protected abstract String getElse(Database database);

    protected String getPostUpdateStatements(Database database) {
        return "";
    }

    @Override
    public ValidationErrors validate(Action action, Scope scope) {
        ValidationErrors errors = super.validate(action, scope)
                .checkForRequiredField(InsertOrUpdateDataAction.Attr.tableName, action)
                .checkForRequiredField(InsertOrUpdateDataAction.Attr.columnNames, action)
                .checkForRequiredField(InsertOrUpdateDataAction.Attr.primaryKeyColumnNames, action);

        if (action.get(InsertOrUpdateDataAction.Attr.columnNames, new ArrayList()).size() != action.get(InsertOrUpdateDataAction.Attr.columnValues, new ArrayList()).size()) {
            errors.addError("InsertOrUpdateData columnNames and columnValues must contain the same number of values");
        }

        return errors;

    }

    @Override
    public ActionResult execute(Action action, Scope scope) throws ActionPerformException {
        throw new ActionPerformException("TODO");
    }

    protected String getWhereClause(Action action, Scope scope) {
        Database database = scope.get(Scope.Attr.database, Database.class);
        StringBuffer where = new StringBuffer();

        String[] pkColumns = action.get(InsertOrUpdateDataAction.Attr.primaryKeyColumnNames, String[].class);
        String[] columnNames = action.get(InsertOrUpdateDataAction.Attr.columnNames, String[].class);
        Object[] newValues = action.get(InsertOrUpdateDataAction.Attr.primaryKeyColumnNames, Object[].class);

        List<String> whereClauses = new ArrayList<>();
        for (String thisPkColumn : pkColumns) {
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(thisPkColumn)) {
                    Object newValue = newValues[i];
                    String thisClause = database.escapeObjectName(thisPkColumn, Column.class);
                    if ((newValue == null || newValue.toString().equalsIgnoreCase("NULL"))) {
                        thisClause += " IS NULL";
                    } else {
                        thisClause +=  " = " + DataTypeFactory.getInstance().fromObject(newValue, database).objectToSql(newValue, database);
                    }
                    whereClauses.add(thisClause);
                }
            }
        }

        return StringUtils.join(whereClauses, " AND ");
    }

//    protected String getInsertStatement(Action action, Scope scope) {
//        StringBuffer insertBuffer = new StringBuffer();
//        InsertDataLogic insert = new InsertDataLogic();
//        Sql[] insertSql = insert.generateSql(insertOrUpdateStatement, database, sqlGeneratorChain);
//
//        for (Sql s : insertSql) {
//            insertBuffer.append(s.toSql());
//            insertBuffer.append(";");
//        }
//
//        insertBuffer.append("\n");
//
//        return insertBuffer.toString();
//    }

//    /**
//     * @return the update statement, if there is nothing to update return null
//     */
//    protected String getUpdateStatement(Action action, Scope scope) throws LiquibaseException {
//
//        StringBuffer updateSqlString = new StringBuffer();
//
//        UpdateDataLogic update = new UpdateDataLogic();
//        UpdateStatement updateStatement = new UpdateStatement(
//                insertOrUpdateStatement.getCatalogName(),
//                insertOrUpdateStatement.getSchemaName(),
//                insertOrUpdateStatement.getTableName());
//        updateStatement.setWhereClause(whereClause + ";\n");
//
//        String[] pkFields = insertOrUpdateStatement.getPrimaryKey().split(",");
//        HashSet<String> hashPkFields = new HashSet<String>(Arrays.asList(pkFields));
//        for (String columnKey : insertOrUpdateStatement.getColumnValues().keySet()) {
//            if (!hashPkFields.contains(columnKey)) {
//                updateStatement.addNewColumnValue(columnKey, insertOrUpdateStatement.getColumnValue(columnKey));
//            }
//        }
//        // this isn't very elegant but the code fails above without any columns to update
//        if (updateStatement.getNewColumnValues().isEmpty()) {
//            throw new LiquibaseException("No fields to update in set clause");
//        }
//
//        Sql[] updateSql = update.generateSql(updateStatement, database, sqlGeneratorChain);
//
//        for (Sql s : updateSql) {
//            updateSqlString.append(s.toSql());
//            updateSqlString.append(";");
//        }
//
//        updateSqlString.deleteCharAt(updateSqlString.lastIndexOf(";"));
//        updateSqlString.append("\n");
//
//        return updateSqlString.toString();
//
//    }

//    @Override
//    public Sql[] generateSql(Action action, Scope scope) {
//        StringBuffer completeSql = new StringBuffer();
//        String whereClause = getWhereClause(insertOrUpdateStatement, database);
//        if (!insertOrUpdateStatement.getOnlyUpdate()) {
//            completeSql.append(getRecordCheck(insertOrUpdateStatement, database, whereClause));
//
//            completeSql.append(getInsertStatement(insertOrUpdateStatement, database, sqlGeneratorChain));
//        }
//        try {
//
//            String updateStatement = getUpdateStatement(insertOrUpdateStatement, database, whereClause, sqlGeneratorChain);
//
//            if (!insertOrUpdateStatement.getOnlyUpdate()) {
//                completeSql.append(getElse(database));
//            }
//
//            completeSql.append(updateStatement);
//
//        } catch (LiquibaseException e) {
//        }
//
//        if (!insertOrUpdateStatement.getOnlyUpdate()) {
//            completeSql.append(getPostUpdateStatements(database));
//        }
//
//        return new Sql[]{
//                new UnparsedSql(completeSql.toString(), "", getAffectedTable(insertOrUpdateStatement))
//        };
//    }
}