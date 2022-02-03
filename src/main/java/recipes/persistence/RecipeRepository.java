package recipes.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {


    Optional<Recipe> findRecipeById(long id);

    List<Recipe> findByCategoryLikeIgnoreCaseOrderByDateAsc(String category);

    List<Recipe> findByNameContainingIgnoreCaseOrderByDateAsc(String name);

    void deleteRecipeById(long id);

}
