package repository;

import model.Display;

import java.util.List;

public interface DisplayRepository {
    Display findById(String displayId);

    void deleteById(String displayId);

    void insert(Display display);

    void deleteAll();

    List<Display> findAll();
}
