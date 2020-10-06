package uk.nhs.digital.uec.api.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.nhs.digital.uec.api.model.DosService;

public interface ServicesRepositoryInterface extends ElasticsearchRepository<DosService, String> {

  // @Query(
  //     "{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\"],"
  //         + " \"fuzziness\": \"AUTO\"}}")
  // The hats make the match field more important.
  // Fuzziness not allowed for multi-match type of cross_fields
  @Query(
      "{\"multi_match\": {\"query\": \"?0\", \"type\": \"best_fields\", \"fields\":"
          + " [\"search_data^5\", \"name^?2\", \"public_name\", \"address^?3\", \"postcode\"],"
          + " \"fuzziness\": \"?1\", \"operator\":\"or\"}}")
  Iterable<DosService> findBySearchTerms(
      String searchTerms, Integer fuzzLevel, Integer namePriority, Integer addressPriority);
}
