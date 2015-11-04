package liquibase.actionlogic.core.db2;

import liquibase.Scope;
import liquibase.action.core.CreateTableAction;
import liquibase.actionlogic.core.CreateTableLogic;
import liquibase.database.Database;
import liquibase.database.core.db2.DB2Database;
import liquibase.util.StringClauses;

public class CreateTableLogicDB2 extends CreateTableLogic {

    @Override
    protected Class<? extends Database> getRequiredDatabase() {
        return DB2Database.class;
    }

    @Override
    protected StringClauses generateSql(CreateTableAction action, Scope scope) {
        StringClauses clauses = super.generateSql(action, scope);

        String tablespace = action.table.tablespace;
        if (tablespace != null) {
            clauses.replace(Clauses.tablespace, "IN "+tablespace);
        }

        return clauses;
    }


}