package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.utils.CollectionUtils;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.dynamo.PostcodeLocation;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;

@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  @Autowired private ApiUtilsServiceInterface apiUtilsService;

  @Autowired private LocationServiceInterface locationService;

  @Autowired private CustomServicesRepositoryInterface elasticsearch;

  @Autowired private ApiRequestParams apiRequestParams;

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(
      final String searchPostcode, final List<String> searchTerms) {

    List<DosService> dosServices = new ArrayList<DosService>();
    dosServices.addAll(
        elasticsearch.findServiceBySearchTerms(apiUtilsService.sanitiseSearchTerms(searchTerms)));

    // Calculate distance to services returned if we have a search location
    PostcodeLocation searchLocation = locationService.getLocationForPostcode(searchPostcode);
    // if dos services returns empty locations populate the dynamodb matching values with postcode
    List<PostcodeLocation> dosServicePostCodeLocation = populateEmptyLocation(dosServices);

    if (searchLocation != null) {
      for (DosService dosService : dosServices) {
        PostcodeLocation serviceLocation = new PostcodeLocation();
        serviceLocation.setPostcode(dosService.getPostcode());
        serviceLocation.setEasting(dosService.getEasting());
        serviceLocation.setNorthing(dosService.getNorthing());

        if (serviceLocation.getEasting() == null && serviceLocation.getNorthing() == null) {
          setServiceLocation(serviceLocation, dosServicePostCodeLocation);
        }
        dosService.setDistance(locationService.distanceBetween(searchLocation, serviceLocation));
      }
    }
    Collections.sort(dosServices);

    // return up to the max number of services, or the number of services returned. Which ever is
    // the least.
    int serviceResultLimit = apiRequestParams.getMaxNumServicesToReturn();
    if (apiRequestParams.getMaxNumServicesToReturn() > dosServices.size()) {
      serviceResultLimit = dosServices.size();
    }

    return dosServices.subList(0, serviceResultLimit);
  }

  private void setServiceLocation(
      PostcodeLocation serviceLocation, List<PostcodeLocation> dosServicePostCodeLocation) {
    if (dosServicePostCodeLocation != null) {
      String servicePostcodeWithoutSpace =
          apiUtilsService.removeBlankSpaces(serviceLocation.getPostcode());
      serviceLocation.setEasting(
          dosServicePostCodeLocation.stream()
              .filter(t -> t.getPostcode().equals(servicePostcodeWithoutSpace))
              .map(PostcodeLocation::getEasting)
              .findFirst()
              .orElse(null));
      serviceLocation.setNorthing(
          dosServicePostCodeLocation.stream()
              .filter(t -> t.getPostcode().equals(servicePostcodeWithoutSpace))
              .map(PostcodeLocation::getNorthing)
              .findFirst()
              .orElse(null));
    }
  }

  private List<PostcodeLocation> populateEmptyLocation(List<DosService> dosServices) {
    List<String> postCodes =
        dosServices.stream()
            .filter(t -> t.getEasting() == null && t.getNorthing() == null)
            .map(DosService::getPostcode)
            .collect(Collectors.toList());
    return !CollectionUtils.isNullOrEmpty(postCodes)
        ? locationService.getLocationsForPostcodes(postCodes)
        : Collections.emptyList();
  }
}
