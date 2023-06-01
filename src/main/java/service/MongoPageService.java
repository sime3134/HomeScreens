package service;

import model.Page;
import repository.PageRepository;

public class MongoPageService implements PageService {
    private final PageRepository pageRepository;

    public MongoPageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public Page getPageData(String path) {
        return pageRepository.findByPath(path);
    }
}
