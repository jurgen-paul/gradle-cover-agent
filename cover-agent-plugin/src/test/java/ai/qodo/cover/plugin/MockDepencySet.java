package ai.qodo.cover.plugin;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectCollection;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskDependency;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class MockDepencySet implements DependencySet {

    private List<Dependency> dependencies = new ArrayList<>();

    @Override
    public TaskDependency getBuildDependencies() {
        return null;
    }

    @Override
    public void addLater(Provider<? extends Dependency> provider) {

    }

    @Override
    public void addAllLater(Provider<? extends Iterable<Dependency>> provider) {

    }

    @Override
    public <S extends Dependency> DomainObjectSet<S> withType(Class<S> type) {
        return null;
    }

    @Override
    public <S extends Dependency> DomainObjectCollection<S> withType(Class<S> type, Action<? super S> configureAction) {
        return null;
    }

    @Override
    public <S extends Dependency> DomainObjectCollection<S> withType(Class<S> type, Closure configureClosure) {
        return null;
    }

    @Override
    public DomainObjectSet<Dependency> matching(Spec<? super Dependency> spec) {
        return null;
    }

    @Override
    public DomainObjectSet<Dependency> matching(Closure spec) {
        return null;
    }

    @Override
    public Action<? super Dependency> whenObjectAdded(Action<? super Dependency> action) {
        return null;
    }

    @Override
    public void whenObjectAdded(Closure action) {

    }

    @Override
    public Action<? super Dependency> whenObjectRemoved(Action<? super Dependency> action) {
        return null;
    }

    @Override
    public void whenObjectRemoved(Closure action) {

    }

    @Override
    public void all(Action<? super Dependency> action) {

    }

    @Override
    public void all(Closure action) {

    }

    @Override
    public void configureEach(Action<? super Dependency> action) {

    }

    @Override
    public Set<Dependency> findAll(Closure spec) {
        return Set.of();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public @NotNull Iterator<Dependency> iterator() {
        return null;
    }

    @Override
    public @NotNull Object[] toArray() {
        return new Object[0];
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] a) {
        return null;
    }

    @Override
    public boolean add(Dependency dependency) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Dependency> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void forEach(Consumer<? super Dependency> action) {
        dependencies.forEach(action);
    }
}
