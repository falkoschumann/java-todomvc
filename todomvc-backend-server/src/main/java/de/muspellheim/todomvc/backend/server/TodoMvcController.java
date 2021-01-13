/*
 * TodoMVC - Backend Server
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.todomvc.backend.server;

import de.muspellheim.messages.*;
import de.muspellheim.todomvc.backend.TodoRepository;
import de.muspellheim.todomvc.backend.adapters.JsonTodoRepository;
import de.muspellheim.todomvc.backend.messagehandlers.ClearCompletedCommandHandler;
import de.muspellheim.todomvc.backend.messagehandlers.DestroyCommandHandler;
import de.muspellheim.todomvc.backend.messagehandlers.EditCommandHandler;
import de.muspellheim.todomvc.backend.messagehandlers.NewTodoCommandHandler;
import de.muspellheim.todomvc.backend.messagehandlers.TodosQueryHandler;
import de.muspellheim.todomvc.backend.messagehandlers.ToggleAllCommandHandler;
import de.muspellheim.todomvc.backend.messagehandlers.ToggleCommandHandler;
import de.muspellheim.todomvc.contract.messages.commands.ClearCompletedCommand;
import de.muspellheim.todomvc.contract.messages.commands.DestroyCommand;
import de.muspellheim.todomvc.contract.messages.commands.EditCommand;
import de.muspellheim.todomvc.contract.messages.commands.NewTodoCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleAllCommand;
import de.muspellheim.todomvc.contract.messages.commands.ToggleCommand;
import de.muspellheim.todomvc.contract.messages.queries.TodosQuery;
import de.muspellheim.todomvc.contract.messages.queries.TodosQueryResult;
import java.nio.file.Paths;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/")
public class TodoMvcController {
  static TodoRepository repository;

  static {
    var file = Paths.get("todos.json");
    repository = new JsonTodoRepository(file);
  }

  @Path("new-todo-command")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Legt ein neues To-Do in der Liste an.")
  @APIResponse(
      responseCode = "200",
      description = "Das Command wurde erfolgreich ausgeführt.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "400",
      description = "Das Command wurde nicht ausgeführt, weil es fehlerhaft formuliert war.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "500",
      description = "Beim Ausführen des Commands ist ein Fehler aufgetreten.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  public Response handleNewTodoCommand(NewTodoCommand command) {
    if (command.getTitle() == null) {
      return badRequest("Missing property `title` in new todo command.");
    }

    NewTodoCommandHandler handler = new NewTodoCommandHandler(repository);
    var status = handler.handle(command);
    return checkCommandStatus(status);
  }

  @Path("toggle-command")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Schaltet den Erledigt-Zustand eines To-Do in der Liste um.")
  @APIResponse(
      responseCode = "200",
      description = "Das Command wurde erfolgreich ausgeführt.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "400",
      description = "Das Command wurde nicht ausgeführt, weil es fehlerhaft formuliert war.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "500",
      description = "Beim Ausführen des Commands ist ein Fehler aufgetreten.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  public Response handleToggleCommand(ToggleCommand command) {
    if (command.getId() == null) {
      return badRequest("Missing property `id` in toggle command.");
    }

    ToggleCommandHandler handler = new ToggleCommandHandler(repository);
    var status = handler.handle(command);
    return checkCommandStatus(status);
  }

  @Path("toggle-all-command")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Setzt den Erledigt-Zustand aller To-Do's in derListe.")
  @APIResponse(
      responseCode = "200",
      description = "Das Command wurde erfolgreich ausgeführt.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "400",
      description = "Das Command wurde nicht ausgeführt, weil es fehlerhaft formuliert war.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "500",
      description = "Beim Ausführen des Commands ist ein Fehler aufgetreten.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  public Response handleToggleAllCommand(ToggleAllCommand command) {
    if (command.getCompleted() == null) {
      return badRequest("Missing property `completed` in toggle all command.");
    }

    ToggleAllCommandHandler handler = new ToggleAllCommandHandler(repository);
    var status = handler.handle(command);
    return checkCommandStatus(status);
  }

  @Path("edit-command")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Ändert ein To-Do in der Liste.")
  @APIResponse(
      responseCode = "200",
      description = "Das Command wurde erfolgreich ausgeführt.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "400",
      description = "Das Command wurde nicht ausgeführt, weil es fehlerhaft formuliert war.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "500",
      description = "Beim Ausführen des Commands ist ein Fehler aufgetreten.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  public Response handleEditCommand(EditCommand command) {
    if (command.getId() == null) {
      return badRequest("Missing property `id` in edit command.");
    }
    if (command.getTitle() == null) {
      return badRequest("Missing property `title` in edit command.");
    }

    EditCommandHandler handler = new EditCommandHandler(repository);
    var status = handler.handle(command);
    return checkCommandStatus(status);
  }

  @Path("destroy-command")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Entfernt ein To-Do aus der Liste.")
  @APIResponse(
      responseCode = "200",
      description = "Das Command wurde erfolgreich ausgeführt.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "400",
      description = "Das Command wurde nicht ausgeführt, weil es fehlerhaft formuliert war.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "500",
      description = "Beim Ausführen des Commands ist ein Fehler aufgetreten.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  public Response handleDestroyCommand(DestroyCommand command) {
    if (command.getId() == null) {
      return badRequest("Missing property `id` in edit command.");
    }

    DestroyCommandHandler handler = new DestroyCommandHandler(repository);
    var status = handler.handle(command);
    return checkCommandStatus(status);
  }

  @Path("clear-completed-command")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Entfernt alle erledigten To-Do's aus der Liste.")
  @APIResponse(
      responseCode = "200",
      description = "Das Command wurde erfolgreich ausgeführt.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "400",
      description = "Das Command wurde nicht ausgeführt, weil es fehlerhaft formuliert war.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  @APIResponse(
      responseCode = "500",
      description = "Beim Ausführen des Commands ist ein Fehler aufgetreten.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = HttpCommandStatus.class)))
  public Response handleClearCompletedCommand(ClearCompletedCommand command) {
    ClearCompletedCommandHandler handler = new ClearCompletedCommandHandler(repository);
    var status = handler.handle(command);
    return checkCommandStatus(status);
  }

  @Path("todos-query")
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Gibt die To-Do-Liste zurück.")
  @APIResponse(
      responseCode = "200",
      description = "Das Ergebnis der Query.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = TodosQueryResult.class)))
  public TodosQueryResult handleTodosQuery() {
    TodosQueryHandler handler = new TodosQueryHandler(repository);
    return handler.handle(new TodosQuery());
  }

  private Response badRequest(String errorMessage) {
    return Response.status(Status.BAD_REQUEST)
        .entity(new HttpCommandStatus(new Failure(errorMessage)))
        .build();
  }

  private Response checkCommandStatus(CommandStatus status) {
    if (status instanceof Success) {
      return Response.ok().entity(new HttpCommandStatus(new Success())).build();
    } else {
      var failure = (Failure) status;
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(new HttpCommandStatus(new Failure(failure.getErrorMessage())))
          .build();
    }
  }
}
