package lando.systems.prong.utils;

@FunctionalInterface
public interface Callback {
    void run(Object... params);
}
