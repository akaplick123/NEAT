package de.andre.neat;

public class ValueNotPresentException extends IllegalArgumentException {

  public ValueNotPresentException(String msg) {
    super(msg);
  }
}
