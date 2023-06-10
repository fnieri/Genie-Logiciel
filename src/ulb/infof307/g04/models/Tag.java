package ulb.infof307.g04.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Tag {
    private static final int MAX_CATEGORY_LENGTH = 20;
    private int id;
    private String name;

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag() {
        super();
    }

    public Tag(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode cardJson = objectMapper.readTree(json);
        this.id = cardJson.get("id").asInt();
        this.name = cardJson.get("name").asText();
    }

    public String toString() {
        return "Tag{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a set of tags from a comma separated string
     * @param tags comma separated string of tags
     * @return set of tags
     */
    public static Set<String> getTagsFromCommaSeparatedString(String tags) {
        List<String> tagsList = Arrays.asList(tags.split("\\s*,\\s*"));
        Set<String> uniqueTags = new HashSet<>(tagsList);

        return uniqueTags.stream()
                .filter(tag -> tag.length() <= MAX_CATEGORY_LENGTH)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            return this.name.equals(other.name) && this.id == other.id;
        }
        return super.equals(obj);
    }
}
