package software.reloadly.sdk.giftcard.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftcardBrand implements Serializable {

    private static final long serialVersionUID = 4789588050925687831L;

    @JsonProperty("brandId")
    private Long id;
    @JsonProperty("brandName")
    private String name;
}
