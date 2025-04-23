package obj;

import store.Store;
import store.StoredObject;

public class Term extends StoredObject {

  public static enum Season {
    SPRING, SUMMER, FALL, WINTER
  }

  private final Season season;
  private final int year;

  public Term(Store store, Season season, int year) {
    super(store);
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
  public String getId() {
    return season.toString() + year;
  }

  public boolean equals(Term other) {
    return this.season == other.season && this.year == other.year;
  }

}
