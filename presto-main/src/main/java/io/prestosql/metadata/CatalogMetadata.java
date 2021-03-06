/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.metadata;

import com.google.common.collect.ImmutableList;
import io.prestosql.Session;
import io.prestosql.connector.ConnectorId;
import io.prestosql.spi.connector.ConnectorMetadata;
import io.prestosql.spi.connector.ConnectorTransactionHandle;

import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

public class CatalogMetadata
{
    private static final String INFORMATION_SCHEMA_NAME = "information_schema";

    private final ConnectorId connectorId;
    private final ConnectorMetadata metadata;
    private final ConnectorTransactionHandle transactionHandle;

    private final ConnectorId informationSchemaId;
    private final ConnectorMetadata informationSchema;
    private final ConnectorTransactionHandle informationSchemaTransactionHandle;

    private final ConnectorId systemTablesId;
    private final ConnectorMetadata systemTables;
    private final ConnectorTransactionHandle systemTablesTransactionHandle;

    public CatalogMetadata(
            ConnectorId connectorId,
            ConnectorMetadata metadata,
            ConnectorTransactionHandle transactionHandle,
            ConnectorId informationSchemaId,
            ConnectorMetadata informationSchema,
            ConnectorTransactionHandle informationSchemaTransactionHandle,
            ConnectorId systemTablesId,
            ConnectorMetadata systemTables,
            ConnectorTransactionHandle systemTablesTransactionHandle)
    {
        this.connectorId = requireNonNull(connectorId, "connectorId is null");
        this.metadata = requireNonNull(metadata, "metadata is null");
        this.transactionHandle = requireNonNull(transactionHandle, "transactionHandle is null");
        this.informationSchemaId = requireNonNull(informationSchemaId, "informationSchemaId is null");
        this.informationSchema = requireNonNull(informationSchema, "informationSchema is null");
        this.informationSchemaTransactionHandle = requireNonNull(informationSchemaTransactionHandle, "informationSchemaTransactionHandle is null");
        this.systemTablesId = requireNonNull(systemTablesId, "systemTablesId is null");
        this.systemTables = requireNonNull(systemTables, "systemTables is null");
        this.systemTablesTransactionHandle = requireNonNull(systemTablesTransactionHandle, "systemTablesTransactionHandle is null");
    }

    public ConnectorId getConnectorId()
    {
        return connectorId;
    }

    public ConnectorMetadata getMetadata()
    {
        return metadata;
    }

    public ConnectorMetadata getMetadataFor(ConnectorId connectorId)
    {
        if (connectorId.equals(this.connectorId)) {
            return metadata;
        }
        if (connectorId.equals(informationSchemaId)) {
            return informationSchema;
        }
        if (connectorId.equals(systemTablesId)) {
            return systemTables;
        }
        throw new IllegalArgumentException("Unknown connector id: " + connectorId);
    }

    public ConnectorTransactionHandle getTransactionHandleFor(ConnectorId connectorId)
    {
        if (connectorId.equals(this.connectorId)) {
            return transactionHandle;
        }
        if (connectorId.equals(informationSchemaId)) {
            return informationSchemaTransactionHandle;
        }
        if (connectorId.equals(systemTablesId)) {
            return systemTablesTransactionHandle;
        }
        throw new IllegalArgumentException("Unknown connector id: " + connectorId);
    }

    public ConnectorId getConnectorId(Session session, QualifiedObjectName table)
    {
        if (table.getSchemaName().equals(INFORMATION_SCHEMA_NAME)) {
            return informationSchemaId;
        }

        if (systemTables.getTableHandle(session.toConnectorSession(systemTablesId), table.asSchemaTableName()) != null) {
            return systemTablesId;
        }

        return connectorId;
    }

    public List<ConnectorId> listConnectorIds()
    {
        return ImmutableList.of(informationSchemaId, systemTablesId, connectorId);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("connectorId", connectorId)
                .toString();
    }
}
