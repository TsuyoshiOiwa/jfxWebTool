package oiwa.tsuyoshi.jfxwebtool.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Objects;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class RequestFormController {
  @FXML
  private Label hostnameLabel;
  @FXML
  private TextField hostnameTxField;
  @FXML
  private Label paramLabel;
  @FXML
  private TextField paramTxField;
  @FXML
  private Label methodLabel;
  @FXML
  private RadioButton postRadio;
  @FXML
  private RadioButton getRadio;
  @FXML
  private TextArea headerTxArea;
  @FXML
  private Button sendButton;
  @FXML
  private Button resultButton;
  @FXML
  private ToggleGroup methodGroup;

  private Service<HttpResponse<String>> getResponseService;

  private HttpResponse<String> lastResponse;

  @FXML
  private void initialize() {
    getResponseService = new GetResponseService();

    getRadio.setUserData("GET");
    postRadio.setUserData("POST");
    hostnameTxField.disableProperty().bind(getResponseService.runningProperty());
    paramTxField.disableProperty().bind(getResponseService.runningProperty());
    sendButton.disableProperty().bind(getResponseService.runningProperty());
    resultButton.disableProperty().bind(getResponseService.runningProperty());
    headerTxArea.setText("value");
  }

  @FXML
  void send(ActionEvent event) {
    getResponseService.start();
  }

  @FXML
  void showResult(ActionEvent event) {
    String body = "empty";
    if(lastResponse != null){
      body = lastResponse.body();
    }

    headerTxArea.setText(body);
  }

  private class GetResponseService extends Service<HttpResponse<String>> {

    private HttpClient client;

    public GetResponseService() {
      this.client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(5))
          .followRedirects(Redirect.NORMAL).build();

    }

    @Override
    protected Task<HttpResponse<String>> createTask() {
      return new GetResponseTask(client);
    }

    @Override
    protected void succeeded(){
      HttpResponse<String> result = this.valueProperty().getValue();
      headerTxArea.setText(result.headers().toString());
      lastResponse = result;
      reset();
    }

    @Override
    protected void failed(){
      StringWriter sw = new StringWriter();
      PrintWriter writer = new PrintWriter(sw);
      exceptionProperty().get().printStackTrace(writer);
      writer.flush();
      headerTxArea.setText(sw.toString());
      reset();
    }

  }

  private class GetResponseTask extends Task<HttpResponse<String>> {

    private HttpClient client;

    private GetResponseTask(HttpClient client) {
      Objects.requireNonNull(client);
      this.client = client;
    }

    @Override
    protected HttpResponse<String> call() throws URISyntaxException, IOException, InterruptedException {
      String hostname = hostnameTxField.textProperty().get();
      String param = paramTxField.textProperty().get();
      var methodList = methodGroup.getToggles();
      String method = (String) methodList.stream().filter(Toggle::isSelected).findFirst().orElseThrow().getUserData();

      URI uri = new URI(hostname + "?" + param);

      HttpRequest request = HttpRequest.newBuilder().uri(uri).method(method, BodyPublishers.noBody()).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response;
    }
  }
}