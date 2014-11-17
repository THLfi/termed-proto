package fi.thl.termed.util;

import com.google.common.base.Objects;

public final class Pair<L, R> {

  private final L left;
  private final R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(getClass()).add("left", left).add("right", right).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Pair that = (Pair) o;

    return Objects.equal(left, that.left) && Objects.equal(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(left, right);
  }

}
