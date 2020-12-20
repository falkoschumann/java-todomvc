/*
 * TodoMVC - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.frontend;

import de.muspellheim.todomvc.contract.data.Todo;
import de.muspellheim.todomvc.contract.messages.commands.ClearCompletedCommand;
import de.muspellheim.todomvc.contract.messages.commands.DestroyCommand;
import de.muspellheim.todomvc.contract.messages.commands.EditCommand;
import de.muspellheim.todomvc.contract.messages.commands.NewTodoCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleAllCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleCommand;
import de.muspellheim.todomvc.contract.messages.queries.TodosQuery;
import de.muspellheim.todomvc.contract.messages.queries.TodosQueryResult;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class TodoAppView extends VBox {
  @Getter @Setter private Consumer<NewTodoCommand> onNewTodoCommand;
  @Getter @Setter private Consumer<ToggleAllCommand> onToggleAllCommand;
  @Getter @Setter private Consumer<ToggleCommand> onToggleCommand;
  @Getter @Setter private Consumer<EditCommand> onEditCommand;
  @Getter @Setter private Consumer<DestroyCommand> onDestroyCommand;
  @Getter @Setter private Consumer<ClearCompletedCommand> onClearCompletedCommand;
  @Getter @Setter private Consumer<TodosQuery> onTodosQuery;

  private final CheckBox toggleAll;

  private final ListView<TodoModel> todoList;

  private final HBox footer;

  private final TextFlow todoCount;

  private final ToggleGroup filterGroup;
  private final ToggleButton allFilter;
  private final ToggleButton activeFilter;
  private final ToggleButton completedFilter;

  private final Button clearCompleted;

  private List<Todo> todos = List.of();

  public TodoAppView() {
    var title = new Label("todos");
    title.setStyle("-fx-text-fill: darksalmon;-fx-font-size: 40;");

    toggleAll = new CheckBox();
    toggleAll.setOnAction(
        event -> {
          var checked = toggleAll.isSelected();
          onToggleAllCommand.accept(new ToggleAllCommand(checked));
        });

    var newTodo = new TextField();
    newTodo.setPromptText("What needs to be done?");
    newTodo.setOnAction(
        e -> {
          var text = newTodo.getText();
          if (text.isBlank()) {
            return;
          }

          onNewTodoCommand.accept(new NewTodoCommand(text));
          newTodo.setText("");
        });
    HBox.setHgrow(newTodo, Priority.ALWAYS);

    HBox newTodoWrapper = new HBox(8);
    newTodoWrapper.setAlignment(Pos.CENTER_LEFT);
    newTodoWrapper.getChildren().addAll(toggleAll, newTodo);

    var header = new VBox(8);
    header.setPadding(new Insets(12, 12, 8, 12));
    header.setAlignment(Pos.CENTER);
    header.getChildren().addAll(title, newTodoWrapper);

    todoList = new ListView<>();
    todoList.setCellFactory(v -> new TodoListCell<>());
    VBox.setVgrow(todoList, Priority.ALWAYS);

    var count = new Text("0");
    count.setStyle("-fx-font-weight: bold;");

    var countSuffix = new Text(" items left");

    todoCount = new TextFlow(count, countSuffix);

    var spacerLeft = new Region();
    HBox.setHgrow(spacerLeft, Priority.ALWAYS);

    filterGroup = new ToggleGroup();

    allFilter = new ToggleButton("All");
    allFilter.setSelected(true);
    allFilter.setToggleGroup(filterGroup);
    allFilter.setOnAction(e -> updateTodoList());

    activeFilter = new ToggleButton("Active");
    activeFilter.setToggleGroup(filterGroup);
    activeFilter.setOnAction(e -> updateTodoList());

    completedFilter = new ToggleButton("Completed");
    completedFilter.setToggleGroup(filterGroup);
    completedFilter.setOnAction(e -> updateTodoList());

    var spacerRight = new Region();
    HBox.setHgrow(spacerRight, Priority.ALWAYS);

    clearCompleted = new Button("Clear Completed");
    clearCompleted.setOnAction(e -> onClearCompletedCommand.accept(new ClearCompletedCommand()));

    footer = new HBox(8);
    footer.setPadding(new Insets(8, 12, 8, 12));
    footer
        .getChildren()
        .addAll(
            todoCount,
            spacerLeft,
            allFilter,
            activeFilter,
            completedFilter,
            spacerRight,
            clearCompleted);

    var helptext = new Label("Double-click to edit a todo");
    helptext.setStyle("-fx-text-fill: darkgrey");

    var info = new HBox(8);
    info.setAlignment(Pos.CENTER);
    info.setPadding(new Insets(8, 12, 12, 12));
    info.getChildren().add(helptext);

    setStyle("-fx-font-family: Verdana");
    setPrefSize(500, 600);
    getChildren().addAll(header, todoList, footer, info);
  }

  public void run() {
    onTodosQuery.accept(new TodosQuery());
  }

  public void display(@NonNull TodosQueryResult result) {
    todos = result.getTodos();
    updateTodoList();

    var completedCount = result.getTodos().stream().filter(Todo::isCompleted).count();
    var activeTodoCount = result.getTodos().stream().filter(Todo::isActive).count();

    var hasTodos = !result.getTodos().isEmpty();
    toggleAll.setVisible(hasTodos);
    todoList.setVisible(hasTodos);
    todoList.setManaged(hasTodos);
    footer.setVisible(hasTodos);
    footer.setManaged(hasTodos);

    boolean allCompleted = result.getTodos().size() == completedCount;
    toggleAll.setSelected(hasTodos && allCompleted);

    var text = new Text(String.valueOf(activeTodoCount));
    text.setStyle("-fx-font-weight: bold");
    todoCount
        .getChildren()
        .setAll(text, new Text(" item" + (completedCount == 1 ? "" : "s") + " left"));

    clearCompleted.setVisible(completedCount > 0);
  }

  private void updateTodoList() {
    var todoModels =
        todos.stream()
            .filter(
                it ->
                    filterGroup.getSelectedToggle() == activeFilter && it.isActive()
                        || filterGroup.getSelectedToggle() == completedFilter && it.isCompleted()
                        || filterGroup.getSelectedToggle() == allFilter)
            .map(it -> new TodoModel(it, onToggleCommand, onEditCommand, onDestroyCommand))
            .collect(Collectors.toList());
    todoList.getItems().setAll(todoModels);
  }
}
