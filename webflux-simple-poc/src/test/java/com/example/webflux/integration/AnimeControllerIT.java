package com.example.webflux.integration;

import com.example.webflux.domain.Anime;
import com.example.webflux.repository.AnimeRepository;
import com.example.webflux.util.AnimeCreator;
import com.example.webflux.util.WebTestClientUtil;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class AnimeControllerIT {

//  @Autowired
//  private WebTestClientUtil webTestClientUtil;

  @MockBean
  private AnimeRepository animeRepositoryMock;

  @Autowired
  private WebTestClient testClient;

//  private WebTestClient testClientUser;
//
//  private WebTestClient testClientAdmin;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install(builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
  }

  @Test
  public void blockHoundWorks() {
    try {
      FutureTask<?> task = new FutureTask<>(() -> {
        Thread.sleep(0);
        return "";
      });
      Schedulers.parallel().schedule(task);

      task.get(10, TimeUnit.SECONDS);
      Assertions.fail("Should fail");
    } catch (Exception e) {
      Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
    }
  }

  @BeforeEach
  public void setUp() {
//    testClientUser = webTestClientUtil.authenticateClient("user", "devdojo");
//    testClientAdmin = webTestClientUtil.authenticateClient("admin", "devdojo");

    BDDMockito.when(animeRepositoryMock.findAll())
        .thenReturn(Flux.just(anime));

    BDDMockito.when(animeRepositoryMock.findById(1))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.deleteById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.empty());

    BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepositoryMock.saveAll(
        List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
        .thenReturn(Flux.just(anime, anime));
  }

  @Test
  @DisplayName("findAll returns a flux of anime")
  @WithUserDetails("user")
  public void findAll_ReturnFluxOFAnime_WhenSuccessful() {

    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.[0].id").isEqualTo(anime.getId())
        .jsonPath("$.[0].name").isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("findAll returns a flux of anime")
  @WithUserDetails("user")
  public void findAll_Flavor2_ReturnFluxOFAnime_WhenSuccessful() {

    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Anime.class)
        .hasSize(1)
        .contains(anime);
  }


  @Test
  @DisplayName("findById returns a Mono with anime when it exists")
  @WithUserDetails("admin")
  public void findById_ReturnFluxOFAnime_WhenSuccessful() {

    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns Error when anime when does not exists")
  @WithUserDetails("admin")
  public void findById_ReturnError_WhenAnimeDoesNotExists() {

    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.empty());

    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
  }

  @Test
  @DisplayName("save creates anime when successful")
  @WithUserDetails("admin")
  public void save_CreatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("save returns error when name is empty")
  @WithUserDetails("admin")
  public void save_ReturnError_WhenNameIsEmpty() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);

  }

  @Test
  @DisplayName("delete removes anime when successful")
  @WithUserDetails("user")
  public void delete_RemovesAnime_WhenSuccessful() {
    testClient
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("update saves updated anime when successful")
  @WithUserDetails("user")
  public void update_UpdatesAnime_WhenSuccessful() {
    Anime animeToBeUpdated = AnimeCreator.createValidAnime();

    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

}
