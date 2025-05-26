package com.dreamsportslabs.guardian.constant;

import com.google.common.collect.ImmutableList;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

public final class Constants {
  public static final String TENANT_ID = "tenant-id";
  public static final String USERID = "userId";

  public static final String EXPIRY_OPTION_REDIS = "EX";

  // Application config
  public static final String PORT = "port";
  public static final String MYSQL_WRITER_HOST = "mysql_writer_host";
  public static final String MYSQL_READER_HOST = "mysql_reader_host";
  public static final String MYSQL_DATABASE = "mysql_database";
  public static final String MYSQL_USER = "mysql_user";
  public static final String MYSQL_PASSWORD = "mysql_password";
  public static final String MYSQL_WRITER_MAX_POOL_SIZE = "mysql_writer_max_pool_size";
  public static final String MYSQL_READER_MAX_POOL_SIZE = "mysql_reader_max_pool_size";
  public static final String REDIS_HOST = "redis_host";
  public static final String REDIS_PORT = "redis_port";
  public static final String REDIS_TYPE = "redis_type";
  public static final String HTTP_CONNECT_TIMEOUT = "http_connect_timeout";
  public static final String HTTP_READ_TIMEOUT = "http_read_timeout";
  public static final String HTTP_WRITE_TIMEOUT = "http_write_timeout";

  // JWT CLAIMS
  public static final String JWT_CLAIMS_ISS = "iss";
  public static final String JWT_CLAIMS_SUB = "sub";
  public static final String JWT_CLAIMS_IAT = "iat";
  public static final String JWT_CLAIMS_EXP = "exp";
  public static final String JWT_CLAIMS_RFT_ID = "rft_id";

  public static final ArrayList<String> prohibitedForwardingHeaders = new ArrayList<>();

  static {
    prohibitedForwardingHeaders.add("CONTENT-LENGTH");
    prohibitedForwardingHeaders.add("ACCEPT");
    prohibitedForwardingHeaders.add("CONNECTION");
    prohibitedForwardingHeaders.add("HOST");
    prohibitedForwardingHeaders.add("CONTENT-TYPE");
    prohibitedForwardingHeaders.add("USER-AGENT");
    prohibitedForwardingHeaders.add("ACCEPT-ENCODING");
  }


  public static final String CACHE_KEY_CODE = "CODE";


  public static final String TOKEN_TYPE = "Bearer";

}
