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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Term other = (Term) obj;
    return this.season == other.season && this.year == other.year;
  }

  @Override
  public int hashCode() {
    int result = season != null ? season.hashCode() : 0;
    result = 31 * result + year;
    return result;
  }
}
