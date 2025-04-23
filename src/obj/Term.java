package obj;

import store.Store;
import store.StoredObject;

public class Term extends StoredObject {

  public static enum Season {
    SPRING, SUMMER, FALL, WINTER
  }

  public final Season season;
  public final int year;

  public Term(Store store, Season season, int year) {
    super(store);
    this.season = season;
    this.year = year;
  }

  @Override
  public String getId() {
    return season.toString() + year;
  }

  public boolean equals(Term other) {
    return this.season == other.season && this.year == other.year;
  }

}
