package recipes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.persistence.Recipe;
import recipes.persistence.RecipeRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RecipeController {

    RecipeRepository repository;

    RecipeController(RecipeRepository repository) {
        this.repository = repository;
    }

    /**
     * POST /api/new endpoint creates a new recipe
     * @param details - details about the currently authenticated user, automatically injected
     * @param recipe - JSON objected unmarshalled into Recipe instance. The JSON object must have
     *               the following keys: name, category, description - all as strings,
     *               and the keys ingredients and directions as arrays of strings. All the corresponding values
     *               must be non-empty.
     *
     * @return ResponseEntity with HttpStatus OK and a JSON with id key and the generated id valued
     */
    @PostMapping("/api/new")
    public ResponseEntity<?> addRecipe(@AuthenticationPrincipal UserDetails details, @RequestBody Recipe recipe) {
        validateRecipe(recipe);
        recipe.setUsername(details.getUsername());
        Recipe saved = repository.save(recipe);
        return new ResponseEntity<KV>(new KV(saved.getId()), HttpStatus.OK);
    }

    /**
     * GET /api/recipe/{id}
     * @param id - the id of the desired recipe
     * @return the recipe with specified id, as a JSON object,
     * or HttpStatus NOT FOUND if no recipe with the id is found
     */
    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable long id) {
        Optional<Recipe> optionalRecipe = repository.findRecipeById(id);
        if (optionalRecipe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalRecipe.get();
    }

    /**
     * DELETE /api/recipe/{id} endpoint that deletes a recipe by id,
     * if the author of the recipe is the currently authenticated user
     * @param id - the id of the recipe to be deleted
     * @param details - automatically injected details of the currently authenticated user
     * @return
     */
    @DeleteMapping("/api/recipe/{id}")
    @Transactional
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable long id, @AuthenticationPrincipal UserDetails details) {
        Optional<Recipe> optionalRecipe = repository.findRecipeById(id);
        if (optionalRecipe.isPresent()) {
            Recipe tmpRecipe = optionalRecipe.get(); ///////////////////////////////////////////
            if (!tmpRecipe.getUsername().equals(details.getUsername())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            System.out.println("ceva aici" + id);
            repository.deleteRecipeById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/recipe/{id} endpoint updates a recipe with the specified id. Solely authors of recipes can update recipes
     * A new JSON object must be sent with the same keys as a new recipe(name, category, description - all as strings,
     *            and the keys ingredients and directions as arrays of strings. All the corresponding values
     *            must be non-empty.)
     * @param id - the id for the recipe to be updated
     * @param restRecipe - JSON objected unmarshalled into Recipe instance
     * @param details - injected details of the currently authenticated user
     * @return
     */
    @PutMapping("/api/recipe/{id}")
    ResponseEntity<Recipe> modifyRecipe(@PathVariable long id, @RequestBody Recipe restRecipe,
                                        @AuthenticationPrincipal UserDetails details) {
        System.out.println(id);
        Optional<Recipe> optionalRecipe = repository.findRecipeById(id);
        if (optionalRecipe.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        validateRecipe(restRecipe);
        Recipe tmpRecipe = optionalRecipe.get();
        if (!tmpRecipe.getUsername().equals(details.getUsername())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        tmpRecipe.setName(restRecipe.getName());
        tmpRecipe.setCategory(restRecipe.getCategory());
        tmpRecipe.setDescription(restRecipe.getDescription());
        tmpRecipe.setIngredients(restRecipe.getIngredients());
        tmpRecipe.setDirections(restRecipe.getDirections());
        repository.save(tmpRecipe);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    /**
     * GET /api/recipe/search/ endpoint allows for searching the recipes either by category or by name via request parameters
     * Usage examples: /api/recipe/search/?name=cocktail /api/recipe/search/?category=beverage
     * @param params automatically injected form the pair ?name=value (where name is either name or category,
     *               and value is the word to be searched
     * @return all the recipes that include the searched term in either name or category, whichever is searched
     */
    @GetMapping("/api/recipe/search/")
    ResponseEntity<?> findRecipes(@RequestParam Map<String, String> params) {
        // validate that there is one parameter
        if(params.size() != 1) System.out.println("error!");

        Map.Entry<String, String> elem = params.entrySet().iterator().next();
        String paramName = elem.getKey();
        String text = elem.getValue();
        List<Recipe> foundRecipes;
        System.out.println(paramName + "  " + text);
        if (paramName.equalsIgnoreCase("category")) {
            foundRecipes = repository.findByCategoryLikeIgnoreCaseOrderByDateAsc(text);
            return new ResponseEntity<List<Recipe>>(foundRecipes, HttpStatus.OK);
        } else if (paramName.equalsIgnoreCase("name")) {
            foundRecipes = repository.findByNameContainingIgnoreCaseOrderByDateAsc(text);
            return new ResponseEntity<List<Recipe>>(foundRecipes, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * A valid recipe must not be null and must have the fields name, category and description non-empty, while the
     *  ingredients and directions fields must have at least one non-empty element
     * @param recipe a Recipe instance
     * @throws ResponseStatusException if recipe is not valid
     */
    public void validateRecipe(Recipe recipe) {
        if (recipe == null) badReq();
        if (recipe.getName() == null || recipe.getName().trim().length() == 0) badReq();
        if (recipe.getCategory() == null || recipe.getCategory().trim().length() == 0) badReq();
        if (recipe.getDescription() == null || recipe.getDescription().trim().length() == 0) badReq();
        if (recipe.getDirections() == null || recipe.getDirections().size() == 0 ||
                recipe.getDirections().get(0).trim().length() == 0) badReq();

        if (recipe.getIngredients() == null || recipe.getIngredients().size() == 0 ||
                recipe.getIngredients().get(0).trim().length() == 0) badReq();
    }

    public static void badReq() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}

/**
 * Key/Value class for JSON response with key id and a value provided via constructor
 */
class KV {
    long id;
    KV() { }
    public KV(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
