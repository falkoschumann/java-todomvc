/*
 * TodoMVC - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.frontend;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InfoView {
  @FXML private ImageView icon;
  @FXML private Label title;
  @FXML private Label version;
  @FXML private Label copyright;

  private final InfoViewModel viewModel = ViewModelFactory.getInfoViewModel();

  public static InfoView create(Stage owner) {
    var factory = new ViewControllerFactory(InfoView.class);

    var stage = new Stage();
    stage.initOwner(owner);
    stage.initStyle(StageStyle.UTILITY);
    stage.setTitle("Info");
    stage.setScene(new Scene(factory.getView()));
    stage.setResizable(false);

    return factory.getController();
  }

  @FXML
  private void initialize() {
    viewModel
        .iconUrlProperty()
        .addListener(
            observable -> icon.setImage(new Image(viewModel.iconUrlProperty().get(), true)));
    title.textProperty().bind(viewModel.titleProperty());
    version.textProperty().bind(Bindings.concat("Version ", viewModel.versionProperty()));
    copyright.textProperty().bind(viewModel.copyrightProperty());
  }

  public void run() {
    getWindow().show();
  }

  private Stage getWindow() {
    return (Stage) icon.getScene().getWindow();
  }
}
