package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import software.reloadly.sdk.core.internal.adapter.JackSonDateDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Discount implements Serializable {

    private static final long serialVersionUID = 5891709864552029712L;
    private double percentage;
    private double internationalPercentage;
    private double localPercentage;
    @JsonDeserialize(using = JackSonDateDeserializer.class)
    private Date updatedAt;
    private SimplifiedOperator operator;
}
