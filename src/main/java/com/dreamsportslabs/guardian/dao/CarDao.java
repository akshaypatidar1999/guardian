package com.dreamsportslabs.guardian.dao;


import com.dreamsportslabs.guardian.client.MysqlClient;
import com.dreamsportslabs.guardian.dao.query.Query;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CarDao {
  private final MysqlClient mysqlClient;


  public Single<List<String>> getCars() {
    return mysqlClient
        .getReaderPool()
        .query(Query.GET_CARS)
        .rxExecute()
        .map(rows -> StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(rows.iterator(), Spliterator.ORDERED),
                false)
            .map(row -> row.getString("name"))
            .toList());
  }
}
