/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-11-22 19:59:01 UTC)
 * on 2013-11-24 at 14:33:58 UTC 
 * Modify at your own risk.
 */

package com.example.testmobiletaskclient.taskendpoint;

/**
 * Service definition for Taskendpoint (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link TaskendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Taskendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.16.0-rc of the taskendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://testtasks01mobileclient.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "taskendpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Taskendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Taskendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "getTask".
   *
   * This request holds the parameters needed by the the taskendpoint server.  After setting any
   * optional parameters, call the {@link GetTask#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public GetTask getTask(java.lang.String id) throws java.io.IOException {
    GetTask result = new GetTask(id);
    initialize(result);
    return result;
  }

  public class GetTask extends TaskendpointRequest<com.example.testmobiletaskclient.taskendpoint.model.Task> {

    private static final String REST_PATH = "task/{id}";

    /**
     * Create a request for the method "getTask".
     *
     * This request holds the parameters needed by the the taskendpoint server.  After setting any
     * optional parameters, call the {@link GetTask#execute()} method to invoke the remote operation.
     * <p> {@link
     * GetTask#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetTask(java.lang.String id) {
      super(Taskendpoint.this, "GET", REST_PATH, null, com.example.testmobiletaskclient.taskendpoint.model.Task.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetTask setAlt(java.lang.String alt) {
      return (GetTask) super.setAlt(alt);
    }

    @Override
    public GetTask setFields(java.lang.String fields) {
      return (GetTask) super.setFields(fields);
    }

    @Override
    public GetTask setKey(java.lang.String key) {
      return (GetTask) super.setKey(key);
    }

    @Override
    public GetTask setOauthToken(java.lang.String oauthToken) {
      return (GetTask) super.setOauthToken(oauthToken);
    }

    @Override
    public GetTask setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetTask) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetTask setQuotaUser(java.lang.String quotaUser) {
      return (GetTask) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetTask setUserIp(java.lang.String userIp) {
      return (GetTask) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String id;

    /**

     */
    public java.lang.String getId() {
      return id;
    }

    public GetTask setId(java.lang.String id) {
      this.id = id;
      return this;
    }

    @Override
    public GetTask set(String parameterName, Object value) {
      return (GetTask) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertTask".
   *
   * This request holds the parameters needed by the the taskendpoint server.  After setting any
   * optional parameters, call the {@link InsertTask#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.example.testmobiletaskclient.taskendpoint.model.Task}
   * @return the request
   */
  public InsertTask insertTask(com.example.testmobiletaskclient.taskendpoint.model.Task content) throws java.io.IOException {
    InsertTask result = new InsertTask(content);
    initialize(result);
    return result;
  }

  public class InsertTask extends TaskendpointRequest<com.example.testmobiletaskclient.taskendpoint.model.Task> {

    private static final String REST_PATH = "task";

    /**
     * Create a request for the method "insertTask".
     *
     * This request holds the parameters needed by the the taskendpoint server.  After setting any
     * optional parameters, call the {@link InsertTask#execute()} method to invoke the remote
     * operation. <p> {@link
     * InsertTask#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.example.testmobiletaskclient.taskendpoint.model.Task}
     * @since 1.13
     */
    protected InsertTask(com.example.testmobiletaskclient.taskendpoint.model.Task content) {
      super(Taskendpoint.this, "POST", REST_PATH, content, com.example.testmobiletaskclient.taskendpoint.model.Task.class);
    }

    @Override
    public InsertTask setAlt(java.lang.String alt) {
      return (InsertTask) super.setAlt(alt);
    }

    @Override
    public InsertTask setFields(java.lang.String fields) {
      return (InsertTask) super.setFields(fields);
    }

    @Override
    public InsertTask setKey(java.lang.String key) {
      return (InsertTask) super.setKey(key);
    }

    @Override
    public InsertTask setOauthToken(java.lang.String oauthToken) {
      return (InsertTask) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertTask setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertTask) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertTask setQuotaUser(java.lang.String quotaUser) {
      return (InsertTask) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertTask setUserIp(java.lang.String userIp) {
      return (InsertTask) super.setUserIp(userIp);
    }

    @Override
    public InsertTask set(String parameterName, Object value) {
      return (InsertTask) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listTask".
   *
   * This request holds the parameters needed by the the taskendpoint server.  After setting any
   * optional parameters, call the {@link ListTask#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public ListTask listTask() throws java.io.IOException {
    ListTask result = new ListTask();
    initialize(result);
    return result;
  }

  public class ListTask extends TaskendpointRequest<com.example.testmobiletaskclient.taskendpoint.model.CollectionResponseTask> {

    private static final String REST_PATH = "task";

    /**
     * Create a request for the method "listTask".
     *
     * This request holds the parameters needed by the the taskendpoint server.  After setting any
     * optional parameters, call the {@link ListTask#execute()} method to invoke the remote operation.
     * <p> {@link
     * ListTask#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListTask() {
      super(Taskendpoint.this, "GET", REST_PATH, null, com.example.testmobiletaskclient.taskendpoint.model.CollectionResponseTask.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ListTask setAlt(java.lang.String alt) {
      return (ListTask) super.setAlt(alt);
    }

    @Override
    public ListTask setFields(java.lang.String fields) {
      return (ListTask) super.setFields(fields);
    }

    @Override
    public ListTask setKey(java.lang.String key) {
      return (ListTask) super.setKey(key);
    }

    @Override
    public ListTask setOauthToken(java.lang.String oauthToken) {
      return (ListTask) super.setOauthToken(oauthToken);
    }

    @Override
    public ListTask setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListTask) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListTask setQuotaUser(java.lang.String quotaUser) {
      return (ListTask) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListTask setUserIp(java.lang.String userIp) {
      return (ListTask) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListTask setCursor(java.lang.String cursor) {
      this.cursor = cursor;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer limit;

    /**

     */
    public java.lang.Integer getLimit() {
      return limit;
    }

    public ListTask setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @Override
    public ListTask set(String parameterName, Object value) {
      return (ListTask) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeTask".
   *
   * This request holds the parameters needed by the the taskendpoint server.  After setting any
   * optional parameters, call the {@link RemoveTask#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public RemoveTask removeTask(java.lang.String id) throws java.io.IOException {
    RemoveTask result = new RemoveTask(id);
    initialize(result);
    return result;
  }

  public class RemoveTask extends TaskendpointRequest<Void> {

    private static final String REST_PATH = "task/{id}";

    /**
     * Create a request for the method "removeTask".
     *
     * This request holds the parameters needed by the the taskendpoint server.  After setting any
     * optional parameters, call the {@link RemoveTask#execute()} method to invoke the remote
     * operation. <p> {@link
     * RemoveTask#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected RemoveTask(java.lang.String id) {
      super(Taskendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public RemoveTask setAlt(java.lang.String alt) {
      return (RemoveTask) super.setAlt(alt);
    }

    @Override
    public RemoveTask setFields(java.lang.String fields) {
      return (RemoveTask) super.setFields(fields);
    }

    @Override
    public RemoveTask setKey(java.lang.String key) {
      return (RemoveTask) super.setKey(key);
    }

    @Override
    public RemoveTask setOauthToken(java.lang.String oauthToken) {
      return (RemoveTask) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveTask setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveTask) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveTask setQuotaUser(java.lang.String quotaUser) {
      return (RemoveTask) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveTask setUserIp(java.lang.String userIp) {
      return (RemoveTask) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String id;

    /**

     */
    public java.lang.String getId() {
      return id;
    }

    public RemoveTask setId(java.lang.String id) {
      this.id = id;
      return this;
    }

    @Override
    public RemoveTask set(String parameterName, Object value) {
      return (RemoveTask) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateTask".
   *
   * This request holds the parameters needed by the the taskendpoint server.  After setting any
   * optional parameters, call the {@link UpdateTask#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.example.testmobiletaskclient.taskendpoint.model.Task}
   * @return the request
   */
  public UpdateTask updateTask(com.example.testmobiletaskclient.taskendpoint.model.Task content) throws java.io.IOException {
    UpdateTask result = new UpdateTask(content);
    initialize(result);
    return result;
  }

  public class UpdateTask extends TaskendpointRequest<com.example.testmobiletaskclient.taskendpoint.model.Task> {

    private static final String REST_PATH = "task";

    /**
     * Create a request for the method "updateTask".
     *
     * This request holds the parameters needed by the the taskendpoint server.  After setting any
     * optional parameters, call the {@link UpdateTask#execute()} method to invoke the remote
     * operation. <p> {@link
     * UpdateTask#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.example.testmobiletaskclient.taskendpoint.model.Task}
     * @since 1.13
     */
    protected UpdateTask(com.example.testmobiletaskclient.taskendpoint.model.Task content) {
      super(Taskendpoint.this, "PUT", REST_PATH, content, com.example.testmobiletaskclient.taskendpoint.model.Task.class);
    }

    @Override
    public UpdateTask setAlt(java.lang.String alt) {
      return (UpdateTask) super.setAlt(alt);
    }

    @Override
    public UpdateTask setFields(java.lang.String fields) {
      return (UpdateTask) super.setFields(fields);
    }

    @Override
    public UpdateTask setKey(java.lang.String key) {
      return (UpdateTask) super.setKey(key);
    }

    @Override
    public UpdateTask setOauthToken(java.lang.String oauthToken) {
      return (UpdateTask) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateTask setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateTask) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateTask setQuotaUser(java.lang.String quotaUser) {
      return (UpdateTask) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateTask setUserIp(java.lang.String userIp) {
      return (UpdateTask) super.setUserIp(userIp);
    }

    @Override
    public UpdateTask set(String parameterName, Object value) {
      return (UpdateTask) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Taskendpoint}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Taskendpoint}. */
    @Override
    public Taskendpoint build() {
      return new Taskendpoint(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link TaskendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setTaskendpointRequestInitializer(
        TaskendpointRequestInitializer taskendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(taskendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
