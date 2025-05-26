package com.dreamsportslabs.guardian.dao;

import com.dreamsportslabs.guardian.client.MysqlClient;
import com.dreamsportslabs.guardian.dao.model.RefreshTokenModel;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.dreamsportslabs.guardian.dao.query.RefreshTokenSql.SAVE_REFRESH_TOKEN;
import static com.dreamsportslabs.guardian.exception.ErrorEnum.INTERNAL_SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__({@Inject}))
public class RefreshTokenDao {
  private final MysqlClient mysqlClient;

  public Completable saveRefreshToken(RefreshTokenModel refreshTokenModel) {
    return mysqlClient
        .getWriterPool()
        .preparedQuery(SAVE_REFRESH_TOKEN)
        .rxExecute(
            Tuple.tuple(
                Arrays.asList(
                    refreshTokenModel.getTenantId(),
                    refreshTokenModel.getUserId(),
                    refreshTokenModel.getRefreshToken(),
                    refreshTokenModel.getRefreshTokenExp(),
                    refreshTokenModel.getSource(),
                    refreshTokenModel.getDeviceName(),
                    refreshTokenModel.getLocation(),
                    refreshTokenModel.getIp())))
        .onErrorResumeNext(err -> Single.error(INTERNAL_SERVER_ERROR.getException(err)))
        .ignoreElement();
  }
}
