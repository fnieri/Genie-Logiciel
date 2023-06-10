package ulb.infof307.g04.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.services.ITagService;
import ulb.infof307.g04.models.Tag;

public class TagService extends AbstractAPIService implements ITagService {
    private static final String tagsEndpoint = "/api/quiz/tags/";

    private final CacheService cacheService = CacheService.getInstance();
    public static final String cacheKey = "api.tags";
    private AppState state = AppState.getInstance();

    public TagService(String baseUri) {
        super(baseUri);
    }

    public TagService(String baseUri, HttpClient client) {
        super(baseUri, client);
        this.state = AppState.getInstance();
    }

    /**
     * Create a tag given a name
     *
     * @param tagName Name of the tag
     * @return The id of the created tag
     * @throws ApiException If the tag already exists
     */
    @Override
    public int createTag(String tagName) throws ApiException {
        // NOTE: Check if the tag already exists
        try {
            Tag tag = this.getTagByName(tagName);
            return tag.getId();
        } catch (ApiException e) {
            // IGNORE: Tag does not exists
        }
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("name", tagName);

        try {
            HttpResponse<String> response = this.sendCreateObjectRequest(
                    tagsEndpoint,
                    this.state.getAuthToken(),
                    requestBody);
            handleExceptions(response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            cacheService.invalidate(cacheKey);
            return node.get("id").asInt();
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * Get a tag given its name
     *
     * @param tagName Name of the tag
     * @return The tag
     * @throws ApiException If the tag does not exist
     */
    @Override
    public Tag getTagByName(String tagName) throws ApiException {

        Optional<Tag> tag = this.getTags().stream()
                .filter(item -> item.getName().equals(tagName))
                .findFirst();

        if (tag.isEmpty()) {
            throw new ApiException(String.format("Tag named `%s` does not exists", tagName));
        }
        return tag.get();
    }

    /**
     * Get all tags
     *
     * @return The list of tags
     * @throws ApiException If the request fails
     */
    @Override
    public List<Tag> getTags() throws ApiException {
        try {
            String responseBody = cacheService.get(cacheKey);
            if (responseBody == null) {

                HttpResponse<String> response = this.sendFetchAllObjectRequest(
                        tagsEndpoint,
                        this.state.getAuthToken());

                handleExceptions(response);
                responseBody = response.body();
                cacheService.put(cacheKey, responseBody);
            }
            ObjectMapper mapper = new ObjectMapper();
            Tag[] tags = mapper.readValue(responseBody, Tag[].class);
            return Arrays.asList(tags);
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public List<String> getTagNames() throws ApiException {
        List<String> tagNames = new java.util.ArrayList<>();
        for (Tag tag : this.getTags()) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }
}
