package software.reloadly.sdk.airtime.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import software.reloadly.sdk.core.internal.adapter.JackSonDateDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Promotion implements Serializable {

    private static final long serialVersionUID = 81835466585326849L;

    /**
     * Unique identifier for the given promotion
     */
    private Long id;

    /**
     * ID of operator to which the promotion applies
     */
    private Long operatorId;

    /**
     * Title of the promotion
     */
    private String title;

    /**
     * 2nd title for the promotion if any
     */
    private String title2;

    /**
     * Description of the promotion
     */
    private String description;

    /**
     * Date on which the promotion starts
     */
    @JsonDeserialize(using = JackSonDateDeserializer.class)
    private Date startDate;

    /**
     * Date on which the promotion ends
     */
    @JsonDeserialize(using = JackSonDateDeserializer.class)
    private Date endDate;

    /**
     * Amounts for which the promotion applies
     */
    private String denominations;

    /**
     * Amounts (in destination country currency) for which the promotion applies
     */
    private String localDenominations;
}
