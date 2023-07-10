package com.budilov.cdk.textract;

import com.budilov.cdk.textract.util.AppProperties;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.ssm.ParameterTier;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

public class DDBTableStack extends Stack {
    public static final int READ_CAPACITY = 5;
    public static final int WRITE_CAPACITY = 5;
    public static final String TABLE_NAME = AppProperties.DDB_NAME;
    public static final String PARTITION_ID = "id";
    public static final String SORT_KEY = "sk";

    final public Table ddbTable;

    public DDBTableStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public DDBTableStack(final Construct scope, final String id, final StackProps props) {

        super(scope, id, props);

        ddbTable = new Table(this, TABLE_NAME, TableProps.builder()
                .tableName(TABLE_NAME)
                .readCapacity(READ_CAPACITY)
                .writeCapacity(WRITE_CAPACITY)
                .partitionKey(Attribute.builder()
                        .name(PARTITION_ID)
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name(SORT_KEY)
                        .type(AttributeType.STRING)
                        .build())
                .build());

        StringParameter.Builder.create(this, TABLE_NAME + "Table")
                .allowedPattern(".*")
                .description(TABLE_NAME)
                .parameterName("ddbTable")
                .stringValue(TABLE_NAME)
                .tier(ParameterTier.STANDARD)
                .build();
    }

}
