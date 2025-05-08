package obj;

import java.io.Serializable;

/**
 * Represents a term in the academic calendar.
 * 
 * A term consists of a season (e.g., Spring, Summer, Fall, Winter) and a year.
 * It implements Comparable to allow sorting based on year and season.
 */
public class Term implements Serializable, Comparable<Term> {

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

  @Override
  public int compareTo(Term o) {
    if (this.year != o.year) {
      return Integer.compare(this.year, o.year);
    }
    return this.season.compareTo(o.season);
  }
}
