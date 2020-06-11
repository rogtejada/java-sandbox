package com.rtejada;

import io.vertx.core.json.JsonObject;
import java.util.UUID;

public class Employee {

  private UUID id;
  private String name;
  private String email;

  public Employee() {
  }

  public Employee(UUID id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public Employee(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public Employee(JsonObject json) {
    this.name = json.getString("name");
    this.email = json.getString("email");
    this.id = UUID.fromString(json.getString("_id"));
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("name", name)
        .put("email", email);
    if (id != null) {
      json.put("_id", id);
    }
    return json;
  }

  public UUID getId() {
    return id;
  }

  public Employee setId(UUID id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Employee setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public Employee setEmail(String email) {
    this.email = email;
    return this;
  }

  @Override
  public String toString() {
    return "Employee{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
