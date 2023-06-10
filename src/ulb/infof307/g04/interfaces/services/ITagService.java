package ulb.infof307.g04.interfaces.services;

import java.util.List;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.models.Tag;

public interface ITagService {
    int createTag(String tag) throws ApiException;
    Tag getTagByName(String tagName) throws ApiException;
    List<Tag> getTags() throws ApiException;
    List<String> getTagNames() throws ApiException;
}
