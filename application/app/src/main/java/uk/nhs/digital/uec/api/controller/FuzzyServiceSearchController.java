package uk.nhs.digital.uec.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.ValidationServiceInterface;

/** Controller for Fuzzy searching of services. */
@RestController
@RequestMapping("/dosapi/dosservices/v0.0.1")
public class FuzzyServiceSearchController {

  @Autowired private FuzzyServiceSearchServiceInterface fuzzyServiceSearchService;

  @Autowired private ValidationServiceInterface validationService;

  @Autowired private ApiUtilsServiceInterface utils;

  @Autowired private ApiRequestParams requestParams;

  @Value("${param.services.max_num_services_to_return}")
  private String defaultMaxNumServicesToReturn;

  @Value("${param.services.fuzz_level}")
  private String defaultFuzzLevel;

  /**
   * Endpoint for retrieving services with attributes that match the search criteria provided.
   *
   * @param searchCriteria the search criteria containing the list of search criteria terms.
   * @param filterReferralRole if passed through, results will be filtered by the referral role
   *     provided.
   * @return {@link ApiResponse}
   */
  @GetMapping("services/byfuzzysearch")
  public ResponseEntity<ApiResponse> getServicesByFuzzySearch(
      @RequestParam(name = "search_term", required = false) List<String> searchCriteria,
      @RequestParam(name = "filter_referral_role", required = false) String filterReferralRole,
      @RequestParam(name = "max_number_of_services_to_return", required = false)
          Integer maxNumServicesToReturn,
      @RequestParam(name = "fuzz_level", required = false) Integer fuzzLevel,
      @RequestParam(name = "name_priority", required = false) Integer namePriority,
      @RequestParam(name = "address_priority", required = false) Integer addressPriority) {

    utils.configureApiRequestParams(
        fuzzLevel, filterReferralRole, maxNumServicesToReturn, namePriority, addressPriority);

    final ApiSuccessResponse response =
        new ApiSuccessResponse.ApiSuccessResponseBuilder()
            .searchCriteria(searchCriteria)
            .fuzzLevel(requestParams.getFuzzLevel())
            .maxNumServicesToReturn(requestParams.getMaxNumServicesToReturn())
            .build();

    try {
      validationService.validateSearchCriteria(searchCriteria);
      validationService.validateMinSearchCriteriaLength(searchCriteria);

      final List<DosService> dosServices =
          fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);
      response.setServices(dosServices);
    } catch (final ValidationException ex) {
      final ApiValidationErrorResponse valResponse =
          new ApiValidationErrorResponse(ex.getValidationCode(), ex.getMessage());
      return ResponseEntity.badRequest().body(valResponse);
    }

    return ResponseEntity.ok(response);
  }
}
