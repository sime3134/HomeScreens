package repository;

import model.PageData;

public interface PageRepository {
    PageData findByPath(String path);

    void deleteByPath(String path);

    void insert(PageData pageData);

    void deleteAll();
}
