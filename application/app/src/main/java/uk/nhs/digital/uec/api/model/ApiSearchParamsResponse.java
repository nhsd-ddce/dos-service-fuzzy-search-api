package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the successful response that is returned from the API when a successful search is
 * performed.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "search_criteria",
  "fuzz_level",
  "address_priority",
  "name_priority",
  "postcode_priority",
  "public_name_priority",
  "max_num_services_to_return"
})
public class ApiSearchParamsResponse {

  @JsonProperty("search_criteria")
  private List<String> searchCriteria;

  @JsonProperty("fuzz_level")
  private int fuzzLevel;

  @JsonProperty("address_priority")
  private int addressPriority;

  @JsonProperty("name_priority")
  private int namePriority;

  @JsonProperty("postcode_priority")
  private int postcodePriority;

  @JsonProperty("public_name_priority")
  private int publicNamePriority;

  @JsonProperty("max_num_services_to_return")
  private int maxNumServicesToReturn;

  public ApiSearchParamsResponse() {}

  private ApiSearchParamsResponse(ApiSearchParamsResponseBuilder builder) {
    this.searchCriteria = builder.searchCriteria;
    this.fuzzLevel = builder.fuzzLevel;
    this.addressPriority = builder.addressPriority;
    this.namePriority = builder.namePriority;
    this.postcodePriority = 0;
    this.publicNamePriority = 0;
    this.maxNumServicesToReturn = builder.maxNumServicesToReturn;
  }

  public static class ApiSearchParamsResponseBuilder {
    private List<String> searchCriteria;
    private int fuzzLevel;
    private int addressPriority;
    private int namePriority;
    private int maxNumServicesToReturn;

    public ApiSearchParamsResponseBuilder searchCriteria(List<String> searchCriteria) {
      this.searchCriteria = searchCriteria;
      return this;
    }

    public ApiSearchParamsResponseBuilder fuzzLevel(Integer fuzzLevel) {
      this.fuzzLevel = fuzzLevel;
      return this;
    }

    public ApiSearchParamsResponseBuilder addressPriority(Integer addressPriority) {
      this.addressPriority = addressPriority;
      return this;
    }

    public ApiSearchParamsResponseBuilder namePriority(Integer namePriority) {
      this.namePriority = namePriority;
      return this;
    }

    public ApiSearchParamsResponseBuilder maxNumServicesToReturn(Integer maxNumServicesToReturn) {
      this.maxNumServicesToReturn = maxNumServicesToReturn;
      return this;
    }

    public ApiSearchParamsResponse build() {
      return new ApiSearchParamsResponse(this);
    }
  }
}
