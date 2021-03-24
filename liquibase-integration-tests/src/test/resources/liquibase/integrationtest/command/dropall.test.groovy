package liquibase.integrationtest.command

import liquibase.change.ColumnConfig
import liquibase.change.core.CreateTableChange
import liquibase.integrationtest.command.LiquibaseCommandTest
import liquibase.integrationtest.setup.SetupDatabaseStructure

[
    new LiquibaseCommandTest.Spec(
        command: ["dropAll"],

        setup: [
                new SetupDatabaseStructure([
                [
                    new CreateTableChange(
                        tableName: "FirstTable",
                        columns: [
                            ColumnConfig.fromName("FirstColumn")
                                        .setType("VARCHAR(255)")
                        ]
                    )
                ] as SetupDatabaseStructure.Entry,
                [
                    new CreateTableChange(
                        tableName: "SecondTable",
                        columns: [
                            ColumnConfig.fromName("SecondColumn")
                                        .setType("VARCHAR(255)")
                        ]
                    )
                ] as SetupDatabaseStructure.Entry
            ])
        ],
        expectedOutput: [
            "",
        ],
        expectedResults: [
            statusCode: 0
        ]
    )

] as LiquibaseCommandTest.Spec[]