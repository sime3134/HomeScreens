package service;

import model.PageData;
import repository.PageRepository;

public class MongoPageService implements PageService {
    private final PageRepository pageRepository;

    public MongoPageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public PageData getPageData(String path) {
        return pageRepository.findByPath(path);
    }
}
