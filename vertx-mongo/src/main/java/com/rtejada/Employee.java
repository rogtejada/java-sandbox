package com.rtejada;

import io.vertx.core.json.JsonObject;

public class Employee {

  private String id;
  private String name;
  private String email;

  public Employee() {
  }

  public Employee(String id, String name, String email) {
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
    this.id = json.getString("_id");
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("name", name)
        .put("email", email);
    if (id != null && !id.isEmpty()) {
      json.put("_id", id);
    }
    return json;
  }

  public String getId() {
    return id;
  }

  public Employee setId(String id) {
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
}
