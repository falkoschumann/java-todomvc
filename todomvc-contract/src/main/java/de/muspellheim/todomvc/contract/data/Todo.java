/*
 * TodoMVC - Contract
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.contract.data;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
  @NonNull String id;
  @NonNull String title;
  boolean completed;

  public Todo(String title) {
    this(UUID.randomUUID().toString(), title, false);
  }

  public final boolean isActive() {
    return !completed;
  }
}
