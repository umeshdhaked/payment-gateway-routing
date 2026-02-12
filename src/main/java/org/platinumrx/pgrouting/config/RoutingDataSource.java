package org.platinumrx.pgrouting.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceContextHolder.DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
        return dataSourceType != null ? dataSourceType : DataSourceContextHolder.DataSourceType.WRITE;
    }
}
