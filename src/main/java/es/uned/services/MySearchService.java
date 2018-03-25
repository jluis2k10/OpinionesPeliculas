package es.uned.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.components.TrakttvLookup;
import es.uned.entities.Account;
import es.uned.entities.Search;
import es.uned.repositories.CommentsWithSentimentRepo;
import es.uned.repositories.SearchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 */
@Service
public class MySearchService implements SearchService {

    @Autowired
    SearchRepo searchRepo;
    @Autowired
    CommentsWithSentimentRepo commentsWithSentimentRepo;
    @Autowired
    TrakttvLookup trakttvLookup;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Search findOne(Long id) {
        return searchRepo.findOne(id);
    }

    public void save(Search search) {
        searchRepo.save(search);
        // Guardamos comentarios
        commentsWithSentimentRepo.save(search.getComments());
    }

    @Override
    public Set<Search> mySearches(Account account) {
        return searchRepo.findByOwner(account);
    }

    @Override
    public Set<Search> usersSearches(Account account) {
        return searchRepo.findByOwnerNotAndOwner_Roles_RoleNot(account, "ADMIN");
    }

    @Override
    public ObjectNode JSONsearches(Account account) {
        ObjectNode result = mapper.createObjectNode();
        Set<Search> mySearches = mySearches(account);
        ArrayNode mySearchesArrayNode = constructSearchesArrayNode(mySearches);
        result.set("searches", mySearchesArrayNode);
        if (account.isAdmin()) {
            Set<Search> usersSearches = usersSearches(account);
            ArrayNode usersSearchesArrayNode = constructSearchesArrayNode(usersSearches);
            result.set("users_searches", usersSearchesArrayNode);
        }
        return result;
    }

    private ArrayNode constructSearchesArrayNode(Set<Search> searches) {
        ArrayNode searchesArrayNode = mapper.createArrayNode();
        searches.forEach(search -> {
            searchesArrayNode.add(search.toJSON(false));
        });
        return searchesArrayNode;
    }

    @Override
    public void delete(Search search) {
        commentsWithSentimentRepo.deleteInBatch(search.getComments());
        searchRepo.delete(search);
    }

}
