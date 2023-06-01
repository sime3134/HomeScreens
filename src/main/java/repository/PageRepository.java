package repository;

import model.Page;

public interface PageRepository {
    Page findByPath(String path);

    void deleteByPath(String path);

    void insert(Page page);

    void deleteAll();
}
