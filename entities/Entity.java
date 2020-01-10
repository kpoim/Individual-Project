package entities;

import java.util.Objects;

public class Entity {
  
  private Integer id;

  public Entity() {}

  public Entity(Integer ID) {
	this.id = ID;
  }
  
  public Integer getId() {
	return id;
  }

  public void setId(Integer id) {
	this.id = id;
  }

  @Override
  public int hashCode() {
	int hash = 5;
	hash = 53 * hash + Objects.hashCode(this.id);
	return hash;
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj) {
	  return true;
	}
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final Entity other = (Entity) obj;
	if (!Objects.equals(this.id, other.id)) {
	  return false;
	}
	return true;
  }

  @Override
  public String toString() {
	return "Entity{" + "ID=" + id + '}';
  }

  
}
