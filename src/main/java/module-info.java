module jfxWebTool {
  requires java.base;
  requires java.net.http;
  requires javafx.controls;
  requires javafx.fxml;
  opens oiwa.tsuyoshi.jfxwebtool to javafx.graphics;
  opens oiwa.tsuyoshi.jfxwebtool.controller to javafx.fxml;
}