package software.reloadly.sdk.core.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Page(@JsonProperty("content") List<T> content,
                @JsonProperty("number") int number,
                @JsonProperty("size") int size,
                @JsonProperty("totalElements") Long totalElements,
                @JsonProperty("pageable") JsonNode pageable,
                @JsonProperty("last") boolean last,
                @JsonProperty("totalPages") int totalPages,
                @JsonProperty("sort") JsonNode sort,
                @JsonProperty("first") boolean first,
                @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public Page(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public Page(List<T> content) {
        super(content);
    }
}
