package obj;

import java.io.Serializable;

public class Term implements Serializable {

  public static enum Season {
    SPRING, SUMMER, FALL, WINTER
  }

  private final Season season;
  private final int year;

  public Term(Season season, int year) {
    this.season = season;
    this.year = year;
  }

  public Season getSeason() {
    return season;
  }

  public int getYear() {
    return year;
  }

  public String getName() {
    return season.toString() + " " + year;
  }

  public boolean equals(Term other) {
    return this.season == other.season && this.year == other.year;
  }

}
