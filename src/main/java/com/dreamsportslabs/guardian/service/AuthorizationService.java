package com.dreamsportslabs.guardian.service;

import com.dreamsportslabs.guardian.config.tenant.TenantConfig;
import com.dreamsportslabs.guardian.dao.CodeDao;
import com.dreamsportslabs.guardian.dao.RefreshTokenDao;
import com.dreamsportslabs.guardian.dao.model.RefreshTokenModel;
import com.dreamsportslabs.guardian.dto.request.MetaInfo;
import com.dreamsportslabs.guardian.dto.request.V1CodeTokenExchangeRequestDto;
import com.dreamsportslabs.guardian.dto.response.TokenResponseDto;
import com.dreamsportslabs.guardian.registry.Registry;
import com.dreamsportslabs.guardian.utils.Utils;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dreamsportslabs.guardian.constant.Constants.TOKEN_TYPE;
import static com.dreamsportslabs.guardian.constant.Constants.USERID;
import static com.dreamsportslabs.guardian.exception.ErrorEnum.INVALID_CODE;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AuthorizationService {
  private final Registry registry;
  private final TokenIssuer tokenIssuer;

  private final RefreshTokenDao refreshTokenDao;
  private final CodeDao codeDao;

  private Single<TokenResponseDto> generateTokens(
      JsonObject user, MetaInfo metaInfo, String tenantId) {
    TenantConfig config = registry.get(tenantId, TenantConfig.class);
    String refreshToken = tokenIssuer.generateRefreshToken();
    Long iat = System.currentTimeMillis() / 1000;
    return Single.zip(
            tokenIssuer.generateAccessToken(
                user.getString(USERID), iat, getRftId(refreshToken), config),
            tokenIssuer.generateIdToken(user, iat, config),
            (accessToken, idToken) ->
                new TokenResponseDto(
                    accessToken,
                    refreshToken,
                    idToken,
                    TOKEN_TYPE,
                    config.getTokenConfig().getAccessTokenExpiry(),
                    user.getBoolean("isNewUser", false)))
        .flatMap(
            dto ->
                refreshTokenDao
                    .saveRefreshToken(getRefreshTokenDto(refreshToken, user, iat, metaInfo, config))
                    .andThen(Single.just(dto)));
  }

  private RefreshTokenModel getRefreshTokenDto(
      String refreshToken, JsonObject user, Long iat, MetaInfo metaInfo, TenantConfig config) {
    return RefreshTokenModel.builder()
        .tenantId(config.getTenantId())
        .userId(user.getString(USERID))
        .refreshToken(refreshToken)
        .refreshTokenExp(iat + config.getTokenConfig().getRefreshTokenExpiry())
        .deviceName(metaInfo.getDeviceName())
        .ip(metaInfo.getIp())
        .location(metaInfo.getLocation())
        .source(metaInfo.getSource())
        .build();
  }


  private String getRftId(String refreshToken) {
    return Utils.getMd5Hash(refreshToken);
  }


  public Single<TokenResponseDto> codeTokenExchange(
      V1CodeTokenExchangeRequestDto dto, String tenantId) {
    return codeDao
        .getCode(dto.getCode(), tenantId)
        .switchIfEmpty(Single.error(INVALID_CODE.getException()))
        .flatMap(
            model -> generateTokens(new JsonObject(model.getUser()), model.getMetaInfo(), tenantId))
        .doOnSuccess(res -> codeDao.deleteCode(dto.getCode(), tenantId).subscribe());
  }

}
