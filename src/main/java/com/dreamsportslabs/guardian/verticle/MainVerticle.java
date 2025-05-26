package com.dreamsportslabs.guardian.verticle;

import com.dreamsportslabs.guardian.client.MysqlClient;
import com.dreamsportslabs.guardian.client.impl.MysqlClientImpl;
import com.dreamsportslabs.guardian.utils.ConfigUtil;
import com.dreamsportslabs.guardian.utils.SharedDataUtils;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static com.dreamsportslabs.guardian.constant.Constants.PORT;

@Slf4j
public class MainVerticle extends AbstractVerticle {
  private MysqlClient mysqlClient;
  private JsonObject config;

  @Override
  public Completable rxStart() {
    return ConfigUtil.getConfigRetriever(vertx)
        .rxGetConfig()
        .map(
            config -> {
              this.config = config;
              return config;
            })
        .flatMapCompletable(this::initializeClients)
        .andThen(
            vertx.rxDeployVerticle(
                () ->
                    new RestVerticle(
                        new HttpServerOptions().setPort(Integer.parseInt(config.getString(PORT)))),
                new DeploymentOptions().setInstances(getNumOfCores())))
        .ignoreElement();
  }

  private Integer getNumOfCores() {
    return CpuCoreSensor.availableProcessors();
  }


  private Completable initializeClients(JsonObject config) {
    this.mysqlClient = new MysqlClientImpl(this.vertx, config);
    SharedDataUtils.put(vertx.getDelegate(), this.mysqlClient);
    return mysqlClient.rxConnect();
  }

}
