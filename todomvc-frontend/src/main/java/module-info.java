module de.muspellheim.todomvc.frontend {
  requires static lombok;
  requires de.muspellheim.todomvc.contract;
  requires javafx.controls;

  exports de.muspellheim.todomvc.frontend;

  opens de.muspellheim.todomvc.frontend;
}
