package com.nicusa.resource;

import org.springframework.hateoas.ResourceSupport;

public class DrugResource extends ResourceSupport {

  private String _id;
  private String name;
  private String unii;

  public String get_id() {
    return _id;
  }
  public void set_id(String _id) {
    this._id = _id;
  }
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUnii() {
    return unii;
  }
  public void setUnii(String unii) {
    this.unii = unii;
  }
}
