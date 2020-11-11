package io.cloudevents.jackson;

import java.util.Objects;

public class MyPojo {
    public int a;
    public String b;

    public MyPojo() {
    }

    public MyPojo(int a, String b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyPojo myPojo = (MyPojo) o;
        return getA() == myPojo.getA() &&
            Objects.equals(b, myPojo.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getA(), b);
    }
}
