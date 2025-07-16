package com.dreamsportslabs.guardian.rest;


import com.dreamsportslabs.guardian.dao.CarDao;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Path("/cars")
public class Cars {

  final CarDao carDao;
  @GET
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  @Hidden
  public CompletionStage<List<String>> getCars() {
    return Single.fromFuture(carDao.getCars()).toCompletionStage();
  }
}